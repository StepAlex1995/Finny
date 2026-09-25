package com.stepalex.finny.presentation.pet_room_bgr

import android.graphics.CornerPathEffect
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

fun DrawScope.drawStar(
    centerX: Float,
    centerY: Float,
    size: Float,
    cornerRadiusDp: Float = 12f // Радиус скругления углов
) {
    val path = Path()

    // Строгая геометрия правильной пятиконечной звезды
    val outerRadius = size / 2f
    // Математически идеальное соотношение для классической равносторонней звезды
    val innerRadius = outerRadius * 0.38196f

    val pointsCount = 10
    val angleStep = Math.PI / 5

    // Поворачиваем на -90 градусов, чтобы верхний луч смотрел строго вверх
    var currentAngle = -Math.PI / 2

    for (i in 0 until pointsCount) {
        val radius = if (i % 2 == 0) outerRadius else innerRadius

        val x = centerX + cos(currentAngle).toFloat() * radius
        val y = centerY + sin(currentAngle).toFloat() * radius

        if (i == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }

        currentAngle += angleStep
    }
    path.close()

    // Переводим радиус скругления из Dp в пиксели холста
    val cornerRadiusPx = cornerRadiusDp * density

    // Используем drawIntoCanvas для доступа к нативным возможностям отрисовки Android
    drawIntoCanvas { canvas ->
        val nativePaint = android.graphics.Paint().apply {
            isAntiAlias = true
            // Применяем аппаратное скругление углов к Path
            pathEffect = CornerPathEffect(cornerRadiusPx)
        }

        // 1. Рисуем тело звезды (Желтая заливка)
        nativePaint.style = android.graphics.Paint.Style.FILL
        nativePaint.color = android.graphics.Color.parseColor("#FFFFE359")
        canvas.nativeCanvas.drawPath(path.asAndroidPath(), nativePaint)

        // 2. Рисуем контур (Черная обводка)
        nativePaint.style = android.graphics.Paint.Style.STROKE
        nativePaint.strokeWidth = outerRadius * 0.08f // Толщина обводки пропорциональна размеру
        nativePaint.strokeJoin = android.graphics.Paint.Join.ROUND
        nativePaint.color = android.graphics.Color.parseColor("#FF232528")
        canvas.nativeCanvas.drawPath(path.asAndroidPath(), nativePaint)
    }
}

@Composable
fun Start(modifier: Modifier) {
    Canvas(modifier = modifier) {
        drawStar(
            centerX = size.width / 2f,
            centerY = size.height *0.55f,
            size = size.minDimension, // Масштабирует звезду под размер Canvas
            cornerRadiusDp = 15f
        )
    }
}

@Preview
@Composable
fun StartPreview() {
    Box(modifier = Modifier.size(200.dp)) {
        Start(modifier = Modifier.fillMaxSize())
    }
}