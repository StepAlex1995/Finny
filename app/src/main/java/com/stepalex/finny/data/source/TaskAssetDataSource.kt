package com.stepalex.finny.data.source

import android.content.Context
import com.stepalex.finny.data.dto.TaskContainerDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class TaskAssetDataSource(private val context: Context, private val json: Json) {
    suspend fun loadTaskContainer(): TaskContainerDto = withContext(Dispatchers.IO) {
        val jsonString = context.assets.open("tasks.json").bufferedReader().use { it.readText() }
        json.decodeFromString<TaskContainerDto>(jsonString)
    }
}