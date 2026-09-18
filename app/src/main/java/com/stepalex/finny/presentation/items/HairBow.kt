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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.sin

fun DrawScope.drawHairBow(
    centerX: Float,
    centerY: Float,
    size: Float,
    rotationDegrees: Float = 0f,       // Новый параметр: угол наклона банта
    colorTheme: PetItemColor = PetItemColor.Red,
    sparklePhase: Float = 0f
) {
    val r = size / 2f
    val baseColor = colorTheme.base
    val darkShadow = colorTheme.shadow
    val outlineColor = Color.Black
    val strokeWidth = size * 0.032f // Тонкий утонченный контур аксессуара

    withTransform({
        // Смещаем холст в центр банта и поворачиваем на заданный угол!
        translate(left = centerX, top = centerY)
        rotate(degrees = rotationDegrees, pivot = Offset.Zero)
    }) {
        // ================= 1. ПОСТРОЕНИЕ ГЕОМЕТРИИ НИЖНИХ ЛЕНТ (ХВОСТИКОВ) =================
        val leftRibbonPath = Path().apply {
            moveTo(-r * 0.1f, r * 0.2f)
            // Левый край ленты тянется вниз-влево
            cubicTo(-r * 0.2f, r * 0.6f, -r * 0.4f, r * 1.1f, -r * 0.5f, r * 1.5f)
            // Треугольный вырез на конце ленты
            lineTo(-r * 0.25f, r * 1.35f)
            lineTo(0f, r * 1.45f)
            // Возврат к центру под узел
            cubicTo(-r * 0.05f, r * 0.9f, -r * 0.05f, r * 0.5f, -r * 0.1f, r * 0.2f)
            close()
        }

        val rightRibbonPath = Path().apply {
            moveTo(r * 0.1f, r * 0.2f)
            cubicTo(r * 0.2f, r * 0.6f, r * 0.4f, r * 1.1f, r * 0.5f, r * 1.5f)
            lineTo(r * 0.25f, r * 1.35f)
            lineTo(0f, r * 1.45f)
            cubicTo(r * 0.05f, r * 0.9f, r * 0.05f, r * 0.5f, r * 0.1f, r * 0.2f)
            close()
        }

        // ================= 2. ПОСТРОЕНИЕ ПЫШНЫХ КРЫЛЬЕВ ДЕВОЧКЕ =================
        val leftWingPath = Path().apply {
            moveTo(0f, 0f)
            // Верхняя дуга уходит сильно вверх и расширяется на конце (в отличие от бабочки)
            cubicTo(-r * 0.3f, -r * 0.6f, -r * 0.7f, -r * 1.0f, -r * 1.1f, -r * 0.8f)
            // Объемный пышный торец крыла
            cubicTo(-r * 1.3f, -r * 0.5f, -r * 1.2f, r * 0.3f, -r * 0.9f, r * 0.5f)
            // Возврат к центру
            cubicTo(-r * 0.6f, r * 0.6f, -r * 0.2f, r * 0.3f, 0f, 0f)
            close()
        }

        val rightWingPath = Path().apply {
            moveTo(0f, 0f)
            cubicTo(r * 0.3f, -r * 0.6f, r * 0.7f, -r * 1.0f, r * 1.1f, -r * 0.8f)
            cubicTo(r * 1.3f, -r * 0.5f, r * 1.2f, r * 0.3f, r * 0.9f, r * 0.5f)
            cubicTo(r * 0.6f, r * 0.6f, r * 0.2f, r * 0.3f, 0f, 0f)
            close()
        }

        // ================= 3. ОТРЕНДЕРИВАНИЕ СЛОЕВ ЗАЛИВКИ И КОНТУРА =================
        // Сначала ленты (чтобы они визуально лежали под бантом)
        drawPath(path = leftRibbonPath, color = baseColor)
        drawPath(path = leftRibbonPath, color = outlineColor, style = Stroke(width = strokeWidth))
        drawPath(path = rightRibbonPath, color = baseColor)
        drawPath(path = rightRibbonPath, color = outlineColor, style = Stroke(width = strokeWidth))

        // Затем пышные крылья банта
        drawPath(path = leftWingPath, color = baseColor)
        drawPath(path = leftWingPath, color = outlineColor, style = Stroke(width = strokeWidth))
        drawPath(path = rightWingPath, color = baseColor)
        drawPath(path = rightWingPath, color = outlineColor, style = Stroke(width = strokeWidth))

        // ================= 4. ГЛУБОКИЕ МУЛЬТЯШНЫЕ ТЕНИ И СКЛАДКИ =================
        // Большие складки ткани у основания крыльев банта
        val foldLeft = Path().apply {
            moveTo(-r * 0.15f, -r * 0.05f)
            cubicTo(-r * 0.5f, -r * 0.4f, -r * 0.8f, -r * 0.3f, -r * 0.9f, -r * 0.1f)
            lineTo(-r * 0.75f, 0f)
            close()
        }
        val foldRight = Path().apply {
            moveTo(r * 0.15f, -r * 0.05f)
            cubicTo(r * 0.5f, -r * 0.4f, r * 0.8f, -r * 0.3f, r * 0.9f, -r * 0.1f)
            lineTo(r * 0.75f, 0f)
            close()
        }
        drawPath(path = foldLeft, color = darkShadow)
        drawPath(path = foldRight, color = darkShadow)

        // ================= 5. ЦЕНТРАЛЬНЫЙ СКРУГЛЕННЫЙ УЗЕЛ БАНТА =================
        val nodeSize = r * 0.55f
        val nodeRect = RoundRect(
            left = -nodeSize / 2f, top = -nodeSize / 2f,
            right = nodeSize / 2f, bottom = nodeSize / 2f,
            cornerRadius = CornerRadius(x = nodeSize * 0.35f, y = nodeSize * 0.35f)
        )
        val nodePath = Path().apply { addRoundRect(nodeRect) }
        drawPath(path = nodePath, color = baseColor)
        drawPath(path = nodePath, color = outlineColor, style = Stroke(width = strokeWidth))

        // ================= 6. ГЛЯНЦЕВЫЕ БЛИКИ (КАПЛИ РАДОСТИ) =================
        // Круглый блик на центральном узле
        drawOval(color = Color.White, topLeft = Offset(nodeSize * 0.08f, -nodeSize * 0.3f), size = Size(nodeSize * 0.25f, nodeSize * 0.25f))
        // Огромный блик вдоль изгиба левого ушка
        drawOval(color = Color.White, topLeft = Offset(-r * 0.85f, -r * 0.65f), size = Size(r * 0.30f, r * 0.12f))
        // Симметричный блик на правом ушке
        drawOval(color = Color.White, topLeft = Offset(r * 0.55f, -r * 0.65f), size = Size(r * 0.30f, r * 0.12f))

        // ================= 7. МАССИВ ИЗ 10 МЕРЦАЮЩИХ ЗВЁЗД =================
        val goldColor = Color(0xFFFFFFFF)

        // 10 точек, распределенных по ушкам, узлу и нижним лентам
        val sparkles = arrayOf(
            Offset(-r * 0.60f, -r * 0.40f), // 1. Левое ушко (верх)
            Offset(r * 0.60f, -r * 0.40f),  // 2. Правое ушко (верх)
            Offset(-r * 0.75f, r * 0.10f),  // 3. Левое ушко (низ)
            Offset(r * 0.75f, r * 0.10f),   // 4. Правое ушко (низ)
            Offset(-r * 0.25f, r * 0.90f),  // 5. Левая нижняя лента
            Offset(r * 0.25f, r * 0.90f),   // 6. Правая нижняя лента
            Offset(0f, -nodeSize * 0.2f),   // 7. По центру узла
            Offset(-r * 0.35f, -r * 0.35f), // 8. Складка слева
            Offset(r * 0.35f, -r * 0.35f),  // 9. Складка справа
            Offset(-r * 0.40f, r * 1.25f)   // 10. Самый кончик левой ленты
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
            drawPath(path = starPath, color = goldColor, alpha = currentAlpha)
        }
    }
}

@Composable
fun HairBow(
    modifier: Modifier,
    colorTheme: PetItemColor = PetItemColor.Blue,
    sparklePhase: Float = 0f,
    rotationDegrees: Float
) {
    Canvas(modifier = modifier) {
        drawHairBow(
            size.width * 0.5f,
            size.height * 0.45f,
            size = size.width * 0.65f,
            colorTheme = colorTheme,
            sparklePhase = sparklePhase,
            rotationDegrees = rotationDegrees
        )
    }
}

@Preview
@Composable
fun HairBowPreview() {
    Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
        HairBow(
            modifier = Modifier.fillMaxSize(),
            rotationDegrees = 30f,
        )
    }
}