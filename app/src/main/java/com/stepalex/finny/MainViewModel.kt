package com.stepalex.finny

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.stepalex.finny.nvgraph.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    var startDestination by mutableStateOf("")
        private set

    init {
        startDestination = Route.HomeNavigation.route
    }
}