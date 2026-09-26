package com.stepalex.finny.domain.use_cases

import com.stepalex.finny.domain.repository.TaskSettingRepository

class GetCompletedTasksCountUseCase(private val taskSettingRepository: TaskSettingRepository) {
    suspend operator fun invoke(): Int = taskSettingRepository.getCompletedTaskCount()
}