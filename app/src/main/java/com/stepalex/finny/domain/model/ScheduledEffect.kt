package com.stepalex.finny.domain.model

/**
 *  Запланированный эффект из прошлого
 */
data class ScheduledEffect(
    val targetPeriodIndex: Int, // В каком периоде сработает эффект
    val task: Task,               // <--- Полноценный объект исходной задачи
    val chosenAnswerText: String, // <--- Текст принятого решения
    val gold: Int,              // Изменение монет
    val food: Int,              // Изменение количества еды (предметов)
    val mood: Int               // Изменение настроения
)

data class RawScheduledEffect(
    val targetPeriodIndex: Int,
    val taskId: Long,
    val answerText: String,
    val gold: Int,
    val food: Int,
    val mood: Int
)