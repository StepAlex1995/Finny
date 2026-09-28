package com.stepalex.finny.domain.use_cases

import com.stepalex.finny.data.source.TaskAssetDataSource
import com.stepalex.finny.domain.repository.TaskRepository
import com.stepalex.finny.domain.repository.TaskSettingRepository
import com.stepalex.finny.domain.use_cases.profile.CheckGoalsUseCase
import com.stepalex.finny.domain.use_cases.profile.GetProfileUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

class SyncTasksUseCase(
    private val assetDataSource: TaskAssetDataSource,
    private val taskRepository: TaskRepository,
    private val versionRepository: TaskSettingRepository,
    private val checkGoalsUseCase: CheckGoalsUseCase,
) {
   /* suspend operator fun invoke(): Result<Unit> = coroutineScope {
        runCatching {
            val syncTaskDeferred = async {
                // 1. Читаем контейнер из Assets
                val container = assetDataSource.loadTaskContainer()

                // 2. Получаем сохраненную версию из хранилища
                val currentSavedVersion = versionRepository.getLastSavedVersion()
                // 3. Проверяем условие: если версия в файле новее
                if (container.version > currentSavedVersion) {
                    // Сохраняем новые задачи в Room
                    taskRepository.saveTasks(container.tasks, version = container.version)
                        .getOrThrow()

                    // Обновляем версию в настройках
                    versionRepository.saveLastVersion(container.version)
                }
            }

            val checkGoalsDeferred = async {
                checkGoalsUseCase()
            }

            syncTaskDeferred.await()
            checkGoalsDeferred.await()
        }

    }*/
   // Переключаем весь контекст на Dispatchers.IO для безопасной работы с диском
   suspend operator fun invoke(): Result<Unit> = withContext(Dispatchers.IO) {
       runCatching {
           // 1. Сначала спокойно проверяем и создаем цели (пишем в SharedPreferences/Room)
           checkGoalsUseCase()

           // 2. Затем читаем контейнер из Assets
           val container = assetDataSource.loadTaskContainer()

           // 3. Получаем сохраненную версию из хранилища
           val currentSavedVersion = versionRepository.getLastSavedVersion()

           // 4. Если версия в файле новее — обновляем таски
           if (container.version > currentSavedVersion) {
               // Сохраняем новые задачи в Room (блокирующая suspend операция)
               taskRepository.saveTasks(container.tasks, version = container.version)
                   .getOrThrow()

               // Только после успешного сохранения тасок обновляем версию в настройках
               versionRepository.saveLastVersion(container.version)
           }
       }
   }
}