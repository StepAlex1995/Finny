package com.stepalex.finny.presentation.common.icons

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
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

fun DrawScope.drawShoppingBasket(
    centerX: Float,
    centerY: Float,
    size: Float
) {
    // Палитра цветов по скриншоту
    val blackStroke = Color(0xFF000000)
    val rimColor = Color(0xFFE56B6F)       // Красный ободок
    val basketBg = Color(0xFFF1EFF2)       // Светло-серый фон корзины
    val handleColor = Color(0xFFFFD166)    // Желтые ручки
    val shadowColor = Color(0x0D000000)    // Легкая полупрозрачная тень

    // Параметры линий контура
    val strokeWidth = size * 0.045f
    val strokeStyle = Stroke(width = strokeWidth, join = StrokeJoin.Round, cap = StrokeCap.Round)

    // Пропорции геометрии корзины
    val rimWidth = size * 0.88f
    val rimHeight = size * 0.08f
    val basketTopWidth = size * 0.82f
    val basketBottomWidth = size * 0.62f
    val basketHeight = size * 0.44f

    // Опорные точки по вертикали
    val rimTopY = centerY - size * 0.05f
    val rimBottomY = rimTopY + rimHeight
    val basketBottomY = rimBottomY + basketHeight

    // ==========================================
    // СЛОЙ 1: ЖЕЛТЫЕ РУЧКИ
    // ==========================================
    // Толщина самой желтой палочки (внутри контура)
    val handleThickness = strokeWidth * 1.1f

    // Координаты верхней точки, где сходятся ручки
    val topXLeft = centerX - size * 0.04f
    val topXRight = centerX + size * 0.04f
    val topY = centerY - size * 0.38f

    // Координаты нижних точек крепления к ободку
    val bottomXLeft = centerX - size * 0.22f
    val bottomXRight = centerX + size * 0.22f

    // Список пар точек (Старт -> Конец) для двух ручек
    val lines = listOf(
        Pair(Offset(bottomXLeft, rimTopY), Offset(topXLeft, topY)),   // Левая палочка
        Pair(Offset(bottomXRight, rimTopY), Offset(topXRight, topY))  // Правая палочка
    )

    lines.forEach { (start, end) ->
        // 1. Рисуем толстую черную линию (внешний контур ручки)
        drawLine(
            color = blackStroke,
            start = start,
            end = end,
            strokeWidth = handleThickness + strokeWidth * 1f,
            cap = StrokeCap.Round
        )
        // 2. Рисуем желтую линию поверх (внутреннее наполнение)
        drawLine(
            color = handleColor,
            start = start,
            end = end,
            strokeWidth = handleThickness,
            cap = StrokeCap.Round
        )
    }

    // ==========================================
    // СЛОЙ 2: ТЕЛО КОРЗИНЫ (ТРАПЕЦИЯ С ФОНОМ)
    // ==========================================
    val basketBodyPath = Path().apply {
        moveTo(centerX - basketTopWidth / 2, rimBottomY)
        lineTo(centerX + basketTopWidth / 2, rimBottomY)
        // Плавный переход к закругленному низу
        lineTo(centerX + basketBottomWidth / 2, basketBottomY - size * 0.05f)
        cubicTo(
            centerX + basketBottomWidth / 2, basketBottomY,
            centerX + basketBottomWidth / 2 - size * 0.05f, basketBottomY,
            centerX + basketBottomWidth / 2 - size * 0.05f, basketBottomY
        )
        lineTo(centerX - basketBottomWidth / 2 + size * 0.05f, basketBottomY)
        cubicTo(
            centerX - basketBottomWidth / 2, basketBottomY,
            centerX - basketBottomWidth / 2, basketBottomY - size * 0.05f,
            centerX - basketBottomWidth / 2, basketBottomY - size * 0.05f
        )
        close()
    }
    drawPath(path = basketBodyPath, color = basketBg)

    // Эффект объемной тени в правой части корзины
    withTransform({ clipPath(basketBodyPath) }) {
        drawRect(
            color = shadowColor,
            topLeft = Offset(centerX + size * 0.20f, rimBottomY),
            size = Size(size, size)
        )
    }

    // ==========================================
    // СЛОЙ 3: СЕТКА КОРЗИНЫ (РЕШЕТКА)
    // ==========================================
    withTransform({ clipPath(basketBodyPath) }) {
        // Горизонтальные полосы решетки (2 линии)
        val hStep = basketHeight / 3
        for (i in 1..2) {
            val y = rimBottomY + i * hStep
            drawLine(
                color = blackStroke,
                start = Offset(centerX - size, y),
                end = Offset(centerX + size, y),
                strokeWidth = strokeWidth * 0.9f
            )
        }

        // Вертикальные полосы решетки (4 линии, расходящиеся веером)
        val topXOffsets = floatArrayOf(-0.30f, -0.10f, 0.10f, 0.30f)
        val bottomXOffsets = floatArrayOf(-0.22f, -0.07f, 0.07f, 0.22f)

        for (i in topXOffsets.indices) {
            val startX = centerX + topXOffsets[i] * size
            val endX = centerX + bottomXOffsets[i] * size
            drawLine(
                color = blackStroke,
                start = Offset(startX, rimBottomY),
                end = Offset(endX, basketBottomY),
                strokeWidth = strokeWidth * 0.9f
            )
        }
    }

    // Основной контур чаши корзины поверх решетки
    drawPath(path = basketBodyPath, color = blackStroke, style = strokeStyle)

    // ==========================================
    // СЛОЙ 4: КРАСНЫЙ ВЕРХНИЙ ОБОДОК
    // ==========================================
    val rimRadius = CornerRadius(rimHeight * 0.5f, rimHeight * 0.5f)

    // Заливка ободка
    drawRoundRect(
        color = rimColor,
        topLeft = Offset(centerX - rimWidth / 2, rimTopY),
        size = Size(rimWidth, rimHeight),
        cornerRadius = rimRadius
    )

    // Тень на правой части ободка
    withTransform({
        val clipRim = Path().apply {
            addRoundRect(RoundRect(centerX - rimWidth / 2, rimTopY, centerX + rimWidth / 2, rimTopY + rimHeight, rimRadius))
        }
        clipPath(clipRim)
    }) {
        drawRect(
            color = shadowColor,
            topLeft = Offset(centerX + rimWidth * 0.25f, rimTopY),
            size = Size(rimWidth, rimHeight)
        )
    }

    // Контур ободка
    drawRoundRect(
        color = blackStroke,
        topLeft = Offset(centerX - rimWidth / 2, rimTopY),
        size = Size(rimWidth, rimHeight),
        cornerRadius = rimRadius,
        style = strokeStyle
    )
}

@Composable
fun ShoppingBasket(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val minSide = size.width.coerceAtMost(size.height)

        drawShoppingBasket(
            centerX = size.width * 0.5f,
            centerY = size.height * 0.55f, // Немного смещаем вниз, чтобы ручки влезли сверху
            size = minSide * 0.85f
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ShoppingBasketPreview() {
    Box(
        modifier = Modifier.size(200.dp).background(Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        ShoppingBasket(modifier = Modifier.fillMaxSize())
    }
}