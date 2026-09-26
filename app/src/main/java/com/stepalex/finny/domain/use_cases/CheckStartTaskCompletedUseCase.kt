package com.stepalex.finny.domain.use_cases

import com.stepalex.finny.domain.repository.TaskSettingRepository

class CheckStartTaskCompletedUseCase(private val taskSettingRepository: TaskSettingRepository) {

    suspend operator fun invoke(): Boolean = taskSettingRepository.isStartTaskCompleted()
}