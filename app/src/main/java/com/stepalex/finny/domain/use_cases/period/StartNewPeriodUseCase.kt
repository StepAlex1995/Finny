package com.stepalex.finny.domain.use_cases.period

import com.stepalex.finny.domain.model.FoodInventory
import com.stepalex.finny.domain.model.Profile
import com.stepalex.finny.domain.model.ScheduledEffect
import com.stepalex.finny.domain.model.Task
import com.stepalex.finny.domain.model.TaskType
import com.stepalex.finny.domain.model.TypeFood
import com.stepalex.finny.domain.repository.PeriodRepository
import com.stepalex.finny.domain.repository.ProfileRepository
import com.stepalex.finny.domain.repository.TaskRepository
import kotlin.math.abs

data class PeriodInfo(
    val profile: Profile,
    val scheduledEffects: List<ScheduledEffect>,
    val currentTask: Task?
)

class StartNewPeriodUseCase(
    private val profileRepository: ProfileRepository,
    private val periodRepository: PeriodRepository,
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(): Result<PeriodInfo> =
        runCatching {
            // 1. Получаем профиль игрока
            val profile = profileRepository.getProfile().getOrThrow()
                ?: throw IllegalStateException("Профиль игрока не найден")

            // Если приложение закрылось и открылось заново, а задачи уже сгенерированы — не пересоздаем их!
            if (profile.currentPeriodTaskIds.isNotEmpty()) {
                val firstTask =
                    taskRepository.getTaskById(profile.currentPeriodTaskIds.first()).getOrNull()
                return@runCatching PeriodInfo(profile, emptyList(), firstTask)
            }

            val currentPeriodIndex = profile.currentPeriodIndex

            // 2. Извлекаем из базы отложенные эффекты текущего раунда
            val rawEffects = periodRepository.getEffectsForPeriod(currentPeriodIndex).getOrThrow()

            // 3. Подтягиваем все оригинальные задачи
            val allTasksList = taskRepository.getAllTasks().getOrThrow()
            val allTasksMap = allTasksList.associateBy { it.id }

            // Разворачиваем эффекты для UI
            val detailedEffects = rawEffects.mapNotNull { raw ->
                val originalTask = allTasksMap[raw.taskId]
                if (originalTask != null) {
                    ScheduledEffect(
                        raw.targetPeriodIndex,
                        originalTask,
                        raw.answerText,
                        raw.gold,
                        raw.food,
                        raw.mood
                    )
                } else null
            }

            // 4. Считаем баланс с учётом работы (+100) и вкладов/долгов
            var newMoney = profile.countMoney + 100 // вознаграждение за работу
            var newFood = profile.countFood - 2 //голод
            var newMood = profile.countMood
            val updatedFoodInventory = profile.foodInventory.map { it.copy() }.toMutableList()

            var moneyEffectByPreviewsPeriod =
                0     //суммарный эффект изменение денег за предыдущие периоды
            for (effect in detailedEffects) {
                moneyEffectByPreviewsPeriod += effect.gold
                newMoney += effect.gold
                newFood += effect.food
                newMood += effect.mood
                /*if (effect.food != 0) {
                    modifyFoodInventory(updatedFoodInventory, TypeFood.Apple, effect.food)
                }*/
            }

            //val totalFoodCount = updatedFoodInventory.sumOf { it.count }

            // 5. Алгоритм УМНОГО ПОДБОРА задач (перенесён прямо сюда)
            val shuffledPool =
                allTasksList.flatMap { task -> List(task.frequency) { task } }.shuffled()
            val selectedTasks = mutableListOf<Task>()
            var strictMaxCost = 0
            val targetTypes = mutableListOf(
                TaskType.Necessity,
                TaskType.Optional,
                TaskType.Optional,
                TaskType.Accumulation,
                TaskType.Scam
            )

            for (type in targetTypes) {
                val suitableTask = shuffledPool.firstOrNull { task ->
                    task.type == type && !selectedTasks.contains(task) && run {
                        val maxTaskCost = task.answers
                            .flatMap { it.taskResult }
                            .filter { it.period == 0 && it.gold < 0 }
                            .map { abs(it.gold) }
                            .maxOrNull() ?: 0
                        (strictMaxCost + maxTaskCost) <= newMoney
                    }
                }
                if (suitableTask != null) {
                    selectedTasks.add(suitableTask)
                    val maxTaskCost = suitableTask.answers.flatMap { it.taskResult }
                        .filter { it.period == 0 && it.gold < 0 }.map { abs(it.gold) }.maxOrNull()
                        ?: 0
                    strictMaxCost += maxTaskCost
                }
            }

            // Страховочный добор бесплатными задачами
            if (selectedTasks.size < 5) {
                val remainingCount = 5 - selectedTasks.size
                val cheapFallback = shuffledPool.filter { task ->
                    !selectedTasks.contains(task) && task.answers.flatMap { it.taskResult }
                        .all { it.period != 0 || it.gold >= 0 }
                }.take(remainingCount)
                selectedTasks.addAll(cheapFallback)
            }

            // 6. Записываем ID сгенерированных задач в профиль
            val updatedProfile = profile.copy(
                countMoney = maxOf(0, newMoney),
                countFood = minOf(maxOf(0, newFood), 5),
                countMood = minOf(maxOf(1, newMood), 5),
                foodInventory = updatedFoodInventory,
                currentPeriodTaskIds = selectedTasks.map { it.id }, // <--- СОХРАНЯЕМ ИХ ТУТ
                currentPeriodChoices = emptyList(), // Очищаем старые ответы раунда
                moneyEffectByPreviewsPeriod = moneyEffectByPreviewsPeriod
            )

            // 7. Обновляем базу данных
            profileRepository.updateProfile(updatedProfile).getOrThrow()
            periodRepository.deleteEffectsForPeriod(currentPeriodIndex).getOrThrow()


            //return@runCatching updatedProfile to detailedEffects
            return@runCatching PeriodInfo(updatedProfile, detailedEffects, selectedTasks.first())
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