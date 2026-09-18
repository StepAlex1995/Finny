package com.stepalex.finny.presentation.items


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
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.sin

fun DrawScope.drawCrown(
    centerX: Float,
    centerY: Float,
    size: Float,
    rotationDegrees: Float = 0f,       // Наклон по умолчанию для кокетливого мультяшного вида
    colorTheme: PetItemColor = PetItemColor.Red, // Цвет бархатной подложки шапки
    sparklePhase: Float = 0f
) {
    val r = size / 2f

    // Цвета строго по стилю короны
    val goldColor = colorTheme.base//Color(0xFFFFD400)    // Сочное мультяшное золото
    val goldShadow = lerp(goldColor,Color.Black,0.25f)//Color(0xFFD4A000)   // Темно-горчичная тень для золотых зубцов
    val outlineColor = Color.Black       // Черный контур
    val strokeWidth = size * 0.032f      // Тонкий контур аксессуара

    withTransform({
        // Смещаем холст в локальный центр короны и заваливаем на бок
        translate(left = centerX, top = centerY)
        rotate(degrees = rotationDegrees, pivot = Offset.Zero)
    }) {
        // ================= 1. ПОСТРОЕНИЕ БАРХАТНОЙ КУПОЛЬНОЙ ПОДЛОЖКИ =================
        val velvetW = r * 1.1f
        val velvetH = r * 0.5f
        val velvetPath = Path().apply {
            moveTo(-velvetW * 0.8f, 0f)
            cubicTo(-velvetW * 0.9f, -velvetH, velvetW * 0.9f, -velvetH, velvetW * 0.8f, 0f)
            close()
        }

        // ================= 2. ПОСТРОЕНИЕ ЗОЛОТЫХ ЗУБЦЕВ КОРОНЫ (3 КЛАССИЧЕСКИХ ПИКА) =================
        val baseW = r * 1.2f
        val crownHeight = r * 1.0f

        val goldCrownPath = Path().apply {
            moveTo(-baseW, 0f)                                     // Левый нижний угол основания
            lineTo(-baseW * 0.9f, -crownHeight * 0.7f)             // Подъем к левому малому зубцу
            lineTo(
                -baseW * 0.4f,
                -crownHeight * 0.35f
            )            // Спуск во внутреннюю левую ложбинку
            lineTo(
                0f,
                -crownHeight
            )                               // Подъем к ГЛАВНОМУ центральному зубцу
            lineTo(
                baseW * 0.4f,
                -crownHeight * 0.35f
            )             // Спуск во внутреннюю правую ложбинку
            lineTo(baseW * 0.9f, -crownHeight * 0.7f)              // Подъем к правому малому зубцу
            lineTo(baseW, 0f)                                      // Спуск к правому нижнему углу
            close()
        }

        // ================= 3. ОСНОВАНИЕ КОРОНЫ (ОБРУЧ) =================
        val brimW = baseW * 1.05f
        val brimH = r * 0.16f
        val brimRect = RoundRect(
            left = -brimW, top = -brimH / 2f,
            right = brimW, bottom = brimH / 2f,
            cornerRadius = CornerRadius(brimW * 0.2f, brimH * 0.5f)
        )
        val brimPath = Path().apply { addRoundRect(brimRect) }

        // ================= 4. ОТРЕНДЕРИВАНИЕ СЛОЕВ ЗАЛИВКИ И КОНТУРА =================
        // 1. Рисуем мягкую бархатную подложку шапки
        drawPath(path = velvetPath, color = colorTheme.base)
        drawPath(
            path = velvetPath,
            color = colorTheme.shadow,
            style = Stroke(width = strokeWidth * 0.5f),
            alpha = 0.4f
        )
        drawPath(path = velvetPath, color = outlineColor, style = Stroke(width = strokeWidth))

        // 2. Рисуем золотые зубцы короны, перекрывающие бархат
        drawPath(path = goldCrownPath, color = goldColor)
        drawPath(path = goldCrownPath, color = outlineColor, style = Stroke(width = strokeWidth))

        // 3. Рисуем золотой обруч основания короны поверх зубцов
        drawPath(path = brimPath, color = goldColor)
        drawPath(path = brimPath, color = outlineColor, style = Stroke(width = strokeWidth))

        // ================= 5. ГЛУБОКИЕ ТЕНИ, ДРАГОЦЕННЫЕ КАМНИ И БЛИКИ =================
        // Горчичные Безье-тени на левой стороне каждого зубца для мультяшного объема
        val crownShadow = Path().apply {
            moveTo(-baseW, 0f)
            lineTo(-baseW * 0.9f, -crownHeight * 0.7f)
            lineTo(-baseW * 0.75f, -crownHeight * 0.45f)
            lineTo(-baseW * 0.4f, -crownHeight * 0.35f)
            lineTo(-baseW * 0.15f, -crownHeight * 0.6f)
            lineTo(0f, -crownHeight)
            lineTo(baseW * 0.2f, -crownHeight * 0.5f)
            lineTo(baseW * 0.4f, -crownHeight * 0.35f)
            close()
        }
        drawPath(path = crownShadow, color = goldShadow, alpha = 0.4f)

        // Драгоценные ромбовидные камни на вершинах зубцов (цвет подложки создает крутой ансамбль!)
        fun drawGem(offsetX: Float, offsetY: Float, gemSize: Float) {
            val gR = gemSize / 2f
            val gemPath = Path().apply {
                moveTo(offsetX, offsetY - gR)
                lineTo(offsetX + gR, offsetY)
                lineTo(offsetX, offsetY + gR)
                lineTo(offsetX - gR, offsetY)
                close()
            }
            drawPath(path = gemPath, color = colorTheme.base)
            drawPath(
                path = gemPath,
                color = outlineColor,
                style = Stroke(width = strokeWidth * 0.8f)
            )
            // Крошечный блик на камне
            drawCircle(
                color = Color.White,
                radius = gemSize * 0.15f,
                center = Offset(offsetX - gR * 0.2f, offsetY - gR * 0.2f)
            )
        }

        // Рисуем 3 камня на пиках
        drawGem(
            offsetX = 0f,
            offsetY = -crownHeight,
            gemSize = size * 0.12f
        )                    // Центральный крупный
        drawGem(
            offsetX = -baseW * 0.9f,
            offsetY = -crownHeight * 0.7f,
            gemSize = size * 0.09f
        )  // Левый малый
        drawGem(
            offsetX = baseW * 0.9f,
            offsetY = -crownHeight * 0.7f,
            gemSize = size * 0.09f
        )   // Правый малый

        // Глянцевые белые круглые блики на золотом обруче основания
        drawOval(
            color = Color.White,
            topLeft = Offset(-brimW * 0.6f, -brimH * 0.2f),
            size = Size(brimW * 0.3f, brimH * 0.4f),
            alpha = 0.6f
        )
        drawOval(
            color = Color.White,
            topLeft = Offset(brimW * 0.4f, -brimH * 0.2f),
            size = Size(brimW * 0.15f, brimH * 0.4f),
            alpha = 0.6f
        )

        // ================= 6. МАССИВ ИЗ 10 МЕРЦАЮЩИХ ЗВЁЗД =================
        val sparkles = arrayOf(
            Offset(-baseW * 0.6f, -crownHeight * 0.5f), // 1. Левый зубец (центр)
            Offset(baseW * 0.6f, -crownHeight * 0.5f),  // 2. Правый зубец (центр)
            Offset(0f, -crownHeight * 0.6f),           // 3. Центральный зубец (середина)
            Offset(-baseW * 0.2f, -crownHeight * 0.25f),// 4. Ложбинка слева
            Offset(baseW * 0.2f, -crownHeight * 0.25f), // 5. Ложбинка справа
            Offset(-brimW * 0.8f, 0f),                  // 6. Обруч (крайний левый угол)
            Offset(brimW * 0.8f, 0f),                   // 7. Обруч (крайний правый угол)
            Offset(0f, brimH * 0.1f),                   // 8. Обруч (центр низ)
            Offset(-baseW * 0.4f, -velvetH * 0.6f),     // 9. Просвет бархата (слева)
            Offset(baseW * 0.4f, -velvetH * 0.6f)       // 10. Просвет бархата (справа)
        )

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

            val currentAlpha = ((sin(sparklePhase + index * 1.8f) + 1f) / 2f).coerceIn(0f, 1f)
            drawPath(path = starPath, color = Color.White, alpha = currentAlpha)
        }
    }
}

@Composable
fun Crown(
    modifier: Modifier,
    colorTheme: PetItemColor = PetItemColor.Gold,
    sparklePhase: Float = 0f,
) {
    Canvas(modifier = modifier) {
        drawCrown(
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
fun CrownPreview() {
    Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
        Crown(
            modifier = Modifier.fillMaxSize(),
        )
    }
}