package com.stepalex.finny.presentation.common.food

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
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

fun DrawScope.drawGrapes(
    centerX: Float,
    centerY: Float,
    size: Float,
    isFilled: Boolean
) {
    val lerpRatio = 0.8f

    val blackStroke = Color(0xFF231F20) // Жирный темный контур
    val stemColor = lerp(Color(0xFF8B5A42), Color.White, if (isFilled) 0f else lerpRatio)
    val leafColor = lerp(Color(0xFF76D235), Color.White, if (isFilled) 0f else lerpRatio)

    // Ягоды переднего плана (яркие)
    val grapeBright = lerp(Color(0xFF9326FF), Color.White, if (isFilled) 0f else lerpRatio)
    // Ягоды заднего плана и тени (более темные/насыщенные)
    val grapeDark = lerp(Color(0xFF7A1FA2), Color.White, if (isFilled) 0f else lerpRatio)
    val grapeHighlight = lerp(Color(0xFFBC7BFF), Color.White, if (isFilled) 0f else lerpRatio)

    val strokeWidth = size * 0.045f // Жирный мультяшный контур
    val strokeStyle = Stroke(width = strokeWidth, join = StrokeJoin.Round, cap = StrokeCap.Round)

    val r = size * 0.21f // Базовый радиус одной ягоды


    // ==========================================
    // СЛОЙ 1: ВЕТОЧКА И ЛИСТЬЯ
    // ==========================================
    // Веточка
    val stemPath = Path().apply {
        moveTo(centerX - size * 0.04f, centerY - size * 0.18f)
        cubicTo(
            centerX - size * 0.12f, centerY - size * 0.42f,
            centerX + size * 0.06f, centerY - size * 0.48f,
            centerX + size * 0.03f, centerY - size * 0.48f
        )
        cubicTo(
            centerX + size * 0.12f, centerY - size * 0.42f,
            centerX + size * 0.02f, centerY - size * 0.18f,
            centerX + size * 0.02f, centerY - size * 0.18f
        )
        close()
    }
    drawPath(path = stemPath, color = stemColor)
    drawPath(path = stemPath, color = blackStroke, style = strokeStyle)

    // Левый листик
    val leftLeaf = Path().apply {
        moveTo(centerX, centerY - size * 0.16f)
        cubicTo(
            centerX - size * 0.22f, centerY - size * 0.18f,
            centerX - size * 0.32f, centerY - size * 0.38f,
            centerX - size * 0.12f, centerY - size * 0.38f
        )
        cubicTo(
            centerX - size * 0.04f, centerY - size * 0.38f,
            centerX - size * 0.02f, centerY - size * 0.24f,
            centerX, centerY - size * 0.16f
        )
        close()
    }
    drawPath(path = leftLeaf, color = leafColor)
    drawPath(path = leftLeaf, color = blackStroke, style = strokeStyle)

    // Правый листик
    val rightLeaf = Path().apply {
        moveTo(centerX, centerY - size * 0.16f)
        cubicTo(
            centerX + size * 0.22f, centerY - size * 0.18f,
            centerX + size * 0.32f, centerY - size * 0.38f,
            centerX + size * 0.12f, centerY - size * 0.38f
        )
        cubicTo(
            centerX + size * 0.04f, centerY - size * 0.38f,
            centerX + size * 0.02f, centerY - size * 0.24f,
            centerX, centerY - size * 0.16f
        )
        close()
    }
    drawPath(path = rightLeaf, color = leafColor)
    drawPath(path = rightLeaf, color = blackStroke, style = strokeStyle)


    // --- 2. ЗАДНИЙ СЛОЙ ЯГОД (ТЕМНЫЕ) ---
    // Нижняя ягода (самый низ)
    val bottomG = Offset(centerX, centerY + size * 0.32f)
    drawCircle(color = grapeDark, radius = r, center = bottomG)
    drawCircle(color = blackStroke, radius = r, center = bottomG, style = strokeStyle)

    // Средний ряд - левая задняя
    val midLeftG = Offset(centerX - size * 0.18f, centerY + size * 0.12f)
    drawCircle(color = grapeDark, radius = r, center = midLeftG)
    drawCircle(color = blackStroke, radius = r, center = midLeftG, style = strokeStyle)

    // Средний ряд - правая задняя
    val midRightG = Offset(centerX + size * 0.18f, centerY + size * 0.12f)
    drawCircle(color = grapeDark, radius = r, center = midRightG)
    drawCircle(color = blackStroke, radius = r, center = midRightG, style = strokeStyle)


    // --- 3. ПЕРЕДНИЙ СЛОЙ ЯГОД (ЯРКИЕ С БЛИКАМИ) ---
    // Левая верхняя ягода
    val topLeftG = Offset(centerX - size * 0.22f, centerY - size * 0.05f)
    drawCircle(color = grapeBright, radius = r, center = topLeftG)
    drawCircle(color = blackStroke, radius = r, center = topLeftG, style = strokeStyle)

    // Правая верхняя ягода
    val topRightG = Offset(centerX + size * 0.22f, centerY - size * 0.05f)
    drawCircle(color = grapeBright, radius = r, center = topRightG)
    drawCircle(color = blackStroke, radius = r, center = topRightG, style = strokeStyle)

    // Центральная ягода (перекрывает верхние боковые)
    val centerG = Offset(centerX, centerY - size * 0.01f)
    drawCircle(color = grapeBright, radius = r, center = centerG)
    drawCircle(color = blackStroke, radius = r, center = centerG, style = strokeStyle)


    // --- 4. ВЕКТОРНЫЕ БЛИКИ (Отрисовываются поверх передних ягод) ---
    // Блик на левой верхней ягоде
    drawArc(
        color = grapeHighlight,
        startAngle = 180f,
        sweepAngle = 90f,
        useCenter = false,
        topLeft = Offset(topLeftG.x - r * 0.65f, topLeftG.y - r * 0.65f),
        size = Size(r * 0.8f, r * 0.8f),
        style = Stroke(width = strokeWidth * 0.8f, cap = StrokeCap.Round)
    )

    // Блик на центральной ягоде
    drawArc(
        color = grapeHighlight,
        startAngle = 180f,
        sweepAngle = 90f,
        useCenter = false,
        topLeft = Offset(centerG.x - r * 0.65f, centerG.y - r * 0.65f),
        size = Size(r * 0.8f, r * 0.8f),
        style = Stroke(width = strokeWidth * 0.8f, cap = StrokeCap.Round)
    )
}

@Composable
fun Grapes(
    modifier: Modifier,
    isFilled: Boolean
) {
    Canvas(modifier = modifier) {
        drawGrapes(
            centerX = size.width * 0.5f,
            centerY = size.height * 0.5f,
            size = size.width * 0.8f,
            isFilled = isFilled
        )
    }
}
@Preview(showBackground = true)
@Composable
fun GrapesFilledPreview() {
    Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
        Grapes(modifier = Modifier.fillMaxSize(), isFilled = true)
    }
}

@Preview(showBackground = true)
@Composable
fun GrapesNotFilledPreview() {
    Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
        Grapes(modifier = Modifier.fillMaxSize(), isFilled = false)
    }
}