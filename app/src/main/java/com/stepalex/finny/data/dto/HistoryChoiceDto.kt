package com.stepalex.finny.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class HistoryChoiceDto(
    val taskId: Long,
    val answerText: String
)