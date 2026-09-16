package com.stepalex.finny.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.stepalex.finny.presentation.pets.Bunny
import com.stepalex.finny.presentation.pets.PetStage

@Composable
fun HomeScreen(event: (HomeEvent) -> Unit, state: HomeState) {


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 32.dp)
            .background(Color(0xFFF3F4F6))
    ) {
        // ИНСТРУКЦИЯ ДЛЯ ТЕСТИРОВАНИЯ
        Text(
            text = "HomeScreeen",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.DarkGray,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        )
        var state by remember { mutableStateOf(PetStage.Baby) }
        Bunny(
            modifier = Modifier
                .size(300.dp)
                .align(alignment = Alignment.Center),
            stage = state
        )

        Button(onClick = {
            state = when (state) {
                PetStage.Baby -> PetStage.Teenager
                PetStage.Teenager -> PetStage.Adult
                PetStage.Adult -> PetStage.Baby
            }
        }) { Text("Current stage = $state")}

    }
}