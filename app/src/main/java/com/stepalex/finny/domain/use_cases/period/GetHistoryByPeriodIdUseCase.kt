package com.stepalex.finny.domain.use_cases.period

import com.stepalex.finny.domain.model.HistoryTaskSnapshot
import com.stepalex.finny.domain.model.PeriodHistory
import com.stepalex.finny.domain.repository.PeriodRepository
import com.stepalex.finny.domain.repository.TaskRepository

class GetHistoryByPeriodIdUseCase(
    val taskRepository: TaskRepository,
    val periodRepository: PeriodRepository
) {

    suspend operator fun invoke(periodId: Int): Result<PeriodHistory> = runCatching {
        val rawHistory = periodRepository.getRawHistoryForPeriod(periodId).getOrThrow()
            ?: throw IllegalStateException("История периода не найдена")

        val historyTaskSnapshotList = mutableListOf<HistoryTaskSnapshot>()

        rawHistory.choices.forEach { choice ->
            val task = taskRepository.getTaskById(choice.taskId).getOrNull()
            task?.let {
                historyTaskSnapshotList.add(HistoryTaskSnapshot(it, choice.answerText))
            }
        }
        PeriodHistory(
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
            chosenTasks = historyTaskSnapshotList
        )
    }
}