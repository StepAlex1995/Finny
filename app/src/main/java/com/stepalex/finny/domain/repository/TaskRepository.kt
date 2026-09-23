package com.stepalex.finny.domain.repository

import com.stepalex.finny.data.dto.TaskDto
import com.stepalex.finny.domain.model.Task

interface TaskRepository {
    suspend fun getAllTasks(): Result<List<Task>>
    suspend fun getTaskByVersion(version: Int): Result<List<Task>>
    suspend fun saveTasks(tasks: List<TaskDto>,version: Int): Result<Unit>
}