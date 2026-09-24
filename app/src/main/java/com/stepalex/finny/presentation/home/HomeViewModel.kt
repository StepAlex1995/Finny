package com.stepalex.finny.presentation.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stepalex.finny.domain.model.GoalState
import com.stepalex.finny.domain.model.GoalType
import com.stepalex.finny.domain.model.Profile
import com.stepalex.finny.domain.use_cases.profile.GetGoalsUseCase
import com.stepalex.finny.domain.use_cases.profile.GetProfileUseCase
import com.stepalex.finny.nvgraph.HomeUIEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val getGoalsUseCase: GetGoalsUseCase

) : ViewModel() {
    private val _uiEvent = Channel<HomeUIEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    var homeState by mutableStateOf(
        HomeState(
            openWindow = OpenWindow.None,
            showDialog = ShowDialog.None,
            profile = null,
            goals = emptyList(),
            selectGoal = null
        )
    )

    init {
        viewModelScope.launch {
            val goals = getGoalsUseCase()
            val profile = getProfileUseCase()
            if (profile == null) {
                homeState = homeState.copy(openWindow = OpenWindow.Goals, goals = goals)
            } else {
                homeState = homeState.copy(goals = goals, profile = profile)
            }

        }
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.OpenQuiz -> {
                viewModelScope.launch {
                    _uiEvent.send(HomeUIEvent.OpenQuiz)
                }
            }

            is HomeEvent.GetProfile -> {
                viewModelScope.launch {
                    val profile = getProfileUseCase()
                    Log.i("TEST", "profile = $profile")
                    val goals = getGoalsUseCase()
                    Log.i("TEST", "goals = $goals")
                }
            }

            is HomeEvent.ShowHomeWindow -> {
                homeState = homeState.copy(openWindow = OpenWindow.None)
            }

            is HomeEvent.OnGoalClick -> {
                if (event.goal.status == GoalState.NOT_AVAILABLE) {
                    homeState =
                        homeState.copy(selectGoal = event.goal, showDialog = ShowDialog.SelectGoal)
                    //homeState = homeState.copy(showDialog = ShowDialog.SelectGoal)
                } else {
                    //todo как быть если цель не недоступна и/или родительская цель
                }

            }

            HomeEvent.ClearSelectGoal -> {//отмена выбора цели через диалог
                homeState = homeState.copy(showDialog = ShowDialog.None)
                //homeState = homeState.copy(selectGoal = null)
            }

            is HomeEvent.SelectGoal -> {//Выбор цели через диалог
                homeState =
                    homeState.copy(openWindow = OpenWindow.CreatePet, showDialog = ShowDialog.None)
            }
        }
    }
}