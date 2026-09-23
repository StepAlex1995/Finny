package com.stepalex.finny.presentation.splash


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SplashScreen(
    onSyncComplete: () -> Unit,
    viewModel: SplashViewModel
) {
    // Слушаем событие завершения синхронизации
    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collectLatest {
            onSyncComplete()
        }
    }

    // Внешний вид вашего сплеш-экрана
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Здесь может быть логотип или просто индикатор загрузки
        CircularProgressIndicator()
    }
}