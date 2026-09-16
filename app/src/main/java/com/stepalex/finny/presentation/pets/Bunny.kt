package com.stepalex.finny.presentation.pets

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.sin
import kotlin.time.Duration.Companion.milliseconds

enum class PetStage {
    Baby,
    Teenager,
    Adult
}

enum class PetMood {
    Sad,
    Normal,
    Happy,
    Sleep
}

private val OutlineColor = Color.Black
private val FillColor = Color.White
private val BlushColor = Color(0xFFFEE1E1)

@Composable
fun Bunny(stage: PetStage, mood: PetMood, modifier: Modifier = Modifier) {
    // --- Создаем транзишн, который следит за изменением состояния stage ---
    val transition = updateTransition(targetState = stage, label = "BunnyStageTransition")
    val moodTransition = updateTransition(targetState = mood, label = "BunnyMoodTransition")
    // --- РОТ ---
    val mouthState = rememberBunnyMouthState(moodTransition)
    // Подключаем систему золотых звёзд
    var canvasWidth by remember { mutableFloatStateOf(0f) }
    var canvasHeight by remember { mutableFloatStateOf(0f) }
    val stars = rememberBunnyStars(mood = mood)
    // Получаем список универсальных частиц (звёзды или Z-z-z)
    val particles = rememberPetParticles(mood = mood)

    // --- Дыхание ---
    // Плавно анимируем саму целевую скорость дыхания (время одного полуцикла в мс)
    // Благодаря этому при смене стадии скорость дыхания переключится не резко, а плавно
    val currentBreedDuration by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600) },
        label = "BreedDuration"
    ) { targetStage ->
        val baseSpeed = when (targetStage) {
            PetStage.Baby -> 800f
            PetStage.Teenager -> 1000f
            PetStage.Adult -> 1200f
        }
        // Если кролик спит — он дышит глубже и медленнее (+400мс), если радуется — чаще (-200мс)
        when (mood) {
            PetMood.Sleep -> baseSpeed + 400f
            PetMood.Happy -> baseSpeed - 200f
            PetMood.Sad -> baseSpeed + 100f
            PetMood.Normal -> baseSpeed
        }
    }
    // Храним текущую фазу дыхания (угол от 0 до 2*PI)
    var breathingPhase by remember { mutableFloatStateOf(0f) }

    // Бесконечный цикл, который обновляет фазу на каждом кадре экрана с учетом текущей скорости
    LaunchedEffect(Unit) {
        var lastTime = withFrameMillis { it }
        while (true) {
            withFrameMillis { currentTime ->
                val deltaTime = currentTime - lastTime
                lastTime = currentTime

                // Вычисляем, на сколько сдвинуть фазу за этот кадр.
                // Формула преобразует длительность полуцикла (currentBreedDuration) в скорость изменения угла синуса
                val phaseSpeed = (PI / currentBreedDuration).toFloat()
                breathingPhase = (breathingPhase + phaseSpeed * deltaTime) % (2f * PI.toFloat())
            }
        }
    }
    // Используем функцию sin() для создания идеальной плавной волны «вдох-выдох» от 0.0f до 1.0f
    // sin дает диапазон от -1 до 1, приводим его к 0..1
    val breathProgress = (sin(breathingPhase) + 1f) / 2f

    // Вычисляем финальные коэффициенты масштаба на основе прогресса синуса
    val breathingScaleY = 1.0f + (0.02f * breathProgress)
    val breathingScaleX = 1.0f + (0.015f * breathProgress)

    // Плавно анимируем базовую прозрачность ореола от настроения
    val haloAlphaBase by moodTransition.animateFloat(
        transitionSpec = { tween(durationMillis = 500) }, label = "HaloAlpha"
    ) { targetMood ->
        if (targetMood == PetMood.Happy) 0.5f else 0f
    }
    // Связываем прозрачность ореола с дыханием кролика для эффекта пульсации:
    // На пике вдоха (breathProgress = 1) сияние становится чуть ярче
    val finalHaloAlpha = haloAlphaBase * (0.7f + 0.3f * breathProgress)


    // --- Анимируем коэффициент масштаба в зависимости от целевой стадии ---
    val stageScale by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600) },
        label = "BunnyScale"
    ) { targetStage ->
        when (targetStage) {
            PetStage.Baby -> 0.6f
            PetStage.Teenager -> 0.75f
            PetStage.Adult -> 0.85f
        }
    }
    // Анимация прогресса для лапок малыша
    val babyFeetProgress by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600) },
        label = "BabyFeetProgress"
    ) { targetStage ->
        if (targetStage == PetStage.Baby) 1f else 0f
    }
    // Анимируем верхнюю границу туловища (bodyMainPath top)
    val bodyTop by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600) }, label = "BodyTop"
    ) { targetStage ->
        when (targetStage) {
            PetStage.Baby -> 0.87f     // Прячется за головой малыша
            PetStage.Teenager -> 0.50f // Позиция подростка
            PetStage.Adult -> 0.70f    // Позиция взрослого
        }
    }
    // Анимируем нижнюю границу туловища (bodyMainPath bottom)
    val bodyBottom by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600) }, label = "BodyBottom"
    ) { targetStage ->
        when (targetStage) {
            PetStage.Baby -> 0.87f     // Схлопнуто в ноль
            PetStage.Teenager -> 0.99f // Позиция подростка
            PetStage.Adult -> 1.19f    // Позиция взрослого
        }
    }
    // Анимируем нижнюю границу выступов нижних лапок
    val bumpBottom by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600) }, label = "BumpBottom"
    ) { targetStage ->
        when (targetStage) {
            PetStage.Baby -> 0.87f
            PetStage.Teenager -> 1.04f
            PetStage.Adult -> 1.24f
        }
    }
    // Анимируем верхнюю границу выступов нижних лапок (они привязаны к низу тела)
    val bumpTop by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600) }, label = "BumpTop"
    ) { targetStage ->
        when (targetStage) {
            PetStage.Baby -> 0.87f
            PetStage.Teenager -> 0.65f
            PetStage.Adult -> 1.05f
        }
    }
    // Прогресс появления хвостика и рук (0f у малыша, 1f у остальных)
    val bodyElementsProgress by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600) }, label = "BodyElementsProgress"
    ) { targetStage ->
        if (targetStage == PetStage.Baby) 0f else 1f
    }
    // Анимация позиции хвостика (сдвигается между Teenager и Adult)
    val tailLeft by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600) }, label = "TailLeft"
    ) { targetStage ->
        if (targetStage == PetStage.Adult) 0.80f else 0.76f
    }
    val tailTop by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600) }, label = "TailTop"
    ) { targetStage ->
        if (targetStage == PetStage.Adult) 0.93f else 0.80f
    }
    val tailRight by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600) }, label = "TailRight"
    ) { targetStage ->
        if (targetStage == PetStage.Adult) 0.94f else 0.90f
    }
    val tailBottom by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600) }, label = "TailBottom"
    ) { targetStage ->
        if (targetStage == PetStage.Adult) 1.07f else 0.94f
    }

    // Координаты рук (плавно интерполируем top и bottom между стадиями)
    val armTop by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600) }, label = "ArmTop"
    ) { targetStage ->
        if (targetStage == PetStage.Adult) 0.85f else 0.775f
    }
    val armBottom by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600) }, label = "ArmBottom"
    ) { targetStage ->
        if (targetStage == PetStage.Adult) 0.97f else 0.87f
    }

    // Вызов стейта глаз и бровей:
    val eyesState = rememberBunnyEyesState(moodTransition)
    // Храним прогресс моргания (0f - открыты, 1f - закрыты)
    val blinkProgress = remember { Animatable(0f) }
    // Бесконечный цикл периодического моргания
    LaunchedEffect(mood) {
        if (mood != PetMood.Sleep) {
            while (true) {
                // Ждем 3.5 секунды между морганиями
                delay(2500.milliseconds)

                // Быстро закрываем глаза (80 миллисекунд)
                blinkProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 80, easing = FastOutLinearInEasing)
                )
                // Быстро открываем глаза (80 миллисекунд)
                blinkProgress.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 80, easing = LinearOutSlowInEasing)
                )
            }
        } else {
            blinkProgress.snapTo(0f)
        }
    }
    // Анимируем плавное закрытие глаз при засыпании от 0f до 1f
    val sleepBlinkProgress by moodTransition.animateFloat(
        transitionSpec = { tween(durationMillis = 500) }, label = "SleepBlinkProgress"
    ) { targetMood ->
        if (targetMood == PetMood.Sleep) 1f else 0f
    }

    // Итоговый прогресс: если кролик спит, тут всегда будет плавно нарастать 1f.
    // Если бодрствует — значение будет полностью управляться вашим морганием.
    val finalBlinkProgress = maxOf(sleepBlinkProgress, blinkProgress.value)


    Canvas(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .aspectRatio(1f)
    ) {
        val w = size.width
        val h = size.height

        // Общие стили линий
        val strokeWidth = w * 0.045f
        val strokeStyle = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
        val mouthStrokeStyle = Stroke(
            width = strokeWidth * 0.7f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )

        // ПРИМЕНЯЕМ МАСШТАБ РОСТА И ДЫХАНИЯ ОДНОВРЕМЕННО
        // Относительно нижней части холста (чтобы кролик «дышал» вверх, опираясь на лапки)
        withTransform({
            // Перемножаем базовый масштаб стадии на текущую фазу дыхания
            val finalScaleX = stageScale * breathingScaleX
            val finalScaleY = stageScale * breathingScaleY
            scale(scaleX = finalScaleX, scaleY = finalScaleY, pivot = Offset(w / 2f, h * 0.95f))
        }) {
            //поднимаем отрисовку выше, чтоб потом дорисовать туловище
            withTransform({
                translate(top = -h * 0.14f)
            }) {
                // Отрисовка базовой анатомии кролика
                drawBunnyBodyAndEars(
                    w = w,
                    h = h,
                    strokeStyle = strokeStyle,
                    bodyTop = bodyTop,
                    bodyBottom = bodyBottom,
                    bumpTop = bumpTop,
                    bumpBottom = bumpBottom,
                    bodyElementsProgress = bodyElementsProgress,
                    tailLeft = tailLeft,
                    tailTop = tailTop,
                    tailRight = tailRight,
                    tailBottom = tailBottom,
                    armTop = armTop,
                    armBottom = armBottom,
                    haloAlpha = finalHaloAlpha
                )
                // Отрисовка элементов мордочки
                drawBunnyFace(
                    w,
                    h,
                    mouthStrokeStyle,
                    strokeStyle,
                    finalBlinkProgress,
                    mood = mood,
                    mouthState = mouthState,
                    eyesState = eyesState
                )
                if (babyFeetProgress > 0f) {
                    drawBunnyFeet(w, h, strokeStyle, babyFeetProgress)
                }
            }
        }
        /*// Логика рождения и отрисовка звезд прямо на холсте
        stars.forEach { star ->
            // Если мы в режиме Happy и звезда мертва — рождаем её, зная точные размеры w и h холста
            if (mood == PetMood.Happy && !star.isAlive) {
                star.reset(w, h)
            }

            // Рисуем только те звезды, у которых альфа больше нуля
            if (star.alpha > 0f) {
                drawGoldenStar(star)
            }
        }*/
        // Логика рождения и отрисовки частиц поверх кролика
        particles.forEachIndexed { index, p ->
            // Рождаем частицу, если мы в активном режиме и она мертва
            if ((mood == PetMood.Happy || mood == PetMood.Sleep) && !p.isAlive) {
                p.reset(w, h, index, stage, mood)
            }

            if (p.alpha > 0f) {
                if (p.text.isEmpty()) {
                    // Если текста нет — рисуем золотую звезду (Ваша прежняя функция)
                    drawGoldenStar(p)
                } else {
                    // Если текст есть — рисуем букву "Z"
                    drawSleepLetter(p)
                }
            }
        }
    }
}

/**
 * Сложная геометрия: построение головы, ушек (внешних и внутренних) и их объединение.
 */
private fun DrawScope.drawBunnyBodyAndEars(
    w: Float,
    h: Float,
    strokeStyle: Stroke,
    bodyTop: Float,
    bodyBottom: Float,
    bumpTop: Float,
    bumpBottom: Float,
    bodyElementsProgress: Float,
    tailLeft: Float, tailTop: Float, tailRight: Float, tailBottom: Float,
    armTop: Float, armBottom: Float,
    haloAlpha: Float
) {
    // Голова
    val headPath = Path().apply {
        addRoundRect(
            RoundRect(
                left = w * 0.08f,
                top = h * 0.31f,
                right = w * 0.92f,
                bottom = h * 0.87f,
                cornerRadius = CornerRadius(w * 0.36f, h * 0.28f)
            )
        )
    }

    val leftInnerEarPath = Path()
    val rightInnerEarPath = Path()

    val leftEarPath = Path().apply {
        addRoundRect(
            RoundRect(
                topLeftCornerRadius = CornerRadius(w * 0.12f, h * 0.20f),
                topRightCornerRadius = CornerRadius(w * 0.125f, h * 0.20f),
                bottomLeftCornerRadius = CornerRadius(w * 0.125f, h * 0.20f),
                bottomRightCornerRadius = CornerRadius(w * 0.125f, h * 0.20f),
                left = w * 0.24f,
                top = h * 0.05f,
                right = w * 0.49f,
                bottom = h * 0.55f
            )
        )
        leftInnerEarPath.addRoundRect(
            RoundRect(
                topLeftCornerRadius = CornerRadius(w * 0.20f, h * 0.25f),
                topRightCornerRadius = CornerRadius(w * 0.20f, h * 0.25f),
                bottomLeftCornerRadius = CornerRadius(w * 0.075f, h * 0.15f),
                bottomRightCornerRadius = CornerRadius(w * 0.2f, h * 0.2f),
                left = w * 0.29f,
                top = h * 0.11f,
                right = w * 0.44f,
                bottom = h * 0.35f
            )
        )
        val pivotX = w * 0.355f
        val pivotY = h * 0.45f
        val matrix = Matrix().apply {
            translate(pivotX, pivotY)
            rotateZ(-8f)
            translate(-pivotX, -pivotY)
        }
        transform(matrix)
        leftInnerEarPath.transform(matrix)
    }

    val rightEarPath = Path().apply {
        addRoundRect(
            RoundRect(
                topLeftCornerRadius = CornerRadius(w * 0.125f, h * 0.20f),
                topRightCornerRadius = CornerRadius(w * 0.12f, h * 0.20f),
                bottomLeftCornerRadius = CornerRadius(w * 0.125f, h * 0.20f),
                bottomRightCornerRadius = CornerRadius(w * 0.125f, h * 0.20f),
                left = w * 0.51f,
                top = h * 0.05f,
                right = w * 0.76f,
                bottom = h * 0.55f
            )
        )
        rightInnerEarPath.addRoundRect(
            RoundRect(
                topLeftCornerRadius = CornerRadius(w * 0.20f, h * 0.25f),
                topRightCornerRadius = CornerRadius(w * 0.20f, h * 0.25f),
                bottomLeftCornerRadius = CornerRadius(w * 0.2f, h * 0.2f),
                bottomRightCornerRadius = CornerRadius(w * 0.075f, h * 0.15f),
                left = w * 0.56f,
                top = h * 0.11f,
                right = w * 0.71f,
                bottom = h * 0.35f
            )
        )
        val pivotX = w * 0.645f
        val pivotY = h * 0.45f
        val matrix = Matrix().apply {
            translate(pivotX, pivotY)
            rotateZ(8f)
            translate(-pivotX, -pivotY)
        }
        transform(matrix)
        rightInnerEarPath.transform(matrix)
    }
    // Хвостик (круглый скругленный прямоугольник, подкладывается под правый нижний бок)
    if (bodyElementsProgress > 0f) {
        val tailPath = Path().apply {
            addRoundRect(
                RoundRect(
                    left = w * tailLeft,
                    top = h * tailTop,
                    right = w * tailRight,
                    bottom = h * tailBottom,
                    cornerRadius = CornerRadius(w * 0.065f, h * 0.065f)
                )
            )
        }
        drawPath(path = tailPath, color = FillColor, alpha = bodyElementsProgress)
        drawPath(
            path = tailPath,
            color = OutlineColor,
            style = strokeStyle,
            alpha = bodyElementsProgress
        )
    }

    // --- Анимированная геометрия туловища ---
    val bodyMainPath = Path().apply {
        addRoundRect(
            RoundRect(
                left = w * 0.15f,
                top = h * bodyTop,
                right = w * 0.85f,
                bottom = h * bodyBottom,
                topLeftCornerRadius = CornerRadius(w * 0.38f, h * 0.32f),
                topRightCornerRadius = CornerRadius(w * 0.38f, h * 0.32f),
                bottomLeftCornerRadius = CornerRadius(w * 0.2f, h * 0.2f),
                bottomRightCornerRadius = CornerRadius(w * 0.2f, h * 0.2f),
            )
        )
    }

    val bottomLeftBump = Path().apply {
        addRoundRect(
            RoundRect(
                left = w * 0.23f,
                top = h * bumpTop,
                right = w * 0.42f,
                bottom = h * bumpBottom,
                cornerRadius = CornerRadius(w * 0.71f, w * 0.71f)
            )
        )
    }
    val bottomRightBump = Path().apply {
        addRoundRect(
            RoundRect(
                left = w * 0.6f,
                top = h * bumpTop,
                right = w * 0.78f,
                bottom = h * bumpBottom,
                cornerRadius = CornerRadius(w * 0.71f, w * 0.71f)
            )
        )
    }

    // Объединяем всё в единый плавный силуэт
    val fullBunnyPath = Path().apply {
        op(headPath, leftEarPath, PathOperation.Union)
        op(this, rightEarPath, PathOperation.Union)
        op(this, bodyMainPath, PathOperation.Union)
        op(this, bottomLeftBump, PathOperation.Union)
        op(this, bottomRightBump, PathOperation.Union)
    }
    // --- ОТРИСОВКА СВЕТЯЩЕГОСЯ ОРЕОЛА (ПОД ТЕЛОМ) ---
    if (haloAlpha > 0f) {
        val glowRadius = w * 0.16f // Радиус размытия ауры (6% от ширины)
        val glowColor = Color(0xFFFFEAA7) // Мягкий, пастельно-жёлтый светящийся оттенок

        // Рисуем размытый силуэт на родном nativeCanvas устройства
        drawContext.canvas.nativeCanvas.save()

        val paint = android.graphics.Paint().apply {
            color = glowColor.toArgb()
            isAntiAlias = true
            // Применяем фильтр размытия (работает на Android с выключенным аппаратным ускорением или на Canvas)
            maskFilter = android.graphics.BlurMaskFilter(
                glowRadius,
                android.graphics.BlurMaskFilter.Blur.NORMAL
            )
        }

        // Отрисовываем путь кролика как светящуюся подложку
        drawContext.canvas.nativeCanvas.drawPath(fullBunnyPath.asAndroidPath(), paint)

        drawContext.canvas.nativeCanvas.restore()
    }

    // --- ОТРИСОВКА САМОГО ТЕЛА
    // Отрисовка силуэта
    drawPath(path = fullBunnyPath, color = FillColor)
    drawPath(path = fullBunnyPath, color = OutlineColor, style = strokeStyle)

    // --- Передние лапки-ручки (плавно прорисовываются поверх пузика) ---
    if (bodyElementsProgress > 0f) {
        val leftArmPath = Path().apply {
            arcTo(
                rect = Rect(
                    left = w * 0.30f,
                    top = h * armTop,
                    right = w * 0.45f,
                    bottom = h * armBottom
                ), startAngleDegrees = -100f, sweepAngleDegrees = 200f, forceMoveTo = true
            )
            val pivotX = w * 0.35f
            val pivotY = h * 0.91f
            val matrix = Matrix().apply {
                translate(
                    pivotX,
                    pivotY
                )
                rotateZ(-30f)
                translate(-pivotX, -pivotY)
            }
            transform(matrix)
        }
        val rightArmPath = Path().apply {
            arcTo(
                rect = Rect(
                    left = w * 0.55f,
                    top = h * armTop,
                    right = w * 0.70f,
                    bottom = h * armBottom
                ), startAngleDegrees = 80f, sweepAngleDegrees = 200f, forceMoveTo = true
            )
            val pivotX = w * 0.65f
            val pivotY = h * 0.91f
            val matrix = Matrix().apply {
                translate(
                    pivotX,
                    pivotY
                )
                rotateZ(30f)
                translate(-pivotX, -pivotY)
            }
            transform(matrix)
        }

        drawPath(
            path = leftArmPath,
            color = OutlineColor,
            style = strokeStyle,
            alpha = bodyElementsProgress
        )
        drawPath(
            path = rightArmPath,
            color = OutlineColor,
            style = strokeStyle,
            alpha = bodyElementsProgress
        )
    }

    // Розовые серединки ушей
    drawPath(path = leftInnerEarPath, color = BlushColor)
    drawPath(path = rightInnerEarPath, color = BlushColor)

}

/**
 * Отрисовка лица: щечки, глаза, ротик.
 */
private fun DrawScope.drawBunnyFace(
    w: Float,
    h: Float,
    mouthStrokeStyle: Stroke,
    strokeStyle: Stroke,
    blinkProgress: Float,
    mood: PetMood,
    mouthState: MouthState,
    eyesState: EyesState
) {
    // Щечки
    drawOval(
        color = BlushColor,
        topLeft = Offset(w * 0.17f, h * 0.60f),
        size = Size(w * 0.16f, h * 0.13f)
    )
    drawOval(
        color = BlushColor,
        topLeft = Offset(w * 0.67f, h * 0.60f),
        size = Size(w * 0.16f, h * 0.13f)
    )

    // --- Глаза ---
    // Вычисляем размеры глаз с учетом моргания
    val baseEyeRadius = w * 0.045f
    // Высота глаза уменьшается до нуля при полном моргании (blinkProgress = 1f)
    val eyeHeight = max(baseEyeRadius * 2f * (1f - blinkProgress), baseEyeRadius * 1.05f)
    val eyeWidth = baseEyeRadius * 2f

    val leftEyeCenter = Offset(w * 0.34f, h * 0.63f)
    val rightEyeCenter = Offset(w * 0.66f, h * 0.63f)

    if (blinkProgress < 0.95f) {
        drawRoundRect(
            color = OutlineColor,
            topLeft = Offset(
                leftEyeCenter.x - baseEyeRadius,
                leftEyeCenter.y - (eyeHeight / 2f) + (h * eyesState.eyeTopInset)
            ),
            size = Size(eyeWidth, eyeHeight - h * eyesState.eyeTopInset),

            cornerRadius = CornerRadius(
                x = eyeWidth * 0.5f * max((1 - blinkProgress), 0.5f),
                y = eyeHeight * 0.5f
            )
        )
        drawRoundRect(
            color = OutlineColor,
            topLeft = Offset(
                rightEyeCenter.x - baseEyeRadius,
                rightEyeCenter.y - (eyeHeight / 2f) + (h * eyesState.eyeTopInset)
            ),
            size = Size(eyeWidth, eyeHeight - (h * eyesState.eyeTopInset)),

            cornerRadius = CornerRadius(
                x = eyeWidth * 0.5f * max((1 - blinkProgress), 0.5f),
                y = eyeHeight * 0.5f
            )
        )

        // Зрачки-блики (плавно исчезают по прозрачности, чтобы не вылезать за пределы сжимающегося глаза)
        val pupilAlpha = (1f - blinkProgress).coerceIn(0f, 1f)
        drawCircle(
            color = Color.White,
            radius = w * 0.015f,
            center = Offset(
                w * 0.35f + (w * eyesState.pupilOffsetX),
                h * 0.62f + (h * eyesState.pupilOffsetY)
            ),
            alpha = pupilAlpha
        )
        drawCircle(
            color = Color.White,
            radius = w * 0.015f,
            center = Offset(
                w * 0.66f - (w * 0.01f + w * eyesState.pupilOffsetX),
                h * 0.62f + (h * eyesState.pupilOffsetY)
            ),
            alpha = pupilAlpha
        )
    }else {
        // ================= ГЛАЗА ЗАКРЫТЫ ВО СНЕ / МОРГАНИИ (Дуги выгнутые вниз) =================
        // Вычисляем ширину дуги на основе радиуса глаза
        val leftStartX = leftEyeCenter.x - baseEyeRadius
        val leftEndX = leftEyeCenter.x + baseEyeRadius

        val rightStartX = rightEyeCenter.x - baseEyeRadius
        val rightEndX = rightEyeCenter.x + baseEyeRadius

        // Опорная точка (Control Point) находится строго по центру глаза по X,
        // и смещена НАВЕРХ по Y, чтобы притянуть кривую Безье и выгнуть её куполом (дугой вниз)
        val controlY = leftEyeCenter.y - (baseEyeRadius * 0.7f)

        val closedEyesPath = Path().apply {
            // Левая спящая дуга
            moveTo(leftStartX, leftEyeCenter.y)
            quadraticTo(x1 = leftEyeCenter.x, y1 = controlY, x2 = leftEndX, y2 = leftEyeCenter.y)

            // Правая спящая дуга
            moveTo(rightStartX, rightEyeCenter.y)
            quadraticTo(x1 = rightEyeCenter.x, y1 = controlY, x2 = rightEndX, y2 = rightEyeCenter.y)
        }

        // Рисуем дуги основным стилем линий (strokeStyle) с закруглёнными краями
        drawPath(path = closedEyesPath, color = OutlineColor, style = strokeStyle)
    }
    // --- БРОВИ ДОМИКОМ (Проявляются только при грусти) ---
    if (eyesState.browAlpha > 0f) {
        val browWidth = w * 0.04f
        val baseBrowY = h * 0.56f // Высота посадки бровей над глазами

        // Левая бровь
        val leftBrowPath = Path().apply {
            // Внешний край (слева) зафиксирован
            moveTo(leftEyeCenter.x - browWidth, baseBrowY)
            // Внутренний край (справа, у носа) плавно приподнимается вверх на "домик"
            quadraticTo(
                x1 = leftEyeCenter.x, y1 = baseBrowY - (h * 0.01f),
                x2 = leftEyeCenter.x + browWidth, y2 = baseBrowY - (h * eyesState.browInnerYOffset)
            )
        }

        // Правая бровь
        val rightBrowPath = Path().apply {
            // Внутренний край (слева, у носа) плавно приподнимается вверх на "домик"
            moveTo(rightEyeCenter.x - browWidth, baseBrowY - (h * eyesState.browInnerYOffset))
            // Внешний край (справа) зафиксирован
            quadraticTo(
                x1 = rightEyeCenter.x, y1 = baseBrowY - (h * 0.01f),
                x2 = rightEyeCenter.x + browWidth, y2 = baseBrowY
            )
        }

        // Рисуем брови с толщиной рта (mouthStrokeStyle) и плавной прозрачностью
        drawPath(
            path = leftBrowPath,
            color = OutlineColor,
            style = mouthStrokeStyle,
            alpha = eyesState.browAlpha
        )
        drawPath(
            path = rightBrowPath,
            color = OutlineColor,
            style = mouthStrokeStyle,
            alpha = eyesState.browAlpha
        )
    }

    // --- АНИМИРОВАННЫЙ РОТ ---
    // Рассчитываем ключевые точки на основе анимированных Float
    val centerX = w * 0.50f
    val centerY = h * mouthState.centerY

    val leftCornerX = centerX - (w * mouthState.widthOffset)
    val rightCornerX = centerX + (w * mouthState.widthOffset)
    val cornersY = h * mouthState.cornerY

    // Контрольные точки находятся посередине между центром и уголками по X
    val leftControlX = (leftCornerX + centerX) / 2f
    val rightControlX = (rightCornerX + centerX) / 2f
    val controlY = h * mouthState.controlY

    val mouthPath = Path().apply {
        // 1. Левая половинка губы: стартуем из левого уголка, тянем к центру
        moveTo(leftCornerX, cornersY)
        // Левая половинка губы: тянем к центру
        quadraticTo(
            x1 = leftControlX, y1 = controlY, // Опорная точка
            x2 = centerX, y2 = centerY        // Конечная точка под носом
        )

        // Правая половинка губы: от центра тянем к правому уголку
        quadraticTo(
            x1 = rightControlX, y1 = controlY, // Опорная точка
            x2 = rightCornerX, y2 = cornersY   // Конечная точка
        )
    }

    // Отрисовываем получившийся эластичный ротик
    drawPath(path = mouthPath, color = OutlineColor, style = mouthStrokeStyle)
}

/**
 * Отрисовка лапок поверх готового тела.
 */
private fun DrawScope.drawBunnyFeet(w: Float, h: Float, strokeStyle: Stroke, progress: Float) {
    val leftFootCenter = Offset(w * 0.24f, h * 0.84f)
    val rightFootCenter = Offset(w * 0.76f, h * 0.84f)
    // Уменьшаем радиус лапок в зависимости от прогресса анимации
    val footRadius = w * 0.11f * progress
    // Левая лапка
    drawCircle(color = FillColor, radius = footRadius, center = leftFootCenter)
    drawCircle(
        color = OutlineColor,
        radius = footRadius,
        center = leftFootCenter,
        style = strokeStyle,
        alpha = progress
    )

    // Правая лапка
    drawCircle(color = FillColor, radius = footRadius, center = rightFootCenter)
    drawCircle(
        color = OutlineColor,
        radius = footRadius,
        center = rightFootCenter,
        style = strokeStyle,
        alpha = progress
    )
}

/**
 * Отрисовка звездочек
 */
private fun DrawScope.drawGoldenStar(star: PetParticle) {
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

/**
 * Отрисовка частиц рядом
 */
private fun DrawScope.drawSleepLetter(p: PetParticle) {
    drawContext.canvas.nativeCanvas.save()

    // 1. НАСТРОЙКА КРАСКИ ДЛЯ ОБВОДКИ (Черный контур)
    val strokePaint = android.graphics.Paint().apply {
        color = Color.Black.toArgb() // белый цвет контура кролика
        textSize = p.size
        isAntiAlias = true
        typeface = android.graphics.Typeface.create(
            android.graphics.Typeface.DEFAULT,
            android.graphics.Typeface.BOLD
        )
        alpha = (p.alpha * 255).toInt().coerceIn(0, 255)

        // Включаем режим обводки
        style = android.graphics.Paint.Style.STROKE
        // Толщина обводки буквы (делаем чуть тоньше основной обводки кролика, чтобы текст читался)
        strokeWidth = p.size * 0.15f
        strokeJoin = android.graphics.Paint.Join.ROUND
        strokeCap = android.graphics.Paint.Cap.ROUND
    }

    // 2. НАСТРОЙКА КРАСКИ ДЛЯ ЗАЛИВКИ (Белая серединка)
    val fillPaint = android.graphics.Paint().apply {
        color = BlushColor.toArgb() // Черный цвет заливки
        textSize = p.size
        isAntiAlias = true
        typeface = android.graphics.Typeface.create(
            android.graphics.Typeface.DEFAULT,
            android.graphics.Typeface.BOLD
        )
        alpha = (p.alpha * 255).toInt().coerceIn(0, 255)

        // Включаем режим сплошной заливки
        style = android.graphics.Paint.Style.FILL
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

@Preview(showBackground = true, widthDp = 400, heightDp = 1200)
@Composable
fun BunnyRoundRect1Preview() {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {

        Bunny(
            stage = PetStage.Baby, modifier = Modifier
                .fillMaxWidth(0.38f)
                .weight(1f),
            mood = PetMood.Sad
        )

        Bunny(
            stage = PetStage.Teenager, modifier = Modifier
                .fillMaxWidth(0.38f)
                .weight(1f),
            mood = PetMood.Sleep
        )

        Bunny(
            stage = PetStage.Adult, modifier = Modifier
                .fillMaxWidth(0.38f)
                .weight(1f),
            mood = PetMood.Happy
        )
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 400)
@Composable
fun BunnyRoundRect2Preview() {
    Bunny(stage = PetStage.Adult, mood = PetMood.Happy)
}