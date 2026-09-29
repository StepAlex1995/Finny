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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.nio.file.Files.size

fun DrawScope.drawResetButton(
    centerX: Float,
    centerY: Float,
    size: Float
) {
    // Палитра цветов по вашему скриншоту
    val darkBlueArrow = Color(0xFF5A0C16)   // Бордово-красный для стрелки (высокий контраст)
    val outerRingLight = Color(0xFFFF5261)  // Светло-красный/коралловый для верхнего края кольца
    val outerRingDark = Color(0xFFD00018)   // Глубокий красный для нижнего края кольца
    val innerBgLight = Color(0xFFFF334B)    // Яркий красный для центральной глянцевой сферы
    val innerBgDark = Color(0xFF9E000F)     // Темно-красный для эффекта глубины внутри

    val radius = size / 2f


    // ==========================================
    // СЛОЙ 2: ВНЕШНЕЕ ОБЪЕМНОЕ КОЛЬЦО
    // ==========================================
    // Градиент сверху вниз дает 3D эффект кнопке
    val outerGradient = Brush.verticalGradient(
        colors = listOf(outerRingLight, outerRingDark),
        startY = centerY - radius,
        endY = centerY + radius
    )
    drawCircle(
        brush = outerGradient,
        radius = radius,
        center = Offset(centerX, centerY)
    )

    // Внутренняя фаска/затемнение внешнего кольца (темная обводка внутри)
    drawCircle(
        color = Color(0x25000000),
        radius = radius * 0.96f,
        center = Offset(centerX, centerY),
        style = Stroke(width = radius * 0.04f)
    )

    // ==========================================
    // СЛОЙ 3: ВНУТРЕННИЙ КРУГ С ГЛУБИНОЙ
    // ==========================================
    // Радиальный градиент со смещением центра вверх создает эффект сферического объема
    val innerGradient = Brush.radialGradient(
        colors = listOf(innerBgLight, innerBgDark),
        center = Offset(centerX, centerY - radius * 0.2f),
        radius = radius * 0.8f
    )
    val innerRadius = radius * 0.78f
    drawCircle(
        brush = innerGradient,
        radius = innerRadius,
        center = Offset(centerX, centerY)
    )

    // Темный внутренний контур для отделения центра от кольца
    drawCircle(
        color = Color(0x40003050),
        radius = innerRadius,
        center = Offset(centerX, centerY),
        style = Stroke(width = radius * 0.02f)
    )

    // ==========================================
    // СЛОЙ 4: ЗНАЧОК СБРОСА (СТРЕЛКА ОБНОВЛЕНИЯ)
    // ==========================================
    val arrowRadius = innerRadius * 0.45f
    val strokeWidth = innerRadius * 0.20f

    // 4.1 Круговая дуга стрелки (разомкнутая сверху справа)
    val arrowPath = Path().apply {
        addArc(
            oval = androidx.compose.ui.geometry.Rect(
                centerX - arrowRadius,
                centerY - arrowRadius,
                centerX + arrowRadius,
                centerY + arrowRadius
            ),
            startAngleDegrees = -15f,  // Начинаем чуть ниже верха справа
            sweepAngleDegrees = 250f  // Идем по часовой стрелке почти до конца
        )
    }

    drawPath(
        path = arrowPath,
        color = darkBlueArrow,
        style = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Square,
            join = StrokeJoin.Miter
        )
    )

    // 4.2 Треугольный наконечник стрелки
    val tipPath = Path().apply {
        // Координаты точки окончания дуги
        moveTo(centerX + arrowRadius * 0.65f, centerY - arrowRadius * 0.8f)
        lineTo(centerX + arrowRadius * 1.35f, centerY - arrowRadius * 0.5f)
        lineTo(centerX + arrowRadius * 0.55f, centerY + arrowRadius * 0.2f)
        close()
    }
    drawPath(path = tipPath, color = darkBlueArrow)

    // ==========================================
    // СЛОЙ 5: СТЕНЯНОЙ ГЛЯНЦЕВЫЙ БЛИК
    // ==========================================
    // Овальный полупрозрачный белый блик в верхней части кнопки
    val glareGradient = Brush.verticalGradient(
        colors = listOf(Color(0x99FFFFFF), Color(0x00FFFFFF)),
        startY = centerY - radius * 0.9f,
        endY = centerY + radius * 0.1f
    )

    // Обрезаем блик по границе внутреннего круга
    val nativeCanvas = drawContext.canvas.nativeCanvas
    val checkpoint = nativeCanvas.save()

    val clipPath = Path().apply {
        //addCircle(centerX, centerY, innerRadius)
    }

    // Делаем сдвинутый овал для имитации отражения света на линзе
    withTransform({
        clipPath(clipPath)
    }) {
        drawOval(
            brush = glareGradient,
            topLeft = Offset(centerX - radius * 0.8f, centerY - radius * 0.95f),
            size = Size(radius * 1.6f, radius * 0.9f)
        )
    }
    nativeCanvas.restoreToCount(checkpoint)
}

@Composable
fun ResetButton(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val minSide = size.width.coerceAtMost(size.height)
        // Оставляем место под падающую тень снизу
        drawResetButton(
            centerX = size.width * 0.5f,
            centerY = size.height * 0.46f,
            size = minSide * 0.8f
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ResetButtonPreview() {
    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        ResetButton(modifier = Modifier.fillMaxSize())
    }
}