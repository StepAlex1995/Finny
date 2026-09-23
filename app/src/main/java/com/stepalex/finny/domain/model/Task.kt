package com.stepalex.finny.domain.model

data class Task(
    val title: String,
    val description: String,
    val help: String?,
    val answers: List<TaskAnswer>,
    val byParent: Boolean,
    val type: TaskType,
    val complexity: Int,
    val frequency: Int
)

data class TaskAnswer(
    val text: String,
    val description: String,
    val taskResult: TaskResult,
    val isPrefer: Boolean           //предпочтительный вариант
)

data class TaskResult(
    val gold: Int,
    val food: Int,
    val mood: Int
)

enum class TaskType {
    Offline,            // Офлайн задание от родителей
    Accumulation,       // Накопления
    Necessity,          // Необходимые траты
    Optional,           // Хотелки
    Scam                // Мошенники
}