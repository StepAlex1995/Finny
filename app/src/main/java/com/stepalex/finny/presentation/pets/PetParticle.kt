package com.stepalex.finny.presentation.pets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import kotlin.math.sin
import kotlin.random.Random


class PetParticle {
    var x by mutableFloatStateOf(0f)
    var y by mutableFloatStateOf(0f)
    var alpha by mutableFloatStateOf(0f)
    var angle by mutableFloatStateOf(0f)
    var size by mutableFloatStateOf(0f)
    var text by mutableStateOf("") // Текст для отображения (пусто для звезд, "Z" для сна)

    var vx: Float = 0f
    var vy: Float = 0f
    var rotationSpeed: Float = 0f
    var isAlive: Boolean = false
    var lifeDuration: Long = 0
    var age: Long = 0

    // Начальная координата X для расчета синусоидальной волны
    var startX: Float = 0f

    // Уникальная частота покачивания волны
    var waveFrequency: Float = 0f

    // Индивидуальная задержка перед стартом (в миллисекундах)
    var spawnDelay: Long = 0L
    // Индивидуальный сдвиг фазы синуса, чтобы буквы летели по разным траекториям
    var wavePhaseOffset: Float = 0f

    // Сброс параметров под конкретный тип частицы
    fun reset(w: Float, h: Float, index: Int = 0,stage: PetStage, mood: PetMood) {
        if (mood == PetMood.Happy) {
            // размер окна в зависимости от размера персонажа
            val scale= if (stage == PetStage.Baby) 0.6f else if (stage == PetStage.Teenager) 0.8f else 1f
            // ЛОГИКА ЗВЕЗД: Окно появления привязано к центру и размеру кролика
            val centerX = w / 2f
            val centerY = h / 2f// - (h * 0.14f) // Учитываем вертикальный translate подъема кролика

            // Радиус облака искр равен половине физического размера кролика (с запасом 1.2f для задорного вылета вокруг)
            val zoneWidth = w * scale //* 0.6f
            val zoneHeight = h * scale //* 0.6f
            x = centerX + (Random.nextFloat() - 0.5f) * zoneWidth
            y = centerY + (Random.nextFloat() - 0.5f) * zoneHeight
            vx = (Random.nextFloat() - 0.5f) * 0.05f
            vy = (Random.nextFloat() - 0.5f) * 0.05f
            size = w * (0.03f + Random.nextFloat() * 0.03f)
            text = ""
            angle = Random.nextFloat() * 360f
            rotationSpeed = (Random.nextFloat() - 0.5f) * 0.1f
            spawnDelay = 0L // Звезды рождаются мгновенно
            lifeDuration = 2000 + Random.nextLong(1500)
        } else if (mood == PetMood.Sleep) {
            // Естественный небольшой разброс точки старта у рта по горизонтали
            startX = w * 0.50f + (Random.nextFloat() - 0.5f) * (w * 0.02f)
            startX = w * 0.50f
            x = startX
            y = h * 0.55f // Примерный уровень рта кролика с учетом трансляции

            // Летят строго вверх (отрицательный vy) с небольшим разбросом
            vx = 0f
            // Лёгкий разброс вертикальной скорости, чтобы буквы не поднимались «строем»
            vy = -h * (if(stage == PetStage.Baby)0.00008f else if(stage == PetStage.Teenager)0.00016f else 0.00024f + Random.nextFloat() * 0.00002f)

            // Случайный выбор регистра букв "Z", "z" для разнообразия
            text = if (Random.nextBoolean()) "Z" else "z"
            size = w * (0.04f + Random.nextFloat() * 0.04f) // Размер шрифта
            angle = (Random.nextFloat() - 0.5f) * 20f // Легкий наклон буквы
            rotationSpeed = 0f // Буквы не крутятся волчком, только качаются по волне
            waveFrequency = 0.003f + Random.nextFloat() * 0.0015f // Скорость волны
            wavePhaseOffset = Random.nextFloat() * (2f * Math.PI.toFloat()) // Случайная начальная точка в синусоиде

            // РАВНОМЕРНЫЙ ВЫЛЕТ: распределяем задержку на основе индекса частицы (0, 1, 2)
            // Первое рождение получит красивый разброс (0мс, 1000мс, 2000мс), выстроив буквы в очередь
            if (age == 0L && !isAlive) {
                spawnDelay = index * 1000L
            } else {
                // При повторном перерождении буква уходит в конец полной 3-секундной очереди
                spawnDelay = 2800L
            }

            lifeDuration = 2800L
        }

        alpha = 0f
        // lifeDuration = 2000 + Random.nextLong(1500) // Живут 2-3.5 секунды
        age = 0
        isAlive = true
    }


}

@Composable
fun rememberPetParticles(mood: PetMood): List<PetParticle> {
    // Память выделяется один раз, список стабилен и не пересоздается
    val particles = remember { List(10) { PetParticle() } }

    LaunchedEffect(mood) {
        // МГНОВЕННЫЙ СБРОС ВСЕХ ЧАСТИЦ ПРИ СМЕНЕ НАСТРОЕНИЯ
        particles.forEach { p ->
            p.isAlive = false
            p.alpha = 0f
            p.age = 0L
            p.spawnDelay = 0L
        }
        var lastTime = withFrameMillis { it }

        while (true) {
            withFrameMillis { currentTime ->
                val deltaTime = (currentTime - lastTime).coerceIn(0, 50)
                lastTime = currentTime
                //определяем кол-во частит для разных типов
                val maxParticlesLimit = if (mood == PetMood.Happy) 10 else 6
                particles.forEachIndexed { index, p ->
                    if (p.isAlive) {
                        // Если у частицы есть задержка старта — уменьшаем её таймер
                        if (p.spawnDelay > 0L && mood == PetMood.Sleep) {
                            p.spawnDelay -= deltaTime
                            // Пока таймер тикает, принудительно держим альфу в нуле
                            p.alpha = 0f
                        } else {
                            p.age += deltaTime
                            val lifeProgress = p.age.toFloat() / p.lifeDuration

                            if ((mood == PetMood.Happy || mood == PetMood.Sleep) && index < maxParticlesLimit) {
                                // Плавное появление в начале жизни и плавное таяние в конце
                                p.alpha = when {
                                    lifeProgress < 0.2f -> lifeProgress / 0.2f
                                    lifeProgress > 0.7f -> (1f - lifeProgress) / 0.3f
                                    else -> 1f
                                }
                            } else {
                                // Если ушли в Normal или Sad — плавно тушим текущие частицы
                                p.alpha -= 0.005f * deltaTime
                                if (p.alpha <= 0f) p.isAlive = false
                            }

                            if (p.age >= p.lifeDuration && (mood == PetMood.Happy || mood == PetMood.Sleep)) {
                                p.isAlive = false // Время вышло, помечаем для перерождения
                            }

                            // Физика движения
                            if (p.text.isEmpty()) {
                                // Движение звезд (прямолинейное)
                                p.x += p.vx * deltaTime
                                p.y += p.vy * deltaTime
                                p.angle += p.rotationSpeed * deltaTime
                            } else {
                                // Движение букв Z-z-z (строго вверх по Y + волнообразный синус по X)
                                p.y += p.vy * deltaTime

                                // Магия волнообразного парения: смещаем X по синусу от времени жизни
                                val amplitude =
                                    p.size * 0.7f // Амплитуда раскачивания зависит от размера буквы
                                p.x = p.startX + sin((p.age * p.waveFrequency) + p.wavePhaseOffset) * amplitude

                                // Буквы могут плавно увеличиваться по мере подъема вверх, как настоящий пар
                                p.size += 0.002f * deltaTime
                            }
                        }
                    }
                }
            }
        }
    }
    return particles
}