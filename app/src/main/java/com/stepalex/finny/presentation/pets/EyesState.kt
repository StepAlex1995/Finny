package com.stepalex.finny.presentation.pets

import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue


@Immutable
data class EyesState(
    val eyeTopInset: Float,     // Насколько верх века опускается (срезает глаз)
    val pupilOffsetX: Float,    // Смещение зрачков по X к носу
    val pupilOffsetY: Float,    // Смещение зрачков по Y вниз
    val browAlpha: Float,       // Прозрачность бровей (0f до 1f)
    val browInnerYOffset: Float // Подъем внутренних уголков бровей ("домик")
)

@Composable
fun rememberBunnyEyesState(moodTransition: Transition<PetMood>): EyesState {
    // 1. На сколько опускается верхнее веко при грусти (срезает глаз сверху)
    val eyeTopInset by moodTransition.animateFloat(
        transitionSpec = { tween(durationMillis = 500) }, label = "EyeTopInset"
    ) { targetMood ->
        if (targetMood == PetMood.Sad) 0.015f else 0f // Плавно прикрываем глаз сверху
    }

    // 2. Смещение зрачков к центру (носу)
    val pupilOffsetX by moodTransition.animateFloat(
        transitionSpec = { tween(durationMillis = 500) }, label = "PupilOffsetX"
    ) { targetMood ->
        if (targetMood == PetMood.Sad) 0.008f else 0f // Левый вправо, правый влево
    }

    // 3. Смещение зрачков вниз
    val pupilOffsetY by moodTransition.animateFloat(
        transitionSpec = { tween(durationMillis = 500) }, label = "PupilOffsetY"
    ) { targetMood ->
        if (targetMood == PetMood.Sad) 0.030f else 0f // Смотрят "под нос"
    }

    // 4. Появление бровей (только в Sad)
    val browAlpha by moodTransition.animateFloat(
        transitionSpec = { tween(durationMillis = 500) }, label = "BrowAlpha"
    ) { targetMood ->
        if (targetMood == PetMood.Sad) 1f else 0f
    }

    // 5. Подъем внутренней части бровей для эффекта "домика"
    val browInnerYOffset by moodTransition.animateFloat(
        transitionSpec = { tween(durationMillis = 500) }, label = "BrowInnerYOffset"
    ) { targetMood ->
        if (targetMood == PetMood.Sad) 0.025f else 0f
    }

    return EyesState(
        eyeTopInset = eyeTopInset,
        pupilOffsetX = pupilOffsetX,
        pupilOffsetY = pupilOffsetY,
        browAlpha = browAlpha,
        browInnerYOffset = browInnerYOffset
    )
}