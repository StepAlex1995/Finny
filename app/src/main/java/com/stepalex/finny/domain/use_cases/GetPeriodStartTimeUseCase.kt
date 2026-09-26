package com.stepalex.finny.domain.use_cases

import com.stepalex.finny.domain.repository.TaskSettingRepository

class GetPeriodStartTimeUseCase(
    private val taskSettingRepository: TaskSettingRepository
) {
    suspend operator fun invoke(): Long {
        return taskSettingRepository.getPeriodStartTime()
    }
}