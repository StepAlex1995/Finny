package com.stepalex.finny.domain.use_cases

import com.stepalex.finny.domain.model.Task
import com.stepalex.finny.domain.repository.TaskRepository
import com.stepalex.finny.domain.repository.TaskSettingRepository

class GetLastVersionTaskUseCase(
    private val repository: TaskRepository,
    private val versionRepository: TaskSettingRepository
) {
    suspend operator fun invoke(): Result<List<Task>> {
        val currentSavedVersion = versionRepository.getLastSavedVersion()
        return repository.getTaskByVersion(currentSavedVersion)
    }
}