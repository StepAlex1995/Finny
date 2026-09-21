package com.stepalex.finny.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
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
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stepalex.finny.presentation.items.PetItems
import com.stepalex.finny.presentation.pet_room.Food
import com.stepalex.finny.presentation.pet_room.MoodIndicator
import com.stepalex.finny.presentation.pet_room.MoodSmile
import com.stepalex.finny.presentation.pet_room.PetRoom
import com.stepalex.finny.presentation.pets.Pet
import com.stepalex.finny.presentation.pets.PetAction
import com.stepalex.finny.presentation.pets.PetColor
import com.stepalex.finny.presentation.pets.PetListener
import com.stepalex.finny.presentation.pets.PetMood
import com.stepalex.finny.presentation.pets.PetStage
import com.stepalex.finny.presentation.pets.PetType

@Composable
fun HomeScreen(event: ((HomeEvent) -> Unit)?, state: HomeState) {


    var type by remember { mutableStateOf(PetType.BUNNY) }
    var state by remember { mutableStateOf(PetStage.Baby) }
    var mood by remember { mutableStateOf(PetMood.Normal) }
    var action by remember { mutableStateOf(PetAction.Play) }
    var touchOffset by remember { mutableStateOf<Offset?>(null) }

    var testCount by remember { mutableStateOf(1) }

    var petItems by remember {
        mutableStateOf(
            PetItems(
                topHat = null,
                neckTie = null,
                hairBow = null,
                glass = null,
                crown = null,
                bowTie = null
            )
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            //.background(Color(0xFFF3F4F6))
            //.padding(all = 32.dp)
            .pointerInput(mood) { // Перезапускаем PointerInput при смене настроения
                if (mood == PetMood.Sleep) {
                    // Если кролик спит, жесты полностью игнорируются
                    return@pointerInput
                }
                awaitEachGesture {
                    // 1. Фаза Press: ловим самое первое касание мгновенно
                    val down = awaitFirstDown(requireUnconsumed = false)
                    touchOffset = down.position

                    // 2. Фаза Move/Release: отслеживаем палец, пока он на экране
                    while (true) {
                        val event = awaitPointerEvent()

                        when (event.type) {
                            PointerEventType.Move -> {
                                // Палец движется — обновляем координаты без задержек slop
                                val pointer = event.changes.firstOrNull()
                                if (pointer != null && pointer.pressed) {
                                    touchOffset = pointer.position
                                }
                            }

                            PointerEventType.Release -> {
                                // 3. Фаза Release: палец оторвали — мгновенный сброс
                                touchOffset = null
                                break // Выходим из цикла этого жеста
                            }
                        }
                    }
                }
            }
    ) {
        PetRoom(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            //verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .align(alignment = Alignment.CenterHorizontally)
                    .padding(top = 160.dp)
                    .height(100.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                    // .background(Color.Red)
                ) {
                    for (i in 0..4) {
                        Food(
                            modifier = Modifier
                                .weight(0.2f)
                                .aspectRatio(1f), isFilled = i < 3
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(50.dp)
                    //.background(Color.Black)
                ) {
                    val count = testCount
                    val moodIndicator =
                        if (count < 2) MoodIndicator.SAD else if (count < 4) MoodIndicator.NORMAL else MoodIndicator.HAPPY
                    for (i in 0..4) {
                        MoodSmile(
                            modifier = Modifier
                                .weight(0.2f)
                                .padding(4.dp)
                                .aspectRatio(1f), isFilled = i < count, moodIndicator
                        )
                    }
                }
            }
        }

        Pet(
            modifier = Modifier
                .size(300.dp)
                .align(alignment = Alignment.Center),
                //.padding(bottom = 100.dp),
            stage = state,
            mood = mood,
            action = action,
            touchOffset = touchOffset,
            petColor = PetColor.White,
            petItems = petItems,
            petType = type,
            petListener = object : PetListener {
                override fun updatePetMod(newMode: PetMood) {
                    mood = newMode
                }
            }
        )

        // ИНСТРУКЦИЯ ДЛЯ ТЕСТИРОВАНИЯ
        Column(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = {
                testCount = if ((testCount + 1) % 6 == 0) 1 else (testCount + 1) % 6
                state = when (state) {
                    PetStage.Baby -> PetStage.Teenager
                    PetStage.Teenager -> PetStage.Adult
                    PetStage.Adult -> PetStage.Baby
                }
            }) { Text("Current stage = $state") }
        }
        /*Row(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.weight(0.5f)) {
            Button(onClick = {
                mood = PetMood.Sleep
            }) { Text("SLEEP") }
            Button(onClick = {
                mood = PetMood.Normal
            }) { Text("NORMAL") }
            Button(onClick = {
                mood = PetMood.Sad
            }) { Text("SAD") }
            Button(onClick = {
                mood = PetMood.Happy
            }) { Text("HAPPY") }
        }
        Column(modifier = Modifier.weight(0.5f)) {
            Button(onClick = {
                action = PetAction.Pet
            }) { Text("PET") }
            Button(onClick = {
                action = PetAction.Eat
            }) { Text("EAT") }
            Button(onClick = {
                action = PetAction.Play
            }) { Text("PLAY") }
            Button(onClick = {
                type = if(type == PetType.BUNNY){
                    PetType.BEAR
                }else {
                    PetType.BUNNY
                }
            }) { Text("PLAY") }
        }
    }
}

Column(
    modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.BottomCenter)
) {
    Button(onClick = {
        petItems = petItems.copy(
            topHat = null,
            hairBow = PetItemParam(
                isSparkles = false,
                position = PetItemPosition.TOP,
                petItemColor = PetItemColor.Fuchsia
            )
        )
    }) { Text("Корона TOP Fuchsia") }
    Button(onClick = {
        petItems = petItems.copy(
            topHat = null,
            hairBow = PetItemParam(
                isSparkles = true,
                position = PetItemPosition.LEFT,
                petItemColor = PetItemColor.Orange
            )
        )
    }) { Text("Корона LEFT Fuchsia") }
   /* Button(onClick = {
        petItems = petItems.copy(
            topHat = null,
            hairBow = PetItemParam(
                isSparkles = true,
                position = PetItemPosition.LEFT,
                petItemColor = PetItemColor.Blue
            )
        )
    }) { Text("Корона LEFT Blue") }

    Button(onClick = {
        petItems = petItems.copy(
            crown = null,
            bowTie = PetItemParam(
                isSparkles = true,
                position = PetItemPosition.TOP,
                petItemColor = PetItemColor.Emerald
            )
        )
    }) { Text("Корона TOP Fuchsia") }
    Button(onClick = {
        petItems = petItems.copy(
            crown = null,
            topHat = PetItemParam(
                isSparkles = true,
                position = PetItemPosition.LEFT,
                petItemColor = PetItemColor.Fuchsia
            )
        )
    }) { Text("Корона LEFT Fuchsia") }
    Button(onClick = {
        petItems = petItems.copy(
            crown = null,
            topHat = PetItemParam(
                isSparkles = false,
                position = PetItemPosition.LEFT,
                petItemColor = PetItemColor.Blue
            )
        )
    }) { Text("Корона LEFT Blue") }
    Button(onClick = {
        petItems = petItems.copy(
            crown = null,
            glass = PetItemParam(
                isSparkles = true,
                position = PetItemPosition.RIGHT,
                petItemColor = PetItemColor.Blue
            )
        )
    }) { Text("Корона LEFT Blue") }*/
    Button(onClick = {
        petItems = petItems.copy(
            crown = null,
            topHat = null,
            hairBow = null,
            bowTie = null,
            neckTie = null,
            glass = null
        )
    }) { Text("CLEAT") }
}*/
    }

}

@Preview
@Composable
fun HomeScreenPreview() {
    Box(modifier = Modifier.fillMaxSize()) {

        HomeScreen(
            event = null,
            state = HomeState(),
        )
    }
}