package com.stepalex.finny.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scheduled_effects")
data class ScheduledEffectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val targetPeriodIndex: Int, // В каком периоде сработает (например, в 3-м)
    val taskId: Long,           // <--- Ссылка на оригинальную задачу
    val answerText: String,     // <--- Какой ответ привел к этому эффекту
    val gold: Int,
    val food: Int,
    val mood: Int
)