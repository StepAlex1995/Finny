package com.stepalex.finny.presentation.common.icons

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
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

fun DrawScope.drawIdeaBulb(
    centerX: Float,
    centerY: Float,
    size: Float,
    isFilled: Boolean
) {
    val lerpRatio = 0.8f

    // Палитра цветов
    val blackStroke = Color(0xFF231F20)

    // Цвета цоколя (металл)
    val baseColor = lerp(Color(0xFFB0BEC5), Color.White, if (isFilled) 0f else lerpRatio)
    val baseThreadColor = lerp(Color(0xFF78909C), Color.White, if (isFilled) 0f else lerpRatio)
    val contactColor = lerp(Color(0xFF546E7A), Color.White, if (isFilled) 0f else lerpRatio)

    // Цвета светящейся лампочки
    val bulbYellow = lerp(Color(0xFFFFD54F), Color.White, if (isFilled) 0f else lerpRatio)
    val bulbShadow = lerp(Color(0xFFFFA000), Color.White, if (isFilled) 0f else lerpRatio)
    val bulbHighlight = lerp(Color(0xFFFFF9C4), Color.White, if (isFilled) 0f else lerpRatio)

    // Цвет нити накала и лучей
    val filamentColor = lerp(Color(0xFFFF6D00), Color.White, if (isFilled) 0f else lerpRatio)
    val raysColor = lerp(Color(0xFFFFB300), Color.White, if (isFilled) 0f else lerpRatio)

    val strokeWidth = size * 0.045f
    val strokeStyle = Stroke(width = strokeWidth, join = StrokeJoin.Round, cap = StrokeCap.Round)
    val thinStrokeStyle = Stroke(width = strokeWidth * 0.7f, join = StrokeJoin.Round, cap = StrokeCap.Round)

    // ==========================================
    // СЛОЙ 1: КУПОЛ ЛАМПОЧКИ
    // ==========================================
    val bulbPath = Path().apply {
        // Начинаем чуть выше цоколя слева
        moveTo(centerX - size * 0.14f, centerY + size * 0.16f)

        // Левый переход к круглой части
        cubicTo(
            centerX - size * 0.22f, centerY + size * 0.12f,
            centerX - size * 0.36f, centerY - size * 0.02f,
            centerX - size * 0.36f, centerY - size * 0.18f
        )
        // Верхний купол (левая и верхняя часть)
        cubicTo(
            centerX - size * 0.36f, centerY - size * 0.44f,
            centerX - size * 0.20f, centerY - size * 0.52f,
            centerX, centerY - size * 0.52f
        )
        // Верхний купол (правая часть)
        cubicTo(
            centerX + size * 0.20f, centerY - size * 0.52f,
            centerX + size * 0.36f, centerY - size * 0.44f,
            centerX + size * 0.36f, centerY - size * 0.18f
        )
        // Правый переход обратно к цоколю
        cubicTo(
            centerX + size * 0.36f, centerY - size * 0.02f,
            centerX + size * 0.22f, centerY + size * 0.12f,
            centerX + size * 0.14f, centerY + size * 0.16f
        )
        close()
    }

    // Заливка купола желтым
    drawPath(path = bulbPath, color = bulbYellow)

    // ТЕНЬ ВНУТРИ ЛАМПОЧКИ (справа и снизу)
    withTransform({ clipPath(bulbPath) }) {
        drawOval(
            color = bulbShadow,
            topLeft = Offset(centerX - size * 0.12f, centerY - size * 0.05f),
            size = Size(size * 0.52f, size * 0.26f)
        )
    }

    // Контур купола
    drawPath(path = bulbPath, color = blackStroke, style = strokeStyle)

    // ==========================================
    // СЛОЙ 2: ВНУТРЕННИЕ ДЕТАЛИ (НИТЬ НАКАЛА И БЛИК)
    // ==========================================
    // Вертикальный овальный блик слева
    withTransform({
        rotate(degrees = 15f, pivot = Offset(centerX - size * 0.20f, centerY - size * 0.30f))
    }) {
        drawOval(
            color = bulbHighlight,
            topLeft = Offset(centerX - size * 0.24f, centerY - size * 0.40f),
            size = Size(size * 0.09f, size * 0.20f)
        )
    }

    // Нить накала в форме петельки идеи (черный контур + оранжевое наполнение)
    val filamentPath = Path().apply {
        moveTo(centerX - size * 0.06f, centerY + size * 0.14f)
        lineTo(centerX - size * 0.06f, centerY - size * 0.06f)
        // Петелька
        cubicTo(
            centerX - size * 0.15f, centerY - size * 0.24f,
            centerX + size * 0.15f, centerY - size * 0.24f,
            centerX + size * 0.06f, centerY - size * 0.06f
        )
        lineTo(centerX + size * 0.06f, centerY + size * 0.14f)
    }
    drawPath(path = filamentPath, color = filamentColor, style = thinStrokeStyle)

    // ==========================================
    // СЛОЙ 3: МЕТАЛЛИЧЕСКИЙ ЦОКОЛЬ (Резьба)
    // ==========================================
    val baseWidth = size * 0.30f
    val baseHeight = size * 0.06f

    // 3 ребра резьбы цоколя
    for (i in 0..2) {
        val yOffset = centerY + size * 0.18f + (i * size * 0.07f)
        val threadPath = Path().apply {
            moveTo(centerX - baseWidth * 0.45f, yOffset)
            cubicTo(
                centerX - baseWidth * 0.20f, yOffset + baseHeight,
                centerX + baseWidth * 0.20f, yOffset + baseHeight,
                centerX + baseWidth * 0.45f, yOffset
            )
            lineTo(centerX + baseWidth * 0.42f, yOffset - size * 0.02f)
            cubicTo(
                centerX + baseWidth * 0.18f, yOffset + baseHeight - size * 0.02f,
                centerX - baseWidth * 0.18f, yOffset + baseHeight - size * 0.02f,
                centerX - baseWidth * 0.42f, yOffset - size * 0.02f
            )
            close()
        }
        drawPath(path = threadPath, color = baseColor)
        // Тень на резьбе
        drawPath(path = threadPath, color = baseThreadColor, style = Stroke(width = strokeWidth * 0.4f))
        drawPath(path = threadPath, color = blackStroke, style = strokeStyle)
    }

    // Самая нижняя точка контакта цоколя
    val contactPath = Path().apply {
        val yStart = centerY + size * 0.36f
        moveTo(centerX - baseWidth * 0.25f, yStart)
        cubicTo(
            centerX - baseWidth * 0.20f, yStart + size * 0.05f,
            centerX + baseWidth * 0.20f, yStart + size * 0.05f,
            centerX + baseWidth * 0.25f, yStart
        )
        close()
    }
    drawPath(path = contactPath, color = contactColor)
    drawPath(path = contactPath, color = blackStroke, style = strokeStyle)

    // ==========================================
    // СЛОЙ 4: ЛУЧИ СВЕТА (Идея!)
    // ==========================================
    val rayLength = size * 0.14f
    val innerRadius = size * 0.42f
    val outerRadius = innerRadius + rayLength

    val rayAngles = floatArrayOf(-135f, -90f, -45f, 0f, 180f, 135f, 45f)
    // Фильтруем углы, чтобы нарисовать лучи только вокруг купола (вверху и по бокам)
    val validAngles = floatArrayOf(-150f, -120f, -90f, -60f, -30f, 0f, 180f)

    validAngles.forEach { angleDegrees ->
        val angleRad = Math.toRadians(angleDegrees.toDouble())
        val cos = Math.cos(angleRad).toFloat()
        val sin = Math.sin(angleRad).toFloat()

        // Смещение центра лучей немного вверх, ближе к центру круглого купола
        val rayPivotY = centerY - size * 0.18f

        val startX = centerX + cos * innerRadius
        val startY = rayPivotY + sin * innerRadius
        val endX = centerX + cos * outerRadius
        val endY = rayPivotY + sin * outerRadius

        // Рисуем лучи только если лампочка заполнена цветом (включена)
        if (isFilled) {
            drawLine(
                color = raysColor,
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = strokeWidth * 1.2f,
                cap = StrokeCap.Round
            )
            // Черный контур луча, может есть смысл оставить, когда выключена?...
            drawLine(
                color = blackStroke,
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }

    }
}

@Composable
fun IdeaBulb(
    modifier: Modifier,
    isFilled: Boolean
) {
    Canvas(modifier = modifier) {
        drawIdeaBulb(
            centerX = size.width * 0.5f,
            centerY = size.height * 0.65f,
            size = size.width * 0.75f, // Оставляем небольшой запас под лучи вокруг
            isFilled = isFilled
        )
    }
}

@Preview(showBackground = true)
@Composable
fun IdeaBulbPreview() {
    Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
        IdeaBulb(modifier = Modifier.fillMaxSize(), isFilled = false)
    }
}