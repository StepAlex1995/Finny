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

fun DrawScope.drawApple(
    centerX: Float,
    centerY: Float,
    size: Float,
    isFilled: Boolean
) {
    val lerpRatio = 0.8f

    // Палитра цветов
    val blackStroke = Color(0xFF231F20)
    val stemColor = lerp(Color(0xFF8B5A42), Color.White, if (isFilled) 0f else lerpRatio)
    val leafColor = lerp(Color(0xFF5CB815), Color.White, if (isFilled) 0f else lerpRatio)
    val leafVeinColor = lerp(Color(0xFF3E8209), Color.White, if (isFilled) 0f else lerpRatio)

    val appleRed = lerp(Color(0xFFE50012), Color.White, if (isFilled) 0f else lerpRatio)
    val appleShadow = lerp(Color(0xFFB0000F), Color.White, if (isFilled) 0f else lerpRatio)
    val appleHighlight = lerp(Color(0xFFFFB3B8), Color.White, if (isFilled) 0f else lerpRatio)

    val strokeWidth = size * 0.045f
    val strokeStyle = Stroke(width = strokeWidth, join = StrokeJoin.Round, cap = StrokeCap.Round)

    // ==========================================
    // СЛОЙ 1: ВЕТОЧКА
    // ==========================================
    val stemPath = Path().apply {
        moveTo(centerX, centerY - size * 0.15f)
        cubicTo(
            centerX - size * 0.06f, centerY - size * 0.28f,
            centerX - size * 0.14f, centerY - size * 0.38f,
            centerX - size * 0.20f, centerY - size * 0.42f // Кончик выше
        )
        cubicTo(
            centerX - size * 0.14f, centerY - size * 0.35f,
            centerX - size * 0.04f, centerY - size * 0.25f,
            centerX + size * 0.02f, centerY - size * 0.15f
        )
        close()
    }
    drawPath(path = stemPath, color = stemColor)
    drawPath(path = stemPath, color = blackStroke, style = strokeStyle)

    // ==========================================
    // СЛОЙ 2: ТЕЛО ЯБЛОКА
    // ==========================================
    val applePath = Path().apply {
        moveTo(centerX, centerY - size * 0.15f)

        // Левое плечо
        cubicTo(
            centerX - size * 0.15f, centerY - size * 0.25f,
            centerX - size * 0.38f, centerY - size * 0.22f,
            centerX - size * 0.38f, centerY + size * 0.04f
        )
        // Левый бок и плавное вытягивание вниз
        cubicTo(
            centerX - size * 0.38f, centerY + size * 0.28f,
            centerX - size * 0.22f, centerY + size * 0.45f,
            centerX - size * 0.06f, centerY + size * 0.44f
        )
        // Нижняя ложбинка
        lineTo(centerX + size * 0.06f, centerY + size * 0.44f)

        // Правый бок
        cubicTo(
            centerX + size * 0.22f, centerY + size * 0.45f,
            centerX + size * 0.38f, centerY + size * 0.28f,
            centerX + size * 0.38f, centerY + size * 0.04f
        )
        // Правое плечо возвращается к выемке
        cubicTo(
            centerX + size * 0.38f, centerY - size * 0.22f,
            centerX + size * 0.15f, centerY - size * 0.25f,
            centerX, centerY - size * 0.15f
        )
        close()
    }

    // Базовая заливка
    drawPath(path = applePath, color = appleRed)

    // ТЕНЬ ВНУТРИ ЯБЛОКА
    withTransform({ clipPath(applePath) }) {
        drawOval(
            color = appleShadow,
            topLeft = Offset(centerX - size * 0.40f, centerY + size * 0.08f),
            size = Size(size * 0.80f, size * 0.42f)
        )
    }

    // Обводка контура яблока
    drawPath(path = applePath, color = blackStroke, style = strokeStyle)

    // ==========================================
    // СЛОЙ 3: ДЕТАЛИ, БЛИК И КРУПНЫЙ ЛИСТ
    // ==========================================
    // Верхняя затемняющая складочка-выемка
    val topCrease = Path().apply {
        moveTo(centerX - size * 0.12f, centerY - size * 0.17f)
        quadraticTo(centerX, centerY - size * 0.09f, centerX + size * 0.14f, centerY - size * 0.16f)
    }
    drawPath(path = topCrease, color = blackStroke, style = strokeStyle)

    // Вертикальный овальный блик
    withTransform({
        rotate(degrees = -10f, pivot = Offset(centerX - size * 0.22f, centerY - size * 0.02f))
    }) {
        drawOval(
            color = appleHighlight,
            topLeft = Offset(centerX - size * 0.26f, centerY - size * 0.12f),
            size = Size(size * 0.11f, size * 0.22f)
        )
    }

    // ИСПРАВЛЕННЫЙ ЛИСТИК
    val leafAttachPoint = Offset(centerX - size * 0.03f, centerY - size * 0.22f)
    val leafPath = Path().apply {
        moveTo(leafAttachPoint.x, leafAttachPoint.y)
        // Верхняя половинка листа
        cubicTo(
            leafAttachPoint.x + size * 0.02f, leafAttachPoint.y - size * 0.18f,
            leafAttachPoint.x + size * 0.18f, leafAttachPoint.y - size * 0.24f,
            leafAttachPoint.x + size * 0.36f, leafAttachPoint.y - size * 0.14f // Кончик листа
        )
        // Нижняя половинка листа
        cubicTo(
            leafAttachPoint.x + size * 0.24f, leafAttachPoint.y - size * 0.02f,
            leafAttachPoint.x + size * 0.08f, leafAttachPoint.y - size * 0.04f,
            leafAttachPoint.x, leafAttachPoint.y
        )
        close()
    }
    drawPath(path = leafPath, color = leafColor)
    drawPath(path = leafPath, color = blackStroke, style = strokeStyle)
}

@Composable
fun Apple(
    modifier: Modifier,
    isFilled: Boolean
) {
    Canvas(modifier = modifier) {
        drawApple(
            centerX = size.width * 0.5f,
            centerY = size.height * 0.5f,
            size = size.width * 0.85f,
            isFilled = isFilled
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ApplePreview() {
    Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
        Apple(modifier = Modifier.fillMaxSize(), isFilled = true)
    }
}
