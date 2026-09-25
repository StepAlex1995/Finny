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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

fun DrawScope.drawCabbage(
    centerX: Float,
    centerY: Float,
    size: Float,
    isFilled: Boolean
) {
    val lerpRatio = 0.8f

    val blackStroke = Color(0xFF11160F)
    val darkGreenEdge = Color(0xFF1E5214)     // Глубокая тень в складках внешних листьев
    val midGreenLeaf = Color(0xFF3B9B26)      // Основной тон внешних листьев
    val lightGreenLeaf = Color(0xFF63C24B)    // Блики на внешних листьях
    val innerCabbageBase = Color(0xFFDDF9CC)  // Светлая сердцевина кочана
    val innerCabbageShadow = Color(0xFF86C667)// Теневые участки внутренних листьев
    val thickVeinColor = Color(0xFFE8FCDA)    // Мясистые центральные прожилки

    // Интерполяция для состояния !isFilled
    val colorDarkEdge = lerp(darkGreenEdge, Color.White, if (isFilled) 0f else lerpRatio)
    val colorMidLeaf = lerp(midGreenLeaf, Color.White, if (isFilled) 0f else lerpRatio)
    val colorLightLeaf = lerp(lightGreenLeaf, Color.White, if (isFilled) 0f else lerpRatio)
    val colorInnerBase = lerp(innerCabbageBase, Color.White, if (isFilled) 0f else lerpRatio)
    val colorInnerShadow = lerp(innerCabbageShadow, Color.White, if (isFilled) 0f else lerpRatio)
    val colorVein = lerp(thickVeinColor, Color.White, if (isFilled) 0f else lerpRatio)

    val mainStroke = Stroke(width = size * 0.035f, join = StrokeJoin.Round, cap = StrokeCap.Round)
    val boldVein = Stroke(width = size * 0.018f, join = StrokeJoin.Round, cap = StrokeCap.Round)
    val mediumVein = Stroke(width = size * 0.010f, join = StrokeJoin.Round, cap = StrokeCap.Round)
    val thinVein = Stroke(width = size * 0.006f, join = StrokeJoin.Round, cap = StrokeCap.Round)

    // ==========================================
    // СЛОЙ 1: ВНЕШНИЙ МНОГОСЛОЙНЫЙ ЛИСТ (ЗАДНИЙ ПЛАН)
    // ==========================================
    val ultraBackLeaves = Path().apply {
        // Прорисовываем сильно изрезанный, волнообразный край
        moveTo(centerX - size * 0.45f, centerY + size * 0.30f)

        // Левая нижняя складка
        cubicTo(
            centerX - size * 0.65f,
            centerY + size * 0.35f,
            centerX - size * 0.68f,
            centerY + size * 0.10f,
            centerX - size * 0.55f,
            centerY + size * 0.02f
        )
        cubicTo(
            centerX - size * 0.65f,
            centerY - size * 0.10f,
            centerX - size * 0.55f,
            centerY - size * 0.28f,
            centerX - size * 0.35f,
            centerY - size * 0.30f
        )

        // Верхний левый гребень листа
        cubicTo(
            centerX - size * 0.48f,
            centerY - size * 0.45f,
            centerX - size * 0.38f,
            centerY - size * 0.58f,
            centerX - size * 0.15f,
            centerY - size * 0.54f
        )
        cubicTo(
            centerX - size * 0.05f,
            centerY - size * 0.65f,
            centerX + size * 0.20f,
            centerY - size * 0.62f,
            centerX + size * 0.28f,
            centerY - size * 0.46f
        )

        // Правое сложное крыло листа
        cubicTo(
            centerX + size * 0.48f,
            centerY - size * 0.55f,
            centerX + size * 0.64f,
            centerY - size * 0.38f,
            centerX + size * 0.58f,
            centerY - size * 0.15f
        )
        cubicTo(
            centerX + size * 0.68f,
            centerY - size * 0.02f,
            centerX + size * 0.62f,
            centerY + size * 0.25f,
            centerX + size * 0.45f,
            centerY + size * 0.32f
        )

        // Нижний нахлест
        cubicTo(
            centerX + size * 0.48f,
            centerY + size * 0.52f,
            centerX + size * 0.22f,
            centerY + size * 0.58f,
            centerX + size * 0.05f,
            centerY + size * 0.50f
        )
        cubicTo(
            centerX - size * 0.15f,
            centerY + size * 0.60f,
            centerX - size * 0.38f,
            centerY + size * 0.52f,
            centerX - size * 0.45f,
            centerY + size * 0.30f
        )
        close()
    }

    // Сложный объемный градиент: от светлых бликов на краях к глубоким теням под кочаном
    drawPath(
        path = ultraBackLeaves,
        brush = Brush.radialGradient(
            colors = listOf(colorLightLeaf, colorMidLeaf, colorDarkEdge),
            center = Offset(centerX, centerY),
            radius = size * 0.65f
        )
    )
    drawPath(path = ultraBackLeaves, color = blackStroke, style = mainStroke)

    // РЕЛЬЕФНЫЕ ПРОЖИЛКИ ВНЕШНЕГО ЛИСТА (Разветвленная структура)
    withTransform({ clipPath(ultraBackLeaves) }) {
        // Левые прожилки
        val leftVein1 = Path().apply {
            moveTo(centerX - size * 0.35f, centerY)
            cubicTo(
                centerX - size * 0.45f,
                centerY - size * 0.05f,
                centerX - size * 0.52f,
                centerY - size * 0.15f,
                centerX - size * 0.58f,
                centerY - size * 0.12f
            )
        }
        drawPath(path = leftVein1, color = colorVein.copy(alpha = 0.7f), style = mediumVein)
        drawLine(
            color = colorVein.copy(alpha = 0.6f),
            start = Offset(centerX - size * 0.46f, centerY - size * 0.07f),
            end = Offset(centerX - size * 0.52f, centerY - size * 0.02f),
            strokeWidth = thinVein.width
        )

        // Правые рельефные складки
        val rightVein1 = Path().apply {
            moveTo(centerX + size * 0.32f, centerY + size * 0.10f)
            cubicTo(
                centerX + size * 0.48f,
                centerY + size * 0.15f,
                centerX + size * 0.55f,
                centerY + size * 0.22f,
                centerX + size * 0.56f,
                centerY + size * 0.14f
            )
        }
        drawPath(path = rightVein1, color = colorVein.copy(alpha = 0.7f), style = mediumVein)
    }

    // ==========================================
    // СЛОЙ 2: ПЛОТНЫЙ ЦЕНТРАЛЬНЫЙ КОЧАН
    // ==========================================
    val mainHead = Path().apply {
        addOval(
            androidx.compose.ui.geometry.Rect(
                centerX - size * 0.35f,
                centerY - size * 0.36f,
                centerX + size * 0.38f,
                centerY + size * 0.36f
            )
        )
    }
    drawPath(
        path = mainHead,
        brush = Brush.radialGradient(
            colors = listOf(colorInnerBase, colorInnerShadow),
            center = Offset(centerX + size * 0.08f, centerY - size * 0.12f),
            radius = size * 0.42f
        )
    )

    // Тень, отбрасываемая листьями на кочан слева
    withTransform({ clipPath(mainHead) }) {
        drawOval(
            color = colorDarkEdge.copy(alpha = 0.30f),
            topLeft = Offset(centerX - size * 0.42f, centerY - size * 0.20f),
            size = Size(size * 0.35f, size * 0.60f)
        )
    }
    drawPath(path = mainHead, color = blackStroke, style = mainStroke)

    // ==========================================
    // СЛОЙ 3: ЛЕВЫЙ ВНУТРЕННИЙ ЛИСТ С ВЫЕМКАМИ
    // ==========================================
    val innerLeftLeaf = Path().apply {
        moveTo(centerX - size * 0.02f, centerY - size * 0.36f)
        // Характерный волнистый стык по центру
        cubicTo(
            centerX - size * 0.15f,
            centerY - size * 0.18f,
            centerX + size * 0.02f,
            centerY - size * 0.02f,
            centerX - size * 0.12f,
            centerY + size * 0.18f
        )
        cubicTo(
            centerX - size * 0.22f,
            centerY + size * 0.28f,
            centerX - size * 0.05f,
            centerY + size * 0.34f,
            centerX + size * 0.10f,
            centerY + size * 0.36f
        )
        // Дуга по форме кочана обратно
        cubicTo(
            centerX - size * 0.28f,
            centerY + size * 0.32f,
            centerX - size * 0.38f,
            centerY - size * 0.10f,
            centerX - size * 0.22f,
            centerY - size * 0.28f
        )
        close()
    }
    drawPath(
        path = innerLeftLeaf,
        brush = Brush.radialGradient(
            colors = listOf(colorInnerBase, colorInnerShadow),
            center = Offset(centerX - size * 0.15f, centerY + size * 0.05f),
            radius = size * 0.35f
        )
    )

    // Мясистые светлые прожилки левого листа
    withTransform({ clipPath(innerLeftLeaf) }) {
        val rootPath = Path().apply {
            moveTo(centerX - size * 0.20f, centerY + size * 0.28f)
            cubicTo(
                centerX - size * 0.15f,
                centerY + size * 0.12f,
                centerX - size * 0.16f,
                centerY - size * 0.05f,
                centerX - size * 0.10f,
                centerY - size * 0.16f
            )
        }
        drawPath(path = rootPath, color = colorVein, style = boldVein) // Толстая у основания

        // Тонкие ответвления, создающие рельеф
        drawLine(
            color = colorVein,
            start = Offset(centerX - size * 0.17f, centerY + size * 0.18f),
            end = Offset(centerX - size * 0.05f, centerY + size * 0.14f),
            strokeWidth = mediumVein.width,
            cap = StrokeCap.Round
        )
        drawLine(
            color = colorVein,
            start = Offset(centerX - size * 0.15f, centerY + size * 0.02f),
            end = Offset(centerX - size * 0.02f, centerY + size * 0.04f),
            strokeWidth = mediumVein.width,
            cap = StrokeCap.Round
        )
    }
    drawPath(path = innerLeftLeaf, color = blackStroke, style = mainStroke)

    // ==========================================
    // СЛОЙ 4: ПРАВЫЙ ВЕРХНИЙ ПЕРЕКРЫВАЮЩИЙ ЛИСТ
    // ==========================================
    val innerRightLeaf = Path().apply {
        moveTo(centerX - size * 0.02f, centerY - size * 0.36f)
        // Край нахлеста с небольшими зазубринами
        cubicTo(
            centerX + size * 0.18f,
            centerY - size * 0.30f,
            centerX + size * 0.05f,
            centerY - size * 0.08f,
            centerX + size * 0.22f,
            centerY + size * 0.02f
        )
        cubicTo(
            centerX + size * 0.34f,
            centerY + size * 0.10f,
            centerX + size * 0.20f,
            centerY + size * 0.26f,
            centerX + size * 0.10f,
            centerY + size * 0.36f
        )
        // Огибаем правый край
        cubicTo(
            centerX + size * 0.32f,
            centerY + size * 0.28f,
            centerX + size * 0.40f,
            centerY - size * 0.05f,
            centerX + size * 0.18f,
            centerY - size * 0.26f
        )
        close()
    }
    drawPath(
        path = innerRightLeaf,
        brush = Brush.radialGradient(
            colors = listOf(colorInnerBase, colorInnerShadow),
            center = Offset(centerX + size * 0.15f, centerY - size * 0.05f),
            radius = size * 0.38f
        )
    )
    // Прожилки правого накладного листа
    withTransform({ clipPath(innerRightLeaf) }) {
        val rightRoot = Path().apply {
            moveTo(
                centerX + size * 0.18f,
                centerY + size * 0.22f
            )
            cubicTo(
                centerX + size * 0.18f,
                centerY + size * 0.08f,
                centerX + size * 0.24f,
                centerY - size * 0.05f,
                centerX + size * 0.14f,
                centerY - size * 0.18f
            )
        }
        drawPath(
            path =
                rightRoot, color = colorVein, style = boldVein
        )
        drawLine(
            color =
                colorVein,
            start = Offset(centerX + size * 0.19f, centerY + size * 0.08f),
            end = Offset(centerX + size * 0.30f, centerY + size * 0.06f),
            strokeWidth = mediumVein.width,
            cap = StrokeCap.Round
        )
        drawLine(
            color =
                colorVein,
            start = Offset(centerX + size * 0.21f, centerY - size * 0.04f),
            end = Offset(centerX + size * 0.32f, centerY - size * 0.12f),
            strokeWidth = mediumVein.width,
            cap = StrokeCap.Round
        )
    }
    drawPath(path = innerRightLeaf, color = blackStroke, style = mainStroke)
}

@Composable
fun Cabbage(
    modifier: Modifier,
    isFilled: Boolean
) {
    Canvas(modifier = modifier) {
        drawCabbage(
            centerX = size.width * 0.5f,
            centerY = size.height * 0.5f,
            size = size.width * 0.75f,
            isFilled = isFilled
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CabbagePreview() {
    Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
        Cabbage(modifier = Modifier.fillMaxSize(), isFilled = true)
    }
}
