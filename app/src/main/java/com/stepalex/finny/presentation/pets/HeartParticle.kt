package com.stepalex.finny.presentation.pets

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import kotlin.math.sin
import kotlin.random.Random
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb

class HeartParticle {
    // Compose State для свойств, меняющихся каждый кадр
    var x by mutableFloatStateOf(0f)
    var y by mutableFloatStateOf(0f)
    var alpha by mutableFloatStateOf(0f)
    var angle by mutableFloatStateOf(0f)
    var size by mutableFloatStateOf(0f)

    var vy: Float = 0f
    var isAlive: Boolean = false
    var lifeDuration: Long = 0
    var age: Long = 0

    var startX: Float = 0f
    var waveFrequency: Float = 0f
    var wavePhaseOffset: Float = 0f
    var spawnDelay: Long = 0L

    // Метод безопасного рождения сердца прямо под пальцем игрока
    fun reset(w: Float, h: Float, index: Int, localX: Float, localY: Float) {
        // Рождаются у пальца с легким естественным разбросом по X
        startX = localX + (Random.nextFloat() - 0.5f) * (w * 0.02f)
        x = startX
        y = localY

        // Ленивый мультяшный подъем вверх (скорость как у Z-z-z)
        vy = -h * (0.00004f + Random.nextFloat() * 0.00002f)

        // Случайный размер для живости фонтана
        size = w * (0.04f + Random.nextFloat() * 0.02f)
        angle = (Random.nextFloat() - 0.5f) * 15f

        // Мягкое волнообразное покачивание
        waveFrequency = 0.0015f
        wavePhaseOffset = Random.nextFloat() * (2f * Math.PI.toFloat())

        // Равномерная бесконечная цепочка вылета (3 частицы вылетают друг за другом)
        if (age == 0L && !isAlive) {
            spawnDelay = index * 1000L // Первая волна выстраивается в очередь
        } else {
            spawnDelay = 3000L // Повторные круги уходят в конец 3-секундной очереди
        }

        lifeDuration = 2800L
        alpha = 0f
        age = 0
        isAlive = true
    }
}


@Composable
fun rememberPetHearts(isEnjoyingPet: Boolean): List<HeartParticle> {
    // Выделяем память ровно под 3 сердца для красивой равномерной змейки
    val hearts = remember { List(3) { HeartParticle() } }

    LaunchedEffect(isEnjoyingPet) {
        // Если поглаживание прекратилось, принудительно тушить новые спавны не нужно,
        // корутина сама плавно растворит летящие сердца в блоке else ниже.
        // Но принудительно обнуляем возраст, если зажмуривание полностью выключилось:
        if (!isEnjoyingPet) {
            hearts.forEach { p ->
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

                hearts.forEach { p ->
                    if (p.isAlive) {
                        // Задержка старта очереди тикает, пока мы гладим кролика
                        if (p.spawnDelay > 0L && isEnjoyingPet) {
                            p.spawnDelay -= deltaTime
                            p.alpha = 0f
                        } else {
                            p.age += deltaTime
                            val lifeProgress = p.age.toFloat() / p.lifeDuration

                            if (isEnjoyingPet) {
                                p.alpha = when {
                                    lifeProgress < 0.15f -> lifeProgress / 0.15f
                                    lifeProgress > 0.75f -> (1f - lifeProgress) / 0.25f
                                    else -> 1f
                                }
                            } else {
                                // Палец убрали — сердца плавно тают в воздухе во время полета
                                p.alpha -= 0.005f * deltaTime
                                if (p.alpha <= 0f) p.isAlive = false
                            }

                            // Время жизни вышло — отправляем на новый круг
                            if (p.age >= p.lifeDuration && isEnjoyingPet) {
                                p.isAlive = false
                            }

                            // Волнообразная физика подъема
                            if (p.isAlive) {
                                p.y += p.vy * deltaTime
                                val amplitude = p.size * 0.7f
                                p.x =
                                    p.startX + sin((p.age * p.waveFrequency) + p.wavePhaseOffset) * amplitude
                                p.size += 0.001f * deltaTime // Сердце плавно расширяется
                            }
                        }
                    }
                }
            }
        }
    }
    return hearts
}

/**
 * Отрисовка Сердечек
 */
fun DrawScope.drawContourHeart(p: HeartParticle) {
    drawContext.canvas.nativeCanvas.save()

    val r = p.size / 2f

    // Строим идеальное симметричное сердечко с помощью кубических кривых Безье
    val heartPath = Path().apply {
        moveTo(0f, -r * 0.35f) // Стартуем из верхней центральной впадины
        // Левое ушко сердца
        cubicTo(-r * 0.5f, -r * 1.1f, -r * 1.2f, -r * 0.6f, -r * 0.5f, r * 0.1f)
        // Левый нижний сход к острию
        cubicTo(-r * 0.2f, r * 0.5f, -r * 0.1f, r * 0.8f, 0f, r)
        // Правый нижний сход от острия
        cubicTo(r * 0.1f, r * 0.8f, r * 0.2f, r * 0.5f, r * 0.5f, r * 0.1f)
        // Правое ушко сердца
        cubicTo(r * 1.2f, -r * 0.6f, r * 0.5f, -r * 1.1f, 0f, -r * 0.35f)
        close()
    }

    // 1. Краска для черного контура
    val strokePaint = android.graphics.Paint().apply {
        color = Color.Black.toArgb() // Черный цвет обводки кролика
        isAntiAlias = true
        style = android.graphics.Paint.Style.STROKE
        strokeWidth = p.size * 0.14f // Контур, пропорциональный размеру сердца
        strokeJoin = android.graphics.Paint.Join.ROUND
        strokeCap = android.graphics.Paint.Cap.ROUND
        alpha = (p.alpha * 255).toInt().coerceIn(0, 255)
    }

    // 2. Краска для яркой алой начинки
    val fillPaint = android.graphics.Paint().apply {
        color = android.graphics.Color.parseColor("#FFFC2D55") // Яркий алый цвет
        isAntiAlias = true
        style = android.graphics.Paint.Style.FILL
        alpha = (p.alpha * 255).toInt().coerceIn(0, 255)
    }

    // Перемещаем и покачиваем холст на основе физики частицы
    withTransform({
        translate(left = p.x, top = p.y)
        rotate(degrees = p.angle, pivot = Offset.Zero)
    }) {
        // Сначала рисуем черный контур подложки, затем алую заливку поверх
        drawContext.canvas.nativeCanvas.drawPath(heartPath.asAndroidPath(), strokePaint)
        drawContext.canvas.nativeCanvas.drawPath(heartPath.asAndroidPath(), fillPaint)
    }

    drawContext.canvas.nativeCanvas.restore()
}