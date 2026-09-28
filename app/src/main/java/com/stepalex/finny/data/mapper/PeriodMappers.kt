package com.stepalex.finny.data.mapper

import com.stepalex.finny.data.dto.HistoryChoiceDto
import com.stepalex.finny.data.entity.PeriodHistoryEntity
import com.stepalex.finny.data.entity.ScheduledEffectEntity
import com.stepalex.finny.domain.model.FinancialRank
import com.stepalex.finny.domain.model.HistoryChoice
import com.stepalex.finny.domain.model.RawPeriodHistory
import com.stepalex.finny.domain.model.RawScheduledEffect
import com.stepalex.finny.domain.model.ScheduledEffect
import kotlinx.serialization.json.Json

private val jsonSerializer = Json { ignoreUnknownKeys = true }

// Конвертация эффектов
fun ScheduledEffectEntity.toDomain() = RawScheduledEffect(
    targetPeriodIndex = targetPeriodIndex, gold = gold, food = food, mood = mood,
    taskId = taskId,
    answerText = answerText
)

fun RawScheduledEffect.toEntity() = ScheduledEffectEntity(
    targetPeriodIndex = targetPeriodIndex, gold = gold, food = food, mood = mood,
    taskId = taskId,
    answerText = answerText
)

// Конвертация истории
fun PeriodHistoryEntity.toDomain(): RawPeriodHistory {
    val dtoChoices = runCatching {
        jsonSerializer.decodeFromString<List<HistoryChoiceDto>>(choicesJson)
    }.getOrDefault(emptyList())

    return RawPeriodHistory(
        periodIndex = periodIndex,
        workIncome = workIncome,
        spentOnNecessity = spentOnNecessity,
        spentOnOptional = spentOnOptional,
        spentOnAccumulation = spentOnAccumulation,
        lostToScam = lostToScam,
        pendingGoldEffect = pendingGoldEffect,
        finalFood = finalFood,
        finalMood = finalMood,
        rankAwarded = runCatching { FinancialRank.valueOf(rankAwarded) }.getOrDefault(FinancialRank.SPENDER),
        choices = dtoChoices.map { HistoryChoice(it.taskId, it.answerText) }
    )
}

fun RawPeriodHistory.toEntity(): PeriodHistoryEntity {
    val dtoChoices = choices.map { HistoryChoiceDto(it.taskId, it.answerText) }
    return PeriodHistoryEntity(
        periodIndex = periodIndex,
        workIncome = workIncome,
        spentOnNecessity = spentOnNecessity,
        spentOnOptional = spentOnOptional,
        spentOnAccumulation = spentOnAccumulation,
        lostToScam = lostToScam,
        pendingGoldEffect = pendingGoldEffect,
        finalFood = finalFood,
        finalMood = finalMood,
        rankAwarded = rankAwarded.name,
        choicesJson = jsonSerializer.encodeToString(dtoChoices)
    )
}