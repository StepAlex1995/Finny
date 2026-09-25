package com.stepalex.finny.presentation.common.items


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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.sin

fun DrawScope.drawTopHat(
    centerX: Float,
    centerY: Float,
    size: Float,
    colorTheme: PetItemColor = PetItemColor.Red, // Цвет ленты на шляпе
    rotationDegrees: Float = 0f,
    sparklePhase: Float = 0f
) {
    val r = size / 2f

    // Цвета строго по стилю
    val hatColor = colorTheme.base//Color(0xFFFF1A1A)     // Матовый глубокий чёрный цвет шляпы
    val hatShadow = Color.Black          // Абсолютно чёрный для глубоких теней
    val outlineColor = Color.Black       // Чёрный контур
    val strokeWidth = size * 0.032f      // Тонкий контур аксессуара

    withTransform({
        translate(left = centerX, top = centerY)
        rotate(degrees = rotationDegrees, pivot = Offset.Zero)
    }) {
        // ================= 1. ПОСТРОЕНИЕ ТУЛЬИ (ВЫСОКАЯ ЧАСТЬ ШЛЯПЫ) =================
        // Тулья слегка расширяется кверху, как у каноничного цилиндра
        val topWidth = r * 0.85f
        val bottomWidth = r * 0.70f
        val hatHeight = r * 1.1f

        val crownPath = Path().apply {
            moveTo(-bottomWidth, 0f)                                     // Нижний левый угол
            lineTo(-topWidth, -hatHeight)                                // Верхний левый угол
            quadraticTo(0f, -hatHeight - (r * 0.08f), topWidth, -hatHeight) // Скругленный верх
            lineTo(bottomWidth, 0f)                                      // Нижний правый угол
            close()
        }

        // ================= 2. ПОСТРОЕНИЕ ПОЛЕЙ ШЛЯПЫ (ИЗОГНУТЫЙ ЭЛЛИПС) =================
        val brimWidth = r * 1.3f
        val brimHeight = r * 0.22f
        val brimPath = Path().apply {
            addRoundRect(
                RoundRect(
                    left = -brimWidth, top = -brimHeight / 2f,
                    right = brimWidth, bottom = brimHeight / 2f,
                    cornerRadius = CornerRadius(brimWidth * 0.5f, brimHeight * 0.5f)
                )
            )
        }

        // ================= 3. ПОСТРОЕНИЕ ЛЕНТЫ У ОСНОВАНИЯ =================
        val ribbonHeight = r * 0.16f
        val ribbonPath = Path().apply {
            moveTo(-bottomWidth, 0f)
            lineTo(-bottomWidth * 0.96f, -ribbonHeight)
            quadraticTo(0f, -ribbonHeight - (r * 0.02f), bottomWidth * 0.96f, -ribbonHeight)
            lineTo(bottomWidth, 0f)
            close()
        }

        // ================= 4. ОТРЕНДЕРИВАНИЕ СЛОЕВ ЗАЛИВКИ И КОНТУРА =================
        // 1. Рисуем тулью (Заливка + Контур)
        drawPath(path = crownPath, color = hatColor)
        drawPath(path = crownPath, color = outlineColor, style = Stroke(width = strokeWidth))

        // 2. Поверх тульи рисуем цветную ленту джентльмена
        drawPath(path = ribbonPath, color = colorTheme.shadow)
        drawPath(path = ribbonPath, color = colorTheme.shadow, style = Stroke(width = strokeWidth * 0.5f), alpha = 0.5f) // Внутренняя дельта
        drawPath(path = ribbonPath, color = colorTheme.shadow, style = Stroke(width = strokeWidth))

        // 3. Рисуем поля шляпы, которые перекроют основание тульи и ленты
        drawPath(path = brimPath, color = hatColor)
        drawPath(path = brimPath, color = outlineColor, style = Stroke(width = strokeWidth))

        // ================= 5. ГЛУБОКИЕ ТЕНИ И МУЛЬТЯШНЫЕ БЛИКИ =================
        // Бордовая полутень на цветной ленте (левая половина)
        val ribbonShadow = Path().apply {
            moveTo(-bottomWidth, 0f)
            lineTo(-bottomWidth * 0.96f, -ribbonHeight)
            quadraticTo(-bottomWidth * 0.4f, -ribbonHeight, 0f, 0f)
            close()
        }
        //drawPath(path = ribbonShadow, color = colorTheme.shadow, alpha = 0.6f)

        // Вертикальный глянцевый белый блик вдоль правой стороны тульи
        drawOval(
            color = Color.White,
            topLeft = Offset(topWidth * 0.45f, -hatHeight * 0.9f),
            size = Size(r * 0.08f, hatHeight * 0.8f),
            alpha = 0.25f // Мягкий блик на черном цилиндре
        )

        // Горизонтальный глянцевый блик на переднем крае полей шляпы
        drawOval(
            color = Color.White,
            topLeft = Offset(-brimWidth * 0.5f, brimHeight * 0.1f),
            size = Size(brimWidth * 0.4f, brimHeight * 0.2f),
            alpha = 0.3f
        )

        // ================= 6. МАССИВ ИЗ 10 МЕРЦАЮЩИХ ЗВЁЗД =================
        val goldColor = Color(0xFFFFFFFF)

        // 10 звёздочек, раскиданных по тулье, цветной ленте и полям шляпы
        val sparkles = arrayOf(
            Offset(-topWidth * 0.4f, -hatHeight * 0.75f),  // 1. Тулья (верх лево)
            Offset(topWidth * 0.3f, -hatHeight * 0.65f),   // 2. Тулья (центр право)
            Offset(-bottomWidth * 0.5f, -hatHeight * 0.35f),// 3. Тулья (низ лево)
            Offset(bottomWidth * 0.6f, -hatHeight * 0.25f), // 4. Тулья (низ право)
            Offset(-bottomWidth * 0.6f, -ribbonHeight * 0.5f), // 5. На ленте (слева)
            Offset(bottomWidth * 0.4f, -ribbonHeight * 0.5f),  // 6. На ленте (справа)
            Offset(-brimWidth * 0.7f, 0f),                  // 7. Поля шляпы (крайний левый угол)
            Offset(brimWidth * 0.7f, 0f),                   // 8. Поля шляпы (крайний правый угол)
            Offset(-brimWidth * 0.2f, brimHeight * 0.2f),   // 9. Поля шляпы (центр низ)
            Offset(0f, -hatHeight * 0.85f)                  // 10. Самый верх тульи по центру
        )

        sparkles.forEachIndexed { index, offset ->
            val starSize = when (index % 3) {
                0 -> size * 0.17f
                1 -> size * 0.16f
                else -> size * 0.04f
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

            // Плавное поочередное мерцание по цепочке
            val currentAlpha = ((sin(sparklePhase + index * 1.8f) + 1f) / 2f).coerceIn(0f, 1f)
            drawPath(path = starPath, color = goldColor, alpha = currentAlpha)
        }
    }
}

@Composable
fun TopHat(
    modifier: Modifier,
    colorTheme: PetItemColor = PetItemColor.Black,
    sparklePhase: Float = 0f,
) {
    Canvas(modifier = modifier) {
        drawTopHat(
            size.width * 0.5f,
            size.height * 0.6f,
            size = size.width * 0.65f,
            colorTheme = colorTheme,
            sparklePhase = sparklePhase,
        )
    }
}

@Preview
@Composable
fun TopHatPreview() {
    Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
        TopHat(
            modifier = Modifier.fillMaxSize(),
        )
    }
}