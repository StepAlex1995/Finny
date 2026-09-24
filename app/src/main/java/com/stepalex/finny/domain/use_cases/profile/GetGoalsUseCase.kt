package com.stepalex.finny.domain.use_cases.profile

import com.stepalex.finny.domain.model.Goal
import com.stepalex.finny.domain.repository.ProfileRepository

class GetGoalsUseCase(private val profileRepository: ProfileRepository) {
    suspend operator fun invoke(): List<Goal> {
        val result = profileRepository.getGoals().getOrNull()
        return result ?: emptyList()
    }
}