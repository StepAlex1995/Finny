package com.stepalex.finny.domain.use_cases.profile

import com.stepalex.finny.domain.model.Profile
import com.stepalex.finny.domain.repository.ProfileRepository

class GetProfileUseCase(private val profileRepository: ProfileRepository) {
    suspend operator fun invoke(): Profile? {
        val requestProfile = profileRepository.getProfile()
        return requestProfile.getOrNull()
    }
}