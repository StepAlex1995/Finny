package com.stepalex.finny.presentation.pet_room

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


fun DrawScope.drawMoney(
    centerX: Float,
    centerY: Float,
    size: Float
) {
    val radius = size / 2f
    val center = Offset(centerX, centerY)

    // Цветовая палитра монетки
    val darkGoldBorder = Color(0xFF8B5A00)
    val baseGold = Color(0xFFD49B00)
    val midGold = Color(0xFFF1C40F)
    val lightGold = Color(0xFFF9E79F)
    val whiteHighlight = Color(0xFFFFFFFF).copy(alpha = 0.6f)
    val shadowColor = Color(0xFF5D3E00)

    // 1. Внешняя темная обводка (ободок монетки)
    drawCircle(
        color = darkGoldBorder,
        radius = radius,
        center = center
    )
    // 2. Основное тело монетки (линейный градиент для объема)
    val baseGradient = Brush.linearGradient(
        colors = listOf(lightGold, midGold, baseGold),
        start = Offset(centerX - radius, centerY - radius),
        end = Offset(centerX + radius, centerY + radius)
    )
    drawCircle(
        brush = baseGradient,
        radius = radius * 0.96f,
        center = center
    )
    // 3. Блик на внешнем ободке (сверху слева)
    val outerHighlightGradient = Brush.radialGradient(
        colors = listOf(whiteHighlight, Color.Transparent),
        center = Offset(centerX - radius * 0.4f, centerY - radius * 0.7f),
        radius = radius * 0.6f
    )
    drawCircle(
        brush = outerHighlightGradient,
        radius = radius * 0.96f,
        center = center
    )
    // 4. Внутренняя утопленная часть (более темная основа)
    val innerRadius = radius * 0.75f
    val innerGradient = Brush.linearGradient(
        colors = listOf(baseGold, darkGoldBorder),
        start = Offset(centerX - innerRadius, centerY - innerRadius),
        end = Offset(centerX + innerRadius, centerY + innerRadius)
    )
    drawCircle(
        brush = innerGradient,
        radius = innerRadius,
        center = center
    )

    // Рисуем лапку (состоит из подушечки и трех пальчиков)
    // Все элементы лапки имеют небольшую тень снизу для эффекта объема
    // 5. Большая подушечка лапки (полукруг/закругленная форма)
    val padWidth = innerRadius * 1.1f
    val padHeight = innerRadius * 0.7f
    val padCenterY = centerY + innerRadius * 0.15f
    // Тень подушечки
    drawOval(
        color = shadowColor,
        topLeft = Offset(centerX - padWidth / 2f, padCenterY - padHeight / 2f + (innerRadius * 0.05f)),
        size = Size(padWidth, padHeight)
    )
    // Сама подушечка
    val padGradient = Brush.verticalGradient(
        colors = listOf(lightGold, midGold),
        startY = padCenterY - padHeight / 2f,
        endY = padCenterY + padHeight / 2f
    )
    drawOval(
        brush = padGradient,
        topLeft = Offset(centerX - padWidth / 2f, padCenterY - padHeight / 2f),
        size = Size(padWidth, padHeight)
    )
    // Блик на подушечке лапки
    drawOval(
        color = whiteHighlight,
        topLeft = Offset(centerX - padWidth * 0.25f, padCenterY - padHeight * 0.4f),
        size = Size(padWidth * 0.5f, padHeight * 0.25f)
    )
    // 6. Пальчики (три овала сверху)
    val toeWidth = innerRadius * 0.45f
    val toeHeight = innerRadius * 0.3f
    val toeY = centerY - innerRadius * 0.35f
    // Конфигурация трех пальцев: смещение по X и Y, и угол наклона
    val toes = listOf(
        Triple(centerX - innerRadius * 0.5f, toeY + innerRadius * 0.05f, -25f), // Левый
        Triple(centerX, toeY - innerRadius * 0.1f, 0f),                       // Средний
        Triple(centerX + innerRadius * 0.5f, toeY + innerRadius * 0.05f, 25f)  // Правый
    )
    toes.forEach { (toeX, toeYPos, rotation) ->
        withTransform({
            rotate(degrees = rotation, pivot = Offset(toeX, toeYPos))
        }) {
            val toeTopLeftY = toeYPos - toeHeight / 2f
            val toeBottomRightY = toeYPos + toeHeight / 2f

            // 1. Тень пальчика
            drawOval(
                color = shadowColor,
                topLeft = Offset(toeX - toeWidth / 2f, toeTopLeftY + (innerRadius * 0.04f)),
                size = Size(toeWidth, toeHeight)
            )

            // 2. Создаем градиент прямо по локальным координатам текущего пальчика
            val localToeGradient = Brush.verticalGradient(
                colors = listOf(lightGold, midGold),
                startY = toeTopLeftY,
                endY = toeBottomRightY
            )

            // 3. Сам пальчик с правильным градиентом
            drawOval(
                brush = localToeGradient,
                topLeft = Offset(toeX - toeWidth / 2f, toeTopLeftY),
                size = Size(toeWidth, toeHeight)
            )
        }
    }
}


@Composable
fun Money(
    modifier: Modifier,
) {
    Canvas(modifier = modifier) {
        drawMoney(
            size.width * 0.5f,
            size.height * 0.5f,
            size = size.width * 0.85f,
        )
    }
}

@Preview
@Composable
fun MoneyPreview() {
    Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
        Money(modifier = Modifier.fillMaxSize())
    }
}