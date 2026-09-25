package com.stepalex.finny.presentation.common.pets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.random.Random


// Класс, описывающий одну летающую звёздочку

class StarParticle {
    // Используем Compose State для свойств, которые меняются каждый кадр
    var x by mutableFloatStateOf(0f)
    var y by mutableFloatStateOf(0f)
    var alpha by mutableFloatStateOf(0f)
    var angle by mutableFloatStateOf(0f)

    var vx: Float = 0f
    var vy: Float = 0f
    var size: Float = 0f
    var rotationSpeed: Float = 0f
    var isAlive: Boolean = false
    var lifeDuration: Long = 0
    var age: Long = 0

    // Метод для безопасного рождения звезды, когда размеры холста уже известны
    fun reset(w: Float, h: Float) {
        x = Random.nextFloat() * w * 0.9f + w * 0.05f
        y = Random.nextFloat() * h * 0.5f
        vx = (Random.nextFloat() - 0.5f) * 0.05f
        vy = (Random.nextFloat() - 0.5f) * 0.05f
        size = w * (0.03f + Random.nextFloat() * 0.03f)
        alpha = 0f
        angle = Random.nextFloat() * 360f
        rotationSpeed = (Random.nextFloat() - 0.5f) * 0.1f
        lifeDuration = 2000 + Random.nextLong(1500)
        age = 0
        isAlive = true
    }
}

@Composable
fun rememberBunnyStars(mood: PetMood): List<StarParticle> {
    val stars = remember { List(10) { StarParticle() } }

    // Эффект перезапускается ТОЛЬКО при реальной смене настроения
    LaunchedEffect(mood) {
        var lastTime = withFrameMillis { it }

        while (true) {
            withFrameMillis { currentTime ->
                val deltaTime = (currentTime - lastTime).coerceIn(0, 50)
                lastTime = currentTime

                stars.forEach { star ->
                    // В движке мы пока просто двигаем живые звезды
                    if (star.isAlive) {
                        star.age += deltaTime

                        val lifeProgress = star.age.toFloat() / star.lifeDuration

                        if (mood == PetMood.Happy) {
                            star.alpha = when {
                                lifeProgress < 0.2f -> lifeProgress / 0.2f
                                lifeProgress > 0.8f -> (1f - lifeProgress) / 0.2f
                                else -> 1f
                            }
                        } else {
                            // Если ушли из Happy — плавно гасим
                            star.alpha -= 0.005f * deltaTime
                            if (star.alpha <= 0f) star.isAlive = false
                        }

                        // Если время жизни вышло в режиме Happy — помечаем как мертвую для перерождения
                        if (star.age >= star.lifeDuration && mood == PetMood.Happy) {
                            star.isAlive = false
                        }

                        // Двигаем
                        star.x += star.vx * deltaTime
                        star.y += star.vy * deltaTime
                        star.angle += star.rotationSpeed * deltaTime
                    }
                }
            }
        }
    }
    return stars
}

/**
 * Отрисовка звездочек
 */
fun DrawScope.drawGoldenStar(star: StarParticle) {
    val r = star.size / 2f
    val goldColor = Color(0xFFFFD700) // Настоящий золотой цвет (Gold)

    val starPath = Path().apply {
        // Стартуем с верхней вершины звезды
        moveTo(0f, -r)
        // Правый внутренний изгиб Безье к центру и переход на правую вершину
        quadraticTo(0f, 0f, r, 0f)
        // Нижний изгиб к нижней вершине
        quadraticTo(0f, 0f, 0f, r)
        // Левый изгиб к левой вершине
        quadraticTo(0f, 0f, -r, 0f)
        // Замыкаем изгиб обратно к верхней вершине
        quadraticTo(0f, 0f, 0f, -r)
    }

    // Рисуем звезду с индивидуальным смещением, поворотом и прозрачностью
    withTransform({
        translate(left = star.x, top = star.y)
        rotate(degrees = star.angle, pivot = Offset.Zero)
    }) {
        drawPath(path = starPath, color = goldColor, alpha = star.alpha)
    }
}