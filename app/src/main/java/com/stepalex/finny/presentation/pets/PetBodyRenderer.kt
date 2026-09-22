package com.stepalex.finny.presentation.pets


import android.graphics.BlurMaskFilter
import android.graphics.Paint
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stepalex.finny.presentation.pet_room.drawFood

/**
 * Главный декомпозированный движок отрисовки силуэта тела и ушей питомца (Кролик / Мишка)
 */
fun DrawScope.drawPetBodyAndEars(
    petType: PetType,
    w: Float,
    h: Float,
    strokeStyle: Stroke,
    bodyTop: Float,
    bodyBottom: Float,
    bumpTop: Float,
    bumpBottom: Float,
    bodyElementsProgress: Float,
    tailLeft: Float,
    tailTop: Float,
    tailRight: Float,
    tailBottom: Float,
    haloAlpha: Float,
    petColor: PetColor
) {
    // 1. БАЗОВАЯ ГОЛОВА (Одинаковая для кролика и мишки под референс)
    val headPath = Path().apply {
        addRoundRect(
            RoundRect(
                left = w * 0.08f, top = h * 0.31f,
                right = w * 0.92f, bottom = h * 0.87f,
                cornerRadius = CornerRadius(w * 0.36f, h * 0.28f)
            )
        )
    }

    // Подготавливаем пути для ушек
    val leftEarPath = Path()
    val rightEarPath = Path()
    val leftInnerEarPath = Path()
    val rightInnerEarPath = Path()

    // Настраиваем цвет внутренностей ушей мишки (Индивидуальный приятный оттенок без розового!)
    val bearInnerEarColor = Color(0xFFC6BCB4)
    val finalInnerEarColor =
        if (petType == PetType.BUNNY) petColor.blushColor else bearInnerEarColor

    when (petType) {
        PetType.BUNNY -> {
            // --- ГЕОМЕТРИЯ КРОЛИКА ---
            leftEarPath.apply {
                addRoundRect(
                    RoundRect(
                        topLeftCornerRadius = CornerRadius(w * 0.12f, h * 0.20f),
                        topRightCornerRadius = CornerRadius(w * 0.125f, h * 0.20f),
                        bottomLeftCornerRadius = CornerRadius(w * 0.125f, h * 0.20f),
                        bottomRightCornerRadius = CornerRadius(w * 0.125f, h * 0.20f),
                        left = w * 0.24f, top = h * 0.05f, right = w * 0.49f, bottom = h * 0.55f
                    )
                )
            }
            leftInnerEarPath.apply {
                addRoundRect(
                    RoundRect(
                        topLeftCornerRadius = CornerRadius(w * 0.20f, h * 0.25f),
                        topRightCornerRadius = CornerRadius(w * 0.20f, h * 0.25f),
                        bottomLeftCornerRadius = CornerRadius(w * 0.075f, h * 0.15f),
                        bottomRightCornerRadius = CornerRadius(w * 0.2f, h * 0.2f),
                        left = w * 0.29f, top = h * 0.11f, right = w * 0.44f, bottom = h * 0.35f
                    )
                )
            }
            val leftPivotX = w * 0.355f
            val leftPivotY = h * 0.45f
            val leftMatrix = Matrix().apply {
                translate(leftPivotX, leftPivotY)
                rotateZ(-8f)
                translate(-leftPivotX, -leftPivotY)
            }
            leftEarPath.transform(leftMatrix)
            leftInnerEarPath.transform(leftMatrix)

            rightEarPath.apply {
                addRoundRect(
                    RoundRect(
                        topLeftCornerRadius = CornerRadius(w * 0.125f, h * 0.20f),
                        topRightCornerRadius = CornerRadius(w * 0.12f, h * 0.20f),
                        bottomLeftCornerRadius = CornerRadius(w * 0.125f, h * 0.20f),
                        bottomRightCornerRadius = CornerRadius(w * 0.125f, h * 0.20f),
                        left = w * 0.51f, top = h * 0.05f, right = w * 0.76f, bottom = h * 0.55f
                    )
                )
            }
            rightInnerEarPath.apply {
                addRoundRect(
                    RoundRect(
                        topLeftCornerRadius = CornerRadius(w * 0.20f, h * 0.25f),
                        topRightCornerRadius = CornerRadius(w * 0.20f, h * 0.25f),
                        bottomLeftCornerRadius = CornerRadius(w * 0.2f, h * 0.2f),
                        bottomRightCornerRadius = CornerRadius(w * 0.075f, h * 0.15f),
                        left = w * 0.56f, top = h * 0.11f, right = w * 0.71f, bottom = h * 0.35f
                    )
                )
            }
            val rightPivotX = w * 0.645f
            val rightPivotY = h * 0.45f
            val rightMatrix = Matrix().apply {
                translate(rightPivotX, rightPivotY)
                rotateZ(8f)
                translate(-rightPivotX, -rightPivotY)
            }
            rightEarPath.transform(rightMatrix)
            rightInnerEarPath.transform(rightMatrix)
        }

        PetType.BEAR -> {
            // --- ГЕОМЕТРИЯ МИШКИ (Круглые кавайные ушки по бокам макушки) ---
            val earRadius = w * 0.14f

            // Левое ушко (Внешнее и внутреннее) через правильный Rect
            leftEarPath.addOval(
                Rect(center = Offset(x = w * 0.24f, y = h * 0.34f), radius = earRadius)
            )
            leftInnerEarPath.addOval(
                Rect(center = Offset(x = w * 0.24f, y = h * 0.34f), radius = earRadius * 0.6f)
            )

            // Правое ушко (Внешнее и внутреннее) через правильный Rect
            rightEarPath.addOval(
                Rect(center = Offset(x = w * 0.76f, y = h * 0.34f), radius = earRadius)
            )
            rightInnerEarPath.addOval(
                Rect(center = Offset(x = w * 0.76f, y = h * 0.34f), radius = earRadius * 0.6f)
            )
        }
    }

    // 2. ОТРИСОВКА ХВОСТИКА (У кролика — твой оригинальный круглый, у мишки — маленький пухлый медвежий)
    if (bodyElementsProgress > 0f) {
        val tailPath = Path().apply {
            if (petType == PetType.BUNNY) {
                addRoundRect(
                    RoundRect(
                        left = w * tailLeft,
                        top = h * tailTop,
                        right = w * tailRight,
                        bottom = h * tailBottom,
                        cornerRadius = CornerRadius(w * 0.065f, h * 0.065f)
                    )
                )
            } /*else {
                // Пухлый медвежий круглый хвостик пониже через Rect
                addOval(
                    Rect(center = Offset(x = w * (tailLeft + 0.02f), y = h * (tailBottom - 0.03f)), radius = w * 0.06f)
                )
            }*/
        }
        drawPath(path = tailPath, color = petColor.fillColor, alpha = bodyElementsProgress)
        drawPath(
            path = tailPath,
            color = petColor.outlineColor,
            style = strokeStyle,
            alpha = bodyElementsProgress
        )
    }

    // 3. СТРОИМ ТУЛОВИЩЕ С УЧЕТОМ АНАТОМИИ
    val bodyMainPath = Path()
    val bottomLeftBump = Path()
    val bottomRightBump = Path()

    if (petType == PetType.BUNNY) {
        // Кроличье вытянутое тельце
        bodyMainPath.addRoundRect(
            RoundRect(
                left = w * 0.15f, top = h * bodyTop, right = w * 0.85f, bottom = h * bodyBottom,
                topLeftCornerRadius = CornerRadius(w * 0.38f, h * 0.32f),
                topRightCornerRadius = CornerRadius(w * 0.38f, h * 0.32f),
                bottomLeftCornerRadius = CornerRadius(w * 0.2f, h * 0.2f),
                bottomRightCornerRadius = CornerRadius(w * 0.2f, h * 0.2f),
            )
        )
        bottomLeftBump.addRoundRect(
            RoundRect(
                left = w * 0.23f,
                top = h * bumpTop,
                right = w * 0.42f,
                bottom = h * bumpBottom,
                cornerRadius = CornerRadius(w * 0.71f, w * 0.71f)
            )
        )
        bottomRightBump.addRoundRect(
            RoundRect(
                left = w * 0.6f,
                top = h * bumpTop,
                right = w * 0.78f,
                bottom = h * bumpBottom,
                cornerRadius = CornerRadius(w * 0.71f, w * 0.71f)
            )
        )
    } else {
        // Медвежьи пухлые ножки-штанины монолитом со скриншота!
        bodyMainPath.addRoundRect(
            RoundRect(
                left = w * 0.12f, top = h * bodyTop, right = w * 0.88f, bottom = h * bodyBottom,
                topLeftCornerRadius = CornerRadius(w * 0.45f, h * 0.35f),
                topRightCornerRadius = CornerRadius(w * 0.45f, h * 0.35f),
                bottomLeftCornerRadius = CornerRadius(w * 0.3f, h * 0.3f),
                bottomRightCornerRadius = CornerRadius(w * 0.3f, h * 0.3f)
            )
        )
        // Нижние ножки-штанины мишки плавно выходят из пузика
        bottomLeftBump.addRoundRect(
            RoundRect(
                left = w * 0.16f,
                top = h * bumpTop,
                right = w * 0.44f,
                bottom = h * bumpBottom,
                cornerRadius = CornerRadius(w * 0.5f, w * 0.5f)
            )
        )
        bottomRightBump.addRoundRect(
            RoundRect(
                left = w * 0.56f,
                top = h * bumpTop,
                right = w * 0.84f,
                bottom = h * bumpBottom,
                cornerRadius = CornerRadius(w * 0.5f, w * 0.5f)
            )
        )
    }

    // 4. СБОРКА МОНОЛИТНОГО СИЛУЭТА ПИТОМЦА
    val fullPetPath = Path().apply {
        op(headPath, leftEarPath, PathOperation.Union)
        op(this, rightEarPath, PathOperation.Union)
        op(this, bodyMainPath, PathOperation.Union)
        op(this, bottomLeftBump, PathOperation.Union)
        op(this, bottomRightBump, PathOperation.Union)
    }
// ================= 5. ОТРИСОВКА СВЕТЯЩЕГОСЯ ОРЕОЛА НАТИВНЫМ БЛЮРОМ =================
    if (haloAlpha > 0f) {
        val glowRadius = w * 0.26f
        val glowColor = Color(0xFFFFEAA7)

        drawContext.canvas.nativeCanvas.save()

        val paint = Paint().apply {
            color = glowColor.toArgb()
            isAntiAlias = true
            alpha = (haloAlpha * 255).toInt().coerceIn(0, 255)
            maskFilter = BlurMaskFilter(glowRadius, BlurMaskFilter.Blur.NORMAL)
        }

        drawContext.canvas.nativeCanvas.drawPath(fullPetPath.asAndroidPath(), paint)
        drawContext.canvas.nativeCanvas.restore()
    }

// ================= 6. ФИНАЛЬНАЯ ЗАЛИВКА ТЕЛА И ЖИРНЫЙ ОБЩИЙ КОНТУР =================
    drawPath(path = fullPetPath, color = petColor.fillColor)
    drawPath(path = fullPetPath, color = petColor.outlineColor, style = strokeStyle)

// ================= 7. СЕРЕДИНКИ УШЕК =================
    drawPath(path = leftInnerEarPath, color = finalInnerEarColor)
    drawPath(path = rightInnerEarPath, color = finalInnerEarColor)

// Мягкий Безье-вырез между штанинами мишки, чтобы подчеркнуть форму со скриншота
    if (petType == PetType.BEAR && bodyElementsProgress > 0f) {
        val groinPath = Path().apply {
            moveTo(w * 0.42f, h * bodyBottom)
            quadraticTo(w * 0.50f, h * (bodyBottom - 0.05f), w * 0.58f, h * bodyBottom)
        }
        drawPath(path = groinPath, color = petColor.outlineColor, style = strokeStyle)
    }

}


/**
 * Отрисовка ручек (Вызывается только для кролика! У мишки они вклеены в монолитный силуэт)
 */
fun DrawScope.drawHands(
    petType: PetType,
    w: Float,
    h: Float,
    strokeStyle: Stroke,
    bodyElementsProgress: Float,
    armTop: Float,
    armBottom: Float,
    petColor: PetColor
) {
    // Если это мишка — выходим, его руки-плечи уже монолитно прорисованы в силуэте!
    if (petType == PetType.BEAR || bodyElementsProgress <= 0f) return

    val leftArmPath = Path().apply {
        arcTo(
            rect = Rect(
                left = w * 0.30f,
                top = h * armTop,
                right = w * 0.45f,
                bottom = h * armBottom
            ),
            startAngleDegrees = -100f,
            sweepAngleDegrees = 200f,
            forceMoveTo = true
        )

        val pivotX = w * 0.35f
        val pivotY = h * 0.91f

        transform(
            Matrix().apply {
                translate(pivotX, pivotY)
                rotateZ(-30f)
                translate(-pivotX, -pivotY)
            }
        )
    }

    val rightArmPath = Path().apply {
        arcTo(
            rect = Rect(
                left = w * 0.55f,
                top = h * armTop,
                right = w * 0.70f,
                bottom = h * armBottom
            ),
            startAngleDegrees = 80f,
            sweepAngleDegrees = 200f,
            forceMoveTo = true
        )

        val pivotX = w * 0.65f
        val pivotY = h * 0.91f

        transform(
            Matrix().apply {
                translate(pivotX, pivotY)
                rotateZ(30f)
                translate(-pivotX, -pivotY)
            }
        )
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

/**
 * Универсальная отрисовка круглых лапок-подушечек малыша (Baby Stage)
 */
fun DrawScope.drawPetFeet(
    w: Float,
    h: Float,
    strokeStyle: Stroke,
    progress: Float,
    petColor: PetColor
) {
    val leftFootCenter = Offset(w * 0.24f, h * 0.84f)
    val rightFootCenter = Offset(w * 0.76f, h * 0.84f)
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

@Composable
fun Silhouette(
    modifier: Modifier,
    petType: PetType,
    stage: PetStage,
    petColor: PetColor
) {
    val bodyTop = if (petType == PetType.BUNNY) {
        when (stage) {
            PetStage.Baby -> 0.87f
            PetStage.Teenager -> 0.50f
            PetStage.Adult -> 0.70f
        }
    } else {
        when (stage) {
            PetStage.Baby -> 0.87f
            PetStage.Teenager -> 0.54f
            PetStage.Adult -> 0.56f
        }
    }
    val bodyBottom = if (petType == PetType.BUNNY) {
        when (stage) {
            PetStage.Baby -> 0.87f
            PetStage.Teenager -> 0.99f
            PetStage.Adult -> 1.19f
        }
    } else {
        when (stage) {
            PetStage.Baby -> 0.87f
            PetStage.Teenager -> 0.94f
            PetStage.Adult -> 1.15f
        }
    }
    val bumpBottom = when (stage) {
        PetStage.Baby -> 0.87f
        PetStage.Teenager -> 1.04f
        PetStage.Adult -> 1.24f
    }
    val bumpTop = when (stage) {
        PetStage.Baby -> 0.87f
        PetStage.Teenager -> 0.65f
        PetStage.Adult -> 1.05f
    }
    val bodyElementsProgress = if (stage == PetStage.Baby) 0f else 1f

    val tailLeft = if (stage == PetStage.Adult) 0.80f else 0.76f
    val tailTop = if (stage == PetStage.Adult) 0.93f else 0.80f
    val tailRight = if (stage == PetStage.Adult) 0.94f else 0.90f
    val tailBottom = if (stage == PetStage.Adult) 1.07f else 0.94f


    Canvas(modifier = modifier) {
        drawPetBodyAndEars(
            petType = petType,
            w = size.width,
            h = size.height,
            strokeStyle = Stroke(width = 1f),
            bodyTop = bodyTop,
            bodyBottom = bodyBottom,
            bumpTop = bumpTop,
            bumpBottom = bumpBottom,
            bodyElementsProgress = bodyElementsProgress,
            tailLeft = tailLeft,
            tailTop = tailTop,
            tailRight = tailRight,
            tailBottom = tailBottom,
            haloAlpha = 0f,
            petColor = petColor
        )
    }
}

@Preview
@Composable
fun SilhouettePreview() {
    Box(modifier = Modifier.size(200.dp)) {
        Silhouette(
            modifier = Modifier.fillMaxSize(),
            petType = PetType.BUNNY,
            stage = PetStage.Baby,
            petColor = PetColor.Indigo,
        )
    }
}