package com.stepalex.finny.presentation.pets

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.max
import kotlin.math.sin


/**
 * Отрисовка лица: щечки, глаза, ротик.
 */
fun DrawScope.drawPetFace(
    w: Float,
    h: Float,
    mouthStrokeStyle: Stroke,
    strokeStyle: Stroke,
    blinkProgress: Float,
    mood: PetMood,
    mouthState: MouthState,
    eyesState: EyesState,
    leftLookX: Float,
    leftLookY: Float,
    rightLookX: Float,
    rightLookY: Float,
    eatOpenProgress: Float,
    isChewing: Boolean,
    chewingPhase: Float
) {
    val outlineColor = Color.Black

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
            color = outlineColor,
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
            color = outlineColor,
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
                //w * 0.35f + (w * eyesState.pupilOffsetX),
                //h * 0.62f + (h * eyesState.pupilOffsetY)
                x = leftEyeCenter.x + (w * eyesState.pupilOffsetX) + leftLookX,
                y = leftEyeCenter.y + (h * eyesState.pupilOffsetY) + leftLookY
            ),
            alpha = pupilAlpha
        )
        drawCircle(
            color = Color.White,
            radius = w * 0.015f,
            center = Offset(
                //w * 0.66f - (w * 0.01f + w * eyesState.pupilOffsetX),
                //h * 0.62f + (h * eyesState.pupilOffsetY)
                x = rightEyeCenter.x - (w * eyesState.pupilOffsetX) + rightLookX,
                y = rightEyeCenter.y + (h * eyesState.pupilOffsetY) + rightLookY
            ),
            alpha = pupilAlpha
        )
    } else {
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
        drawPath(path = closedEyesPath, color = outlineColor, style = strokeStyle)
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
            color = outlineColor,
            style = mouthStrokeStyle,
            alpha = eyesState.browAlpha
        )
        drawPath(
            path = rightBrowPath,
            color = outlineColor,
            style = mouthStrokeStyle,
            alpha = eyesState.browAlpha
        )
    }


    // --- ИНТЕРАКТИВНЫЙ РОТ КОРМЛЕНИЯ ---
    val centerX = w * 0.50f
    val baseCenterY = h * mouthState.centerY

    if (eatOpenProgress > 0f) {
        // ================= ФАЗА СБЛИЖЕНИЯ: ОТКРЫВАЕМ ВЕРТИКАЛЬНЫЙ ОВАЛ С ЯЗЫЧКОМ =================
        val maxMouthW = w * 0.065f
        val maxMouthH = h * 0.08f

        val currentMouthW = maxMouthW * eatOpenProgress
        val currentMouthH = maxMouthH * eatOpenProgress

        // Рисуем чёрную полость открытого рта
        drawOval(
            color = outlineColor,
            topLeft = Offset(centerX - (currentMouthW / 2f), baseCenterY - (currentMouthH * 0.2f)),
            size = Size(currentMouthW, currentMouthH)
        )

        // Рисуем розовый язычок
        if (eatOpenProgress > 0.4f) {
            val tongueW = currentMouthW * 0.7f
            val tongueH = currentMouthH * 0.35f
            val tongueColor = Color(0xFFFF8A8A)

            drawOval(
                color = tongueColor,
                topLeft = Offset(
                    centerX - (tongueW / 2f),
                    baseCenterY + (currentMouthH * 0.4f) - (tongueH / 2f)
                ),
                size = Size(tongueW, tongueH)
            )
        }
    } else {
        // ================= ФАЗА ПОКОЯ, СНА ИЛИ ЖЕВАНИЯ (Мордочка Безье) =================
        // 1. Берем абсолютные координаты прямо из MouthState
        var finalCenterY = h * mouthState.centerY
        var finalCornerY = h * mouthState.cornerY
        var finalControlY = h * mouthState.controlY
        if (isChewing) {
            // ЭФФЕКТ ЖЕВАНИЯ: заставляем высоту Безье-губы быстро сжиматься по синусоиде
            val chewingScaleY = (sin(chewingPhase) + 1f) / 2f // Диапазон от 0 до 1

            // Быстро и забавно колеблем челюсть вверх-вниз в пределах 1.2% от высоты холста
            val chewDelta = h * 0.012f * chewingScaleY
            finalCenterY += chewDelta
            finalControlY -= chewDelta * 0.5f
            finalCornerY += chewDelta
        }
        // Сонное посапывание
        // Смещаем ВСЕ три координаты рта одновременно, чтобы сжатый ротик-точка ожил!
        // Слегка увеличим амплитуду до 0.006f для хорошей мультяшной видимости
        if (mood == PetMood.Sleep) {
            val snoreScale = (sin(chewingPhase * 0.5f) + 1f) / 2f
            val snoreDelta = h * 0.01f * snoreScale

            finalCenterY += snoreDelta
            finalCornerY += snoreDelta
            finalControlY += snoreDelta
        }

        // 2.Расчёт контрольных точек по оси X
        val leftCornerX = centerX - (w * mouthState.widthOffset)
        val rightCornerX = centerX + (w * mouthState.widthOffset)

        val leftControlX = (leftCornerX + centerX) / 2f
        val rightControlX = (rightCornerX + centerX) / 2f

        // 3. Анатомическое построение пути из двух половинок губы
        val mouthPath = Path().apply {
            // 1. Левая половинка губы: стартуем из левого уголка, тянем к центру
            moveTo(leftCornerX, finalCornerY)
            quadraticTo(
                x1 = leftControlX, y1 = finalControlY, // Опорная точка
                x2 = centerX, y2 = finalCenterY        // Конечная точка под носом
            )

            // 2. Правая половинка губы: от центра тянем к правому уголку
            quadraticTo(
                x1 = rightControlX, y1 = finalControlY, // Опорная точка
                x2 = rightCornerX, y2 = finalCornerY   // Конечная точка
            )
        }

        // Отрисовываем получившийся эластичный ротик твоим стилем
        drawPath(path = mouthPath, color = outlineColor, style = mouthStrokeStyle)
    }
}