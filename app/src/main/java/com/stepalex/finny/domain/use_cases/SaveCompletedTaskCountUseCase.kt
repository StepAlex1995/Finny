package com.stepalex.finny.domain.use_cases

import com.stepalex.finny.domain.repository.TaskSettingRepository

class SaveCompletedTaskCountUseCase(private val taskSettingRepository: TaskSettingRepository) {

    suspend operator fun invoke(count: Int) {
        taskSettingRepository.saveCompletedTaskCount(count)
    }
}