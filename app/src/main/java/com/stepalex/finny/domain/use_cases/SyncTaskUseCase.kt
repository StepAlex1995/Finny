package com.stepalex.finny.domain.use_cases

import com.stepalex.finny.data.source.TaskAssetDataSource
import com.stepalex.finny.domain.repository.TaskRepository
import com.stepalex.finny.domain.repository.TaskSettingRepository

class SyncTasksUseCase(
    private val assetDataSource: TaskAssetDataSource,
    private val taskRepository: TaskRepository,
    private val versionRepository: TaskSettingRepository
) {
    suspend operator fun invoke(): Result<Unit> = runCatching {
        // 1. Читаем контейнер из Assets
        val container = assetDataSource.loadTaskContainer()

        // 2. Получаем сохраненную версию из хранилища
        val currentSavedVersion = versionRepository.getLastSavedVersion()
        // 3. Проверяем условие: если версия в файле новее
        if (container.version > currentSavedVersion) {
            // Сохраняем новые задачи в Room
            taskRepository.saveTasks(container.tasks, version = container.version).getOrThrow()

            // Обновляем версию в настройках
            versionRepository.saveLastVersion(container.version)
        }
    }
}