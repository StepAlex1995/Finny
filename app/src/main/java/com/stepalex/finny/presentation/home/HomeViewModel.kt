package com.stepalex.finny.presentation.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stepalex.finny.domain.model.FoodInventory
import com.stepalex.finny.domain.model.GoalState
import com.stepalex.finny.domain.model.ItemColor
import com.stepalex.finny.domain.model.ItemInventory
import com.stepalex.finny.domain.model.ItemPosition
import com.stepalex.finny.domain.model.PetColorType
import com.stepalex.finny.domain.model.PetStyle
import com.stepalex.finny.domain.model.Profile
import com.stepalex.finny.domain.model.TypeFood
import com.stepalex.finny.domain.model.TypeItem
import com.stepalex.finny.domain.use_cases.profile.GetGoalsUseCase
import com.stepalex.finny.domain.use_cases.profile.GetProfileUseCase
import com.stepalex.finny.domain.use_cases.profile.UpdateProfileUseCase
import com.stepalex.finny.nvgraph.HomeUIEvent
import com.stepalex.finny.presentation.common.pets.PetType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
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
                    homeState.copy(
                        openWindow = OpenWindow.CreatePet,
                        showDialog = ShowDialog.None,
                        profile = Profile(
                            currentGoal = event.goal,
                            countMoney = 100,
                            countFood = 2,
                            countMood = 3,
                            isSleep = false,
                            foodInventory = listOf(
                                FoodInventory(
                                    typeFood = TypeFood.Carrot,
                                    count = 1
                                )
                            ),
                            itemInventory = listOf(
                                ItemInventory(
                                    typeItem = TypeItem.TopHat,
                                    position = ItemPosition.Top,
                                    isUsing = false,
                                    isAvailable = false,
                                    itemColor = ItemColor.Black
                                ),
                                ItemInventory(
                                    typeItem = TypeItem.Crown,
                                    position = ItemPosition.Top,
                                    isUsing = false,
                                    isAvailable = false,
                                    itemColor = ItemColor.Gold
                                ),
                                ItemInventory(
                                    typeItem = TypeItem.HairBow,
                                    position = ItemPosition.Top,
                                    isUsing = false,
                                    isAvailable = false,
                                    itemColor = ItemColor.Blue
                                ),
                                ItemInventory(
                                    typeItem = TypeItem.Glass,
                                    position = ItemPosition.Top,
                                    isUsing = false,
                                    isAvailable = false,
                                    itemColor = ItemColor.Purple
                                ),
                                ItemInventory(
                                    typeItem = TypeItem.NeckTie,
                                    position = ItemPosition.Top,
                                    isUsing = false,
                                    isAvailable = false,
                                    itemColor = ItemColor.Blue
                                ),
                                ItemInventory(
                                    typeItem = TypeItem.BowTie,
                                    position = ItemPosition.Top,
                                    isUsing = false,
                                    isAvailable = false,
                                    itemColor = ItemColor.Red
                                ),
                            ),
                            petStyle = PetStyle(PetType.BEAR, petColor = PetColorType.TeddyBear)
                        )
                    )
            }

            is HomeEvent.UpdatePetStyle -> {//обновить стиль питомца
                homeState.profile?.let { profile ->
                    val updatedProfile = profile.copy(petStyle = event.petStyle)
                    homeState = homeState.copy(profile = updatedProfile)
                }
            }

            is HomeEvent.UpdatePetItems -> {//обновить кол-во предметов (выбор, что надето)
                homeState.profile?.let { profile ->
                    val updatedProfile = profile.copy(itemInventory = event.petItems)
                    homeState = homeState.copy(profile = updatedProfile)
                }
            }

            HomeEvent.SelectPet -> {
                homeState = homeState.copy(showDialog = ShowDialog.SelectPet)
            }
            HomeEvent.HideDialogSelectPet -> {
                homeState = homeState.copy(showDialog = ShowDialog.None)
            }

            HomeEvent.SaveProfile -> {
                if (homeState.profile != null) {
                    viewModelScope.launch {
                        updateProfileUseCase(homeState.profile!!)
                        homeState = homeState.copy(openWindow = OpenWindow.None, showDialog = ShowDialog.None)
                    }
                }
            }

        }
    }
}