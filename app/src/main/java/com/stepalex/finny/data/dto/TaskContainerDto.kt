package com.stepalex.finny.data.dto

import com.stepalex.finny.domain.model.Task
import com.stepalex.finny.domain.model.TaskAnswer
import com.stepalex.finny.domain.model.TaskResult
import com.stepalex.finny.domain.model.TaskType
import kotlinx.serialization.Serializable


@Serializable
data class TaskContainerDto(
    val version: Int,
    val tasks: List<TaskDto>
)

@Serializable
data class TaskDto(
    val title: String,
    val description: String,
    val help: String? = null,
    val answers: List<TaskAnswerDto>,
    val byParent: Boolean = false, // Защита от отсутствующего поля в JSON
    val type: String,
    val complexity: Int = 1,
    val frequency: Int = 1
) {
    fun toDomain() = Task(
        title = title,
        description = description,
        help = help,
        answers = answers.map { it.toDomain() },
        byParent = byParent,
        type = runCatching { TaskType.valueOf(type) }.getOrDefault(TaskType.Optional),
        complexity = complexity,
        frequency = frequency
    )
}

@Serializable
data class TaskAnswerDto(
    val text: String,
    val description: String,
    val taskResult: TaskResultDto,
    val isPrefer: Boolean = false
) {
    fun toDomain() = TaskAnswer(
        text = text,
        description = description,
        taskResult = taskResult.toDomain(),
        isPrefer = isPrefer
    )
}

@Serializable
data class TaskResultDto(
    val gold: Int = 0,
    val food: Int = 0,
    val mood: Int =0
) {
    fun toDomain() = TaskResult(gold = gold, food = food, mood = mood)
}