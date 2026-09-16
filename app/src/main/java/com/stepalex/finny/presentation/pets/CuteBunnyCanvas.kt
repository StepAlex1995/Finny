package com.stepalex.finny.presentation.pets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CuteBunnyCanvas(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .aspectRatio(1f) // Картинка квадратная
    ) {
        val w = size.width
        val h = size.height

        // Динамическая толщина обводки в зависимости от размера холста (~4.5% от ширины)
        val strokeWidth = w * 0.045f
        val strokeStyle = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )

        val outlineColor = Color.Black
        val fillColor = Color.White
        val blushColor = Color(0xFFFEE1E1) // Нежно-розовые щечки

        // --- Построение объединенного тела (голова + уши) с помощью овалов ---
        val bodyPath = Path().apply {
            // Овал головы
            addOval(
                Rect(
                    left = w * 0.08f,
                    top = h * 0.32f,
                    right = w * 0.92f,
                    bottom = h * 0.9f
                )
            )
        }

        val leftEarPath = Path().apply {
            // Левое ушко (вертикальный овал)
            addOval(
                Rect(
                    left = w * 0.23f,
                    top = h * 0.10f,
                    right = w * 0.45f,
                    bottom = h * 0.55f
                )
            )
        }

        val rightEarPath = Path().apply {
            // Правое ушко (вертикальный овал)
            addOval(
                Rect(
                    left = w * 0.55f,
                    top = h * 0.10f,
                    right = w * 0.77f,
                    bottom = h * 0.55f
                )
            )
        }

        // Объединяем уши и голову в один общий контур без внутренних швов
        val fullBunnyPath = Path().apply {
            op(bodyPath, leftEarPath, PathOperation.Union)
            op(this, rightEarPath, PathOperation.Union)
        }

        // Рисуем заливку тела и ушей
        drawPath(path = fullBunnyPath, color = fillColor)
        // Рисуем толстый черный контур тела и ушей
        drawPath(path = fullBunnyPath, color = outlineColor, style = strokeStyle)

        // --- Щечки (Розовые овалы) ---
        // Левая щечка
        drawOval(
            color = blushColor,
            topLeft = Offset(w * 0.17f, h * 0.60f),
            size = Size(w * 0.16f, h * 0.13f)
        )
        // Правая щечка
        drawOval(
            color = blushColor,
            topLeft = Offset(w * 0.67f, h * 0.60f),
            size = Size(w * 0.16f, h * 0.13f)
        )

        // --- Глаза (Круги) ---
        // Левый глаз
        drawCircle(
            color = outlineColor,
            radius = w * 0.045f,
            center = Offset(w * 0.34f, h * 0.63f)
        )
        // Правый глаз
        drawCircle(
            color = outlineColor,
            radius = w * 0.045f,
            center = Offset(w * 0.66f, h * 0.63f)
        )

        // --- Рот (W-образная линия из двух дуг) ---
        val mouthPath = Path().apply {
            // Левая половинка ротика
            arcTo(
                rect = Rect(
                    left = w * 0.435f,
                    top = h * 0.64f,
                    right = w * 0.50f,
                    bottom = h * 0.69f
                ),
                startAngleDegrees = 180f,
                sweepAngleDegrees = -180f,
                forceMoveTo = true
            )
            // Правая половинка ротика
            arcTo(
                rect = Rect(
                    left = w * 0.50f,
                    top = h * 0.64f,
                    right = w * 0.565f,
                    bottom = h * 0.69f
                ),
                startAngleDegrees = 180f,
                sweepAngleDegrees = -180f,
                forceMoveTo = false
            )
        }
        drawPath(path = mouthPath, color = outlineColor, style = strokeStyle)

        // --- Лапки (Круги внизу, перекрывающие контур тела) ---
        val leftFootCenter = Offset(w * 0.20f, h * 0.83f)
        val rightFootCenter = Offset(w * 0.80f, h * 0.83f)
        val footRadius = w * 0.11f

        // Заливка лапок белым цветом (чтобы спрятать под собой линию живота)
        drawCircle(color = fillColor, radius = footRadius, center = leftFootCenter)
        drawCircle(color = fillColor, radius = footRadius, center = rightFootCenter)

        // Обводка лапок
        drawCircle(color = outlineColor, radius = footRadius, center = leftFootCenter, style = strokeStyle)
        drawCircle(color = outlineColor, radius = footRadius, center = rightFootCenter, style = strokeStyle)
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 400)
@Composable
fun BunnyPreview() {
    CuteBunnyCanvas()
}