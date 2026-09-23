package com.stepalex.finny.data.repository

import android.content.Context
import com.stepalex.finny.domain.repository.TaskSettingRepository
import androidx.core.content.edit

class TaskSettingRepositoryImpl(context: Context) : TaskSettingRepository {
    private val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    override suspend fun getLastSavedVersion(): Int = prefs.getInt("tasks_json_version", 0)

    override suspend fun saveLastVersion(version: Int) {
        prefs.edit { putInt("tasks_json_version", version) }
    }

    override suspend fun getCountTaskPerPeriod(): Int = prefs.getInt("count_task_per_period", 5)

    override suspend fun saveCountTaskPerPeriod(count: Int) {
        prefs.edit { putInt("count_task_per_period", count) }
    }

    override suspend fun getDurationPeriod(): Int = prefs.getInt("duration_period", 24)

    override suspend fun saveDurationPeriod(hours: Int) {
        prefs.edit { putInt("duration_period", hours) }
    }
}