package com.stepalex.finny.presentation.pet_room

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stepalex.finny.domain.model.Goal
import com.stepalex.finny.domain.model.GoalState
import com.stepalex.finny.domain.model.GoalType
import com.stepalex.finny.domain.model.PetColorType
import com.stepalex.finny.domain.model.PetStyle
import com.stepalex.finny.domain.model.Profile
import com.stepalex.finny.presentation.common.CountMoneyOutlineText
import com.stepalex.finny.presentation.common.FinnyProgressBar
import com.stepalex.finny.presentation.common.FinnyProgressStyle
import com.stepalex.finny.presentation.common.FinnyTickStyle
import com.stepalex.finny.presentation.common.items.getPetItems
import com.stepalex.finny.presentation.common.pets.Pet
import com.stepalex.finny.presentation.common.pets.PetAction
import com.stepalex.finny.presentation.common.pets.PetColor
import com.stepalex.finny.presentation.common.pets.PetListener
import com.stepalex.finny.presentation.common.pets.PetMood
import com.stepalex.finny.presentation.common.pets.PetStage
import com.stepalex.finny.presentation.common.pets.PetType
import com.stepalex.finny.presentation.common.pets.Silhouette
import com.stepalex.finny.presentation.common.pets.getPetColorScheme
import com.stepalex.finny.presentation.home.HomeEvent
import com.stepalex.finny.presentation.home.HomeState
import com.stepalex.finny.presentation.home.OpenWindow
import com.stepalex.finny.presentation.home.ShowDialog
import com.stepalex.finny.presentation.common.food.Carrot
import com.stepalex.finny.presentation.pet_room_bgr.Money
import com.stepalex.finny.presentation.pet_room_bgr.MoodIndicator
import com.stepalex.finny.presentation.pet_room_bgr.MoodSmile
import com.stepalex.finny.presentation.pet_room_bgr.PetRoomBgr
import com.stepalex.finny.presentation.pet_room_bgr.Start
import com.stepalex.finny.utils.Fonts

@Composable
fun PetRoom(modifier: Modifier, event: (HomeEvent) -> Unit, state: HomeState) {
    val profile = state.profile
    var action by remember { mutableStateOf(PetAction.Play) }
    var touchOffset by remember { mutableStateOf<Offset?>(null) }

    if (profile != null) {
        Box(
            modifier = modifier
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.55f)
                        .align(alignment = Alignment.CenterHorizontally)
                        .padding(top = 25.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    //Индикатор денег
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                    ) {
                        Money(
                            modifier = Modifier
                                .width(40.dp)
                                .height(40.dp)
                        )
                        CountMoneyOutlineText(
                            text = profile.countMoney.toString(),
                            modifier = Modifier,
                            fontSize = 40.sp,
                            strokeSize = 10f,
                            alignment = Alignment.BottomStart,
                            fontFamily = Fonts.CountMoneyFontFamily
                        )
                    }
                    FinnyProgressBar(
                        progress = ((profile.countMoney.toFloat() / profile.currentGoal.cost.toFloat())),
                        trackStyle = FinnyProgressStyle(
                            color = Color.LightGray, cornerRadius = 4.dp
                        ),
                        progressStyle = FinnyProgressStyle(
                            color = Color(0xFF7000FF), cornerRadius = 4.dp
                        ),
                        segmentsCount = 3,
                        tickStyle = FinnyTickStyle(
                            inactiveColor = Color.LightGray,
                            activeColor = Color(0xFF1A004D),
                            extraHeight = 3.dp
                        ),
                        modifier = Modifier
                    )
                    // Иконки роста питомца
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Silhouette(
                            modifier = Modifier.size(10.dp),
                            petType = profile.petStyle.petType,
                            stage = PetStage.Baby,
                            petColor = PetColor.CyberPurple,
                        )
                        Silhouette(
                            modifier = Modifier.size(10.dp),
                            petType = profile.petStyle.petType,
                            stage = PetStage.Teenager,
                            petColor = if (profile.countMoney.toFloat() / profile.currentGoal.cost > 0.33) {
                                PetColor.CyberPurple
                            } else PetColor.SilverGrey,
                        )
                        Silhouette(
                            modifier = Modifier.size(10.dp),
                            petType = profile.petStyle.petType,
                            stage = PetStage.Adult,
                            petColor = if (profile.countMoney.toFloat() / profile.currentGoal.cost > 0.66) {
                                PetColor.CyberPurple
                            } else PetColor.SilverGrey,
                        )
                        Start(modifier = Modifier.size(10.dp))
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        CountMoneyOutlineText(
                            text = profile.currentGoal.name,//"Цель:",
                            fontFamily = Fonts.RegularTextFontFamily, strokeSize = 5f
                        )
                        CountMoneyOutlineText(
                            text = profile.currentGoal.cost.toString(),
                            fontFamily = Fonts.RegularTextFontFamily,
                            strokeSize = 5f
                        )
                    }
                    //Индикатор еды
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .padding(top = 4.dp)
                    ) {
                        for (i in 0..4) {
                            Carrot(
                                modifier = Modifier
                                    .weight(0.2f)
                                    .aspectRatio(1f),
                                isFilled = i < profile.countFood
                            )
                        }
                    }
                    //Индикатор настроения
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(50.dp)
                    ) {
                        val count = profile.countMood
                        val moodIndicator =
                            if (count < 2) MoodIndicator.SAD else if (count < 4) MoodIndicator.NORMAL else MoodIndicator.HAPPY
                        for (i in 0..4) {
                            MoodSmile(
                                modifier = Modifier
                                    .weight(0.2f)
                                    .padding(4.dp)
                                    .aspectRatio(1f),
                                isFilled = i < count,
                                moodIndicator
                            )
                        }
                    }
                }
            }

            Pet(
                modifier = Modifier
                    .size(300.dp)
                    .align(alignment = Alignment.Center)
                    .padding(bottom = 50.dp),
                stage = when {
                    profile.countMoney.toFloat() / profile.currentGoal.cost < 0.33 -> PetStage.Baby
                    profile.countMoney.toFloat() / profile.currentGoal.cost < 0.66 -> PetStage.Teenager
                    else -> PetStage.Adult
                },
                mood = if (profile.isSleep) PetMood.Sleep else when {
                    profile.countMood < 2 -> PetMood.Sad
                    profile.countMood < 4 -> PetMood.Normal
                    else -> PetMood.Happy
                },
                action = action,
                touchOffset = touchOffset,
                petColor = getPetColorScheme(profile.petStyle.petColor),
                petItems = getPetItems(profile.itemInventory),
                petType = profile.petStyle.petType,
                petListener = object : PetListener {
                    override fun updatePetMod(newMode: PetMood) {
                        // = newMode
                    }
                })
            // НИЖНЯЯ ПАНЕЛЬ
            BottomPetRoomPanel(
                profile = profile,
                periodState = state.periodState,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(alignment = Alignment.BottomCenter),
                onFoodClick = { selectedFood ->
                    // Здесь можно отправить ивент на кормление, например:
                    // event(HomeEvent.FeedPet(selectedFood.typeFood))
                },
                onItemClick = { selectedItem ->
                    // Здесь отправляем ивент на смену одежды:
                    // Нам нужно обновить статус isUsing в списке предметов и послать в профиль
                    val updatedItems = profile.itemInventory.map {
                        if (it.typeItem == selectedItem.typeItem) it.copy(isUsing = !it.isUsing) else it
                    }
                    event(HomeEvent.UpdatePetItems(updatedItems))
                },
                onStartTaskClick = {
                    event(HomeEvent.CompleteStartTask(1))
                },
                onQuizClick = {
                    event(HomeEvent.CompleteQuizTask(1))
                },
                onSkipTimer = { event(HomeEvent.SkipTimer) })
        }
    }
}


@Preview
@Composable
fun PetRoomBgrPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        PetRoomBgr()
        PetRoom(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(1f),
            event = object : Function1<HomeEvent, Unit> {
                override fun invoke(p1: HomeEvent) {

                }
            },
            state = HomeState(
                openWindow = OpenWindow.None, showDialog = ShowDialog.None, profile = Profile(
                    currentGoal = Goal(
                        "Погоня за скидками",
                        "Описание цели",
                        600,
                        GoalType.WONT_AND_NEED,
                        GoalState.IN_PROCESSING
                    ),
                    countMoney = 100,
                    countFood = 3,
                    countMood = 3,
                    isSleep = false,
                    foodInventory = emptyList(),
                    itemInventory = emptyList(),
                    petStyle = PetStyle(
                        petType = PetType.BUNNY, petColor = PetColorType.White
                    )
                ), goals = emptyList(), selectGoal = null
            )
        )
    }
}