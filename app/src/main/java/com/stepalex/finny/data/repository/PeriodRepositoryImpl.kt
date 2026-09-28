package com.stepalex.finny.data.repository

import com.stepalex.finny.data.local.room.PeriodHistoryDao
import com.stepalex.finny.data.local.room.ScheduledEffectDao
import com.stepalex.finny.data.mapper.toDomain
import com.stepalex.finny.data.mapper.toEntity
import com.stepalex.finny.domain.model.RawPeriodHistory
import com.stepalex.finny.domain.model.RawScheduledEffect
import com.stepalex.finny.domain.model.ScheduledEffect
import com.stepalex.finny.domain.repository.PeriodRepository


class PeriodRepositoryImpl(
    private val historyDao: PeriodHistoryDao,
    private val effectDao: ScheduledEffectDao
) : PeriodRepository {

    override suspend fun savePeriodHistory(history: RawPeriodHistory): Result<Unit> = runCatching {
        historyDao.insertHistoryEntry(history.toEntity())
    }

    override suspend fun getRawHistoryForPeriod(periodIndex: Int): Result<RawPeriodHistory?> =
        runCatching {
            historyDao.getHistoryForPeriod(periodIndex)?.toDomain()
        }

    override suspend fun getAllRawHistory(): Result<List<RawPeriodHistory>> = runCatching {
        historyDao.getAllHistory().map { it.toDomain() }
    }

    override suspend fun saveScheduledEffects(effects: List<RawScheduledEffect>): Result<Unit> =
        runCatching {
            effectDao.insertEffects(effects.map { it.toEntity() })
        }

    override suspend fun getEffectsForPeriod(periodIndex: Int): Result<List<RawScheduledEffect>> =
        runCatching {
            effectDao.getEffectsForPeriod(periodIndex).map { it.toDomain() }
        }

    override suspend fun deleteEffectsForPeriod(periodIndex: Int): Result<Unit> = runCatching {
        effectDao.deleteEffectsForPeriod(periodIndex)
    }
}