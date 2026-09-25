package com.stepalex.finny.presentation.common.pets


import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import com.stepalex.finny.presentation.common.items.PetItemColor
import com.stepalex.finny.presentation.common.items.PetItemPosition
import com.stepalex.finny.presentation.common.items.PetItems
import kotlinx.coroutines.delay
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.time.Duration.Companion.milliseconds

// Контейнер, который просто вынесет твои переменные наружу, не ломая их работу
data class PetInteractionsOut(
    val leftX: Float,
    val leftY: Float,
    val rightX: Float,
    val rightY: Float,
    val finalBlinkProgress: Float,
    val isCurrentlyTouchingPet: Boolean,
    val isEnjoyingPet: Boolean,
    val localTouchPixelX: Float,
    val localTouchPixelY: Float,
    val eatMouthOpenProgress: Float,
    val lastMouthProgressBeforeRelease: Float,
    val canvasPositionInWindow: Offset,
    val pixelW: Float,
    val pixelH: Float,
    // Колбеки для Canvas
    val updatePosition: (Offset) -> Unit,
    val updateSize: (Float, Float) -> Unit,
    val updateMouthProgress: (Float) -> Unit,
    val updateReleaseProgress: (Float) -> Unit,
    val updateTouching: (Boolean) -> Unit,
    val updateEnjoying: (Boolean) -> Unit,
    val updateChewing: (Boolean) -> Unit,
    val updateSatisfied: (Boolean) -> Unit,
    val topHatScale: Float,
    val neckTieScale: Float,
    val hairBowScale: Float,
    val glassScale: Float,
    val crownScale: Float,
    val bowTieScale: Float,
    val prevTopHatPos: PetItemPosition?,
    val prevCrownPos: PetItemPosition?,
    val prevHairBowPos: PetItemPosition?,

    val cachedCrownColor: PetItemColor,
    val cachedCrownPos: PetItemPosition?,

    val cachedTopHatColor: PetItemColor,
    val cachedTopHatPos: PetItemPosition?,

    val cachedHairBowColor: PetItemColor,
    val cachedHairBowPos: PetItemPosition?,

    val cachedBowTieColor: PetItemColor,

    val cachedNeckTieColor: PetItemColor,

    val cachedGlassColor: PetItemColor
)

@Composable
fun rememberPetInteractionsState(
    stage: PetStage,
    mood: PetMood,
    action: PetAction,
    touchOffset: Offset?,
    stageScale: Float,
    sleepBlinkProgress: Float,
    mouthState: MouthState,
    petListener: PetListener,
    // Передаем текущие стейты из Bunny.kt внутрь, чтобы они синхронизировались!
    isChewing: Boolean,
    initialEatMouthOpenProgress: Float,
    initialLastMouthProgressBeforeRelease: Float,
    isSatisfied: Boolean,
    petItems: PetItems?
): PetInteractionsOut {

    // Восстанавливаем твои remember-переменные в их первозданном виде
    var eatMouthOpenProgress by remember { mutableFloatStateOf(initialEatMouthOpenProgress) }
    var lastMouthProgressBeforeRelease by remember {
        mutableFloatStateOf(
            initialLastMouthProgressBeforeRelease
        )
    }
    var isEnjoyingPet by remember { mutableStateOf(false) }
    var localTouchPixelX by remember { mutableFloatStateOf(0f) }
    var localTouchPixelY by remember { mutableFloatStateOf(0f) }
    var isCurrentlyTouchingPet by remember { mutableStateOf(false) }
    var canvasPositionInWindow by remember { mutableStateOf(Offset.Zero) }
    var pixelW by remember { mutableFloatStateOf(0f) }
    var pixelH by remember { mutableFloatStateOf(0f) }

    // --- АНИМАЦИЯ ПЛАВНОГО ПОЯВЛЕНИЯ/ИСЧЕЗНОВЕНИЯ ОДЕЖДЫ (SCALE) ---
    // --- УМНЫЙ КОНТРОЛЬ СМЕНЫ ПОЗИЦИЙ ---
    // Запоминаем предыдущие позиции, чтобы поймать момент клика
    var prevTopHatPos by remember { mutableStateOf<PetItemPosition?>(null) }
    var prevCrownPos by remember { mutableStateOf<PetItemPosition?>(null) }
    var prevHairBowPos by remember { mutableStateOf<PetItemPosition?>(null) }

    // Флаги принудительного схлопывания при смене места
    var isChangingTopHatPos by remember { mutableStateOf(false) }
    var isChangingCrownPos by remember { mutableStateOf(false) }
    var isChangingHairBowPos by remember { mutableStateOf(false) }

    // Ловим смену позиции Короны
    LaunchedEffect(petItems?.crown?.position) {
        val newPos = petItems?.crown?.position
        if (prevCrownPos != null && newPos != null && newPos != prevCrownPos) {
            isChangingCrownPos = true  // Включаем экстренное схлопывание
            delay(250)                // Ждём 250 мс, пока корона сдуется в старой точке
            isChangingCrownPos = false // Выключаем блокировку, разрешая раздувание в новой точке
        }
        prevCrownPos = newPos
    }

    // Ловим смену позиции Шляпки
    LaunchedEffect(petItems?.topHat?.position) {
        val newPos = petItems?.topHat?.position
        if (prevTopHatPos != null && newPos != null && newPos != prevTopHatPos) {
            isChangingTopHatPos = true
            delay(250)
            isChangingTopHatPos = false
        }
        prevTopHatPos = newPos
    }

    // Ловим смену позиции Бантика
    LaunchedEffect(petItems?.hairBow?.position) {
        val newPos = petItems?.hairBow?.position
        if (prevHairBowPos != null && newPos != null && newPos != prevHairBowPos) {
            isChangingHairBowPos = true
            delay(250)
            isChangingHairBowPos = false
        }
        prevHairBowPos = newPos
    }
    val targetTopHat = if (petItems?.topHat != null && !isChangingTopHatPos) 1f else 0f
    val targetNeckTie = if (petItems?.neckTie != null) 1f else 0f
    val targetHairBow = if (petItems?.hairBow != null && !isChangingHairBowPos) 1f else 0f
    val targetGlass = if (petItems?.glass != null) 1f else 0f
    val targetCrown = if (petItems?.crown != null && !isChangingCrownPos) 1f else 0f
    val targetBowTie = if (petItems?.bowTie != null) 1f else 0f

    val animTopHat by animateFloatAsState(
        targetValue = targetTopHat,
        animationSpec = tween(350, easing = LinearOutSlowInEasing),
        label = "HatScale"
    )
    val animNeckTie by animateFloatAsState(
        targetValue = targetNeckTie,
        animationSpec = tween(350, easing = LinearOutSlowInEasing),
        label = "TieScale"
    )
    val animHairBow by animateFloatAsState(
        targetValue = targetHairBow,
        animationSpec = tween(350, easing = LinearOutSlowInEasing),
        label = "BowScale"
    )
    val animGlass by animateFloatAsState(
        targetValue = targetGlass,
        animationSpec = tween(350, easing = LinearOutSlowInEasing),
        label = "GlassScale"
    )
    val animCrown by animateFloatAsState(
        targetValue = targetCrown,
        animationSpec = tween(350, easing = LinearOutSlowInEasing),
        label = "CrownScale"
    )
    val animBowTie by animateFloatAsState(
        targetValue = targetBowTie,
        animationSpec = tween(350, easing = LinearOutSlowInEasing),
        label = "BowTieScale"
    )

    // Кэшируем цвет и позицию короны, пока она существует
    var lastCrownColor by remember { mutableStateOf(PetItemColor.Gold) }
    var lastCrownPos by remember { mutableStateOf<PetItemPosition?>(PetItemPosition.TOP) }
    if (petItems?.crown != null) {
        lastCrownColor = petItems.crown.petItemColor?: PetItemColor.Gold
        lastCrownPos = petItems.crown.position
    }
    // Кэшируем цвет и позицию шляпы, пока она существует
    var lastTopHatColor by remember { mutableStateOf(PetItemColor.Red) }
    var lastTopHatPos by remember { mutableStateOf<PetItemPosition?>(PetItemPosition.TOP) }
    if (petItems?.topHat != null) {
        lastTopHatColor = petItems.topHat.petItemColor?: PetItemColor.Black
        lastTopHatPos = petItems.topHat.position
    }
    // Кэшируем цвет и позицию бантика, пока он надет
    var lastHairBowColor by remember { mutableStateOf(PetItemColor.Blue) }
    var lastHairBowPos by remember { mutableStateOf<PetItemPosition?>(PetItemPosition.RIGHT) }
    if (petItems?.hairBow != null) {
        lastHairBowColor = petItems.hairBow.petItemColor?: PetItemColor.Blue
        lastHairBowPos = petItems.hairBow.position
    }
    // Кэшируем цвет бабочки, пока она надета
    var lastBowTieColor by remember { mutableStateOf(PetItemColor.Red) }
    if (petItems?.bowTie != null) {
        lastBowTieColor = petItems.bowTie.petItemColor?: PetItemColor.Red
    }
    // Кэшируем цвет галстука, пока он надет
    var lastNeckTieColor by remember { mutableStateOf(PetItemColor.Red) }
    if (petItems?.neckTie != null) {
        lastNeckTieColor = petItems.neckTie.petItemColor?: PetItemColor.Blue
    }
    // Кэшируем цвет очков, пока они надеты
    var lastGlassColor by remember { mutableStateOf(PetItemColor.Purple) }
    if (petItems?.glass != null) {
        lastGlassColor = petItems.glass.petItemColor?: PetItemColor.Purple
    }
    // ---КОНЕЦ АНИМАЦИИ ПЛАВНОГО ПОЯВЛЕНИЯ/ИСЧЕЗНОВЕНИЯ ОДЕЖДЫ (SCALE) ---

    val blinkProgress = remember { Animatable(0f) }
    LaunchedEffect(mood) {
        if (mood != PetMood.Sleep) {
            while (true) {
                delay(2500.milliseconds)
                blinkProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 80, easing = FastOutLinearInEasing)
                )
                blinkProgress.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 80, easing = LinearOutSlowInEasing)
                )
            }
        } else {
            blinkProgress.snapTo(0f)
        }
    }

    var targetLeftX = 0.01f
    var targetLeftY = -0.01f
    var targetRightX = -0.01f
    var targetRightY = -0.01f

    if (mood != PetMood.Sleep && !isChewing && touchOffset != null &&
        canvasPositionInWindow != Offset.Zero && pixelW > 0f
    ) {
        val maxRadius = 0.012f

        val localTouchX = touchOffset.x - canvasPositionInWindow.x
        val localTouchY = touchOffset.y - canvasPositionInWindow.y

        localTouchPixelX = localTouchX
        localTouchPixelY = localTouchY

        val touchXNormal = localTouchPixelX / pixelW
        val touchYNormal = localTouchPixelY / pixelH

        val bunnyCenterNormalX = 0.50f
        val bunnyCenterNormalY = 0.50f - 0.14f

        val dxNormal = touchXNormal - bunnyCenterNormalX
        val dyNormal = touchYNormal - bunnyCenterNormalY
        val distanceNormal = sqrt(dxNormal * dxNormal + dyNormal * dyNormal)

        val currentPetRadiusNormal = 0.35f * stageScale
        isCurrentlyTouchingPet = distanceNormal <= currentPetRadiusNormal

        if (action == PetAction.Play || action == PetAction.Eat) {
            val leftEyePixelX = pixelW * 0.34f
            val leftEyePixelY = pixelH * 0.63f - (pixelH * 0.14f)

            val rightEyePixelX = pixelW * 0.66f
            val rightEyePixelY = leftEyePixelY

            val dxLeft = localTouchX - leftEyePixelX
            val dyLeft = localTouchY - leftEyePixelY
            val angleLeft = atan2(dyLeft, dxLeft)
            targetLeftX = cos(angleLeft) * maxRadius
            targetLeftY = sin(angleLeft) * maxRadius

            val dxRight = localTouchX - rightEyePixelX
            val dyRight = localTouchY - rightEyePixelY
            val angleRight = atan2(dyRight, dxRight)
            targetRightX = cos(angleRight) * maxRadius
            targetRightY = sin(angleRight) * maxRadius
        }

        if (action == PetAction.Eat) {
            val mouthPixelX = pixelW * 0.50f
            val mouthPixelY = pixelH * mouthState.centerY - (pixelH * 0.14f)

            val dxToMouth = localTouchX - mouthPixelX
            val dyToMouth = localTouchY - mouthPixelY
            val distanceToMouthPixels =
                sqrt(dxToMouth * dxToMouth + dyToMouth * dyToMouth)

            val startOpenRadiusPixels = pixelW * 0.82f * stageScale
            val maxOpenRadiusPixels = pixelW * 0.34f * stageScale

            eatMouthOpenProgress = when {
                distanceToMouthPixels >= startOpenRadiusPixels -> 0f
                distanceToMouthPixels <= maxOpenRadiusPixels -> 1f
                else -> {
                    1f - ((distanceToMouthPixels - maxOpenRadiusPixels) / (startOpenRadiusPixels - maxOpenRadiusPixels))
                }
            }
            lastMouthProgressBeforeRelease = eatMouthOpenProgress
        } else {
            eatMouthOpenProgress = 0f
            lastMouthProgressBeforeRelease = 0f
        }
    } else {
        isCurrentlyTouchingPet = false
        if (!isChewing) {
            eatMouthOpenProgress = 0f
        }
    }

    val leftX by animateFloatAsState(
        targetValue = targetLeftX,
        animationSpec = tween(120, easing = LinearOutSlowInEasing),
        label = "LX"
    )
    val leftY by animateFloatAsState(
        targetValue = targetLeftY,
        animationSpec = tween(120, easing = LinearOutSlowInEasing),
        label = "LY"
    )
    val rightX by animateFloatAsState(
        targetValue = targetRightX,
        animationSpec = tween(120, easing = LinearOutSlowInEasing),
        label = "RX"
    )
    val rightY by animateFloatAsState(
        targetValue = targetRightY,
        animationSpec = tween(120, easing = LinearOutSlowInEasing),
        label = "RY"
    )

    // ТВОИ ОРИГИНАЛЬНЫЕ ТАЙМЕРЫ
    LaunchedEffect(touchOffset, action, isCurrentlyTouchingPet) {
        if (mood != PetMood.Sleep && action == PetAction.Pet && touchOffset != null && isCurrentlyTouchingPet) {
            isEnjoyingPet = true
        } else {
            delay(1500.milliseconds)
            isEnjoyingPet = false
        }
    }

    val petBlinkProgress by animateFloatAsState(
        targetValue = if (isEnjoyingPet || isSatisfied) 1f else 0f,
        animationSpec = tween(300, easing = LinearOutSlowInEasing), label = "PetBlink"
    )

    val finalBlinkProgress = maxOf(sleepBlinkProgress, blinkProgress.value, petBlinkProgress)

    return PetInteractionsOut(
        leftX = leftX,
        leftY = leftY,
        rightX = rightX,
        rightY = rightY,
        finalBlinkProgress = finalBlinkProgress,
        isCurrentlyTouchingPet = isCurrentlyTouchingPet,
        isEnjoyingPet = isEnjoyingPet,
        localTouchPixelX = localTouchPixelX,
        localTouchPixelY = localTouchPixelY,
        eatMouthOpenProgress = eatMouthOpenProgress,
        lastMouthProgressBeforeRelease = lastMouthProgressBeforeRelease,
        canvasPositionInWindow = canvasPositionInWindow,
        pixelW = pixelW,
        pixelH = pixelH,
        updatePosition = { canvasPositionInWindow = it },
        updateSize = { w, h -> pixelW = w; pixelH = h },
        updateMouthProgress = { eatMouthOpenProgress = it },
        updateReleaseProgress = { lastMouthProgressBeforeRelease = it },
        updateTouching = { isCurrentlyTouchingPet = it },
        updateEnjoying = { isEnjoyingPet = it },
        updateChewing = { /* управляем снаружи */ },
        updateSatisfied = { /* управляем снаружи */ },
        topHatScale = animTopHat,
        neckTieScale = animNeckTie,
        hairBowScale = animHairBow,
        glassScale = animGlass,
        crownScale = animCrown,
        bowTieScale = animBowTie,
        prevTopHatPos = prevTopHatPos,
        prevCrownPos = prevCrownPos,
        prevHairBowPos = prevHairBowPos,
        cachedCrownColor = lastCrownColor,
        cachedCrownPos = lastCrownPos,
        cachedTopHatColor = lastTopHatColor,
        cachedTopHatPos = lastTopHatPos,
        cachedHairBowColor = lastHairBowColor,
        cachedHairBowPos = lastHairBowPos,
        cachedBowTieColor = lastBowTieColor,
        cachedNeckTieColor = lastNeckTieColor,
        cachedGlassColor = lastGlassColor,
    )
}