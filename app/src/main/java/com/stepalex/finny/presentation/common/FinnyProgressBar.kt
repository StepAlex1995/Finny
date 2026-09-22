package com.stepalex.finny.presentation.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Класс-конфигуратор для стилизации каждого элемента
data class FinnyProgressStyle(
    val brush: Brush,                   // Цвет или градиент элемента
    val strokeWidth: Float = 0f,        // Толщина обводки (0f для сплошной заливки)
    val cornerRadius: Dp = 0.dp,        // Скругление углов
) {
    // Вспомогательный конструктор для простой сплошной заливки цветом
    constructor(color: Color, strokeWidth: Float = 0f, cornerRadius: Dp = 0.dp) : this(
        brush = SolidColor(color),
        strokeWidth = strokeWidth,
        cornerRadius = cornerRadius
    )
}

// Стиль для засечек (делений)
data class FinnyTickStyle(
    val width: Dp = 2.dp,                    // Ширина засечки
    val inactiveColor: Color = Color.Gray,   // Цвет, пока прогресс не дошел
    val activeColor: Color = Color.White,    // Цвет, когда прогресс накрыл засечку
    val extraHeight: Dp = 0.dp,              // Насколько засечка выступает СВЕРХУ и СНИЗУ
)

@Composable
fun FinnyProgressBar(
    progress: Float,                               // Значение от 0.0f до 1.0f
    trackStyle: FinnyProgressStyle,                 // Стилизация дорожки (заднего фона)
    progressStyle: FinnyProgressStyle,              // Стилизация заполняющего прогресса
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,                               // Высота индикатора
    segmentsCount: Int = 1,                          // Кол-во сегментов
    tickStyle: FinnyTickStyle = FinnyTickStyle(),    // Стилизация засечек
    animate: Boolean = true,                         // Плавная анимация изменения прогресса
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        label = "FinnyProgressBarAnimation"
    )
    val currentProgress = if (animate) animatedProgress else progress.coerceIn(0f, 1f)
    // Общая высота Canvas теперь включает высоту бара + выступы засечек с двух сторон
    val totalCanvasHeight = height + (tickStyle.extraHeight * 2)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(totalCanvasHeight)
    ) {
        val width = size.width
        val progressBarHeightPx = height.toPx()
        val extraHeightPx = tickStyle.extraHeight.toPx()

        // Вычисляем Y-координату для верхней границы дорожки прогресса,
        // чтобы она была идеально отцентрирована по вертикали
        val trackTopY = extraHeightPx

        // --- 1. Отрисовка Дорожки (Track) ---
        val trackCornerRadiusPx = trackStyle.cornerRadius.toPx()
        val trackDrawStyle: DrawStyle = if (trackStyle.strokeWidth > 0f) {
            Stroke(width = trackStyle.strokeWidth, join = StrokeJoin.Round)
        } else {
            Fill
        }

        drawRoundRect(
            brush = trackStyle.brush,
            topLeft = Offset(0f, trackTopY), // Смещаем дорожку вниз на размер верхнего выступа
            size = Size(width, progressBarHeightPx),
            cornerRadius = CornerRadius(trackCornerRadiusPx, trackCornerRadiusPx),
            style = trackDrawStyle
        )
        // --- 2. Отрисовка Прогресса (Progress) ---
        if (currentProgress > 0f) {
            val progressCornerRadiusPx = progressStyle.cornerRadius.toPx()
            val progressDrawStyle: DrawStyle = if (progressStyle.strokeWidth > 0f) {
                Stroke(width = progressStyle.strokeWidth, join = StrokeJoin.Round)
            } else {
                Fill
            }

            drawRoundRect(
                brush = progressStyle.brush,
                topLeft = Offset(0f, trackTopY), // Смещаем прогресс аналогично дорожке
                size = Size(width * currentProgress, progressBarHeightPx),
                cornerRadius = CornerRadius(progressCornerRadiusPx, progressCornerRadiusPx),
                style = progressDrawStyle
            )
        }
        // --- 3. Отрисовка засечек (Ticks) ---
        if (segmentsCount > 1) {
            val tickWidthPx = tickStyle.width.toPx()
            val step = width / segmentsCount

            for (i in 1..<segmentsCount) {
                val tickX = step * i
                val tickProgressRatio = tickX / width

                val isColorActive = currentProgress >= tickProgressRatio
                val currentColor = if (isColorActive) tickStyle.activeColor else tickStyle.inactiveColor

                // Засечка начинается в самой верхней точке Canvas (0f)
                val startY = 0f
                // И идет до самого низа Canvas (полная высота)
                val endY = size.height

                val correctedX = tickX - (tickWidthPx / 2f)

                drawRect(
                    color = currentColor,
                    topLeft = Offset(correctedX, startY),
                    size = Size(tickWidthPx, endY - startY)
                )
            }
        }
    }
}

@Preview
@Composable
fun FinnyProgressBarPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(102, 102, 102)),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FinnyProgressBar(
            progress = 0.7f,
            height = 8.dp,
            trackStyle = FinnyProgressStyle(
                color = Color.LightGray,
                cornerRadius = 4.dp
            ),
            progressStyle = FinnyProgressStyle(
                color = Color(0xFF4CAF50),
                cornerRadius = 4.dp
            ),
            segmentsCount = 4,
            tickStyle = FinnyTickStyle(inactiveColor = Color.Red, activeColor = Color(0xFF4CAF50),extraHeight = 2.dp),
            modifier = Modifier.padding(16.dp)
        )

    }

}
