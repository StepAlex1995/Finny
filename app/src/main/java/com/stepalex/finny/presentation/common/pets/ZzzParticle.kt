package com.stepalex.finny.presentation.common.pets

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import kotlin.math.sin
import kotlin.random.Random

class ZzzParticle {
    // Compose State для свойств, меняющихся каждый кадр
    var x by mutableFloatStateOf(0f)
    var y by mutableFloatStateOf(0f)
    var alpha by mutableFloatStateOf(0f)
    var angle by mutableFloatStateOf(0f)
    var size by mutableFloatStateOf(0f)
    var text by mutableStateOf("")

    var vy: Float = 0f
    var isAlive: Boolean = false
    var lifeDuration: Long = 0
    var age: Long = 0

    var startX: Float = 0f
    var waveFrequency: Float = 0f
    var wavePhaseOffset: Float = 0f
    var spawnDelay: Long = 0L

    // Метод безопасного рождения буквы у ротика питомца
    fun reset(w: Float, h: Float, index: Int) {
        // Точка старта у ротика с лёгким естественным разбросом по X
        startX = w * 0.50f + (Random.nextFloat() - 0.5f) * (w * 0.02f)
        x = startX
        y = h * 0.55f

        // Спокойный медленный подъем вверх
        vy = -h * (0.00004f + Random.nextFloat() * 0.00002f)

        // Случайный регистр букв
        text = if (Random.nextBoolean()) "Z" else "z"
        size = w * (0.04f + Random.nextFloat() * 0.02f)
        angle = (Random.nextFloat() - 0.5f) * 20f

        // Параметры волнообразного покачивания
        waveFrequency = 0.0012f + Random.nextFloat() * 0.0006f
        wavePhaseOffset = Random.nextFloat() * (2f * Math.PI.toFloat())

        // Равномерный поочередный вылет (0мс, 1000мс, 2000мс)
        if (age == 0L && !isAlive) {
            spawnDelay = index * 1000L
        } else {
            spawnDelay = 3000L // Цикличный уход в конец 3-секундной очереди
        }

        lifeDuration = 2800L
        alpha = 0f
        age = 0
        isAlive = true
    }
}

@Composable
fun rememberPetZzz(mood: PetMood): List<ZzzParticle> {
    // Создаем стабильный список из 3 спящих букв (как мы и планировали для сна)
    val zzzList = remember { List(3) { ZzzParticle() } }

    LaunchedEffect(mood) {
        // Сброс принудительно гасит буквы, если настроение поменялось
        if (mood != PetMood.Sleep) {
            zzzList.forEach { p ->
                p.isAlive = false
                p.alpha = 0f
                p.age = 0L
                p.spawnDelay = 0L
            }
        }

        var lastTime = withFrameMillis { it }

        while (true) {
            withFrameMillis { currentTime ->
                val deltaTime = (currentTime - lastTime).coerceIn(0, 50)
                lastTime = currentTime

                zzzList.forEach { p ->
                    if (p.isAlive) {
                        // Обработка задержки очереди
                        if (p.spawnDelay > 0L && mood == PetMood.Sleep) {
                            p.spawnDelay -= deltaTime
                            p.alpha = 0f
                        } else {
                            p.age += deltaTime
                            val lifeProgress = p.age.toFloat() / p.lifeDuration

                            if (mood == PetMood.Sleep) {
                                p.alpha = when {
                                    lifeProgress < 0.15f -> lifeProgress / 0.15f
                                    lifeProgress > 0.75f -> (1f - lifeProgress) / 0.25f
                                    else -> 1f
                                }
                            } else {
                                // Плавно тушим буквы, если кролик проснулся во время их полета
                                p.alpha -= 0.005f * deltaTime
                                if (p.alpha <= 0f) p.isAlive = false
                            }

                            // Время полета вышло — отправляем на перерождение
                            if (p.age >= p.lifeDuration && mood == PetMood.Sleep) {
                                p.isAlive = false
                            }

                            // Волнообразное движение
                            if (p.isAlive) {
                                p.y += p.vy * deltaTime
                                val amplitude = p.size * 0.7f
                                p.x =
                                    p.startX + sin((p.age * p.waveFrequency) + p.wavePhaseOffset) * amplitude
                                p.size += 0.001f * deltaTime // Буква плавно увеличивается, как пар
                            }
                        }
                    }
                }
            }
        }
    }
    return zzzList
}

/**
 * Отрисовка Z-z-z
 */
fun DrawScope.drawSleepLetter(p: ZzzParticle, bgrColor: Color) {
    drawContext.canvas.nativeCanvas.save()

    // 1. НАСТРОЙКА КРАСКИ ДЛЯ ОБВОДКИ (Черный контур)
    val strokePaint = Paint().apply {
        color = Color.Black.toArgb() // белый цвет контура кролика
        textSize = p.size
        isAntiAlias = true
        typeface = Typeface.create(
            Typeface.DEFAULT,
            Typeface.BOLD
        )
        alpha = (p.alpha * 255).toInt().coerceIn(0, 255)

        // Включаем режим обводки
        style = Paint.Style.STROKE
        // Толщина обводки буквы (делаем чуть тоньше основной обводки кролика, чтобы текст читался)
        strokeWidth = p.size * 0.15f
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    // 2. НАСТРОЙКА КРАСКИ ДЛЯ ЗАЛИВКИ (Белая серединка)
    val fillPaint = Paint().apply {
        color = bgrColor.toArgb() // Черный цвет заливки
        textSize = p.size
        isAntiAlias = true
        typeface = Typeface.create(
            Typeface.DEFAULT,
            Typeface.BOLD
        )
        alpha = (p.alpha * 255).toInt().coerceIn(0, 255)

        // Включаем режим сплошной заливки
        style = Paint.Style.FILL
    }

    // Трансформируем холст (смещение и поворот)
    withTransform({
        translate(left = p.x, top = p.y)
        rotate(degrees = p.angle, pivot = Offset.Zero)
    }) {
        // Вычисляем смещение для центрирования текста по оси X
        val textOffsetX = -p.size / 3f

        // Сначала рисуем толстый черный контур
        drawContext.canvas.nativeCanvas.drawText(p.text, textOffsetX, 0f, strokePaint)
        // Затем поверх него накладываем белую начинку
        drawContext.canvas.nativeCanvas.drawText(p.text, textOffsetX, 0f, fillPaint)
    }

    drawContext.canvas.nativeCanvas.restore()
}