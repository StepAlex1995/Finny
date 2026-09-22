package com.stepalex.finny.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp


@Composable
fun OutlineText(
    text: String,
    alignment: Alignment = Alignment.Center,
    fontSize: TextUnit = 16.sp,
    textColor: Color = Color.White,
    outlineColor: Color = Color(0xFF232528),
    modifier: Modifier = Modifier,
    strokeSize: Float = fontSize.value / 2f,
    fontFamily: FontFamily = FontFamily.SansSerif
) {
    val textAlign = when (alignment) {
        Alignment.CenterStart, Alignment.TopStart, Alignment.BottomStart -> TextAlign.Start
        Alignment.CenterEnd, Alignment.TopEnd, Alignment.BottomEnd -> TextAlign.End
        else -> TextAlign.Center
    }

    Box(
        modifier = modifier,
        contentAlignment = alignment
    ) {
        Text(
            text = text,
            textAlign = textAlign,
            softWrap = false, // Запрещаем перенос строки из-за нехватки места
            overflow = TextOverflow.Visible, // Разрешаем тексту выходить за рамки контейнера
            style = TextStyle(
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                color = outlineColor,
                drawStyle = Stroke(
                    width = strokeSize,
                    join = StrokeJoin.Round
                ),
                fontFamily = fontFamily
            )
        )
        Text(
            text = text,
            textAlign = textAlign,
            softWrap = false,
            overflow = TextOverflow.Visible, // Разрешаем тексту выходить за рамки контейнера
            style = TextStyle(
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                color = textColor,
                fontFamily = fontFamily
            )
        )
    }
}


@Preview
@Composable
fun OutlineTextPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(102, 102, 102)),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlineText(
            text = "15",
            fontSize = 72.sp,
            textColor = Color.DarkGray,
            outlineColor = Color.White,
            strokeSize = 12f
        )
        OutlineText(text = "15", fontSize = 60.sp, strokeSize = 12f)
        OutlineText(text = "15", fontSize = 60.sp)
        OutlineText(text = "15", fontSize = 15.sp)

    }

}
