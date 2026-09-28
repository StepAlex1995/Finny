package com.stepalex.finny.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.stepalex.finny.presentation.create_pet.ConfirmSelectPetDialogAnimatable
import com.stepalex.finny.presentation.create_pet.CreatePetScreenAnimatable
import com.stepalex.finny.presentation.goals.GameDialogAnimatable
import com.stepalex.finny.presentation.goals.GoalScreenAnimatable
import com.stepalex.finny.presentation.period.PeriodResultScreenAnimatable
import com.stepalex.finny.presentation.period.StartPeriodInfoScreenAnimatable
import com.stepalex.finny.presentation.pet_room.PetRoom
import com.stepalex.finny.presentation.pet_room_bgr.PetRoomBgr
import com.stepalex.finny.presentation.pet_room_bgr.PetRoomColor
import com.stepalex.finny.presentation.task.ResultTaskScreenAnimatable
import com.stepalex.finny.presentation.task.TaskQuestionScreenAnimatable

@Composable
fun HomeScreen(event: ((HomeEvent) -> Unit), state: HomeState) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(PetRoomColor().floorColor)) {
        PetRoomBgr(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f))
        PetRoom(modifier = Modifier.fillMaxSize(), event, state)

        TaskQuestionScreenAnimatable(event, state)
        ResultTaskScreenAnimatable(event, state)
        StartPeriodInfoScreenAnimatable(event,state)
        PeriodResultScreenAnimatable(event,state)

        GoalScreenAnimatable(event, state)
        GameDialogAnimatable(event, state)
        CreatePetScreenAnimatable(event, state)
        ConfirmSelectPetDialogAnimatable(event, state)
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    Box(modifier = Modifier.fillMaxSize()) {

        HomeScreen(
            event = object : Function1<HomeEvent, Unit> {
                override fun invoke(p1: HomeEvent) {

                }
            },
            state = HomeState(
                openWindow = OpenWindow.None,
                showDialog = ShowDialog.None,
                profile = null,
                goals = emptyList(),
                selectGoal = null,
                selectedTaskAnswer = null
            ),
        )
    }
}