package com.stepalex.finny.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.stepalex.finny.data.entity.TaskEntity

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Query("DELETE FROM tasks")
    suspend fun clearTable()

    // Вообще все задачи, которые есть в базе
    @Query("SELECT * FROM tasks")
    suspend fun getAllTasks(): List<TaskEntity>

    // Только задачи конкретной версии
    @Query("SELECT * FROM tasks WHERE version = :version")
    suspend fun getTasksByVersion(version: Int): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTasksById(id: Long): List<TaskEntity>

    @Transaction
    suspend fun refreshTasks(tasks: List<TaskEntity>) {
        clearTable()
        insertTasks(tasks)
    }
}