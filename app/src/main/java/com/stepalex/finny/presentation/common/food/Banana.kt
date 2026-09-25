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

fun DrawScope.drawBanana(
    centerX: Float,
    centerY: Float,
    size: Float,
    isFilled: Boolean
) {
    val lerpRatio = 0.8f

    val blackStroke = Color(0xFF000000)
    val bananaYellow = lerp(Color(0xFFFFE11D), Color.White, if (isFilled) 0f else lerpRatio)
    val bananaShadowYellow = lerp(Color(0xFFEAB300), Color.White, if (isFilled) 0f else lerpRatio)
    val bananaLightYellow = lerp(Color(0xFFFFF275), Color.White, if (isFilled) 0f else lerpRatio)
    val stemGreen = lerp(Color(0xFF8DC63F), Color.White, if (isFilled) 0f else lerpRatio)
    val tipBrown = lerp(Color(0xFF534123), Color.White, if (isFilled) 0f else lerpRatio)

    val strokeWidth = size * 0.035f

    // Центрирование и небольшой наклон всей композиции для динамики
    withTransform({
        rotate(degrees = -10f, pivot = Offset(centerX, centerY))
    }) {

        // --- 1. ВЕРХНИЙ ХВОСТИК (ЗЕЛЕНЫЙ ЧЕРЕШОК) ---
        val stemPath = Path().apply {
            moveTo(centerX + size * 0.12f, centerY - size * 0.40f)
            // Левая грань хвостика
            quadraticTo(
                centerX + size * 0.05f, centerY - size * 0.50f,
                centerX + size * 0.10f, centerY - size * 0.58f
            )
            // Верхний срез хвостика (овальная вершина)
            quadraticTo(
                centerX + size * 0.18f, centerY - size * 0.62f,
                centerX + size * 0.23f, centerY - size * 0.56f
            )
            // Правая грань хвостика
            quadraticTo(
                centerX + size * 0.22f, centerY - size * 0.48f,
                centerX + size * 0.27f, centerY - size * 0.35f
            )
            close()
        }
        drawPath(path = stemPath, color = stemGreen)
        drawPath(
            path = stemPath,
            color = blackStroke,
            style = Stroke(width = strokeWidth, join = StrokeJoin.Round)
        )

        // Внутренний блик на хвостике
        val stemHighlight = Path().apply {
            moveTo(centerX + size * 0.13f, centerY - size * 0.53f)
            quadraticTo(
                centerX + size * 0.15f, centerY - size * 0.56f,
                centerX + size * 0.18f, centerY - size * 0.55f
            )
        }
        drawPath(
            path = stemHighlight,
            color = Color.White.copy(alpha = if (isFilled) 0.6f else 0.1f),
            style = Stroke(width = strokeWidth * 0.5f, cap = StrokeCap.Round)
        )


        // --- 2. ОСНОВНОЕ ТЕЛО БАНАНА ---
        val bananaPath = Path().apply {
            // Начало у основания хвостика
            moveTo(centerX + size * 0.25f, centerY - size * 0.36f)

            // Внешний правый изгиб банана вниз к кончику
            cubicTo(
                centerX + size * 0.55f, centerY - size * 0.10f,
                centerX + size * 0.50f, centerY + size * 0.35f,
                centerX - size * 0.22f, centerY + size * 0.48f
            )
            // Нижний кончик
            lineTo(centerX - size * 0.28f, centerY + size * 0.45f)

            // Внутренний левый изгиб банана вверх к хвостику
            cubicTo(
                centerX + size * 0.10f, centerY + size * 0.25f,
                centerX + size * 0.18f, centerY - size * 0.10f,
                centerX + size * 0.12f, centerY - size * 0.39f
            )
            close()
        }
        drawPath(path = bananaPath, color = bananaYellow)


        // --- 3. ТЕНИ И БЛИКИ ПО МАСКЕ БАНАНА ---
        withTransform({
            clipPath(bananaPath)
        }) {
            // Правая объемная тень вдоль внешнего изгиба
            drawOval(
                color = bananaShadowYellow,
                topLeft = Offset(centerX + size * 0.12f, centerY - size * 0.30f),
                size = Size(size * 0.45f, size * 0.85f)
            )

            // Левый мягкий светлый блик (заливка)
            drawOval(
                color = bananaLightYellow,
                topLeft = Offset(centerX - size * 0.15f, centerY - size * 0.20f),
                size = Size(size * 0.40f, size * 0.70f)
            )

            // Белые жесткие блики
            val whiteGlowPath = Path().apply {
                moveTo(centerX - size * 0.18f, centerY + size * 0.38f)
                quadraticTo(
                    centerX - size * 0.05f, centerY + size * 0.28f,
                    centerX + size * 0.05f, centerY + size * 0.10f
                )
            }
            drawPath(
                path = whiteGlowPath,
                color = Color.White.copy(alpha = if (isFilled) 0.8f else 0.2f),
                style = Stroke(width = strokeWidth * 1.5f, cap = StrokeCap.Round)
            )

            // Маленький дополнительный белый блик чуть ниже
            val whiteGlowDot = Path().apply {
                moveTo(centerX - size * 0.21f, centerY + size * 0.41f)
                lineTo(centerX - size * 0.19f, centerY + size * 0.40f)
            }
            drawPath(
                path = whiteGlowDot,
                color = Color.White.copy(alpha = if (isFilled) 0.8f else 0.2f),
                style = Stroke(width = strokeWidth * 1.5f, cap = StrokeCap.Round)
            )
        }


        // --- 4. КОНТУР И РАЗДЕЛИТЕЛЬНЫЕ ЛИНИИ ГРАНЕЙ ---
        // Основной внешний контур поверх теней
        drawPath(
            path = bananaPath,
            color = blackStroke,
            style = Stroke(width = strokeWidth, join = StrokeJoin.Round)
        )

        // Продольная линия, разделяющая грани банана по центру
        val ridgeLine = Path().apply {
            moveTo(centerX + size * 0.19f, centerY - size * 0.38f)
            cubicTo(
                centerX + size * 0.32f, centerY + size * 0.05f,
                centerX + size * 0.15f, centerY + size * 0.30f,
                centerX - size * 0.25f, centerY + size * 0.46f
            )
        }
        drawPath(
            path = ridgeLine,
            color = blackStroke,
            style = Stroke(width = strokeWidth * 0.8f, join = StrokeJoin.Round, cap = StrokeCap.Round)
        )


        // --- 5. ТЕМНЫЙ КОНЧИК БАНАНА (ОСНОВАНИЕ ЦВЕТКА) ---
        val tipPath = Path().apply {
            moveTo(centerX - size * 0.22f, centerY + size * 0.48f)
            lineTo(centerX - size * 0.28f, centerY + size * 0.45f)
            quadraticTo(
                centerX - size * 0.32f, centerY + size * 0.48f,
                centerX - size * 0.28f, centerY + size * 0.52f
            )
            quadraticTo(
                centerX - size * 0.22f, centerY + size * 0.53f,
                centerX - size * 0.22f, centerY + size * 0.48f
            )
            close()
        }
        drawPath(path = tipPath, color = tipBrown)
        drawPath(
            path = tipPath,
            color = blackStroke,
            style = Stroke(width = strokeWidth, join = StrokeJoin.Round)
        )
    }
}
@Composable
fun Banana(
    modifier: Modifier,
    isFilled: Boolean
) {
    Canvas(modifier = modifier) {
        drawBanana(
            centerX = size.width * 0.45f,
            centerY = size.height * 0.48f,
            size = size.width * 0.85f,
            isFilled = isFilled
        )
    }
}
@Preview(showBackground = true)
@Composable
fun BananaPreview() {
    Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
        Banana(modifier = Modifier.fillMaxSize(), isFilled = true)
    }
}
