package com.stepalex.finny.presentation.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stepalex.finny.domain.use_cases.SyncTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SplashViewModel @Inject constructor(
    private val syncTasksUseCase: SyncTasksUseCase
) : ViewModel() {

    // Однократное событие для навигации после завершения синхронизации
    private val _navigationEvent = MutableSharedFlow<Unit>()
    val navigationEvent: SharedFlow<Unit> = _navigationEvent.asSharedFlow()

    init {
        checkAndSyncDatabase()
    }

    private fun checkAndSyncDatabase() {
        viewModelScope.launch {
            // Вызываем UseCase: он проверит версию JSON и при необходимости обновит Room
            syncTasksUseCase()
                .onSuccess {
                    // Всё успешно проверено/обновлено
                    Log.d("TEST","Susccess")
                    _navigationEvent.emit(Unit)
                }
                .onFailure { error ->
                    error.printStackTrace()
                    // Даже если произошла ошибка (например, сломался JSON),
                    // мы переключаем в true, чтобы приложение не зависло намертво на сплеше.
                    Log.d("TEST","error = $error")
                    _navigationEvent.emit(Unit)
                }
        }
    }
}