package com.stepalex.finny.presentation.pet_room_bgr

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun PetRoomBgr(modifier: Modifier = Modifier, petRoomColor: PetRoomColor = PetRoomColor()) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        val lineWidth = w * 0.006f
        //пол и стены
        val wallHeight = h * 0.55f
        drawWallAndFlow(w, h, wallHeight, lineWidth, petRoomColor)
        //окно
        drawWindow(w, wallHeight, petRoomColor)
        //занавески
        drawCurtain(w, h, wallHeight, petRoomColor)
    }
}

/**
    Занавески
 */
private fun DrawScope.drawCurtain(
    w: Float,
    h: Float,
    wallHeight: Float,
    petRoomColor: PetRoomColor
) {
    val curtainTopW = w * 0.26f
    val curtainCenterW = w * 0.1f //0.14f
    val curtainBottomW = w * 0.18f
    val curtainH = h * 0.58f          // Длина занавесок до нижнего края экрана
    val tieY = h * 0.28f              // Положение подвязки
    val tieH = 55f                    // Широкая подвязка
    val outlineWidth = w * 0.008f     // Четкая обводка

    // --- ЛЕВАЯ ЗАНАВЕСКА ---
    val leftCurtain = Path().apply {
        moveTo(-outlineWidth, -outlineWidth)
        lineTo(curtainTopW, -outlineWidth)
        // Спуск до подвязки и изгиб к внутренней нижней точке шторы
        cubicTo(curtainTopW, tieY * 0.4f, curtainCenterW, tieY * 0.8f, curtainCenterW, tieY)
        cubicTo(
            curtainCenterW,
            tieY + (curtainH - tieY) * 0.3f,
            curtainBottomW,
            curtainH * 0.85f,
            curtainBottomW,
            curtainH
        )

        // 3 дуги, каскадом спускающиеся вниз по направлению к левой стене
        val stepLeft = curtainBottomW / 3f
        val arcH = 22f // Глубина провисания дуги

        // 1-я дуга (внутренняя, самая высокая)
        quadraticTo(
            curtainBottomW - stepLeft * 0.5f,
            curtainH + arcH,
            curtainBottomW - stepLeft,
            curtainH + 15f
        )
        // 2-я дуга (средняя)
        quadraticTo(
            curtainBottomW - stepLeft * 1.5f,
            curtainH + 15f + arcH,
            curtainBottomW - stepLeft * 2f,
            curtainH + 30f
        )
        // 3-я дуга (внешняя, упирается в левый край экрана в самой низкой точке)
        quadraticTo(stepLeft * 0.5f, curtainH + 30f + arcH, 0f, curtainH + 45f)

        lineTo(-outlineWidth, curtainH + 45f)
        lineTo(-outlineWidth, -outlineWidth)
        close()
    }
    drawPath(path = leftCurtain, color =  petRoomColor.curtainColor)

    // Обводка левой занавески
    val leftOutline = Path().apply {
        moveTo(curtainTopW, 0f)
        cubicTo(curtainTopW, tieY * 0.4f, curtainCenterW, tieY * 0.8f, curtainCenterW, tieY)
        cubicTo(
            curtainCenterW,
            tieY + (curtainH - tieY) * 0.3f,
            curtainBottomW,
            curtainH * 0.85f,
            curtainBottomW,
            curtainH
        )

        val stepLeft = curtainBottomW / 3f
        val arcH = 22f

        quadraticTo(
            curtainBottomW - stepLeft * 0.5f,
            curtainH + arcH,
            curtainBottomW - stepLeft,
            curtainH + 15f
        )
        quadraticTo(
            curtainBottomW - stepLeft * 1.5f,
            curtainH + 15f + arcH,
            curtainBottomW - stepLeft * 2f,
            curtainH + 30f
        )
        quadraticTo(stepLeft * 0.5f, curtainH + 30f + arcH, 0f, curtainH + 45f)
    }
    drawPath(path = leftOutline, color = petRoomColor.curtainLineColor, style = Stroke(width = outlineWidth))

    // Внутренние линии складок для объема (ЛЕВАЯ)
    val leftFoldLine = Path().apply {
        // Линия над подвязкой (идет от верха к подвязке)
        moveTo(curtainTopW * 0.5f, wallHeight * 0.2f)
        quadraticTo(curtainTopW * 0.45f, tieY * 0.8f, curtainCenterW * 0.2f, tieY)
        // Линия под подвязкой (спускается вниз к одной из дуг, как на скриншоте)
        val startUnderTieY = tieY + tieH
        moveTo(curtainCenterW * 0.65f, startUnderTieY)
        cubicTo(
            curtainCenterW * 0.7f, startUnderTieY + (curtainH - startUnderTieY) * 0.4f,
            curtainBottomW * 0.45f, curtainH * 0.85f,
            curtainBottomW * 0.35f, curtainH + 28f
        )
    }
    drawPath(
        path = leftFoldLine,
        color = petRoomColor.curtainLineColor,
        style = Stroke(width = outlineWidth * 0.6f)
    )

    // Подвязка левой занавески
    val tieExtension = w * 0.04f
    val leftTieW = curtainCenterW + tieExtension
    drawRoundRect(
        color = petRoomColor.curtainHorizontalColor,
        topLeft = Offset(-outlineWidth, tieY),
        size = Size(leftTieW * 0.85f, tieH),
        cornerRadius = CornerRadius(8f, 8f)
    )
    drawRoundRect(
        color = petRoomColor.curtainHorizontalStrokeColor,
        topLeft = Offset(-outlineWidth, tieY),
        size = Size(leftTieW * 0.85f, tieH),
        cornerRadius = CornerRadius(8f, 8f),
        style = Stroke(width = outlineWidth)
    )


    // --- ПРАВАЯ ЗАНАВЕСКА ---
    val rightCurtain = Path().apply {
        moveTo(w + outlineWidth, -outlineWidth)
        lineTo(w - curtainTopW, -outlineWidth)
        cubicTo(
            w - curtainTopW,
            tieY * 0.4f,
            w - curtainCenterW,
            tieY * 0.8f,
            w - curtainCenterW,
            tieY
        )
        cubicTo(
            w - curtainCenterW,
            tieY + (curtainH - tieY) * 0.3f,
            w - curtainBottomW,
            curtainH * 0.85f,
            w - curtainBottomW,
            curtainH
        )

        // 3 дуги, каскадом спускающиеся вниз по направлению к правому краю
        val stepRight = curtainBottomW / 3f
        val arcH = 22f
        val startX = w - curtainBottomW

        // 1-я дуга (внутренняя, самая высокая)
        quadraticTo(startX + stepRight * 0.5f, curtainH + arcH, startX + stepRight, curtainH + 15f)
        // 2-я дуга (средняя)
        quadraticTo(
            startX + stepRight * 1.5f,
            curtainH + 15f + arcH,
            startX + stepRight * 2f,
            curtainH + 30f
        )
        // 3-я дуга (внешняя, самая низкая у правого края экрана)
        quadraticTo(w - stepRight * 0.5f, curtainH + 30f + arcH, w, curtainH + 45f)

        lineTo(w + outlineWidth, curtainH + 45f)
        close()
    }
    drawPath(path = rightCurtain, color =  petRoomColor.curtainColor)

    // Обводка правой занавески
    val rightOutline = Path().apply {
        moveTo(w - curtainTopW, 0f)
        cubicTo(
            w - curtainTopW,
            tieY * 0.4f,
            w - curtainCenterW,
            tieY * 0.8f,
            w - curtainCenterW,
            tieY
        )
        cubicTo(
            w - curtainCenterW,
            tieY + (curtainH - tieY) * 0.3f,
            w - curtainBottomW,
            curtainH * 0.85f,
            w - curtainBottomW,
            curtainH
        )

        val stepRight = curtainBottomW / 3f
        val arcH = 22f
        val startX = w - curtainBottomW

        quadraticTo(startX + stepRight * 0.5f, curtainH + arcH, startX + stepRight, curtainH + 15f)
        quadraticTo(
            startX + stepRight * 1.5f,
            curtainH + 15f + arcH,
            startX + stepRight * 2f,
            curtainH + 30f
        )
        quadraticTo(w - stepRight * 0.5f, curtainH + 30f + arcH, w, curtainH + 45f)
    }
    drawPath(path = rightOutline, color = petRoomColor.curtainLineColor, style = Stroke(width = outlineWidth))

    // Внутренние линии складок для объема (ПРАВАЯ)
    val rightFoldLine = Path().apply {
        // Линия над подвязкой
        moveTo(w - curtainTopW * 0.5f, wallHeight * 0.2f)
        quadraticTo(w - curtainTopW * 0.45f, tieY * 0.8f, w - curtainCenterW * 0.2f, tieY)
        // Линия под подвязкой
        val startUnderTieY = tieY + tieH
        moveTo(w - curtainCenterW * 0.65f, startUnderTieY)
        cubicTo(
            w - curtainCenterW * 0.7f, startUnderTieY + (curtainH - startUnderTieY) * 0.4f,
            w - curtainBottomW * 0.45f, curtainH * 0.85f,
            w - curtainBottomW * 0.36f, curtainH + 28f
        )
    }
    drawPath(
        path = rightFoldLine,
        color = petRoomColor.curtainLineColor,
        style = Stroke(width = outlineWidth * 0.6f)
    )

    // Подвязка правой занавески
    val rightTieW = curtainCenterW + tieExtension
    drawRoundRect(
        color = petRoomColor.curtainHorizontalColor,
        topLeft = Offset(w - rightTieW * 0.85f + outlineWidth, tieY),
        size = Size(rightTieW, tieH),
        cornerRadius = CornerRadius(8f, 8f)
    )
    drawRoundRect(
        color = petRoomColor.curtainHorizontalStrokeColor,
        topLeft = Offset(w - rightTieW * 0.85f + outlineWidth, tieY),
        size = Size(rightTieW, tieH),
        cornerRadius = CornerRadius(8f, 8f),
        style = Stroke(width = outlineWidth)
    )
}


/**
    Окно
 */
private fun DrawScope.drawWindow(
    w: Float,
    wallHeight: Float,
    petRoomColor: PetRoomColor
) {
    val windowW = w * 0.55f
    val windowH = wallHeight * 0.75f
    val windowCornetRadius = 12f
    val windowStrokeWidth = windowW * 0.012f         // толщина обводок у окна
    val windowFrameWidth = windowW * 0.08f         //толщина рамы
    val smallWindowFrameWidth = windowW * 0.07f         //толщина маленькой рамы
    //внешняя рамка маленького окна
    val smallWindowW = windowW * 0.67f
    val smallWindowH = windowH * 0.45f
    //внешняя обводка рамы окна
    val outsideWindowFrameStrokePath = Path().apply {
        addRoundRect(
            roundRect = RoundRect(
                left = 0f,
                top = 0f,
                right = windowW,
                bottom = windowH,
                topLeftCornerRadius = CornerRadius(0),
                topRightCornerRadius = CornerRadius(0),
                bottomRightCornerRadius = CornerRadius(windowCornetRadius),
                bottomLeftCornerRadius = CornerRadius(0)
            )
        )
    }
    //рама окна
    val windowFramePath = Path().apply {
        addRoundRect(
            roundRect = RoundRect(
                left = 0f,
                top = 0f,
                right = windowW - windowStrokeWidth,
                bottom = windowH - windowStrokeWidth,
                topLeftCornerRadius = CornerRadius(0),
                topRightCornerRadius = CornerRadius(0),
                bottomRightCornerRadius = CornerRadius(windowCornetRadius),
                bottomLeftCornerRadius = CornerRadius(0)
            )
        )
    }
    //внутренняя обводка рамы окна
    val insideWindowFrameStrokePath = Path().apply {
        addRoundRect(
            roundRect = RoundRect(
                left = 0f,
                top = 0f,
                right = windowW - windowFrameWidth,
                bottom = windowH - windowFrameWidth,
                topLeftCornerRadius = CornerRadius(0),
                topRightCornerRadius = CornerRadius(0),
                bottomRightCornerRadius = CornerRadius(windowCornetRadius),
                bottomLeftCornerRadius = CornerRadius(0)
            )
        )
    }
    //Внутренняя часть окна - небо
    val windowBgrPath = Path().apply {
        addRoundRect(
            roundRect = RoundRect(
                left = 0f,
                top = 0f,
                right = windowW - windowFrameWidth - windowStrokeWidth,
                bottom = windowH - windowFrameWidth - windowStrokeWidth,
                topLeftCornerRadius = CornerRadius(0),
                topRightCornerRadius = CornerRadius(0),
                bottomRightCornerRadius = CornerRadius(windowCornetRadius),
                bottomLeftCornerRadius = CornerRadius(0)
            )
        )
    }
    val outsideSmallWindowFrameStrokePath = Path().apply {
        addRoundRect(
            roundRect = RoundRect(
                left = windowW - smallWindowW,
                top = 0f,
                right = windowW - windowFrameWidth,
                bottom = smallWindowH - windowFrameWidth,
                topLeftCornerRadius = CornerRadius(0),
                topRightCornerRadius = CornerRadius(0),
                bottomRightCornerRadius = CornerRadius(windowCornetRadius),
                bottomLeftCornerRadius = CornerRadius(windowCornetRadius)
            )
        )
    }
    //рамка маленького окна
    val smallWindowFramePath = Path().apply {
        addRoundRect(
            roundRect = RoundRect(
                left = windowW - smallWindowW + windowStrokeWidth,
                top = 0f,
                right = windowW - windowFrameWidth - windowStrokeWidth,
                bottom = smallWindowH - windowFrameWidth - windowStrokeWidth,
                topLeftCornerRadius = CornerRadius(0),
                topRightCornerRadius = CornerRadius(0),
                bottomRightCornerRadius = CornerRadius(windowCornetRadius),
                bottomLeftCornerRadius = CornerRadius(windowCornetRadius)
            )
        )
    }
    //внутренняя рамка маленького окна
    val smallWindowBgrPath = Path().apply {
        addRoundRect(
            roundRect = RoundRect(
                left = windowW - smallWindowW + windowStrokeWidth + smallWindowFrameWidth,
                top = 0f,
                right = windowW - windowFrameWidth - windowStrokeWidth - smallWindowFrameWidth,
                bottom = smallWindowH - windowFrameWidth - windowStrokeWidth - smallWindowFrameWidth,
                topLeftCornerRadius = CornerRadius(0),
                topRightCornerRadius = CornerRadius(0),
                bottomRightCornerRadius = CornerRadius(windowCornetRadius),
                bottomLeftCornerRadius = CornerRadius(windowCornetRadius)
            )
        )
    }
    //Вертикальная палка окна
    //Внешняя огранка с обоих сторон
    val verticalWindowFrameStrokePath = Path().apply {
        addRoundRect(
            roundRect = RoundRect(
                left = windowW - smallWindowW - smallWindowFrameWidth - windowStrokeWidth,
                top = 0f,
                right = windowW - smallWindowW + windowStrokeWidth,
                bottom = windowH - windowFrameWidth,
                topLeftCornerRadius = CornerRadius(0),
                topRightCornerRadius = CornerRadius(0),
                bottomRightCornerRadius = CornerRadius(windowCornetRadius),
                bottomLeftCornerRadius = CornerRadius(windowCornetRadius)
            )
        )
    }
    //рама вертикальной палки
    val verticalWindowFramePath = Path().apply {
        addRoundRect(
            roundRect = RoundRect(
                left = windowW - smallWindowW - smallWindowFrameWidth,
                top = 0f,
                right = windowW - smallWindowW,
                bottom = windowH - windowFrameWidth + 1,
                topLeftCornerRadius = CornerRadius(0),
                topRightCornerRadius = CornerRadius(0),
                bottomRightCornerRadius = CornerRadius(0),
                bottomLeftCornerRadius = CornerRadius(0)
            )
        )
    }

    drawPath(
        path = outsideWindowFrameStrokePath,
        color = petRoomColor.windowsFrameStrokeColor
    )
    drawPath(
        path = windowFramePath,
        color = petRoomColor.windowsFrameColor
    )
    drawPath(
        path = insideWindowFrameStrokePath,
        color = petRoomColor.windowsFrameStrokeColor
    )
    drawPath(
        path = windowBgrPath,
        color = petRoomColor.windowBgColor
    )

    drawPath(
        path = outsideSmallWindowFrameStrokePath,
        color = petRoomColor.windowsFrameStrokeColor
    )
    drawPath(
        path = smallWindowFramePath,
        color = petRoomColor.windowsFrameColor
    )
    drawPath(
        path = smallWindowBgrPath,
        color = petRoomColor.windowBgColor
    )

    drawPath(
        path = verticalWindowFrameStrokePath,
        color = petRoomColor.windowsFrameStrokeColor
    )
    drawPath(
        path = verticalWindowFramePath,
        color = petRoomColor.windowsFrameColor
    )
}

/**
Пол и стены
 */
private fun DrawScope.drawWallAndFlow(
    w: Float,
    h: Float,
    wallHeight: Float,
    lineWidth: Float,
    petRoomColor: PetRoomColor
) {
    val flowHeight = h - wallHeight
    drawRect(color = petRoomColor.wallColor, size = Size(w, wallHeight))
    drawRect(
        color = petRoomColor.floorColor,
        topLeft = Offset(0f, wallHeight),
        size = Size(w, h - wallHeight)
    )
    val maxLine = 6
    for (i in 0..maxLine) {
        drawLine(
            color = petRoomColor.floorLineColor,
            start = Offset(0f, wallHeight + i * (flowHeight / maxLine)),
            end = Offset(w, wallHeight + i * (flowHeight / maxLine)),
            strokeWidth = lineWidth * 1.5f
        )
        if (i >= maxLine) {
            break   //визуально лишняя полоска
        }
        for (j in 0..1) {
            drawLine(
                color = petRoomColor.floorLineColor,
                start = Offset(
                    w * (if (i % 2 == 0) 0.55f else if (j == 0) 0.25f else 0.75f),
                    wallHeight + i * (flowHeight / maxLine)
                ),
                end = Offset(
                    w * (if (i % 2 == 0) 0.55f else if (j == 0) 0.25f else 0.75f),
                    wallHeight + (i + 1) * (flowHeight / maxLine)
                ),
                strokeWidth = lineWidth * 1.5f
            )
        }
    }
    //черточки на полу
    drawLine(
        color = petRoomColor.floorLineColor,
        start = Offset(w * 0.13f, (h - flowHeight) + flowHeight * 0.23f),
        end = Offset(w * 0.25f, (h - flowHeight) + flowHeight * 0.22f),
        strokeWidth = lineWidth * 1.5f
    )
    drawLine(
        color = petRoomColor.floorLineColor,
        start = Offset(w * 0.75f, (h - flowHeight) + flowHeight * 0.22f),
        end = Offset(w * 0.89f, (h - flowHeight) + flowHeight * 0.21f),
        strokeWidth = lineWidth * 1.5f
    )
    drawLine(
        color = petRoomColor.floorLineColor,
        start = Offset(w * 0.73f, (h - flowHeight) + flowHeight * 0.06f),
        end = Offset(w * 0.79f, (h - flowHeight) + flowHeight * 0.07f),
        strokeWidth = lineWidth * 1.5f
    )
    drawLine(
        color = petRoomColor.floorLineColor,
        start = Offset(w * 0f, (h - flowHeight) + flowHeight * 0.43f),
        end = Offset(w * 0.18f, (h - flowHeight) + flowHeight * 0.40f),
        strokeWidth = lineWidth * 1.5f
    )
    drawLine(
        color = petRoomColor.floorLineColor,
        start = Offset(w * 0.55f, (h - flowHeight) + flowHeight * 0.41f),
        end = Offset(w * 0.8f, (h - flowHeight) + flowHeight * 0.43f),
        strokeWidth = lineWidth * 1.5f
    )
    drawLine(
        color = petRoomColor.floorLineColor,
        start = Offset(w * 0.88f, (h - flowHeight) + flowHeight * 0.39f),
        end = Offset(w * 0.96f, (h - flowHeight) + flowHeight * 0.38f),
        strokeWidth = lineWidth * 1.5f
    )

    drawLine(
        color = petRoomColor.floorLineColor,
        start = Offset(w * 0.1f, (h - flowHeight) + flowHeight * 0.61f),
        end = Offset(w * 0.25f, (h - flowHeight) + flowHeight * 0.62f),
        strokeWidth = lineWidth * 1.5f
    )
    drawLine(
        color = petRoomColor.floorLineColor,
        start = Offset(w * 0.75f, (h - flowHeight) + flowHeight * 0.60f),
        end = Offset(w * 0.93f, (h - flowHeight) + flowHeight * 0.61f),
        strokeWidth = lineWidth * 1.5f
    )
    drawLine(
        color = petRoomColor.floorLineColor,
        start = Offset(w * 0.4f, (h - flowHeight) + flowHeight * 0.56f),
        end = Offset(w * 0.47f, (h - flowHeight) + flowHeight * 0.565f),
        strokeWidth = lineWidth * 1.5f
    )
    drawLine(
        color = petRoomColor.floorLineColor,
        start = Offset(w * 0.20f, (h - flowHeight) + flowHeight * 0.77f),
        end = Offset(w * 0.40f, (h - flowHeight) + flowHeight * 0.76f),
        strokeWidth = lineWidth * 1.5f
    )
    drawLine(
        color = petRoomColor.floorLineColor,
        start = Offset(w * 0.83f, (h - flowHeight) + flowHeight * 0.78f),
        end = Offset(w * 1f, (h - flowHeight) + flowHeight * 0.80f),
        strokeWidth = lineWidth * 1.5f
    )
    drawLine(
        color = petRoomColor.floorLineColor,
        start = Offset(w * 0.75f, (h - flowHeight) + flowHeight * 0.91f),
        end = Offset(w * 0.56f, (h - flowHeight) + flowHeight * 0.92f),
        strokeWidth = lineWidth * 1.5f
    )
}


@Preview
@Composable
fun PetRoomBgrPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        PetRoomBgr(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
        )
    }
}