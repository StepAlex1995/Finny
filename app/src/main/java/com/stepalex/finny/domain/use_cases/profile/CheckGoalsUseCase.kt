package com.stepalex.finny.domain.use_cases.profile

import com.stepalex.finny.domain.model.Goal
import com.stepalex.finny.domain.model.GoalState
import com.stepalex.finny.domain.model.GoalType
import com.stepalex.finny.domain.repository.ProfileRepository

class CheckGoalsUseCase(private val profileRepository: ProfileRepository) {
    suspend operator fun invoke() {
        if (profileRepository.isFirstLaunch()) {
            val defaultGoals = listOf(
                Goal(
                    name = "Хочу или надо",
                    description = "Кто-то перемешал все хотелки и нужды. Распредели все по местам и получи монетки!",
                    cost = 600,
                    goalType = GoalType.WONT_AND_NEED,
                    status = GoalState.NOT_AVAILABLE
                ),
                Goal(
                    name = "Что это такое",
                    description = "Найди среди всех табличек два одинаковых, угадай, что это такое и получай больше монеток!",
                    cost = 800,
                    goalType = GoalType.TERMS,
                    status = GoalState.NOT_AVAILABLE
                ),
                Goal(
                    name = "Шахта сокровищ",
                    description = "Один богач закопал свои сокровища в этой шахте, но чтоб их получить, нужно решить задачку.",
                    cost = 1200,
                    goalType = GoalType.MINE,
                    status = GoalState.NOT_AVAILABLE
                )
            )
            val resultSaving = profileRepository.saveGoals(goals = defaultGoals)
            if (resultSaving.isSuccess) {
                profileRepository.setNotFirstLaunch()
            }
        }
    }
}