package com.stepalex.finny.data.repository

import com.stepalex.finny.data.dto.TaskAnswerDto
import com.stepalex.finny.data.dto.TaskDto
import com.stepalex.finny.data.local.room.TaskDao
import com.stepalex.finny.data.entity.TaskEntity
import com.stepalex.finny.data.source.TaskAssetDataSource
import com.stepalex.finny.domain.model.Task
import com.stepalex.finny.domain.model.TaskType
import com.stepalex.finny.domain.repository.TaskRepository
import kotlinx.serialization.json.Json

class TaskRepositoryImpl(
    private val dataSource: TaskAssetDataSource,
    private val taskDao: TaskDao,
    private val json: Json
) : TaskRepository {
    override suspend fun getAllTasks(): Result<List<Task>> = runCatching {
        val entities = taskDao.getAllTasks()

        // Мапим каждую сущность БД в доменную модель
        entities.map { entity ->
            // Десериализуем JSON-строку ответов обратно в список DTO
            val answersDtoList = json.decodeFromString<List<TaskAnswerDto>>(entity.answersJson)

            Task(
                id = entity.id,
                title = entity.title,
                description = entity.description,
                help = entity.help,
                answers = answersDtoList.map { it.toDomain() },
                byParent = entity.byParent,
                type = runCatching { TaskType.valueOf(entity.type) }.getOrDefault(TaskType.Optional),
                complexity = entity.complexity,
                frequency = entity.frequency
            )
        }
    }

    override suspend fun getTaskById(id: Long): Result<Task?> = runCatching {
        val entities = taskDao.getTasksById(id)

        // Мапим каждую сущность БД в доменную модель
        entities.map { entity ->
            // Десериализуем JSON-строку ответов обратно в список DTO
            val answersDtoList = json.decodeFromString<List<TaskAnswerDto>>(entity.answersJson)

            Task(
                id = entity.id,
                title = entity.title,
                description = entity.description,
                help = entity.help,
                answers = answersDtoList.map { it.toDomain() },
                byParent = entity.byParent,
                type = runCatching { TaskType.valueOf(entity.type) }.getOrDefault(TaskType.Optional),
                complexity = entity.complexity,
                frequency = entity.frequency
            )
        }.first()
    }

    override suspend fun getTaskByVersion(version: Int): Result<List<Task>> = runCatching {
        val entities = taskDao.getTasksByVersion(version)

        // Мапим каждую сущность БД в доменную модель
        entities.map { entity ->
            // Десериализуем JSON-строку ответов обратно в список DTO
            val answersDtoList = json.decodeFromString<List<TaskAnswerDto>>(entity.answersJson)

            Task(
                id = entity.id,
                title = entity.title,
                description = entity.description,
                help = entity.help,
                answers = answersDtoList.map { it.toDomain() },
                byParent = entity.byParent,
                type = runCatching { TaskType.valueOf(entity.type) }.getOrDefault(TaskType.Optional),
                complexity = entity.complexity,
                frequency = entity.frequency
            )
        }
    }

    override suspend fun saveTasks(tasks: List<TaskDto>, version: Int): Result<Unit> = runCatching {
        val entities = tasks.map { dto ->
            TaskEntity(
                title = dto.title,
                description = dto.description,
                help = dto.help,
                answersJson = Json.encodeToString(dto.answers),
                byParent = dto.byParent,
                type = dto.type,
                complexity = dto.complexity,
                frequency = dto.frequency,
                version = version
            )
        }
        taskDao.insertTasks(entities)
    }
}