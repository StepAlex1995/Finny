package com.stepalex.finny.presentation.pets

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope


/**
 * Отрисовка щечек
 */
fun DrawScope.drawPetChecks(
    w: Float,
    h: Float,
    color: Color
) {
    drawOval(
        color = color,
        topLeft = Offset(w * 0.17f, h * 0.60f),
        size = Size(w * 0.16f, h * 0.13f)
    )
    drawOval(
        color = color,
        topLeft = Offset(w * 0.67f, h * 0.60f),
        size = Size(w * 0.16f, h * 0.13f)
    )
}