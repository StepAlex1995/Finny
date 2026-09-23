package com.stepalex.finny.di

import android.content.Context
import androidx.room.Room
import com.stepalex.finny.data.local.room.AppDatabase
import com.stepalex.finny.data.local.room.TaskDao
import com.stepalex.finny.data.repository.TaskRepositoryImpl
import com.stepalex.finny.data.repository.TaskSettingRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton
import com.stepalex.finny.data.source.TaskAssetDataSource
import com.stepalex.finny.domain.repository.TaskRepository
import com.stepalex.finny.domain.repository.TaskSettingRepository
import com.stepalex.finny.domain.use_cases.GetAllTasksUseCase
import com.stepalex.finny.domain.use_cases.GetLastVersionTaskUseCase
import com.stepalex.finny.domain.use_cases.SyncTasksUseCase

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database.db"
        )
            // .fallbackToDestructiveMigration() // Раскомментировать, если при изменении структуры БД в будущем будем просто очищать её, а не писать миграции
            .build()
    }

    @Provides
    @Singleton
    fun provideTaskDao(database: AppDatabase): TaskDao {
        return database.taskDao()
    }

    @Provides
    @Singleton
    fun provideTaskAssetDataSource(
        @ApplicationContext context: Context,
        json: Json
    ): TaskAssetDataSource {
        return TaskAssetDataSource(context, json)
    }

    @Provides
    @Singleton
    fun provideTaskRepository(
        dataSource: TaskAssetDataSource,
        taskDao: TaskDao,
        json: Json
    ): TaskRepository {
        return TaskRepositoryImpl(dataSource, taskDao, json)
    }

    @Provides
    @Singleton
    fun provideTaskVersionRepository(@ApplicationContext context: Context): TaskSettingRepository {
        return TaskSettingRepositoryImpl(context)
    }


    @Provides
    @Singleton
    fun provideSyncTasksUseCase(
        assetDataSource: TaskAssetDataSource,
        taskRepository: TaskRepository,
        versionRepository: TaskSettingRepository
    ): SyncTasksUseCase {
        return SyncTasksUseCase(assetDataSource, taskRepository, versionRepository)
    }

    @Provides
    @Singleton
    fun provideGetTasksUseCase(repository: TaskRepository): GetAllTasksUseCase {
        return GetAllTasksUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetLastVersionTaskUseCase(
        repository: TaskRepository,
        versionRepository: TaskSettingRepository
    ): GetLastVersionTaskUseCase {
        return GetLastVersionTaskUseCase(repository, versionRepository)
    }
}