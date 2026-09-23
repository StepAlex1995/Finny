package com.stepalex.finny.presentation.home

sealed class HomeEvent {
    data class OpenQuiz(val taskId: Int) : HomeEvent()
}

data class HomeState(
    val id: Int = 1
)