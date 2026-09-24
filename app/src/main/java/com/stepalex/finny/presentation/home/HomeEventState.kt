package com.stepalex.finny.presentation.home

import com.stepalex.finny.domain.model.Goal
import com.stepalex.finny.domain.model.Profile

sealed class HomeEvent {
    data class OpenQuiz(val taskId: Int) : HomeEvent()
    object ShowHomeWindow : HomeEvent()
    data class OnGoalClick(val goal: Goal) : HomeEvent()
    data class SelectGoal(val goal: Goal) : HomeEvent()
    object ClearSelectGoal : HomeEvent()
    object GetProfile : HomeEvent()
}

data class HomeState(
    val openWindow: OpenWindow,
    val showDialog: ShowDialog,//пока можно удалить
    val profile: Profile?,
    val goals: List<Goal>,
    val selectGoal: Goal?
)

enum class OpenWindow {
    None,
    Goals,
    CreatePet
}

enum class ShowDialog {
    None,
    SelectGoal
}