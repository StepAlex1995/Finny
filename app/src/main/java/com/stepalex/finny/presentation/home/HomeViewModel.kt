package com.stepalex.finny.presentation.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.stepalex.finny.nvgraph.HomeUIEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    private val _uiEvent = Channel<HomeUIEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    var homeState by mutableStateOf(HomeState())

    fun onEvent(event: HomeEvent) {

    }
}