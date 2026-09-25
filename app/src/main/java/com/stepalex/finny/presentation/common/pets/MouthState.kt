package com.stepalex.finny.presentation.common.pets

import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue

@Immutable
data class MouthState(
    val centerY: Float,
    val widthOffset: Float,
    val cornerY: Float,
    val controlY: Float
)

@Composable
fun rememberBunnyMouthState(moodTransition: Transition<PetMood>): MouthState {
    // 1. Позиция центра рта под носом
    val mouthCenterY by moodTransition.animateFloat(
        transitionSpec = { tween(durationMillis = 500) }, label = "MouthCenterY"
    ) { targetMood ->
        if (targetMood == PetMood.Sad) 0.68f else if (targetMood == PetMood.Happy) 0.66f else 0.68f
    }

    // 2. Позиция уголков рта по горизонтали (X)
    val mouthWidthOffset by moodTransition.animateFloat(
        transitionSpec = { tween(durationMillis = 500) }, label = "MouthWidthOffset"
    ) { targetMood ->
        when (targetMood) {
            PetMood.Happy -> 0.065f
            PetMood.Sleep -> 0.005f
            PetMood.Sad, PetMood.Normal -> 0.045f
        }
    }

    // 3. Анимируем смещение уголков рта относительно центра (Delta Y)
    val mouthCornerDeltaY by moodTransition.animateFloat(
        transitionSpec = { tween(durationMillis = 500) }, label = "MouthCornerDeltaY"
    ) { targetMood ->
        when (targetMood) {
            PetMood.Happy -> 0.001f
            PetMood.Sad -> 0.04f
            PetMood.Normal, PetMood.Sleep -> 0.0f
        }
    }

    // 4. Анимируем смещение контрольных точек относительно центра (Delta Control Y)
    val mouthControlDeltaY by moodTransition.animateFloat(
        transitionSpec = { tween(durationMillis = 500) }, label = "MouthControlDeltaY"
    ) { targetMood ->
        when (targetMood) {
            PetMood.Happy -> 0.04f
            PetMood.Sad -> -0.01f
            PetMood.Normal -> 0.02f
            PetMood.Sleep -> 0.04f
        }
    }

    // Возвращаем собранный объект с абсолютными координатами
    return MouthState(
        centerY = mouthCenterY,
        widthOffset = mouthWidthOffset,
        cornerY = mouthCenterY + mouthCornerDeltaY,
        controlY = mouthCenterY + mouthControlDeltaY
    )
}