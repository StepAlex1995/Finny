package com.stepalex.finny.presentation.common.items


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.sin

fun DrawScope.drawNeckTie(
    centerX: Float,
    centerY: Float,
    size: Float,
    colorTheme: PetItemColor = PetItemColor.Red, // Используем те же цвета из BowTieColor
    sparklePhase: Float = 0f
) {
    // В отличие от бабочки, галстук вытянут по вертикали, поэтому r — это базовая ширина
    val r = size / 2f

    val baseColor = colorTheme.base
    val darkShadow = colorTheme.shadow
    val outlineColor = Color.Black
    val strokeWidth = size * 0.032f

    withTransform({
        translate(left = centerX, top = centerY)
    }) {
        // ================= 1. ПОСТРОЕНИЕ ГЕОМЕТРИИ НИЖНЕГО ПОЛОТНА =================
        val tieBodyPath = Path().apply {
            moveTo(-r * 0.20f, r * 0.25f) // Стартуем из-под левого края узла
            // Левая грань: плавно расширяется книзу по кривой Безье
            cubicTo(-r * 0.22f, r * 0.7f, -r * 0.38f, r * 1.5f, -r * 0.45f, r * 2.1f)
            // Левый нижний скос к острию галстука
            lineTo(0f, r * 2.5f)
            // Правый нижний скос от острия вверх
            lineTo(r * 0.45f, r * 2.1f)
            // Правая грань: возвращается под узел
            cubicTo(r * 0.38f, r * 1.5f, r * 0.22f, r * 0.7f, r * 0.20f, r * 0.25f)
            close()
        }

        // ================= 2. ПОСТРОЕНИЕ ВЕРХНЕГО УЗЛА (ТРАПЕЦИЯ) =================
        val tieNodePath = Path().apply {
            moveTo(-r * 0.25f, -r * 0.3f) // Верхний левый угол
            lineTo(r * 0.25f, -r * 0.3f)  // Верхний правый угол
            lineTo(r * 0.18f, r * 0.28f)  // Нижний правый угол
            lineTo(-r * 0.18f, r * 0.28f) // Нижний левый угол
            close()
        }

        // ================= 3. ОТРИСОВКА ЗАЛИВКИ И КОНТУРОВ =================
        // Сначала рисуем полотно (Заливка + Контур)
        drawPath(path = tieBodyPath, color = baseColor)
        drawPath(path = tieBodyPath, color = outlineColor, style = Stroke(width = strokeWidth))

        // Поверх полотна рисуем узел (Заливка + Контур)
        drawPath(path = tieNodePath, color = baseColor)
        drawPath(path = tieNodePath, color = outlineColor, style = Stroke(width = strokeWidth))

        // ================= 4. СЛОЙ ОБЪЁМА: БОРДОВЫЕ СКЛАДКИ И ТЕНИ =================
        // Две маленькие складки Безье на узле (выходят из углов вниз к центру)
        val nodeShadowLeft = Path().apply {
            moveTo(-r * 0.22f, -r * 0.25f)
            quadraticTo(-r * 0.10f, 0f, -r * 0.05f, r * 0.2f)
            lineTo(-r * 0.15f, r * 0.25f)
            close()
        }
        drawPath(path = nodeShadowLeft, color = darkShadow)

        // Большая продольная полупрозрачная тень на левой половине полотна
        val bodyLeftShadow = Path().apply {
            moveTo(0f, r * 0.26f)
            cubicTo(-r * 0.15f, r * 0.7f, -r * 0.30f, r * 1.5f, -r * 0.43f, r * 2.05f)
            lineTo(0f, r * 2.45f)
            close()
        }
        drawPath(path = bodyLeftShadow, color = darkShadow, alpha = 0.5f)

        // ================= 5. ГЛЯНЦЕВЫЕ БЕЛЫЕ БЛИКИ =================
        // Блик на узле (аккуратный вертикальный овал справа)
        drawOval(
            color = Color.White,
            topLeft = Offset(r * 0.06f, -r * 0.22f),
            size = Size(r * 0.07f, r * 0.35f)
        )

        // Длинный элегантный блик-полоса вдоль правой грани полотна галстука
        withTransform({
            rotate(degrees = -6f, pivot = Offset(r * 0.18f, r * 0.8f))
        }) {
            drawOval(
                color = Color.White,
                topLeft = Offset(r * 0.18f, r * 0.5f),
                size = Size(r * 0.04f, r * 1.1f),
                alpha = 0.85f
            )
        }

        // ================= 6. СЛОЙ ИСКРОК ИЗ МАССИВА (С ТРЕБОВАНИЕМ НА 10 ЗВЁЗД) =================
        val goldColor = Color(0xFFFFFFFF)

        // МАССИВ ИЗ 10 ЗВЁЗД (Равномерно распределены по вертикали сверху вниз)
        val sparkles = arrayOf(
            Offset(-r * 0.12f, -r * 0.15f), // 1. На узле (слева)
            Offset(r * 0.12f, r * 1.0f),    // 2. По центру полотна
            Offset(-r * 0.15f, r * 1.8f),   // 3. Чуть выше нижнего острия (слева)
            Offset(r * 0.18f, r * 0.4f),    // 4. Верхняя треть полотна (справа)
            Offset(-r * 0.22f, r * 0.7f),   // 5. Верхняя треть полотна (слева в тени)
            Offset(r * 0.05f, -r * 0.05f),  // 6. На узле (справа у блика)
            Offset(-r * 0.05f, r * 1.4f),   // 7. Геометрический центр полотна
            Offset(r * 0.28f, r * 1.6f),    // 8. Нижняя треть полотна (справа)
            Offset(0f, r * 2.22f),          // 9. Прямо над нижним кончиком острия
            Offset(r * 0.35f, r * 1.1f)     // 10. Посередине полотна (ближе к правому краю)
        )

        // Отрисовка всех 10 звёзд
        sparkles.forEachIndexed { index, offset ->
            val starSize = when (index % 3) {
                0 -> size * 0.18f
                1 -> size * 0.16f
                else -> size * 0.045f
            }
            val sR = starSize / 2f

            val starPath = Path().apply {
                moveTo(offset.x, offset.y - sR)
                quadraticTo(offset.x, offset.y, offset.x + sR, offset.y)
                quadraticTo(offset.x, offset.y, offset.x, offset.y + sR)
                quadraticTo(offset.x, offset.y, offset.x - sR, offset.y)
                quadraticTo(offset.x, offset.y, offset.x, offset.y - sR)
                close()
            }

            // Плавный волновой перелив мерцания
            val currentAlpha = ((sin(sparklePhase + index * 1.8f) + 1f) / 2f).coerceIn(0f, 1f)
            drawPath(path = starPath, color = goldColor, alpha = currentAlpha)
        }
    }
}

@Composable
fun NeckTie(
    modifier: Modifier,
    colorTheme: PetItemColor = PetItemColor.Blue,
    sparklePhase: Float = 0f
) {
    Canvas(modifier = modifier) {
        drawNeckTie(
            centerX = size.width / 2f,
            centerY = size.height * 0.2f, // Смещаем центр чуть выше, так как полотно тянется вниз
            size = size.width * 0.5f,
            colorTheme = colorTheme,
            sparklePhase = sparklePhase
        )
    }
}

@Preview
@Composable
fun NeckTiePreview() {
    Box(
        modifier = Modifier
            .width(100.dp)
            .height(100.dp), contentAlignment = Alignment.Center
    ) {
        NeckTie(modifier = Modifier.fillMaxSize())
    }
}