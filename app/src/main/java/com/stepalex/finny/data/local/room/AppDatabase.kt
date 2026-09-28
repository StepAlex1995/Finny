package com.stepalex.finny.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.stepalex.finny.data.entity.PeriodHistoryEntity
import com.stepalex.finny.data.entity.ScheduledEffectEntity
import com.stepalex.finny.data.entity.TaskEntity

@Database(
    entities = [TaskEntity::class, ScheduledEffectEntity::class, PeriodHistoryEntity::class],
    version = 1,
    exportSchema = false
)

@TypeConverters(RoomTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun periodHistoryDao(): PeriodHistoryDao
    abstract fun scheduledEffectDao(): ScheduledEffectDao
}