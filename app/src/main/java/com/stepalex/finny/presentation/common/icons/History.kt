package com.stepalex.finny.presentation.common.icons

import androidx.compose.foundation.Canvas
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

fun DrawScope.drawHourglass(
    centerX: Float,
    centerY: Float,
    size: Float
) {
    // Палитра цветов по вашему скриншоту
    val blackStroke = Color(0xFF000000)
    val woodColor = Color(0xFFB07C7D)
    val woodShadow = Color(0xFF966263)
    val glassBg = Color(0xFFEBEFF2)
    val sandColor = Color(0xFFFFD500)
    val sandShadow = Color(0xFFE0BB00)

    val strokeWidth = size * 0.045f
    val strokeStyle = Stroke(width = strokeWidth, join = StrokeJoin.Round, cap = StrokeCap.Round)

    // Размеры основных элементов
    val capWidth = size * 0.75f
    val capHeight = size * 0.09f
    val glassWidth = size * 0.56f
    val glassHeight = size * 0.68f

    // Координаты верхней и нижней деревянных крышек
    val topCapY = centerY - glassHeight / 2 - capHeight
    val bottomCapY = centerY + glassHeight / 2

    // ==========================================
    // СЛОЙ 1: СТЕКЛЯННАЯ КОЛБА (ФОН)
    // ==========================================
    val glassPath = Path().apply {
        // Начинаем сверху слева под крышкой
        moveTo(centerX - glassWidth / 2, centerY - glassHeight / 2)
        // Верхнее основание колбы
        lineTo(centerX + glassWidth / 2, centerY - glassHeight / 2)
        // Правое сужение к центру и расширение вниз
        cubicTo(
            centerX + glassWidth / 2, centerY - glassHeight * 0.2f,
            centerX + size * 0.08f, centerY - size * 0.04f,
            centerX + size * 0.08f, centerY
        )
        cubicTo(
            centerX + size * 0.08f, centerY + size * 0.04f,
            centerX + glassWidth / 2, centerY + glassHeight * 0.2f,
            centerX + glassWidth / 2, centerY + glassHeight / 2
        )
        // Нижнее основание колбы
        lineTo(centerX - glassWidth / 2, centerY + glassHeight / 2)
        // Левое сужение к центру и расширение вверх
        cubicTo(
            centerX - glassWidth / 2, centerY + glassHeight * 0.2f,
            centerX - size * 0.08f, centerY + size * 0.04f,
            centerX - size * 0.08f, centerY
        )
        cubicTo(
            centerX - size * 0.08f, centerY - size * 0.04f,
            centerX - glassWidth / 2, centerY - glassHeight * 0.2f,
            centerX - glassWidth / 2, centerY - glassHeight / 2
        )
        close()
    }

    // Заливка стекла базовым светло-серым цветом
    drawPath(path = glassPath, color = glassBg)

    // ==========================================
    // СЛОЙ 2: ЖЕЛТЫЙ ПЕСОК (С НАЛОЖЕНИЕМ НА СТЕКЛО)
    // ==========================================
    withTransform({ clipPath(glassPath) }) {
        // 2.1 Песок в верхней чаше (наполовину пустой, ровная линия)
        val topSandPath = Path().apply {
            moveTo(centerX - glassWidth, centerY - glassHeight * 0.12f)
            lineTo(centerX + glassWidth, centerY - glassHeight * 0.12f)
            lineTo(centerX + glassWidth, centerY + glassHeight)
            lineTo(centerX - glassWidth, centerY + glassHeight)
            close()
        }
        withTransform({ clipPath(topSandPath) }) {
            // Ограничиваем область видимости только верхней половиной стекла (до узкого горлышка)
            val topHalfClip = Path().apply {
                addRect(
                    androidx.compose.ui.geometry.Rect(
                        centerX - size,
                        centerY - size,
                        centerX + size,
                        centerY
                    )
                )
            }
            withTransform({ clipPath(topHalfClip) }) {
                drawPath(path = glassPath, color = sandColor)
                // Тень песка справа в верхней чаше
                drawRect(
                    color = sandShadow,
                    topLeft = Offset(centerX + glassWidth * 0.15f, centerY - size),
                    size = Size(glassWidth, size * 2)
                )
            }
        }

        // 2.2 Песок в нижней чаше (горка)
        val bottomSandPath = Path().apply {
            // Начинаем из центра горлышка
            moveTo(centerX, centerY)
            // Плавный скат горки вправо и вниз
            cubicTo(
                centerX + size * 0.05f, centerY + glassHeight * 0.22f,
                centerX + glassWidth * 0.45f, centerY + glassHeight * 0.22f,
                centerX + glassWidth * 0.45f, centerY + glassHeight / 2
            )
            lineTo(centerX - glassWidth * 0.45f, centerY + glassHeight / 2)
            // Плавный скат горки влево и вверх обратно к центру
            cubicTo(
                centerX - glassWidth * 0.45f, centerY + glassHeight * 0.22f,
                centerX - size * 0.05f, centerY + glassHeight * 0.22f,
                centerX, centerY
            )
            close()
        }
        drawPath(path = bottomSandPath, color = sandColor)

        // Тень песка справа в нижней чаше
        withTransform({ clipPath(bottomSandPath) }) {
            drawRect(
                color = sandShadow,
                topLeft = Offset(centerX + glassWidth * 0.15f, centerY),
                size = Size(glassWidth, glassHeight)
            )
        }
    }

    // ==========================================
    // СЛОЙ 3: КОНТУР КОЛБЫ И БЛИКИ СТЕКЛА
    // ==========================================
    // Черный контур самой стеклянной колбы
    drawPath(path = glassPath, color = blackStroke, style = strokeStyle)

    // Объемные полупрозрачные тени/блики на стекле (эффект закругления по бокам)
    withTransform({ clipPath(glassPath) }) {
        // Тень на стекле справа
        drawRect(
            color = Color(0x10000000), // Легкий черный полупрозрачный
            topLeft = Offset(centerX + glassWidth * 0.36f, centerY - glassHeight),
            size = Size(glassWidth, glassHeight * 2)
        )
    }

    // ==========================================
    // СЛОЙ 4: ДЕРЕВЯННЫЕ КРЫШКИ (ВЕРХ И НИЗ)
    // ==========================================
    val capRadius = CornerRadius(capHeight * 0.5f, capHeight * 0.5f)

    // 4.1 ВЕРХНЯЯ КРЫШКА
    // Заливка базовым цветом дерева
    drawRoundRect(
        color = woodColor,
        topLeft = Offset(centerX - capWidth / 2, topCapY),
        size = Size(capWidth, capHeight),
        cornerRadius = capRadius
    )
    // Правая тень на дереве
    withTransform({
        val clipTop = Path().apply {
            addRoundRect(
                RoundRect(
                    centerX - capWidth / 2,
                    topCapY,
                    centerX + capWidth / 2,
                    topCapY + capHeight,
                    capRadius
                )
            )
        }
        clipPath(clipTop)
    }) {
        drawRect(
            color = woodShadow,
            topLeft = Offset(centerX + capWidth * 0.22f, topCapY),
            size = Size(capWidth, capHeight)
        )
    }
    // Черный контур
    drawRoundRect(
        color = blackStroke,
        topLeft = Offset(centerX - capWidth / 2, topCapY),
        size = Size(capWidth, capHeight),
        cornerRadius = capRadius,
        style = strokeStyle
    )

    // 4.2 НИЖНЯЯ КРЫШКА
    // Заливка базовым цветом дерева
    drawRoundRect(
        color = woodColor,
        topLeft = Offset(centerX - capWidth / 2, bottomCapY),
        size = Size(capWidth, capHeight),
        cornerRadius = capRadius
    )
    // Правая тень на дереве
    withTransform({
        val clipBottom = Path().apply {
            addRoundRect(
                RoundRect(
                    centerX - capWidth / 2,
                    bottomCapY,
                    centerX + capWidth / 2,
                    bottomCapY + capHeight,
                    capRadius
                )
            )
        }
        clipPath(clipBottom)
    }) {
        drawRect(
            color = woodShadow,
            topLeft = Offset(centerX + capWidth * 0.22f, bottomCapY),
            size = Size(capWidth, capHeight)
        )
    }
    // Черный контур
    drawRoundRect(
        color = blackStroke,
        topLeft = Offset(centerX - capWidth / 2, bottomCapY),
        size = Size(capWidth, capHeight),
        cornerRadius = capRadius,
        style = strokeStyle
    )
}

@Composable
fun Hourglass(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        // Рассчитываем оптимальный размер на основе ширины или высоты контейнера
        val minSide = size.width.coerceAtMost(size.height)

        drawHourglass(
            centerX = size.width * 0.5f,
            centerY = size.height * 0.5f,
            size = minSide * 0.85f // Оставляем небольшой отступ по краям
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HourglassPreview() {
    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Hourglass(modifier = Modifier.fillMaxSize())
    }
}