package com.stepalex.finny.presentation.common.pets

import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import kotlin.math.PI
import kotlin.math.sin

// Класс-стейт, который хранит итоговые масштабы дыхания для холста
@Stable
class PetBreathing(
    scaleX: Float,
    scaleY: Float,
    progress: Float
) {
    var scaleX by mutableFloatStateOf(scaleX)
        internal set
    var scaleY by mutableFloatStateOf(scaleY)
        internal set
    var progress by mutableFloatStateOf(progress)
        internal set
}

@Composable
fun rememberPetBreathing(
    transition: Transition<PetStage>,
    mood: PetMood
): PetBreathing {
    // Плавно анимируем целевую скорость дыхания (время одного полуцикла в мс)
    val currentBreedDuration by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600) },
        label = "BreedDuration"
    ) { targetStage ->
        val baseSpeed = when (targetStage) {
            PetStage.Baby -> 800f
            PetStage.Teenager -> 1000f
            PetStage.Adult -> 1200f
        }
        // Корректируем скорость под настроение питомца
        when (mood) {
            PetMood.Sleep -> baseSpeed + 400f
            PetMood.Happy -> baseSpeed - 200f
            PetMood.Sad -> baseSpeed + 100f
            PetMood.Normal -> baseSpeed
        }
    }

    // Храним текущую фазу дыхания (угол от 0 до 2*PI)
    var breathingPhase by remember { mutableFloatStateOf(0f) }

    // Изолированный бесконечный цикл обновления фазы дыхания на каждом кадре
    LaunchedEffect(currentBreedDuration) {
        var lastTime = withFrameMillis { it }
        while (true) {
            withFrameMillis { currentTime ->
                val deltaTime = currentTime - lastTime
                lastTime = currentTime

                val phaseSpeed = (PI / currentBreedDuration).toFloat()
                breathingPhase = (breathingPhase + phaseSpeed * deltaTime) % (2f * PI.toFloat())
            }
        }
    }

    // Рассчитываем прогресс волны «вдох-выдох» (0.0f..1.0f)
    val breathProgress = (sin(breathingPhase) + 1f) / 2f

    // Вычисляем финальные коэффициенты масштаба
    val finalScaleY = 1.0f + (0.02f * breathProgress)
    val finalScaleX = 1.0f + (0.015f * breathProgress)

    // Запоминаем и обновляем объект стейта
    val breathingState = remember { PetBreathing(finalScaleX, finalScaleY,breathProgress) }

    // Синхронизируем Compose State при изменении значений
    breathingState.scaleX = finalScaleX
    breathingState.scaleY = finalScaleY
    breathingState.progress = breathProgress

    return breathingState
}