package com.stepalex.finny.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.stepalex.finny.data.dto.TaskAnswerDto
import kotlinx.serialization.json.Json

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val help: String? = null,
    val answersJson: String, // Храним ответы в виде JSON строки
    val byParent: Boolean,
    val type: String,
    val complexity: Int,
    val frequency: Int,
    val version: Int
)

// Конвертер для сложных объектов Room
class RoomTypeConverters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromAnswersList(answers: List<TaskAnswerDto>): String = json.encodeToString(answers)

    @TypeConverter
    fun toAnswersList(jsonString: String): List<TaskAnswerDto> = json.decodeFromString(jsonString)
}

