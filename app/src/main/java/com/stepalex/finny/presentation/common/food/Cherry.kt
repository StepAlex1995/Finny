package com.stepalex.finny.presentation.common.food

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

fun DrawScope.drawCherry(
    centerX: Float,
    centerY: Float,
    size: Float,
    isFilled: Boolean
) {
    val lerpRatio = 0.8f

    val blackStroke = Color(0xFF231F20) // Жирный темный контур
    val leafColor = lerp(Color(0xFF68BC24), Color.White, if (isFilled) 0f else lerpRatio)
    val cherryRed = lerp(Color(0xFFE50012), Color.White, if (isFilled) 0f else lerpRatio)
    val cherryShadow = lerp(Color(0xFFB0000F), Color.White, if (isFilled) 0f else lerpRatio)
    val cherryWhite = Color.White // Блики всегда белые

    val strokeWidth = size * 0.045f
    val strokeStyle = Stroke(width = strokeWidth, join = StrokeJoin.Round, cap = StrokeCap.Round)

    val r = size * 0.23f // Радиус вишенки

    // Точки центров ягод и узла веточек
    val leftCherry = Offset(centerX - size * 0.24f, centerY + size * 0.16f)
    val rightCherry = Offset(centerX + size * 0.24f, centerY + size * 0.20f)
    val jointPoint = Offset(centerX - size * 0.05f, centerY - size * 0.20f)

    // ==========================================
    // СЛОЙ 1: ВЕТОЧКИ (СТЕБЛИ)
    // ==========================================

    // Левая веточка
    val leftStem = Path().apply {
        moveTo(jointPoint.x, jointPoint.y)
        cubicTo(
            centerX - size * 0.05f, centerY - size * 0.05f,
            leftCherry.x + size * 0.12f, leftCherry.y - size * 0.18f,
            leftCherry.x + size * 0.06f, leftCherry.y - size * 0.04f
        )
    }
    drawPath(path = leftStem, color = blackStroke, style = strokeStyle)

    // Правая веточка
    val rightStem = Path().apply {
        moveTo(jointPoint.x, jointPoint.y)
        cubicTo(
            centerX + size * 0.20f, centerY - size * 0.18f,
            rightCherry.x + size * 0.02f, rightCherry.y - size * 0.24f,
            rightCherry.x - size * 0.05f, rightCherry.y - size * 0.06f
        )
    }
    drawPath(path = rightStem, color = blackStroke, style = strokeStyle)


    // ==========================================
    // СЛОЙ 2: ЛИСТЬЯ
    // ==========================================
    // Левый лист
    val leftLeaf = Path().apply {
        moveTo(jointPoint.x, jointPoint.y)
        cubicTo(
            jointPoint.x - size * 0.18f, jointPoint.y + size * 0.05f,
            jointPoint.x - size * 0.26f, jointPoint.y - size * 0.12f,
            jointPoint.x - size * 0.16f, jointPoint.y - size * 0.16f
        )
        cubicTo(
            jointPoint.x - size * 0.08f, jointPoint.y - size * 0.18f,
            jointPoint.x - size * 0.02f, jointPoint.y - size * 0.06f,
            jointPoint.x, jointPoint.y
        )
        close()
    }
    drawPath(path = leftLeaf, color = leafColor)
    drawPath(path = leftLeaf, color = blackStroke, style = strokeStyle)

    // Правый лист
    val rightLeaf = Path().apply {
        moveTo(jointPoint.x, jointPoint.y)
        cubicTo(
            jointPoint.x - size * 0.02f, jointPoint.y - size * 0.22f,
            jointPoint.x + size * 0.12f, jointPoint.y - size * 0.32f,
            jointPoint.x + size * 0.15f, jointPoint.y - size * 0.20f
        )
        cubicTo(
            jointPoint.x + size * 0.16f, jointPoint.y - size * 0.10f,
            jointPoint.x + size * 0.06f, jointPoint.y - size * 0.06f,
            jointPoint.x, jointPoint.y
        )
        close()
    }
    drawPath(path = rightLeaf, color = leafColor)
    drawPath(path = rightLeaf, color = blackStroke, style = strokeStyle)


    // ==========================================
    // СЛОЙ 3: ЛЕВАЯ ВИШНЯ (Основа, Тень, Контур)
    // ==========================================

    val leftPath = Path().apply { addOval(Rect(leftCherry, r)) }
    drawCircle(color = cherryRed, radius = r, center = leftCherry)

    // Полукруглая правая тень по маске ягоды
    withTransform({ clipPath(leftPath) }) {
        drawOval(
            color = cherryShadow,
            topLeft = Offset(leftCherry.x - r * 0.25f, leftCherry.y - r * 0.95f),
            size = Size(r * 1.3f, r * 1.9f)
        )
    }
    drawCircle(color = blackStroke, radius = r, center = leftCherry, style = strokeStyle)


    // ==========================================
    // СЛОЙ 4: ПРАВАЯ ВИШНЯ (Основа, Тень, Контур)
    // ==========================================

    val rightPath = Path().apply { addOval(Rect(rightCherry, r)) }
    drawCircle(color = cherryRed, radius = r, center = rightCherry)

    // Полукруглая правая тень по маске ягоды
    withTransform({ clipPath(rightPath) }) {
        drawOval(
            color = cherryShadow,
            topLeft = Offset(rightCherry.x - r * 0.25f, rightCherry.y - r * 0.95f),
            size = Size(r * 1.3f, r * 1.9f)
        )
    }
    drawCircle(color = blackStroke, radius = r, center = rightCherry, style = strokeStyle)


    // ==========================================
    // СЛОЙ 5: ДЕТАЛИЗАЦИЯ И БЛИКИ (Поверх контуров)
    // ==========================================

    // Маленькие штрихи-впадинки в месте крепления веточек
    drawArc(
        color = blackStroke,
        startAngle = 10f,
        sweepAngle = 100f,
        useCenter = false,
        topLeft = Offset(leftCherry.x + size * 0.02f, leftCherry.y - r * 0.85f),
        size = Size(r * 0.5f, r * 0.5f),
        style = strokeStyle
    )
    drawArc(
        color = blackStroke,
        startAngle = 60f,
        sweepAngle = 110f,
        useCenter = false,
        topLeft = Offset(rightCherry.x - r * 0.6f, rightCherry.y - r * 0.85f),
        size = Size(r * 0.5f, r * 0.5f),
        style = strokeStyle
    )

    // Белый изогнутый блик на левой вишне
    drawArc(
        color = cherryWhite,
        startAngle = 130f,
        sweepAngle = 90f,
        useCenter = false,
        topLeft = Offset(leftCherry.x - r * 0.75f, leftCherry.y - r * 0.75f),
        size = Size(r * 1.5f, r * 1.5f),
        style = Stroke(width = strokeWidth * 1.1f, cap = StrokeCap.Round)
    )

    // Белый изогнутый блик на правой вишне
    drawArc(
        color = cherryWhite,
        startAngle = 130f,
        sweepAngle = 90f,
        useCenter = false,
        topLeft = Offset(rightCherry.x - r * 0.75f, rightCherry.y - r * 0.75f),
        size = Size(r * 1.5f, r * 1.5f),
        style = Stroke(width = strokeWidth * 1.1f, cap = StrokeCap.Round)
    )
}

// Вспомогательная функция расширения для создания Rect из центра и радиуса
private fun Rect(center: Offset, radius: Float): androidx.compose.ui.geometry.Rect {
    return androidx.compose.ui.geometry.Rect(
        center.x - radius,
        center.y - radius,
        center.x + radius,
        center.y + radius
    )
}

@Composable
fun Cherry(
    modifier: Modifier,
    isFilled: Boolean
) {
    Canvas(modifier = modifier) {
        drawCherry(
            centerX = size.width * 0.5f,
            centerY = size.height * 0.45f,
            size = size.width * 0.8f,
            isFilled = isFilled
        )
    }
}
@Preview(showBackground = true)
@Composable
fun CherryPreview() {
    Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
        Cherry(modifier = Modifier.fillMaxSize(), isFilled = true)
    }
}