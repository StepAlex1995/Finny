package com.stepalex.finny.presentation.common.items


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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.sin

fun DrawScope.drawGlasses(
    centerX: Float,
    centerY: Float,
    size: Float,
    colorTheme: PetItemColor = PetItemColor.Purple, // Цвет оправы очков
    sparklePhase: Float = 0f
) {
    val r = size / 1f

    // Цвета для очков
    val frameColor = colorTheme.base
    val frameShadow = colorTheme.shadow
    val outlineColor = Color.Black
    val strokeWidth = size * 0.032f

    // Дымчато-черный полупрозрачный цвет для стекол очков
    val glassColor = Color(0x7A1A1A1A) // Альфа 0x7A (около 48% прозрачности)

    withTransform({
        // Смещаем холст в центр лица (между глазами)
        translate(left = centerX, top = centerY)
    }) {
        // 1. ЮВЕЛИРНЫЕ МУЛЬТЯШНЫЕ ПРОПОРЦИИ
        val leftLensX = -r * 0.38f
        val rightLensX = r * 0.38f
        val lensY = 0f

        // Делаем стекла чуть меньше, чтобы жирная оправа не пересекалась над носом!
        val lensRadiusW = r * 0.26f
        val lensRadiusH = r * 0.26f
        val frameStroke = size * 0.065f

        // ================= 1. СЛОЙ ПОЛУПРОЗРАЧНЫХ СТЁКОЛ =================
        drawCircle(color = glassColor, radius = lensRadiusW, center = Offset(leftLensX, lensY))
        drawCircle(color = glassColor, radius = lensRadiusW, center = Offset(rightLensX, lensY))

        // ================= 2. ДИАГОНАЛЬНЫЕ КОМИКС-БЛИКИ НА СТЁКЛАХ =================
        fun drawGlassGlare(lX: Float) {
            withTransform({
                rotate(degrees = -35f, pivot = Offset(lX, lensY))
            }) {
                drawOval(
                    color = Color.White,
                    topLeft = Offset(lX - (lensRadiusW * 0.15f), lensY - (lensRadiusH * 0.8f)),
                    size = Size(lensRadiusW * 0.22f, lensRadiusH * 1.6f),
                    alpha = 0.35f
                )
                drawOval(
                    color = Color.White,
                    topLeft = Offset(lX + (lensRadiusW * 0.3f), lensY - (lensRadiusH * 0.5f)),
                    size = Size(lensRadiusW * 0.08f, lensRadiusH * 1.0f),
                    alpha = 0.2f
                )
            }
        }
        drawGlassGlare(leftLensX)
        drawGlassGlare(rightLensX)

        // ================= 3. ЖИРНАЯ ПЛАСТИКОВАЯ ОПРАВА ОЧКОВ =================
        // Левый окуляр
        drawCircle(color = frameColor, radius = lensRadiusW + (frameStroke / 2f), center = Offset(leftLensX, lensY), style = Stroke(width = frameStroke))
        drawCircle(color = outlineColor, radius = lensRadiusW, center = Offset(leftLensX, lensY), style = Stroke(width = strokeWidth))
        drawCircle(color = outlineColor, radius = lensRadiusW + frameStroke, center = Offset(leftLensX, lensY), style = Stroke(width = strokeWidth))

        // Правый окуляр
        drawCircle(color = frameColor, radius = lensRadiusW + (frameStroke / 2f), center = Offset(rightLensX, lensY), style = Stroke(width = frameStroke))
        drawCircle(color = outlineColor, radius = lensRadiusW, center = Offset(rightLensX, lensY), style = Stroke(width = strokeWidth))
        drawCircle(color = outlineColor, radius = lensRadiusW + frameStroke, center = Offset(rightLensX, lensY), style = Stroke(width = strokeWidth))

        // Мягкая тень на оправе для объема
        drawCircle(color = frameShadow, radius = lensRadiusW + (frameStroke / 2f), center = Offset(leftLensX, lensY), style = Stroke(width = frameStroke * 0.4f), alpha = 0.4f)
        drawCircle(color = frameShadow, radius = lensRadiusW + (frameStroke / 2f), center = Offset(rightLensX, lensY), style = Stroke(width = frameStroke * 0.4f), alpha = 0.4f)

        // ================= 4. ПЕРЕМЫЧКА ОЧКОВ НАД НОСОМ (ИСПРАВЛЕНО) =================
        // Теперь перемычка честно соединяет края оправы, а не тонет внутри
        val bridgePath = Path().apply {
            moveTo(leftLensX + lensRadiusW + (frameStroke * 0.3f), lensY - (lensRadiusH * 0.1f))
            quadraticTo(0f, lensY - (lensRadiusH * 0.25f), rightLensX - lensRadiusW - (frameStroke * 0.3f), lensY - (lensRadiusH * 0.1f))
        }
        drawPath(path = bridgePath, color = frameColor, style = Stroke(width = frameStroke * 0.7f, cap = StrokeCap.Round))
        drawPath(path = bridgePath, color = outlineColor, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))

        // ================= 5. БОКОВЫЕ ДУЖКИ (ИСПРАВЛЕНО) =================
        // Автоматически рассчитываем точку привязки дужек к внешнему краю оправы, чтоб они не висели в воздухе!
        val leftAttachX = leftLensX - lensRadiusW - frameStroke
        val rightAttachX = rightLensX + lensRadiusW + frameStroke
        val earPieceW = size * 0.08f
        val earPieceH = frameStroke * 0.6f

        drawRect(color = frameColor, topLeft = Offset(leftAttachX - earPieceW, lensY - (earPieceH / 2f)), size = Size(earPieceW, earPieceH))
        drawRect(color = outlineColor, topLeft = Offset(leftAttachX - earPieceW, lensY - (earPieceH / 2f)), size = Size(earPieceW, earPieceH), style = Stroke(width = strokeWidth))

        drawRect(color = frameColor, topLeft = Offset(rightAttachX, lensY - (earPieceH / 2f)), size = Size(earPieceW, earPieceH))
        drawRect(color = outlineColor, topLeft = Offset(rightAttachX, lensY - (earPieceH / 2f)), size = Size(earPieceW, earPieceH), style = Stroke(width = strokeWidth))

        // ================= 6. МАССИВ ИЗ 10 МЕРЦАЮЩИХ ЗВЁЗД НА ОПРАВЕ =================
        val goldColor = Color(0xFFFFFFFF)
        val sparkles = arrayOf(
            Offset(leftLensX - lensRadiusW - (frameStroke * 0.5f), lensY - (lensRadiusH * 0.4f)),
            Offset(rightLensX + lensRadiusW + (frameStroke * 0.5f), lensY - (lensRadiusH * 0.4f)),
            Offset(leftLensX, lensY - lensRadiusH - (frameStroke * 0.5f)),
            Offset(rightLensX, lensY - lensRadiusH - (frameStroke * 0.5f)),
            Offset(leftLensX + (lensRadiusW * 0.5f), lensY + lensRadiusH + (frameStroke * 0.2f)),
            Offset(rightLensX - (lensRadiusW * 0.5f), lensY + lensRadiusH + (frameStroke * 0.2f)),
            Offset(0f, lensY - (lensRadiusH * 0.3f)),
            Offset(leftLensX - (lensRadiusW * 0.5f), lensY + lensRadiusH + (frameStroke * 0.2f)),
            Offset(rightLensX + (lensRadiusW * 0.5f), lensY + lensRadiusH + (frameStroke * 0.2f)),
            Offset(leftLensX - lensRadiusW, lensY + (lensRadiusH * 0.5f))
        )

        sparkles.forEachIndexed { index, offset ->
            val starSize = when (index % 3) {
                0 -> size * 0.16f
                1 -> size * 0.15f
                else -> size * 0.035f
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
fun Glasses(
    modifier: Modifier,
    colorTheme: PetItemColor = PetItemColor.Purple,
    sparklePhase: Float = 0f,
) {
    Canvas(modifier = modifier) {
        drawGlasses(
            size.width * 0.5f,
            size.height * 0.5f,
            size = size.width * 0.55f,
            colorTheme = colorTheme,
            sparklePhase = sparklePhase,
        )
    }
}

@Preview
@Composable
fun GlassesPreview() {
    Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
        Glasses(
            modifier = Modifier.fillMaxSize(),
        )
    }
}