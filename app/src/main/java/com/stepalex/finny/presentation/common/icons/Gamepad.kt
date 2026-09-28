package com.stepalex.finny.presentation.common.icons

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

fun DrawScope.drawGamepad(
    centerX: Float,
    centerY: Float,
    size: Float
) {
    // Палитра цветов по вашему скриншоту
    val darkStroke = Color(0xFF1E202A)       // Жирный темно-синий контур
    val bodyBg = Color(0xFFFFFFFF)           // Белый корпус
    val bodyShadow = Color(0xFFECEFF1)       // Внутренний серый объемный кант
    val darkButton = Color(0xFF2D3142)       // Темный цвет крестовины и стиков

    // Цвета экшен-кнопок справа
    val btnYellow = Color(0xFFFFD166)
    val btnRed = Color(0xFFEF476F)
    val btnGreen = Color(0xFF06D6A0)
    val btnBlue = Color(0xFF118AB2)

    val strokeWidth = size * 0.045f
    val strokeStyle = Stroke(width = strokeWidth, join = StrokeJoin.Round, cap = StrokeCap.Round)
    val strokeStyleBroke = Stroke(width = strokeWidth*0.5f, join = StrokeJoin.Round, cap = StrokeCap.Round)

    // ==========================================
    // СЛОЙ 1: ОСНОВНОЙ КОРПУС ГЕЙМПАДА
    // ==========================================
    val bodyPath = Path().apply {
        // Начало в верхней левой точке корпуса
        moveTo(centerX - size * 0.22f, centerY - size * 0.32f)

        // Верхняя выемка (между рогами)
        cubicTo(
            centerX - size * 0.10f, centerY - size * 0.26f,
            centerX + size * 0.10f, centerY - size * 0.26f,
            centerX + size * 0.22f, centerY - size * 0.32f
        )
        // Верхний правый изгиб к рукоятке
        cubicTo(
            centerX + size * 0.42f, centerY - size * 0.28f,
            centerX + size * 0.46f, centerY - size * 0.05f,
            centerX + size * 0.46f, centerY + size * 0.15f
        )
        // Нижняя правая закругленная рукоятка (рог)
        cubicTo(
            centerX + size * 0.46f, centerY + size * 0.45f,
            centerX + size * 0.28f, centerY + size * 0.45f,
            centerX + size * 0.26f, centerY + size * 0.25f
        )
        // Нижняя центральная арка-вырез (между рукоятками)
        cubicTo(
            centerX + size * 0.18f, centerY + size * 0.08f,
            centerX - size * 0.18f, centerY + size * 0.08f,
            centerX - size * 0.26f, centerY + size * 0.25f
        )
        // Нижняя левая закругленная рукоятка (рог)
        cubicTo(
            centerX - size * 0.28f, centerY + size * 0.45f,
            centerX - size * 0.46f, centerY + size * 0.45f,
            centerX - size * 0.46f, centerY + size * 0.15f
        )
        // Левый изгиб обратно к верхней части
        cubicTo(
            centerX - size * 0.46f, centerY - size * 0.05f,
            centerX - size * 0.42f, centerY - size * 0.28f,
            centerX - size * 0.22f, centerY - size * 0.32f
        )
        close()
    }

    // Рисуем задний серый объемный кант за счет смещения и утолщения линии
    drawPath(
        path = bodyPath,
        color = darkStroke,
        style = Stroke(width = strokeWidth + size * 0.03f, join = StrokeJoin.Round)
    )
    drawPath(path = bodyPath, color = bodyShadow)

    // Рисуем сам белый корпус поверх
    withTransform({
        scale(scaleX = 0.96f, scaleY = 0.96f, pivot = Offset(centerX, centerY))
    }) {
        drawPath(path = bodyPath, color = bodyBg)
    }

    // Основной контур геймпада
    drawPath(path = bodyPath, color = Color.LightGray, style = strokeStyleBroke)

    // ==========================================
    // СЛОЙ 2: ЛЕВАЯ КРЕСТОВИНА (D-PAD)
    // ==========================================
    val dPadX = centerX - size * 0.22f
    val dPadY = centerY - size * 0.10f
    val dPadSize = size * 0.18f
    val dPadThickness = size * 0.06f

    val dPadPath = Path().apply {
        // Горизонтальная перекладина креста
        moveTo(dPadX - dPadSize / 2, dPadY - dPadThickness / 2)
        lineTo(dPadX + dPadSize / 2, dPadY - dPadThickness / 2)
        lineTo(dPadX + dPadSize / 2, dPadY + dPadThickness / 2)
        lineTo(dPadX - dPadSize / 2, dPadY + dPadThickness / 2)
        close()
        // Вертикальная перекладина креста
        moveTo(dPadX - dPadThickness / 2, dPadY - dPadSize / 2)
        lineTo(dPadX + dPadThickness / 2, dPadY - dPadSize / 2)
        lineTo(dPadX + dPadThickness / 2, dPadY + dPadSize / 2)
        lineTo(dPadX - dPadThickness / 2, dPadY + dPadSize / 2)
        close()
    }
    drawPath(path = dPadPath, color = darkButton)
    drawPath(path = dPadPath, color = darkStroke, style = strokeStyle)

    // ==========================================
    // СЛОЙ 3: ЦЕНТРАЛЬНЫЕ КНОПКИ (ДВЕ ПАЛОЧКИ)
    // ==========================================
    val menuLineLength = size * 0.05f
    val menuLineThickness = strokeWidth * 0.9f
    val menuYTop = centerY - size * 0.12f
    val menuYBottom = centerY - size * 0.06f

    drawLine(
        color = darkStroke,
        start = Offset(centerX - menuLineLength / 2, menuYTop),
        end = Offset(centerX + menuLineLength / 2, menuYTop),
        strokeWidth = menuLineThickness,
        cap = StrokeCap.Round
    )
    drawLine(
        color = darkStroke,
        start = Offset(centerX - menuLineLength / 2, menuYBottom),
        end = Offset(centerX + menuLineLength / 2, menuYBottom),
        strokeWidth = menuLineThickness,
        cap = StrokeCap.Round
    )

    // ==========================================
    // СЛОЙ 4: ПРАВЫЕ ЭКШЕН-КНОПКИ (КРУГЛЫЕ)
    // ==========================================
    val actionCenterX = centerX + size * 0.22f
    val actionCenterY = centerY - size * 0.10f
    val btnRadius = size * 0.022f
    val btnOffset = size * 0.07f

    val buttons = listOf(
        Pair(Offset(actionCenterX, actionCenterY - btnOffset), btnYellow), // Верхняя
        Pair(Offset(actionCenterX + btnOffset, actionCenterY), btnRed),    // Правая
        Pair(Offset(actionCenterX, actionCenterY + btnOffset), btnGreen),  // Нижняя
        Pair(Offset(actionCenterX - btnOffset, actionCenterY), btnBlue)    // Левая
    )

    buttons.forEach { (pos, color) ->
        drawCircle(color = darkStroke, radius = btnRadius + strokeWidth *0.5f, center = pos)
        drawCircle(color = color, radius = btnRadius, center = pos)
    }

    // ==========================================
    // СЛОЙ 5: АНАЛОГОВЫЕ СТИКИ (ГРИБКИ) СНИЗУ
    // ==========================================
    val stickRadius = size * 0.07f
    val leftStickCenter = Offset(centerX - size * 0.11f, centerY + size * 0.12f)
    val rightStickCenter = Offset(centerX + size * 0.11f, centerY + size * 0.12f)

    // Левый стик
    drawCircle(color = darkStroke, radius = stickRadius + strokeWidth / 2, center = leftStickCenter)
    drawCircle(color = darkButton, radius = stickRadius, center = leftStickCenter)

    // Правый стик
    drawCircle(
        color = darkStroke,
        radius = stickRadius + strokeWidth / 2,
        center = rightStickCenter
    )
    drawCircle(color = darkButton, radius = stickRadius, center = rightStickCenter)
}

@Composable
fun Gamepad(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val minSide = size.width.coerceAtMost(size.height)

        drawGamepad(
            centerX = size.width * 0.5f,
            centerY = size.height * 0.48f, // Небольшое смещение вверх, чтобы рога не обрезались
            size = minSide * 0.85f
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GamepadPreview() {
    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Gamepad(modifier = Modifier.fillMaxSize())
    }
}