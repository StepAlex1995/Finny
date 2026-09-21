package com.stepalex.finny.presentation.pet_room

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

enum class MoodIndicator {
    HAPPY,
    NORMAL,
    SAD
}

fun DrawScope.drawMood(
    centerX: Float,
    centerY: Float,
    size: Float,
    centerColor: Color,
    edgeColor: Color,
    mouthProgress: Float
) {
    val radius = size / 2f
    val baseStrokeWidth = size * 0.04f // Обводка
    val strokeColor = Color(0xFF232528)

    // 1. ВНУТРЕННЯЯ ЗАЛИВКА (Радиальный градиент для объема)
    if (centerColor != Color.Transparent && edgeColor != Color.Transparent) {
        val radialGradient = Brush.radialGradient(
            colors = listOf(centerColor, edgeColor),
            center = Offset(centerX - radius * 0.2f, centerY - radius * 0.3f), // Смещаем центр света чуть вверх и влево
            radius = radius * 1.2f
        )
        drawCircle(
            brush = radialGradient,
            radius = radius - baseStrokeWidth / 2f,
            center = Offset(centerX, centerY)
        )

        // 2. ГЛЯНЦЕВЫЕ БЛИКИ (Рисуются только при включенной заливке)
        // Большой верхний полукруглый блик
        val highlightWhite = Color.White.copy(alpha = 0.45f)
        val highlightGradient = Brush.verticalGradient(
            colors = listOf(highlightWhite, Color.Transparent),
            startY = centerY - radius + baseStrokeWidth,
            endY = centerY - radius * 0.1f
        )

        drawOval(
            brush = highlightGradient,
            topLeft = Offset(centerX - radius * 0.65f, centerY - radius * 0.85f),
            size = Size(radius * 1.3f, radius * 0.75f)
        )

        // Маленький нижний рефлекс (свет, отраженный от пола/поверхности)
        val reflexWhite = Color.White.copy(alpha = 0.2f)
        drawOval(
            color = reflexWhite,
            topLeft = Offset(centerX - radius * 0.4f, centerY + radius * 0.55f),
            size = Size(radius * 0.8f, radius * 0.2f)
        )
    }

    // 2. Внешний контур лица
    drawCircle(
        color = strokeColor,
        radius = radius - baseStrokeWidth / 2f,
        center = Offset(centerX, centerY),
        style = Stroke(width = baseStrokeWidth)
    )
    // Размеры и позиции глаз
    val eyeRadius = size * 0.05f
    val eyeOffsetY = size * 0.12f
    val eyeOffsetX = size * 0.16f
    // 3. Левый глаз
    drawCircle(
        color = strokeColor,
        radius = eyeRadius,
        center = Offset(centerX - eyeOffsetX, centerY - eyeOffsetY)
    )
    // 4. ГЛАЗА
    // Левый глаз
    val leftEyeCenter = Offset(centerX - eyeOffsetX, centerY - eyeOffsetY)
    drawCircle(color = strokeColor, radius = eyeRadius, center = leftEyeCenter)
    if (centerColor != Color.Transparent) {
        drawCircle(color = Color.White, radius = eyeRadius * 0.3f, center = Offset(leftEyeCenter.x - eyeRadius * 0.3f, leftEyeCenter.y - eyeRadius * 0.3f))
    }
    // Правый глаз
    val rightEyeCenter = Offset(centerX + eyeOffsetX, centerY - eyeOffsetY)
    drawCircle(color = strokeColor, radius = eyeRadius, center = rightEyeCenter)
    if (centerColor != Color.Transparent) {
        drawCircle(color = Color.White, radius = eyeRadius * 0.3f, center = Offset(rightEyeCenter.x - eyeRadius * 0.3f, rightEyeCenter.y - eyeRadius * 0.3f))
    }

    // 5. Анимированная отрисовка рта через кривую Безье
    val mouthY = centerY + size * 0.12f     // Базовая линия рта по высоте
    val mouthWidth = size * 0.18f           // Ширина рта от центра в одну сторону
    val maxMouthDrop = size * 0.14f          // Максимальный изгиб улыбки вниз/вверх

    val mouthPath = Path().apply {
        // Начинаем с левого уголка рта
        moveTo(centerX - mouthWidth, mouthY)

        // Считаем динамическую точку прогиба на основе прогресса анимации
        // Если mouthProgress == 0f (NORMAL), то контрольная точка лежит прямо на линии рта -> прямая полоса
        val controlY = mouthY + (maxMouthDrop * mouthProgress)
        quadraticTo(
            x1 = centerX,
            y1 = controlY,
            x2 = centerX + mouthWidth,
            y2 = mouthY
        )
    }
    drawPath(
        path = mouthPath,
        color = strokeColor,
        style = Stroke(
            width = baseStrokeWidth,
            cap = StrokeCap.Round // Идеально круглые концы у линии рта
        )
    )
}

@Composable
fun MoodSmile(
    modifier: Modifier,
    isFilled: Boolean,
    moodIndicator: MoodIndicator
) {
    // Анимируем изгиб рта: 1f для HAPPY, 0f для NORMAL, -1f для SAD
    val targetMouthCurve = when (moodIndicator) {
        MoodIndicator.HAPPY -> 1f
        MoodIndicator.NORMAL -> 0f
        MoodIndicator.SAD -> -1f
    }
    val mouthCurveAnim = remember { Animatable(targetMouthCurve) }
    // Запускаем анимацию при изменении состояния настроения
    LaunchedEffect(moodIndicator) {
        mouthCurveAnim.animateTo(
            targetValue = targetMouthCurve,
            animationSpec = tween(durationMillis = 400) // Длительность перехода
        )
    }

    // Палитра градиентов: анимируем базовый (центральный) цвет
    val targetCenterColor = when (moodIndicator) {
        MoodIndicator.HAPPY -> Color(0xFFADFF2F)
        MoodIndicator.NORMAL -> Color(0xFFFFF01F) // Кислотно-желтый (Neon Yellow)
        MoodIndicator.SAD -> Color(0xFFFF3366)
    }

    // Анимируем темный край градиента
    val targetEdgeColor = when (moodIndicator) {
        MoodIndicator.HAPPY -> Color(0xFF42E61A)
        MoodIndicator.NORMAL -> Color(0xFFFF9100) // Сочный неоновый оранжевый
        MoodIndicator.SAD -> Color(0xFFD50000)
    }

    val centerColor by animateColorAsState(
        targetValue = if (isFilled) targetCenterColor else Color.Transparent,
        animationSpec = tween(durationMillis = 400)
    )
    val edgeColor by animateColorAsState(
        targetValue = if (isFilled) targetEdgeColor else Color.Transparent,
        animationSpec = tween(durationMillis = 400)
    )

    Canvas(modifier = modifier) {
        drawMood(
            centerX = size.width / 2f,
            centerY = size.height / 2f,
            size = size.minDimension,
            centerColor = centerColor,
            edgeColor = edgeColor,
            mouthProgress = mouthCurveAnim.value
        )
    }
}

@Preview
@Composable
fun Mood1Preview() {
    Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
        MoodSmile(
            modifier = Modifier.fillMaxSize(),
            isFilled = true,
            moodIndicator = MoodIndicator.HAPPY
        )
    }
}

@Preview
@Composable
fun Mood2Preview() {
    Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
        MoodSmile(
            modifier = Modifier.fillMaxSize(),
            isFilled = true,
            moodIndicator = MoodIndicator.NORMAL
        )
    }
}

@Preview
@Composable
fun Mood3Preview() {
    Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
        MoodSmile(modifier = Modifier.fillMaxSize(), isFilled = true, moodIndicator = MoodIndicator.SAD)
    }
}