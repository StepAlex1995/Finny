package com.stepalex.finny.domain.use_cases.profile

import com.stepalex.finny.domain.model.Profile
import com.stepalex.finny.domain.repository.ProfileRepository

class UpdateProfileUseCase(private val profileRepository: ProfileRepository) {
    suspend operator fun invoke(profile: Profile) {
        profileRepository.updateProfile(profile)
    }
}