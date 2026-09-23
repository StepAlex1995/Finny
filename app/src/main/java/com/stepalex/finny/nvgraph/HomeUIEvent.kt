package com.stepalex.finny.nvgraph

sealed interface HomeUIEvent {
    data object OpenQuiz : HomeUIEvent
}