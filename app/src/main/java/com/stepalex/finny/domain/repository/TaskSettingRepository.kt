package com.stepalex.finny.domain.repository

interface TaskSettingRepository {
    suspend fun getLastSavedVersion(): Int
    suspend fun saveLastVersion(version: Int)

    suspend fun getCountTaskPerPeriod(): Int
    suspend fun saveCountTaskPerPeriod(count: Int)
    suspend fun getDurationPeriod(): Int
    suspend fun saveDurationPeriod(hours: Int)

}