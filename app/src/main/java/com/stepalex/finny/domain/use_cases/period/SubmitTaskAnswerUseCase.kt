package com.stepalex.finny.domain.use_cases.period

import android.util.Log
import com.stepalex.finny.domain.model.FinancialRank
import com.stepalex.finny.domain.model.FoodInventory
import com.stepalex.finny.domain.model.HistoryChoice
import com.stepalex.finny.domain.model.HistoryTaskSnapshot
import com.stepalex.finny.domain.model.PeriodHistory
import com.stepalex.finny.domain.model.Profile
import com.stepalex.finny.domain.model.RawPeriodHistory
import com.stepalex.finny.domain.model.RawScheduledEffect
import com.stepalex.finny.domain.model.Task
import com.stepalex.finny.domain.model.TaskAnswer
import com.stepalex.finny.domain.model.TaskType
import com.stepalex.finny.domain.model.TypeFood
import com.stepalex.finny.domain.repository.PeriodRepository
import com.stepalex.finny.domain.repository.ProfileRepository
import com.stepalex.finny.domain.repository.TaskRepository
import kotlin.collections.isNotEmpty
import kotlin.math.abs


class SubmitTaskAnswerUseCase(
    private val profileRepository: ProfileRepository,
    private val periodRepository: PeriodRepository,
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(
        task: Task,
        chosenAnswer: TaskAnswer
    ): Result<Pair<Profile, PeriodHistory?>> =
        runCatching {
            val profile = profileRepository.getProfile().getOrThrow()
                ?: throw IllegalStateException("Профиль игрока не найден")

            // 1. Применяем мгновенные эффекты (period == 0) ответа к текущим ресурсам
            var money = profile.countMoney
            var food = profile.countFood
            var mood = profile.countMood
            //Потом можно будет давать вещи из инвентаря и еды за задания
            val updatedFoodInventory = profile.foodInventory.map { it.copy() }.toMutableList()

            // Создаём временный список для сбора отложенных эффектов из ТЕКУЩЕЙ задачи
            val futureEffectsToSave = mutableListOf<RawScheduledEffect>()

            for (result in chosenAnswer.taskResult) {
                if (result.period == 0) {
                    money += result.gold
                    food += result.food
                    mood += result.mood
                    /*if (result.food != 0) {
                    //Потом можно будет давать вещи из инвентаря и еды за задания
                        modifyFoodInventory(updatedFoodInventory, TypeFood.Apple, result.food)
                    }*/
                } else {
                    // СЮДА ПОПАДАЮТ ЭФФЕКТЫ НА БУДУЩЕЕ (например, period = 1 или 2)
                    futureEffectsToSave.add(
                        RawScheduledEffect(
                            targetPeriodIndex = profile.currentPeriodIndex + result.period, // Абсолютный номер будущего периода
                            taskId = task.id,                                              // Ссылка на текущую задачу
                            answerText = chosenAnswer.text,                                // Текст выбранного ответа для истории трат
                            gold = result.gold,
                            food = result.food,
                            mood = result.mood
                        )
                    )
                }
            }
            // МГНОВЕННО ЗАПИСЫВАЕМ БУДУЩИЕ ЭФФЕКТЫ В БД
            // Даже если раунд не закончен, вклад или долг уже сохранены на будущее!
            if (futureEffectsToSave.isNotEmpty()) {
                periodRepository.saveScheduledEffects(futureEffectsToSave).getOrThrow()
            }
            Log.i("TEST", "futureEffectsToSave = $futureEffectsToSave")

            //val totalFoodCount = updatedFoodInventory.sumOf { it.count }

            // Добавляем текущий выбор в список раунда
            val updatedChoices = profile.currentPeriodChoices.toMutableList().apply {
                add(HistoryChoice(taskId = task.id, answerText = chosenAnswer.text))
            }

            var updatedProfile = profile.copy(
                countMoney = maxOf(0, money),
                countFood = minOf(maxOf(0, food), 5),
                countMood = minOf(maxOf(1, mood), 5),
                foodInventory = updatedFoodInventory,
                currentPeriodChoices = updatedChoices
            )


            var periodHistory: PeriodHistory? = null
            // 2. ПРОВЕРКА: Если это был ПОСЛЕДНИЙ (5-й) квиз периода — закрываем период
            if (updatedChoices.size >= 5) {
                val (rawHistory, history) = compilePeriodFinals(updatedProfile, 100)
                Log.i("TEST", "rawHistory = $rawHistory")
                Log.i("TEST", "history = $history")
                periodRepository.savePeriodHistory(rawHistory).getOrThrow()
                periodHistory = history

                // Сбрасываем поля раунда в профиле и переключаем счетчик периода вперед
                updatedProfile = updatedProfile.copy(
                    currentPeriodIndex = profile.currentPeriodIndex + 1,
                    currentPeriodTaskIds = emptyList(),
                    currentPeriodChoices = emptyList(),
                    countMoney = updatedProfile.countMoney,
                    countMood = updatedProfile.countMood,
                    countFood = updatedProfile.countFood,
                    foodInventory = updatedProfile.foodInventory,
                    moneyEffectByPreviewsPeriod = 0
                )
            }

            // 3. Сохраняем профиль (после каждого квиза или при закрытии периода)
            profileRepository.updateProfile(updatedProfile).getOrThrow()

            return@runCatching Pair(updatedProfile, periodHistory)
        }

    /**
     * Внутренний хелпер для сборки финальных отчетов трат по категориям при закрытии периода
     */
    suspend fun compilePeriodFinals(
        profile: Profile,
        workIncome: Int = 100,
    ): Pair<RawPeriodHistory, PeriodHistory> {
        // Поочередно или параллельно достаем из репозитория именно те 5 задач, которые были в раунде
        val tasksPool = profile.currentPeriodTaskIds.mapNotNull { id ->
            taskRepository.getTaskById(id).getOrNull()
        }
        // Создаем карту для быстрого сопоставления внутри цикла
        val tasksMap = tasksPool.associateBy { it.id }

        var spentOnNecessity = 0
        var spentOnOptional = 0
        var spentOnAccumulation = 0
        var lostToScam = 0

        var preferAnswersCount = 0
        var fellForScam = false

        val historyTaskSnapshotList = mutableListOf<HistoryTaskSnapshot>()
        // Проходимся по накопленному списку выборов ребенка в профиле
        for (choice in profile.currentPeriodChoices) {
            //для ui модели отображения данных
            if (tasksMap[choice.taskId] != null) {
                historyTaskSnapshotList.add(
                    HistoryTaskSnapshot(
                        tasksMap[choice.taskId]!!,
                        choice.answerText
                    )
                )
            }
            val originalTask = tasksMap[choice.taskId] ?: continue
            val chosenAnswer =
                originalTask.answers.firstOrNull { it.text == choice.answerText } ?: continue

            // Считаем количество предпочтительных ответов
            if (chosenAnswer.isPrefer) {
                preferAnswersCount++
            }

            // Проверяем, попался ли на удочку мошенников
            if (originalTask.type == TaskType.Scam && !chosenAnswer.isPrefer) {
                fellForScam = true
            }

            // Подсчитываем траты золота в текущем периоде (period == 0) для каждого типа задач
            for (result in chosenAnswer.taskResult) {
                if (result.period == 0 && result.gold < 0) {
                    val spentAmount = abs(result.gold)
                    when (originalTask.type) {
                        TaskType.Necessity -> spentOnNecessity += spentAmount
                        TaskType.Optional -> spentOnOptional += spentAmount
                        TaskType.Accumulation -> spentOnAccumulation += spentAmount
                        TaskType.Scam -> lostToScam += spentAmount
                        else -> {}
                    }
                }
            }
        }

        // Высчитываем финансовое звание периода на основе успехов ребенка
        val rank = when {
            fellForScam -> FinancialRank.SCAM_VICTIM
            preferAnswersCount == profile.currentPeriodChoices.size -> FinancialRank.MASTER
            preferAnswersCount >= 3 -> FinancialRank.SMART_BUYER
            else -> FinancialRank.SPENDER
        }

        // Собираем итоговую сырую модель истории
        return Pair(
            RawPeriodHistory(
                periodIndex = profile.currentPeriodIndex,
                workIncome = workIncome,
                spentOnNecessity = spentOnNecessity,
                spentOnOptional = spentOnOptional,
                spentOnAccumulation = spentOnAccumulation,
                lostToScam = lostToScam,
                pendingGoldEffect = profile.moneyEffectByPreviewsPeriod,
                finalFood = profile.countFood,
                finalMood = profile.countMood,
                rankAwarded = rank,
                choices = profile.currentPeriodChoices
            ),
            PeriodHistory(
                periodIndex = profile.currentPeriodIndex,
                workIncome = workIncome,
                spentOnNecessity = spentOnNecessity,
                spentOnOptional = spentOnOptional,
                spentOnAccumulation = spentOnAccumulation,
                lostToScam = lostToScam,
                pendingGoldEffect = profile.moneyEffectByPreviewsPeriod,
                finalFood = profile.countFood,
                finalMood = profile.countMood,
                rankAwarded = rank,
                chosenTasks = historyTaskSnapshotList
            )
        )
    }

    private fun modifyFoodInventory(
        inventory: MutableList<FoodInventory>,
        type: TypeFood,
        delta: Int
    ) {
        val index = inventory.indexOfFirst { it.typeFood == type }
        if (index != -1) {
            inventory[index] =
                inventory[index].copy(count = maxOf(0, inventory[index].count + delta))
        } else if (delta > 0) {
            inventory.add(FoodInventory(type, delta))
        }
    }
}