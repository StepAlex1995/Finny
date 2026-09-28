package com.stepalex.finny.data.local.room

import androidx.room.TypeConverter
import com.stepalex.finny.data.dto.HistoryChoiceDto
import com.stepalex.finny.data.dto.TaskAnswerDto
import kotlinx.serialization.json.Json

// Конвертер для сложных объектов Room
class RoomTypeConverters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromAnswersList(answers: List<TaskAnswerDto>): String = json.encodeToString(answers)

    @TypeConverter
    fun toAnswersList(jsonString: String): List<TaskAnswerDto> = json.decodeFromString(jsonString)

    @TypeConverter
    fun fromChoicesList(choices: List<HistoryChoiceDto>): String = json.encodeToString(choices)

    @TypeConverter
    fun toChoicesList(jsonString: String): List<HistoryChoiceDto> = json.decodeFromString(jsonString)
}

