package com.stepalex.finny.presentation.pets

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stepalex.finny.presentation.items.PetItems
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

enum class PetType{
    BUNNY, BEAR
}

@Composable
fun Pet(
    stage: PetStage,
    mood: PetMood,
    action: PetAction = PetAction.Pet,
    touchOffset: Offset?,
    modifier: Modifier = Modifier,
    petListener: PetListener,
    petColor: PetColor = PetColor.White,
    petItems: PetItems?,
    petType: PetType = PetType.BEAR
) {
    // --- Создаем транзишн, который следит за изменением состояния stage ---
    val transition = updateTransition(targetState = stage, label = "BunnyStageTransition")
    val moodTransition = updateTransition(targetState = mood, label = "BunnyMoodTransition")
    // --- РОТ ---
    val mouthState = rememberBunnyMouthState(moodTransition)
    // --- СОСТОЯНИЯ КОРМЛЕНИЯ И ЖЕВАНИЯ ---
    var isChewing by remember { mutableStateOf(false) }     // Флаг: кролик жует (блокирует тач)
    var isSatisfied by remember { mutableStateOf(false) }   // Флаг: кролик сыт (глаза дугами)
    // Переменная, которая запоминает, насколько сильно был открыт рот прямо перед отрывом пальца
    var lastMouthProgressBeforeRelease by remember { mutableFloatStateOf(0f) }

    // Прогресс открытия рта: 0f (закрыт) до 1f (открыт на максимум)
    var eatMouthOpenProgress by remember { mutableFloatStateOf(0f) }

    // Переменная для синусоидальной анимации жевания рта Безье
    var chewingAnimationTime by remember { mutableFloatStateOf(0f) }

    // Бесконечный цикл для анимации движения челюстей во время жевания
    LaunchedEffect(isChewing, mood) {
        if (isChewing || mood == PetMood.Sleep) {
            var lastTime = withFrameMillis { it }
            while (isChewing || mood == PetMood.Sleep) {
                withFrameMillis { currentTime ->
                    val deltaTime = currentTime - lastTime
                    lastTime = currentTime
                    // Скорость жевания: делаем рот быстрым и забавным
                    chewingAnimationTime =
                        (chewingAnimationTime + 0.02f * deltaTime) % (2f * Math.PI.toFloat())
                }
            }
        } else {
            chewingAnimationTime = 0f
        }
    }

    // ПОДКЛЮЧАЕМ СИСТЕМУ ДЫХАНИЯ (Всего одна строчка!)
    val breathing = rememberPetBreathing(transition = transition, mood = mood)

    // Плавно анимируем базовую прозрачность ореола от настроения
    val haloAlphaBase by moodTransition.animateFloat(
        transitionSpec = { tween(durationMillis = 500) }, label = "HaloAlpha"
    ) { targetMood ->
        if (targetMood == PetMood.Happy) 0.75f else 0f
    }
    // Связываем прозрачность ореола с дыханием кролика для эффекта пульсации:
    // На пике вдоха (breathProgress = 1) сияние становится чуть ярче
    val finalHaloAlpha = haloAlphaBase * (0.8f + 0.3f * breathing.progress)


    // --- Анимируем коэффициент масштаба в зависимости от целевой стадии ---
    val stageScale by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600) }, label = "BunnyScale"
    ) { targetStage ->
        when (targetStage) {
            PetStage.Baby -> 0.6f
            PetStage.Teenager -> 0.75f
            PetStage.Adult -> 0.85f
        }
    }
    // Анимация прогресса для лапок малыша
    val babyFeetProgress by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600) }, label = "BabyFeetProgress"
    ) { targetStage ->
        if (targetStage == PetStage.Baby) 1f else 0f
    }
    // Анимируем верхнюю границу туловища (bodyMainPath top) с учетом типа питомца
    val bodyTop by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600) }, label = "BodyTop"
    ) { targetStage ->
        if (petType == PetType.BUNNY) {
            when (targetStage) {
                PetStage.Baby -> 0.87f
                PetStage.Teenager -> 0.50f
                PetStage.Adult -> 0.70f
            }
        } else {
            // Для мишки: плечи у подростка и взрослого сидят повыше, формируя монолитную шею со скриншота
            when (targetStage) {
                PetStage.Baby -> 0.87f
                PetStage.Teenager -> 0.54f
                PetStage.Adult -> 0.56f
            }
        }
    }

    // Анимируем нижнюю границу туловища (bodyMainPath bottom)
    val bodyBottom by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600) }, label = "BodyBottom"
    ) { targetStage ->
        if (petType == PetType.BUNNY) {
            when (targetStage) {
                PetStage.Baby -> 0.87f
                PetStage.Teenager -> 0.99f
                PetStage.Adult -> 1.19f
            }
        } else {
            // Для мишки: пузико чуть компактнее по вертикали, но шире по бокам (ширина задана в Renderer)
            when (targetStage) {
                PetStage.Baby -> 0.87f
                PetStage.Teenager -> 0.94f
                PetStage.Adult -> 1.15f
            }
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

    // Вызов стейта глаз
    val eyesState = rememberBunnyEyesState(moodTransition)

    // Анимируем плавное закрытие глаз при засыпании от 0f до 1f
    val sleepBlinkProgress by moodTransition.animateFloat(
        transitionSpec = { tween(durationMillis = 500) }, label = "SleepBlinkProgress"
    ) { targetMood ->
        if (targetMood == PetMood.Sleep) 1f else 0f
    }

    // Инициализируем вынесенный стейт-файл
    val interactions = rememberPetInteractionsState(
        stage = stage,
        mood = mood,
        action = action,
        touchOffset = touchOffset,
        stageScale = stageScale,
        sleepBlinkProgress = sleepBlinkProgress,
        mouthState = mouthState,
        petListener = petListener,
        isChewing = isChewing,
        initialEatMouthOpenProgress = eatMouthOpenProgress,
        initialLastMouthProgressBeforeRelease = lastMouthProgressBeforeRelease,
        isSatisfied = isSatisfied,
        petItems = petItems
    )

    // Специальный триггер для запуска еды, защищённый от прерывания корутины
    var eatTrigger by remember { mutableStateOf(0) }
    // СБРОСЫ жевания
    LaunchedEffect(action, mood, stage) {
        isChewing = false
        isSatisfied = false
    }
    // Блок отслеживания отрыва пальца: реагирует строго на изменение touchOffset,
    // но выполняет только мгновенный взвод триггера без тяжелых delay
    LaunchedEffect(touchOffset) {
        if (action == PetAction.Eat && touchOffset == null && interactions.lastMouthProgressBeforeRelease > 0.8f && !isChewing) {
            interactions.updateReleaseProgress(0f) // Сбрасываем пиковое значение рта
            eatTrigger++                           // Мгновенно взводим триггер еды!
        }
    }
    // ЖЕЛЕЗНЫЙ ТАЙМЕР ЕДЫ: Слушает ТОЛЬКО eatTrigger.
    // Пока тикают delay, повторные касания экрана больше не могут отменить или сбросить этот цикл!
    LaunchedEffect(eatTrigger) {
        if (eatTrigger > 0) {
            isChewing = true          // Включаем фазу жевания на 1.5 секунды

            delay(1500.milliseconds)               // Ровно 1.5 секунды идет анимация жевания губами Безье
            isChewing = false

            isSatisfied = true        // Включаем фазу блаженства сытости на 1 секунду
            delay(1000.milliseconds)
            isSatisfied = false       // Кролик снова открывает глазки

            if (mood == PetMood.Sad) {
                petListener.updatePetMod(newMode = PetMood.Normal)
            }
        }
    }
    // Синхронизируем обратно локальные переменные для движка челюстей
    eatMouthOpenProgress = interactions.eatMouthOpenProgress
    lastMouthProgressBeforeRelease = interactions.lastMouthProgressBeforeRelease


    // Системы частиц теперь берут флаги из объекта interactions
    val stars = rememberBunnyStars(mood = mood)
    val zzzList = rememberPetZzz(mood = mood)
    val hearts = rememberPetHearts(isEnjoyingPet = interactions.isEnjoyingPet)

    // Анимация блесска аксессуаров
    var sparkleAnimationTime by remember { mutableFloatStateOf(0f) }


    LaunchedEffect(Unit) {
        var lastTime = withFrameMillis { it }
        while (true) {
            withFrameMillis { currentTime ->
                val deltaTime = currentTime - lastTime
                lastTime = currentTime
                // Скорость мерцания искорок
                sparkleAnimationTime =
                    (sparkleAnimationTime + 0.002f * deltaTime) % (2f * Math.PI.toFloat())
            }
        }
    }
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .aspectRatio(1f)
            .onGloballyPositioned { layoutCoordinates ->
                interactions.updatePosition(layoutCoordinates.positionInWindow())
            }) {
        // Сохраняем точные пиксельные размеры холста для расчётов выше
        interactions.updateSize(size.width, size.height)
        val w = size.width
        val h = size.height

        // Общие стили линий
        val strokeWidth = w * 0.045f
        val strokeStyle = Stroke(
            width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round
        )

        val mouthStrokeStyle = Stroke(
            width = strokeWidth * 0.7f, cap = StrokeCap.Round, join = StrokeJoin.Round
        )

        // ПРИМЕНЯЕМ МАСШТАБ РОСТА И ДЫХАНИЯ ОДНОВРЕМЕННО
        // Относительно нижней части холста (чтобы кролик «дышал» вверх, опираясь на лапки)
        withTransform({
            // Перемножаем базовый масштаб стадии на текущую фазу дыхания
            val finalScaleX = stageScale * breathing.scaleX
            val finalScaleY = stageScale * breathing.scaleY
            scale(scaleX = finalScaleX, scaleY = finalScaleY, pivot = Offset(w / 2f, h * 0.95f))
        }) {
            //поднимаем отрисовку выше, чтоб потом дорисовать туловище
            withTransform({
                translate(top = -h * 0.14f)
            }) {
                // Отрисовка базовой анатомии кролика
                /*drawBunnyBodyAndEars(
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
                    haloAlpha = finalHaloAlpha,
                    petColor
                )*/
                drawPetBodyAndEars(
                    petType = petType, // Прокидываем тип зверя
                    w = w, h = h, strokeStyle = strokeStyle, bodyTop = bodyTop, bodyBottom = bodyBottom,
                    bumpTop = bumpTop, bumpBottom = bumpBottom, bodyElementsProgress = bodyElementsProgress,
                    tailLeft = tailLeft, tailTop = tailTop, tailRight = tailRight, tailBottom = tailBottom,
                    haloAlpha = finalHaloAlpha, petColor = petColor
                )
                // Отрисовка щечек
                drawPetChecks(w, h, petColor.blushColor)
                // Отрисовка элементов мордочки
                drawPetFace(
                    w,
                    h,
                    mouthStrokeStyle,
                    strokeStyle,
                    interactions.finalBlinkProgress,
                    mood = mood,
                    mouthState = mouthState,
                    eyesState = eyesState,
                    leftLookX = interactions.leftX * w,
                    leftLookY = interactions.leftY * h,
                    rightLookX = interactions.rightX * w,
                    rightLookY = interactions.rightY * h,
                    eatOpenProgress = eatMouthOpenProgress,
                    isChewing = isChewing,
                    chewingPhase = chewingAnimationTime,
                    petType = petType,
                    petColor = petColor
                )
                if (babyFeetProgress > 0f) {
                    drawBunnyFeet(w, h, strokeStyle, babyFeetProgress, petColor)
                }
                //--- ОТРИСОВКА ПРЕДМЕТОВ ---
                drawPetItems(
                    w = w,
                    h = h,
                    interactions = interactions,
                    petItems = petItems,
                    sparkleAnimationTime = sparkleAnimationTime,
                    stageScale = stageScale
                )
                // --- РУКИ ---
                drawHands(w, h, strokeStyle, bodyElementsProgress, armTop, armBottom, petColor)
            }
        }
        // ================= ИНТЕГРАЦИЯ ЗВЁЗД =================
        // Ограничиваем количество активных звезд до 10 штук
        val maxStarsLimit = 10

        stars.forEachIndexed { index, star ->
            // Рождаем звезду, только если мы в Happy, она мертва и укладывается в лимит
            if (mood == PetMood.Happy && !star.isAlive && index < maxStarsLimit) {
                star.reset(w, h)
            }

            // Отрисовываем звезду, если её альфа больше нуля
            if (star.alpha > 0f) {
                drawGoldenStar(star) // Передаем объект StarParticle в функцию отрисовки
            }
        }
        // --- ОТРИСОВКА БУКВ Z-z-z ---
        val maxZzzLimit = 3
        zzzList.forEachIndexed { index, p ->
            if (mood == PetMood.Sleep && !p.isAlive && index < maxZzzLimit) {
                p.reset(w, h, index) // Передаем w, h и index для очереди
            }
            if (p.alpha > 0f) {
                drawSleepLetter(
                    p, petColor.blushColor
                ) // Твоя оригинальная функция белых букв с обводкой
            }
        }
        // --- ОТРИСОВКА АЛЫХ СЕРДЕЧЕК ---
        val maxHeartsLimit = 3
        hearts.forEachIndexed { index, p ->
            // Рождаем сердце, если кролик жмурится от ласки, оно мертво и входит в лимит
            // Передаем w, h, index и АКТУАЛЬНЫЕ локальные пиксели пальца localTouchPixelX/Y
            if (interactions.isEnjoyingPet && !p.isAlive && index < maxHeartsLimit) {
                p.reset(w, h, index, interactions.localTouchPixelX, interactions.localTouchPixelY)
            }
            if (p.alpha > 0f) {
                drawContourHeart(p) // Твоя оригинальная функция отрисовки алого сердца с контуром
            }
        }
    }
}

/**
 * Отрисовка ручек
 */
private fun DrawScope.drawHands(
    w: Float,
    h: Float,
    strokeStyle: Stroke,
    bodyElementsProgress: Float,
    armTop: Float,
    armBottom: Float,
    petColor: PetColor
) {
    if (bodyElementsProgress > 0f) {
        val leftArmPath = Path().apply {
            arcTo(
                rect = Rect(
                    left = w * 0.30f, top = h * armTop, right = w * 0.45f, bottom = h * armBottom
                ), startAngleDegrees = -100f, sweepAngleDegrees = 200f, forceMoveTo = true
            )
            val pivotX = w * 0.35f
            val pivotY = h * 0.91f
            val matrix = Matrix().apply {
                translate(
                    pivotX, pivotY
                )
                rotateZ(-30f)
                translate(-pivotX, -pivotY)
            }
            transform(matrix)
        }
        val rightArmPath = Path().apply {
            arcTo(
                rect = Rect(
                    left = w * 0.55f, top = h * armTop, right = w * 0.70f, bottom = h * armBottom
                ), startAngleDegrees = 80f, sweepAngleDegrees = 200f, forceMoveTo = true
            )
            val pivotX = w * 0.65f
            val pivotY = h * 0.91f
            val matrix = Matrix().apply {
                translate(
                    pivotX, pivotY
                )
                rotateZ(30f)
                translate(-pivotX, -pivotY)
            }
            transform(matrix)
        }

        drawPath(
            path = leftArmPath,
            color = petColor.fillColor,
            style = Fill,
            alpha = bodyElementsProgress
        )
        drawPath(
            path = leftArmPath,
            color = petColor.outlineColor,
            style = strokeStyle,
            alpha = bodyElementsProgress
        )

        drawPath(
            path = rightArmPath,
            color = petColor.fillColor,
            style = Fill,
            alpha = bodyElementsProgress
        )
        drawPath(
            path = rightArmPath,
            color = petColor.outlineColor,
            style = strokeStyle,
            alpha = bodyElementsProgress
        )
    }
}


/**
 * Отрисовка лапок поверх готового тела.
 */
private fun DrawScope.drawBunnyFeet(
    w: Float, h: Float, strokeStyle: Stroke, progress: Float, petColor: PetColor
) {
    val leftFootCenter = Offset(w * 0.24f, h * 0.84f)
    val rightFootCenter = Offset(w * 0.76f, h * 0.84f)
    // Уменьшаем радиус лапок в зависимости от прогресса анимации
    val footRadius = w * 0.11f * progress
    // Левая лапка
    drawCircle(color = petColor.fillColor, radius = footRadius, center = leftFootCenter)
    drawCircle(
        color = petColor.outlineColor,
        radius = footRadius,
        center = leftFootCenter,
        style = strokeStyle,
        alpha = progress
    )

    // Правая лапка
    drawCircle(color = petColor.fillColor, radius = footRadius, center = rightFootCenter)
    drawCircle(
        color = petColor.outlineColor,
        radius = footRadius,
        center = rightFootCenter,
        style = strokeStyle,
        alpha = progress
    )
}


@Preview(showBackground = true, widthDp = 400, heightDp = 1200)
@Composable
fun PetRoundRect1Preview() {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {

        Pet(
            stage = PetStage.Baby,
            modifier = Modifier
                .fillMaxWidth(0.38f)
                .weight(1f),
            mood = PetMood.Sad,
            touchOffset = null,
            petItems = null,
            petListener = object : PetListener {
                override fun updatePetMod(newMode: PetMood) {

                }
            })
        Pet(
            stage = PetStage.Teenager,
            modifier = Modifier
                .fillMaxWidth(0.38f)
                .weight(1f),
            mood = PetMood.Sleep,
            touchOffset = null,
            petItems = null,
            petListener = object : PetListener {
                override fun updatePetMod(newMode: PetMood) {

                }
            })

        Pet(
            stage = PetStage.Adult,
            modifier = Modifier
                .fillMaxWidth(0.38f)
                .weight(1f),
            mood = PetMood.Happy,
            touchOffset = null,
            petItems = null,
            petListener = object : PetListener {
                override fun updatePetMod(newMode: PetMood) {

                }
            })
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 400)
@Composable
fun PetRoundRect2Preview() {
    Pet(
        stage = PetStage.Adult,
        mood = PetMood.Happy,
        touchOffset = null,
        petItems = null,
        petListener = object : PetListener {
            override fun updatePetMod(newMode: PetMood) {

            }
        })
}