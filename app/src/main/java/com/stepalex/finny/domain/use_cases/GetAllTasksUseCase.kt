package com.stepalex.finny.domain.use_cases

import com.stepalex.finny.domain.model.Task
import com.stepalex.finny.domain.repository.TaskRepository

class GetAllTasksUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(): Result<List<Task>> {
        return repository.getAllTasks()
    }
}