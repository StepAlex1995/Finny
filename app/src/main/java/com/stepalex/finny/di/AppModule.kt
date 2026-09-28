package com.stepalex.finny.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.stepalex.finny.data.local.room.AppDatabase
import com.stepalex.finny.data.local.room.PeriodHistoryDao
import com.stepalex.finny.data.local.room.ScheduledEffectDao
import com.stepalex.finny.data.local.room.TaskDao
import com.stepalex.finny.data.repository.PeriodRepositoryImpl
import com.stepalex.finny.data.repository.ProfileRepositoryImpl
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
import com.stepalex.finny.domain.repository.PeriodRepository
import com.stepalex.finny.domain.repository.ProfileRepository
import com.stepalex.finny.domain.repository.TaskRepository
import com.stepalex.finny.domain.repository.TaskSettingRepository
import com.stepalex.finny.domain.use_cases.CheckStartTaskCompletedUseCase
import com.stepalex.finny.domain.use_cases.GetAllTasksUseCase
import com.stepalex.finny.domain.use_cases.GetCompletedTasksCountUseCase
import com.stepalex.finny.domain.use_cases.GetLastVersionTaskUseCase
import com.stepalex.finny.domain.use_cases.GetPeriodStartTimeUseCase
import com.stepalex.finny.domain.use_cases.SaveCompletedTaskCountUseCase
import com.stepalex.finny.domain.use_cases.SavePeriodStartTimeUseCase
import com.stepalex.finny.domain.use_cases.SaveStartTaskCompletedUseCase
import com.stepalex.finny.domain.use_cases.SyncTasksUseCase
import com.stepalex.finny.domain.use_cases.period.GetCurrentTaskUseCase
import com.stepalex.finny.domain.use_cases.period.GetDetailedPeriodHistoryUseCase
import com.stepalex.finny.domain.use_cases.period.GetHistoryByPeriodIdUseCase
import com.stepalex.finny.domain.use_cases.period.StartNewPeriodUseCase
import com.stepalex.finny.domain.use_cases.period.SubmitTaskAnswerUseCase
import com.stepalex.finny.domain.use_cases.profile.CheckGoalsUseCase
import com.stepalex.finny.domain.use_cases.profile.GetGoalsUseCase
import com.stepalex.finny.domain.use_cases.profile.GetProfileUseCase
import com.stepalex.finny.domain.use_cases.profile.UpdateProfileUseCase

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
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("app_shared_prefs", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context, AppDatabase::class.java, "app_database.db"
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
    fun providePeriodHistoryDao(database: AppDatabase): PeriodHistoryDao {
        return database.periodHistoryDao()
    }

    @Provides
    @Singleton
    fun provideScheduledEffectDao(database: AppDatabase): ScheduledEffectDao {
        return database.scheduledEffectDao()
    }

    @Provides
    @Singleton
    fun provideTaskAssetDataSource(
        @ApplicationContext context: Context, json: Json
    ): TaskAssetDataSource {
        return TaskAssetDataSource(context, json)
    }

    @Provides
    @Singleton
    fun provideTaskRepository(
        dataSource: TaskAssetDataSource, taskDao: TaskDao, json: Json
    ): TaskRepository {
        return TaskRepositoryImpl(dataSource, taskDao, json)
    }

    @Provides
    @Singleton
    fun provideTaskVersionRepository(sharedPreferences: SharedPreferences): TaskSettingRepository {
        return TaskSettingRepositoryImpl(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(
        json: Json, sharedPreferences: SharedPreferences
    ): ProfileRepository {
        return ProfileRepositoryImpl(json, sharedPreferences)
    }

    @Provides
    @Singleton
    fun providePeriodRepository(
        historyDao: PeriodHistoryDao,
        effectDao: ScheduledEffectDao
    ): PeriodRepository {
        return PeriodRepositoryImpl(historyDao, effectDao)
    }

    // --- USE CASES ---
    @Provides
    @Singleton
    fun provideSyncTasksUseCase(
        assetDataSource: TaskAssetDataSource,
        taskRepository: TaskRepository,
        versionRepository: TaskSettingRepository,
        checkGoalsUseCase: CheckGoalsUseCase,
    ): SyncTasksUseCase {
        return SyncTasksUseCase(
            assetDataSource, taskRepository, versionRepository, checkGoalsUseCase
        )
    }

    @Provides
    @Singleton
    fun provideGetTasksUseCase(repository: TaskRepository): GetAllTasksUseCase {
        return GetAllTasksUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetLastVersionTaskUseCase(
        repository: TaskRepository, versionRepository: TaskSettingRepository
    ): GetLastVersionTaskUseCase {
        return GetLastVersionTaskUseCase(repository, versionRepository)
    }

    @Provides
    @Singleton
    fun provideGetProfileUseCase(profileRepository: ProfileRepository): GetProfileUseCase {
        return GetProfileUseCase(profileRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateProfileUseCase(profileRepository: ProfileRepository): UpdateProfileUseCase {
        return UpdateProfileUseCase(profileRepository)
    }

    @Provides
    @Singleton
    fun provideCheckGoalsUseCase(profileRepository: ProfileRepository): CheckGoalsUseCase {
        return CheckGoalsUseCase(profileRepository)
    }

    @Provides
    @Singleton
    fun provideGetGoalsUseCase(profileRepository: ProfileRepository): GetGoalsUseCase {
        return GetGoalsUseCase(profileRepository)
    }

    @Provides
    @Singleton
    fun provideGetPeriodStartTimeUseCase(taskSettingRepository: TaskSettingRepository): GetPeriodStartTimeUseCase {
        return GetPeriodStartTimeUseCase(taskSettingRepository)
    }

    @Provides
    @Singleton
    fun provideSavePeriodStartTimeUseCase(taskSettingRepository: TaskSettingRepository): SavePeriodStartTimeUseCase {
        return SavePeriodStartTimeUseCase(taskSettingRepository)
    }

    @Provides
    @Singleton
    fun provideGetCompleteTaskCountUseCase(taskSettingRepository: TaskSettingRepository): GetCompletedTasksCountUseCase {
        return GetCompletedTasksCountUseCase(taskSettingRepository)
    }

    @Provides
    @Singleton
    fun provideSaveCompleteTaskCountUseCase(taskSettingRepository: TaskSettingRepository): SaveCompletedTaskCountUseCase {
        return SaveCompletedTaskCountUseCase(taskSettingRepository)
    }

    @Provides
    @Singleton
    fun provideCheckStartTaskCompletedUseCase(taskSettingRepository: TaskSettingRepository): CheckStartTaskCompletedUseCase {
        return CheckStartTaskCompletedUseCase(taskSettingRepository)
    }

    @Provides
    @Singleton
    fun provideSaveStartTaskCompletedUseCase(taskSettingRepository: TaskSettingRepository): SaveStartTaskCompletedUseCase {
        return SaveStartTaskCompletedUseCase(taskSettingRepository)
    }

    @Provides
    @Singleton
    fun provideGetDetailedPeriodHistoryUseCase(
        periodRepository: PeriodRepository,
        taskRepository: TaskRepository
    ): GetDetailedPeriodHistoryUseCase {
        return GetDetailedPeriodHistoryUseCase(
            periodRepository = periodRepository,
            taskRepository = taskRepository
        )
    }

    @Provides
    @Singleton
    fun provideStartNewPeriodUseCase(
        profileRepository: ProfileRepository,
        periodRepository: PeriodRepository,
        taskRepository: TaskRepository
    ): StartNewPeriodUseCase {
        return StartNewPeriodUseCase(
            profileRepository = profileRepository,
            periodRepository = periodRepository,
            taskRepository = taskRepository
        )
    }

    @Provides
    @Singleton
    fun provideSubmitTaskAnswerUseCase(
        profileRepository: ProfileRepository,
        periodRepository: PeriodRepository,
        taskRepository: TaskRepository
    ): SubmitTaskAnswerUseCase {
        return SubmitTaskAnswerUseCase(
            profileRepository = profileRepository,
            periodRepository = periodRepository,
            taskRepository = taskRepository
        )
    }

    @Provides
    @Singleton
    fun provideGetCurrentTaskUseCase(
        profileRepository: ProfileRepository,
        taskRepository: TaskRepository,
    ): GetCurrentTaskUseCase {
        return GetCurrentTaskUseCase(
            profileRepository = profileRepository,
            taskRepository = taskRepository,
        )
    }

    @Provides
    @Singleton
    fun provideGetHistoryByPeriodIdUseCase(
        periodRepository: PeriodRepository,
        taskRepository: TaskRepository,
    ): GetHistoryByPeriodIdUseCase {
        return GetHistoryByPeriodIdUseCase(
            periodRepository = periodRepository,
            taskRepository = taskRepository,
        )
    }
    
}