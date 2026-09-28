package com.stepalex.finny.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stepalex.finny.data.entity.ScheduledEffectEntity


@Dao
interface ScheduledEffectDao {

    /**
     * Сохраняет один отложенный эффект (например, вклад/долг из одной задачи)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEffect(effect: ScheduledEffectEntity)

    /**
     * Сохраняет сразу список отложенных эффектов (если за раунд их накопилось несколько)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEffects(effects: List<ScheduledEffectEntity>)

    /**
     * Достаёт все эффекты, запланированные строго на конкретный период
     */
    @Query("SELECT * FROM scheduled_effects WHERE targetPeriodIndex = :periodIndex")
    suspend fun getEffectsForPeriod(periodIndex: Int): List<ScheduledEffectEntity>

    /**
     * Удаляет из базы эффекты конкретного периода после того, как они применились к профилю
     */
    @Query("DELETE FROM scheduled_effects WHERE targetPeriodIndex = :periodIndex")
    suspend fun deleteEffectsForPeriod(periodIndex: Int)

    /**
     * Очищает всю таблицу (может пригодиться при сбросе игры или начале новой сессии)
     */
    @Query("DELETE FROM scheduled_effects")
    suspend fun clearAllEffects()
}