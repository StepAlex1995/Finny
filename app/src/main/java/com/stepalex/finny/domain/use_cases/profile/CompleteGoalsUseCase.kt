package com.stepalex.finny.domain.use_cases.profile

import com.stepalex.finny.domain.model.Goal
import com.stepalex.finny.domain.model.GoalState
import com.stepalex.finny.domain.model.Profile
import com.stepalex.finny.domain.repository.ProfileRepository

class CompleteGoalsUseCase(private val profileRepository: ProfileRepository) {
    suspend operator fun invoke(): Result<Profile?> = runCatching {
        var profile = profileRepository.getProfile().getOrNull()
        val goals = profileRepository.getGoals().getOrNull()

        val updatedGoals = mutableListOf<Goal>()
        goals!!.forEach { goal ->
            if (goal.name == profile!!.currentGoal!!.name) {
                updatedGoals.add(goal.copy(status = GoalState.AVAILABLE))
            } else {
                updatedGoals.add(goal)
            }
        }

        profileRepository.saveGoals(goals)
        profile = profile!!.copy(
            countMoney = maxOf(0, profile.countMoney - profile.currentGoal!!.cost),
            currentGoal = null
        )

        profileRepository.updateProfile(profile = profile)
        profile
    }
}