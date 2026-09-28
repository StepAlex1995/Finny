package com.stepalex.finny.domain.repository

import com.stepalex.finny.domain.model.RawPeriodHistory
import com.stepalex.finny.domain.model.RawScheduledEffect


interface PeriodRepository {
    // Работа с историей периодов
    suspend fun savePeriodHistory(history: RawPeriodHistory): Result<Unit>
    suspend fun getRawHistoryForPeriod(periodIndex: Int): Result<RawPeriodHistory?>
    suspend fun getAllRawHistory(): Result<List<RawPeriodHistory>>

    // Работа с отложенными эффектами (вклады / долги)
    suspend fun saveScheduledEffects(effects: List<RawScheduledEffect>): Result<Unit>
    suspend fun getEffectsForPeriod(periodIndex: Int): Result<List<RawScheduledEffect>>
    suspend fun deleteEffectsForPeriod(periodIndex: Int): Result<Unit>
}