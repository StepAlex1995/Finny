package com.stepalex.finny.data.local.room


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stepalex.finny.data.entity.PeriodHistoryEntity

@Dao
interface PeriodHistoryDao {

    /**
     * Сохраняет слепок пройденного периода в историю.
     * Если период с таким индексом уже был, он перезапишется (защита от багов).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistoryEntry(historyEntry: PeriodHistoryEntity)

    /**
     * Возвращает историю конкретного периода (например, чтобы показать детальный итог раунда)
     */
    @Query("SELECT * FROM period_history WHERE periodIndex = :periodIndex")
    suspend fun getHistoryForPeriod(periodIndex: Int): PeriodHistoryEntity?

    /**
     * Возвращает всю историю игры, отсортированную от первых периодов к последним.
     * Отлично подойдёт для отображения списков, графиков трат или круговых диаграмм.
     */
    @Query("SELECT * FROM period_history ORDER BY periodIndex ASC")
    suspend fun getAllHistory(): List<PeriodHistoryEntity>

    /**
     * Специальный SQL-запрос, который автоматически посчитает суммарный ущерб от мошенников
     * за ВСЮ игру. Полезно для вывода финального экрана достижений!
     */
    @Query("SELECT SUM(lostToScam) FROM period_history")
    suspend fun getTotalLostToScam(): Int?

    /**
     * Очищает всю историю трат при сбросе игрового прогресса
     */
    @Query("DELETE FROM period_history")
    suspend fun clearHistory()
}