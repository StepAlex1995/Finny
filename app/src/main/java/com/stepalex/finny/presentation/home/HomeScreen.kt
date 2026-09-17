package com.stepalex.finny.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import com.stepalex.finny.presentation.pets.Bunny
import com.stepalex.finny.presentation.pets.PetAction
import com.stepalex.finny.presentation.pets.PetListener
import com.stepalex.finny.presentation.pets.PetMood
import com.stepalex.finny.presentation.pets.PetStage

@Composable
fun HomeScreen(event: (HomeEvent) -> Unit, state: HomeState) {


    var state by remember { mutableStateOf(PetStage.Baby) }
    var mood by remember { mutableStateOf(PetMood.Normal) }
    var action by remember { mutableStateOf(PetAction.Play) }
    var touchOffset by remember { mutableStateOf<Offset?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray)
            //.background(Color(0xFFF3F4F6))
            .padding(all = 32.dp)
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
        // ИНСТРУКЦИЯ ДЛЯ ТЕСТИРОВАНИЯ
        Bunny(
            modifier = Modifier
                .size(300.dp)
                .align(alignment = Alignment.Center),
            stage = state,
            mood = mood,
            action = action,
            touchOffset = touchOffset,
            petListener = object : PetListener {
                override fun updatePetMod(newMode: PetMood) {
                    mood = newMode
                }

            }
        )
        Column(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = {
                state = when (state) {
                    PetStage.Baby -> PetStage.Teenager
                    PetStage.Teenager -> PetStage.Adult
                    PetStage.Adult -> PetStage.Baby
                }
            }) { Text("Current stage = $state") }
            Row(modifier = Modifier.fillMaxWidth()) {
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
                }
            }
        }
    }

}