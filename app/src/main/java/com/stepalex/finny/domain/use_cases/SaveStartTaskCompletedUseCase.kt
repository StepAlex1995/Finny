package com.stepalex.finny.domain.use_cases

import com.stepalex.finny.domain.repository.TaskSettingRepository

class SaveStartTaskCompletedUseCase(private val taskSettingRepository: TaskSettingRepository) {
    suspend operator fun invoke(isStarted: Boolean) {
        taskSettingRepository.saveStartTaskCompleted(isStarted)
    }
}