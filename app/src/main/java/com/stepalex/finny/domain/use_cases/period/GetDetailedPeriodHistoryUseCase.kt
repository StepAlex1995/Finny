package com.stepalex.finny.domain.use_cases.period

import com.stepalex.finny.domain.model.HistoryTaskSnapshot
import com.stepalex.finny.domain.model.PeriodHistory
import com.stepalex.finny.domain.repository.PeriodRepository
import com.stepalex.finny.domain.repository.TaskRepository


class GetDetailedPeriodHistoryUseCase(
    private val periodRepository: PeriodRepository,
    private val taskRepository: TaskRepository
) {
    /**
     * Возвращает полностью развёрнутую доменную модель истории с полноценными объектами Task.
     */
    suspend operator fun invoke(periodIndex: Int): Result<PeriodHistory?> = runCatching {
        // 1. Извлекаем сырую историю из объединенного PeriodRepository
        val rawHistory = periodRepository.getRawHistoryForPeriod(periodIndex).getOrThrow()
            ?: return@runCatching null

        // 2. Достаем список всех задач через ваш TaskRepository
        val allTasksList = taskRepository.getAllTasks().getOrThrow()

        // Превращаем список в быструю Map<Long, Task> по полю id
        val allTasksMap = allTasksList.associateBy { it.id }

        // 3. Собираем развёрнутые слепки задач и ответов
        val detailedSnapshots = rawHistory.choices.mapNotNull { choice ->
            val originalTask = allTasksMap[choice.taskId]
            if (originalTask != null) {
                HistoryTaskSnapshot(
                    task = originalTask,
                    chosenAnswerText = choice.answerText
                )
            } else null
        }

        // 4. Формируем финальную развернутую модель для UI
        return@runCatching PeriodHistory(
            periodIndex = rawHistory.periodIndex,
            workIncome = rawHistory.workIncome,
            spentOnNecessity = rawHistory.spentOnNecessity,
            spentOnOptional = rawHistory.spentOnOptional,
            spentOnAccumulation = rawHistory.spentOnAccumulation,
            lostToScam = rawHistory.lostToScam,
            pendingGoldEffect = rawHistory.pendingGoldEffect,
            finalFood = rawHistory.finalFood,
            finalMood = rawHistory.finalMood,
            rankAwarded = rawHistory.rankAwarded,
            chosenTasks = detailedSnapshots
        )
    }
}