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


fun DrawScope.drawBowTie(
    centerX: Float,
    centerY: Float,
    size: Float,
    colorTheme: PetItemColor = PetItemColor.Red,
    sparklePhase: Float = 0f
) {
    // Базовый радиус бабочки (половина от общего размера)
    val r = size / 2f

    // Настраиваем цвета строго по скриншоту
    val baseRed = colorTheme.base      // Сочный алый цвет бабочки
    val darkShadow = colorTheme.shadow   // Бордовая полутень для складок и объёма
    val outlineColor = Color.Black       // Чёрный контур

    // Настраиваем утончённый стиль линий (тоньше, чем у кролика)
    val strokeWidth = size * 0.032f

    // Смещаем холст в локальный центр бабочки, чтобы вся математика Безье считалась от нуля
    withTransform({
        translate(left = centerX, top = centerY)
    }) {
        // ================= 1. ПОСТРОЕНИЕ ГЕОМЕТРИИ КРЫЛЬЕВ (ЛЕВОЕ И ПРАВОЕ) =================
        val leftWingPath = Path().apply {
            moveTo(0f, 0f) // Стартуем от центрального узла
            // Верхняя дуга левого крыла: тянем влево-вверх и закругляем на краю
            cubicTo(-r * 0.4f, -r * 0.4f, -r * 0.9f, -r * 0.9f, -r, -r * 0.6f)
            // Боковой левый торец крыла: спускаемся вниз к основанию
            cubicTo(-r * 1.1f, -r * 0.3f, -r * 1.1f, r * 0.5f, -r, r * 0.7f)
            // Нижная дуга левого крыла: возвращаемся к центру под углом
            cubicTo(-r * 0.9f, r * 0.9f, -r * 0.4f, r * 0.4f, 0f, 0f)
            close()
        }

        val rightWingPath = Path().apply {
            moveTo(0f, 0f)
            // Верхняя дуга правого крыла
            cubicTo(r * 0.4f, -r * 0.4f, r * 0.9f, -r * 0.9f, r, -r * 0.6f)
            // Боковой правый торец крыла
            cubicTo(r * 1.1f, -r * 0.3f, r * 1.1f, r * 0.5f, r, r * 0.7f)
            // Нижняя дуга правого крыла
            cubicTo(r * 0.9f, r * 0.9f, r * 0.4f, r * 0.4f, 0f, 0f)
            close()
        }

        // ================= 2. ОТРИСОВКА ЗАЛИВКИ КРЫЛЬЕВ И КОНТУРОВ =================
        // Рисуем левое крыло (Заливка + Контур)
        drawPath(path = leftWingPath, color = baseRed)
        drawPath(path = leftWingPath, color = outlineColor, style = Stroke(width = strokeWidth))

        // Рисуем правое крыло (Заливка + Контур)
        drawPath(path = rightWingPath, color = baseRed)
        drawPath(path = rightWingPath, color = outlineColor, style = Stroke(width = strokeWidth))

        // ================= 3. СЛОЙ ОБЪЁМА: БОРДОВЫЕ СКЛАДКИ И ТЕНИ =================
        // Внутренние затемнения у основания крыльев (как темные "лепестки" на скриншоте)
        val leftShadowPath = Path().apply {
            moveTo(-r * 0.15f, -r * 0.05f)
            cubicTo(-r * 0.4f, -r * 0.25f, -r * 0.6f, -r * 0.15f, -r * 0.65f, 0f)
            cubicTo(-r * 0.6f, r * 0.15f, -r * 0.4f, r * 0.25f, -r * 0.15f, r * 0.05f)
            close()
        }
        val rightShadowPath = Path().apply {
            moveTo(r * 0.15f, -r * 0.05f)
            cubicTo(r * 0.4f, -r * 0.25f, r * 0.6f, -r * 0.15f, r * 0.65f, 0f)
            cubicTo(r * 0.6f, r * 0.15f, r * 0.4f, r * 0.25f, r * 0.15f, r * 0.05f)
            close()
        }
        drawPath(path = leftShadowPath, color = darkShadow)
        drawPath(path = rightShadowPath, color = darkShadow)

        // Тёмный градиентный полумесяц объёма на левом и правом торце крыла
        drawOval(
            color = darkShadow,
            topLeft = Offset(-r * 1.02f, -r * 0.3f),
            size = Size(r * 0.25f, r * 0.8f),
            alpha = 0.4f
        )
        drawOval(
            color = darkShadow,
            topLeft = Offset(r * 0.77f, -r * 0.3f),
            size = Size(r * 0.25f, r * 0.8f),
            alpha = 0.4f
        )

        // ================= 4. ЦЕНТРАЛЬНЫЙ УЗЕЛ БАБОЧКИ =================
        // Скругленный вертикальный прямоугольник по центру
        val nodeW = r * 0.42f
        val nodeH = r * 0.70f
        val nodeRect = RoundRect(
            left = -nodeW / 2f, top = -nodeH / 2f,
            right = nodeW / 2f, bottom = nodeH / 2f,
            cornerRadius = CornerRadius(x = nodeW * 0.3f, y = nodeH * 0.25f)
        )
        val nodePath = Path().apply { addRoundRect(nodeRect) }

        // Рисуем узел: алая заливка поверх крыльев + тонкий контур
        drawPath(path = nodePath, color = baseRed)
        drawPath(path = nodePath, color = outlineColor, style = Stroke(width = strokeWidth))

        // ================= 5. ГЛЯНЦЕВЫЕ БЕЛЫЕ БЛИКИ =================
        // Блик на центральном узле (вертикальная вытянутая капля справа)
        drawOval(
            color = Color.White,
            topLeft = Offset(nodeW * 0.12f, -nodeH * 0.28f),
            size = Size(nodeW * 0.22f, nodeH * 0.32f)
        )

        // Большой верхний блик на правом крыле
        drawOval(
            color = Color.White,
            topLeft = Offset(r * 0.62f, -r * 0.50f),
            size = Size(r * 0.14f, r * 0.32f)
        )

        // Маленький нижний блик на правом крыле
        drawOval(
            color = Color.White,
            topLeft = Offset(r * 0.74f, -r * 0.08f),
            size = Size(r * 0.10f, r * 0.20f)
        )


        // ================= 6. СЛОЙ ИСКРОК ИЗ МАССИВА =================
            //val goldColor = Color(0xFFFFD700)
            val goldColor = Color(0xFFFFFFFF)

        // Твой МАССИВ искорок: добавляй или удаляй точки прямо здесь!
        // Координаты заданы относительно центра бабочки (0, 0)
        val sparkles = arrayOf(
            Offset(-r * 0.55f, -r * 0.15f), // 1. Левое крыло (центр)
            Offset(-nodeW * 0.2f, nodeH * 0.25f), // 2. Центральный узел (низ)
            Offset(r * 0.45f, r * 0.25f),   // 3. Правое крыло (низ)
            Offset(-r * 0.75f, -r * 0.45f), // 4. Левое крыло (верхний край)
            Offset(r * 0.70f, -r * 0.40f),  // 5. Правое крыло (верхний край)
            Offset(-r * 0.35f, r * 0.35f),  // 6. Левое крыло (нижний изгиб)
            Offset(r * 0.30f, -r * 0.20f),  // 7. Правое крыло (внутреннее ушко)
            Offset(nodeW * 0.15f, -nodeH * 0.20f), // 8. Центральный узел (верх у блика)
            Offset(-r * 0.85f, r * 0.40f),  // 9. Левое крыло (дальний нижний угол)
            Offset(r * 0.80f, r * 0.50f)    // 10. Правое крыло (дальний нижний угол)
        )

        // Цикл отрисовки пробегается по всему твоему массиву
        sparkles.forEachIndexed { index, offset ->
            // Размер искорки (пусть немного отличается для живости, чётные поменьше, нечётные побольше)
            val starSize = if (index % 2 == 0) size * 0.18f else size * 0.16f
            val sR = starSize / 2f

            val starPath = Path().apply {
                moveTo(offset.x, offset.y - sR)
                quadraticTo(offset.x, offset.y, offset.x + sR, offset.y)
                quadraticTo(offset.x, offset.y, offset.x, offset.y + sR)
                quadraticTo(offset.x, offset.y, offset.x - sR, offset.y)
                quadraticTo(offset.x, offset.y, offset.x, offset.y - sR)
                close()
            }

            // Магический сдвиг фазы: привязываем синус к индексу искорки (index * 2f),
            // благодаря чему они вспыхивают размеренно по цепочке, а не толпой
            val currentAlpha = ((sin(sparklePhase + index * 2f) + 1f) / 2f).coerceIn(0f, 1f)
            drawPath(path = starPath, color = goldColor, alpha = currentAlpha)
        }
    }
}

@Composable
fun BowTie(
    modifier: Modifier,
    colorTheme: PetItemColor = PetItemColor.Red,
    sparklePhase: Float = 0f
) {
    Canvas(modifier = modifier) {
        drawBowTie(
            size.width * 0.5f,
            size.height * 0.5f,
            size = size.width * 0.85f,
            colorTheme = colorTheme,
            sparklePhase = sparklePhase
        )
    }
}

@Preview
@Composable
fun BowTiePreview() {
    Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
        BowTie(modifier = Modifier.fillMaxSize())
    }
}