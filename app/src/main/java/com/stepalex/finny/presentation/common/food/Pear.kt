package com.stepalex.finny.presentation.common.food

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

fun DrawScope.drawPear(
    centerX: Float,
    centerY: Float,
    size: Float,
    isFilled: Boolean
) {
    val lerpRatio = 0.8f

    val blackStroke = Color(0xFF000000)
    val pearGreenYellow = lerp(Color(0xFFD3E01C), Color.White, if (isFilled) 0f else lerpRatio)
    val pearShadowGreen = lerp(Color(0xFFB0BD10), Color.White, if (isFilled) 0f else lerpRatio)
    val pearLightGlow = lerp(Color(0xFFE6F334), Color.White, if (isFilled) 0f else lerpRatio)
    val leafGreen = lerp(Color(0xFF5CB84D), Color.White, if (isFilled) 0f else lerpRatio)
    val stemBrown = lerp(Color(0xFF423229), Color.White, if (isFilled) 0f else lerpRatio)

    val strokeWidth = size * 0.035f

    // Отрисовка без наклона, так как груша стоит ровно по центру
    withTransform({
        // При необходимости здесь можно добавить общую трансформацию
    }) {

        // --- 1. Веточка (КОРИЧНЕВЫЙ ЧЕРЕШОК) ---
        val stemPath = Path().apply {
            moveTo(centerX, centerY - size * 0.30f)
            // Изгиб веточки вправо вверх
            cubicTo(
                centerX + size * 0.05f, centerY - size * 0.35f,
                centerX + size * 0.12f, centerY - size * 0.42f,
                centerX + size * 0.16f, centerY - size * 0.44f
            )
            // Толщина веточки на кончике
            lineTo(centerX + size * 0.20f, centerY - size * 0.42f)
            // Обратный изгиб к основанию
            cubicTo(
                centerX + size * 0.15f, centerY - size * 0.38f,
                centerX + size * 0.06f, centerY - size * 0.32f,
                centerX + size * 0.02f, centerY - size * 0.28f
            )
            close()
        }
        drawPath(path = stemPath, color = stemBrown)
        drawPath(
            path = stemPath,
            color = blackStroke,
            style = Stroke(width = strokeWidth, join = StrokeJoin.Round)
        )


        // --- 2. ЗЕЛЕНЫЙ ЛИСТИК ---
        val leafPath = Path().apply {
            // Листик выходит из сочленения веточки
            moveTo(centerX + size * 0.06f, centerY - size * 0.34f)
            // Верхняя дуга листика влево вверх
            cubicTo(
                centerX - size * 0.05f, centerY - size * 0.48f,
                centerX - size * 0.15f, centerY - size * 0.48f,
                centerX - size * 0.22f, centerY - size * 0.44f // Острый кончик листика
            )
            // Нижняя дуга возвращается к основанию
            cubicTo(
                centerX - size * 0.16f, centerY - size * 0.34f,
                centerX - size * 0.02f, centerY - size * 0.32f,
                centerX + size * 0.06f, centerY - size * 0.34f
            )
            close()
        }
        drawPath(path = leafPath, color = leafGreen)
        drawPath(
            path = leafPath,
            color = blackStroke,
            style = Stroke(width = strokeWidth, join = StrokeJoin.Round)
        )


        // --- 3. АНАТОМИЧЕСКОЕ ТЕЛО ГРУШИ ---
        val pearPath = Path().apply {
            // Верхняя закругленная макушка груши
            moveTo(centerX - size * 0.08f, centerY - size * 0.26f)
            quadraticTo(centerX, centerY - size * 0.30f, centerX + size * 0.08f, centerY - size * 0.26f)

            // Правое сужение («шея» груши) переходящее в широкое бедро
            cubicTo(
                centerX + size * 0.16f, centerY - size * 0.18f,
                centerX + size * 0.12f, centerY - size * 0.02f,
                centerX + size * 0.34f, centerY + size * 0.10f
            )
            // Правое нижнее закругление к основанию
            cubicTo(
                centerX + size * 0.52f, centerY + size * 0.22f,
                centerX + size * 0.38f, centerY + size * 0.48f,
                centerX, centerY + size * 0.48f
            )
            // Левое нижнее закругление основания
            cubicTo(
                centerX - size * 0.38f, centerY + size * 0.48f,
                centerX - size * 0.52f, centerY + size * 0.22f,
                centerX - size * 0.34f, centerY + size * 0.10f
            )
            // Левое сужение обратно к макушке
            cubicTo(
                centerX - size * 0.12f, centerY - size * 0.02f,
                centerX - size * 0.16f, centerY - size * 0.18f,
                centerX - size * 0.08f, centerY - size * 0.26f
            )
            close()
        }
        drawPath(path = pearPath, color = pearGreenYellow)


        // --- 4. МАСКИРОВАННЫЕ ЭЛЕМЕНТЫ (ТЕНЬ И БЛИК) ---
        withTransform({
            clipPath(pearPath)
        }) {
            // Большая полукруглая тень с правой стороны груши
            drawOval(
                color = pearShadowGreen,
                topLeft = Offset(centerX + size * 0.08f, centerY - size * 0.25f),
                size = Size(size * 0.42f, size * 0.72f)
            )

            // Мягкое внутреннее осветление для объема слева
            drawOval(
                color = pearLightGlow,
                topLeft = Offset(centerX - size * 0.42f, centerY - size * 0.20f),
                size = Size(size * 0.55f, size * 0.65f)
            )

            // Белый серповидный жесткий блик вдоль левого края
            val highlightPath = Path().apply {
                moveTo(centerX - size * 0.25f, centerY - size * 0.05f)
                cubicTo(
                    centerX - size * 0.40f, centerY + size * 0.10f,
                    centerX - size * 0.35f, centerY + size * 0.35f,
                    centerX - size * 0.10f, centerY + size * 0.44f
                )
            }
            drawPath(
                path = highlightPath,
                color = Color.White.copy(alpha = if (isFilled) 0.7f else 0.15f),
                style = Stroke(width = strokeWidth * 1.6f, cap = StrokeCap.Round)
            )
        }


        // --- 5. ФИНАЛЬНЫЙ КОНТУР ПОВЕРХ ВСЕГО ---
        drawPath(
            path = pearPath,
            color = blackStroke,
            style = Stroke(width = strokeWidth, join = StrokeJoin.Round)
        )
    }
}

@Composable
fun Pear(
    modifier: Modifier,
    isFilled: Boolean
) {
    Canvas(modifier = modifier) {
        drawPear(
            centerX = size.width * 0.5f,
            centerY = size.height * 0.5f,
            size = size.width * 0.80f,
            isFilled = isFilled
        )
    }
}

@Preview
@Composable
fun PearNotFilledPreview() {
    Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
        Pear(modifier = Modifier.fillMaxSize(), isFilled = false)
    }
}

@Preview
@Composable
fun PearFilledPreview() {
    Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
        Pear(modifier = Modifier.fillMaxSize(), isFilled = true)
    }
}