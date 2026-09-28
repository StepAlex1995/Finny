package com.stepalex.finny.domain.use_cases.period

import com.stepalex.finny.domain.model.Task
import com.stepalex.finny.domain.repository.ProfileRepository
import com.stepalex.finny.domain.repository.TaskRepository

class GetCurrentTaskUseCase(
    private val profileRepository: ProfileRepository,
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(): Result<Task> =
        runCatching {
            val profile = profileRepository.getProfile().getOrThrow()
                ?: throw IllegalStateException("Профиль игрока не найден")

            val idTask = profile.currentPeriodTaskIds[profile.currentPeriodChoices.size]
            taskRepository.getTaskById(idTask).getOrThrow()
                ?: throw IllegalStateException("Задача не найдена")
        }

}