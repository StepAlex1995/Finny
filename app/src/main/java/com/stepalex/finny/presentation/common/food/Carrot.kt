package com.stepalex.finny.presentation.common.food

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.lerp

fun DrawScope.drawCarrot(
    centerX: Float,
    centerY: Float,
    size: Float,
    isFilled: Boolean
) {
    val lerpRatio = 0.8f
    // Цветовая палитра
    val blackStroke = Color(0xFF000000)
    val carrotOrange = lerp(Color(0xFFFF8138), Color.White, if (isFilled) 0f else lerpRatio)
    val carrotShadowOrange =  lerp(Color(0xFFE25B16), Color.White, if (isFilled) 0f else lerpRatio)
    val carrotLightOrange =  lerp(Color(0xFFFF9E66), Color.White, if (isFilled) 0f else lerpRatio)
    val leafGreen =  lerp(Color(0xFF4CB03E), Color.White, if (isFilled) 0f else lerpRatio)
    val leafDarkGreen =  lerp(Color(0xFF38852D), Color.White, if (isFilled) 0f else lerpRatio)

    val strokeWidth = size * 0.04f

    // угол на 45f, чтобы наклонить морковку вправо
    withTransform({
        rotate(degrees = 45f, pivot = Offset(centerX, centerY))
    }) {

        // --- 1. ОТРИСОВКА БОТВЫ (ЗЕЛЕНЫХ ЛИСТЬЕВ) ---
        // Левый маленький листик
        val leftLeafPath = Path().apply {
            moveTo(centerX - size * 0.12f, centerY - size * 0.2f)
            cubicTo(
                centerX - size * 0.35f, centerY - size * 0.4f,
                centerX - size * 0.22f, centerY - size * 0.65f,
                centerX - size * 0.04f, centerY - size * 0.45f
            )
            close()
        }
        drawPath(path = leftLeafPath, color = leafDarkGreen)
        drawPath(
            path = leftLeafPath,
            color = blackStroke,
            style = Stroke(width = strokeWidth, join = StrokeJoin.Round)
        )

        // Правый листик
        val rightLeafPath = Path().apply {
            moveTo(centerX + size * 0.04f, centerY - size * 0.2f)
            cubicTo(
                centerX + size * 0.32f, centerY - size * 0.3f,
                centerX + size * 0.35f, centerY - size * 0.55f,
                centerX + size * 0.12f, centerY - size * 0.45f
            )
            close()
        }
        drawPath(path = rightLeafPath, color = leafDarkGreen)
        drawPath(
            path = rightLeafPath,
            color = blackStroke,
            style = Stroke(width = strokeWidth, join = StrokeJoin.Round)
        )

        // Центральный большой лист
        val mainLeafPath = Path().apply {
            moveTo(centerX - size * 0.14f, centerY - size * 0.22f)
            cubicTo(
                centerX - size * 0.28f, centerY - size * 0.55f,
                centerX + size * 0.18f, centerY - size * 0.70f,
                centerX + size * 0.14f, centerY - size * 0.22f
            )
            close()
        }
        drawPath(path = mainLeafPath, color = leafGreen)
        drawPath(
            path = mainLeafPath,
            color = blackStroke,
            style = Stroke(width = strokeWidth, join = StrokeJoin.Round)
        )

        // Центральная прожилка листа
        val leafLine = Path().apply {
            moveTo(centerX, centerY - size * 0.25f)
            lineTo(centerX - size * 0.03f, centerY - size * 0.48f)
        }
        drawPath(
            path = leafLine,
            color = blackStroke,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )


        // --- 2. ОТРИСОВКА ТЕЛА МОРКОВКИ ---
        val carrotPath = Path().apply {
            // Верхняя скругленная грань
            moveTo(centerX - size * 0.24f, centerY - size * 0.18f)
            quadraticTo(
                centerX, centerY - size * 0.24f,
                centerX + size * 0.24f, centerY - size * 0.18f
            )
            // Правый плавный изгиб к носику
            cubicTo(
                centerX + size * 0.26f, centerY + size * 0.1f,
                centerX + size * 0.16f, centerY + size * 0.42f,
                centerX, centerY + size * 0.58f // Острый, но аккуратный кончик
            )
            // Левый плавный изгиб обратно к верху
            cubicTo(
                centerX - size * 0.16f, centerY + size * 0.42f,
                centerX - size * 0.26f, centerY + size * 0.1f,
                centerX - size * 0.24f, centerY - size * 0.18f
            )
            close()
        }

        // Базовая заливка
        drawPath(path = carrotPath, color = carrotOrange)

        // --- 3. ТЕНЬ И БЛИК ПО МАСКЕ МОРКОВКИ ---
        withTransform({
            clipPath(carrotPath)
        }) {
            // Мягкая полукруглая тень с правой стороны моркови
            drawOval(
                color = carrotShadowOrange,
                topLeft = Offset(centerX, centerY - size * 0.25f),
                size = Size(size * 0.45f, size * 0.9f)
            )
            // Мягкий светлый блик с левой стороны
            drawOval(
                color = carrotLightOrange,
                topLeft = Offset(centerX - size * 0.22f, centerY - size * 0.14f),
                size = Size(size * 0.08f, size * 0.5f)
            )
        }

        // Обводка контура поверх всех внутренних слоев теней
        drawPath(
            path = carrotPath,
            color = blackStroke,
            style = Stroke(width = strokeWidth, join = StrokeJoin.Round)
        )


        // --- 4. ХАРАКТЕРНЫЕ ГОРИЗОНТАЛЬНЫЕ ШТРИХИ ---
        val lineStyle = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        // 1. НАВЕРХУ (Слева, длинный штрих у основания)
        drawLine(
            color = blackStroke,
            start = Offset(centerX - size * 0.22f, centerY - size * 0.04f),
            end = Offset(centerX - size * 0.04f, centerY + size * 0.04f),
            strokeWidth = lineStyle.width,
            cap = lineStyle.cap
        )
        // 2. ВНИЗУ (Справа, средний штрих в нижней половине)
        drawLine(
            color = blackStroke,
            start = Offset(centerX + size * 0.04f, centerY + size * 0.25f),
            end = Offset(centerX + size * 0.14f, centerY + size * 0.21f),
            strokeWidth = lineStyle.width,
            cap = lineStyle.cap
        )
        // 3. НАВЕРХУ (Справа, в зоне тени ближе к верху)
        drawLine(
            color = blackStroke,
            start = Offset(centerX + size * 0.06f, centerY - size * 0.02f),
            end = Offset(centerX + size * 0.21f, centerY - size * 0.05f),
            strokeWidth = lineStyle.width,
            cap = lineStyle.cap
        )
        // 4. ВНИЗУ (Слева, у самого кончика морковки)
        drawLine(
            color = blackStroke,
            start = Offset(centerX - size * 0.10f, centerY + size * 0.40f),
            end = Offset(centerX - size * 0.01f, centerY + size * 0.43f),
            strokeWidth = lineStyle.width,
            cap = lineStyle.cap
        )
    }
}

@Composable
fun Carrot(
    modifier: Modifier,
    isFilled: Boolean
) {
    Canvas(modifier = modifier) {
        drawCarrot(
            size.width * 0.5f,
            size.height * 0.5f,
            size = size.width * 0.85f,
            isFilled = isFilled
        )
    }
}

@Preview
@Composable
fun CarrotNotFilledPreview() {
    Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
        Carrot(modifier = Modifier.fillMaxSize(), isFilled = false)
    }
}
@Preview
@Composable
fun CarrotFilledPreview() {
    Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
        Carrot(modifier = Modifier.fillMaxSize(), isFilled = true)
    }
}