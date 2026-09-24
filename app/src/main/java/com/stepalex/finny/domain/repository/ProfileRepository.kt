package com.stepalex.finny.domain.repository

import com.stepalex.finny.domain.model.Goal
import com.stepalex.finny.domain.model.Profile

interface ProfileRepository {
    suspend fun getProfile(): Result<Profile?>
    suspend fun updateProfile(profile: Profile): Result<Unit>

    suspend fun getGoals(): Result<List<Goal>>
    suspend fun saveGoals(goals: List<Goal>): Result<Unit>

    suspend fun isFirstLaunch(): Boolean
    suspend fun setNotFirstLaunch()
}