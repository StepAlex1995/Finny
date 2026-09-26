package com.stepalex.finny.domain.use_cases

import com.stepalex.finny.domain.repository.TaskSettingRepository

class SavePeriodStartTimeUseCase(private val taskSettingRepository: TaskSettingRepository) {
    suspend operator fun invoke(count: Long) {
        taskSettingRepository.savePeriodStartTime(count)
    }
}