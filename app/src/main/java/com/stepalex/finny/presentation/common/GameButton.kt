package com.stepalex.finny.presentation.common

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch


@Composable
fun GameButton(
    size: DpSize,
    colorBgr: Color,
    colorBorder: Color = colorBgr,
    sizeBorder: Dp = 0.dp,
    cornerRadius: Dp = 0.dp,
    iconId: Int? = null,
    iconColor: Color? = null,
    contentDescription: String? = null,
    typeGlare: TypeGlare = TypeGlare.TWO,
    onClick: () -> Unit,
    content: @Composable () -> Unit = {}
) {
    val scale = remember { Animatable(1f) }
    val animationScope = rememberCoroutineScope()

    val lightColor = lerp(colorBgr, Color.White, 0.2f)
    val lightColor2 = lerp(colorBgr, Color.White, 0.05f)
    val middleColor = colorBgr
    val darkColor = lerp(colorBgr, Color.Black, 0.3f)
    var blackoutColor by remember { mutableStateOf(Color.Black.copy(alpha = 0.0f)) }

    Box(
        modifier = Modifier
            .size(size)
            .graphicsLayer(
                scaleX = scale.value,
                scaleY = scale.value
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        blackoutColor = Color.Black.copy(0.25f)
                        animationScope.launch {
                            scale.animateTo(0.925f, animationSpec = tween(90))
                            scale.animateTo(0.95f, animationSpec = tween(10))
                        }
                        try {
                            awaitRelease()
                        } finally {
                            blackoutColor = Color.Black.copy(0f)
                            animationScope.launch {
                                scale.animateTo(1.05f, animationSpec = tween(90))
                                scale.animateTo(1f, animationSpec = tween(10))
                            }
                        }
                    },
                    onTap = { onClick() })
            }
            .background(darkColor, shape = RoundedCornerShape(cornerRadius))
            .background(blackoutColor, shape = RoundedCornerShape(cornerRadius))
            .border(
                border = BorderStroke(sizeBorder, colorBorder),
                shape = RoundedCornerShape(cornerRadius)
            )
            .padding(bottom = 5.dp, start = 2.dp, end = 2.dp)
            .background(lightColor, shape = RoundedCornerShape(cornerRadius))
            .background(blackoutColor, shape = RoundedCornerShape(cornerRadius))
            .padding(top = 5.dp, start = 3.dp, bottom = 3.dp, end = 3.dp)
            .background(middleColor, shape = RoundedCornerShape(cornerRadius * 0.75F))
            .background(blackoutColor, shape = RoundedCornerShape(cornerRadius * 0.75F)),
        contentAlignment = Alignment.Center
    ) {
        when (typeGlare) {
            TypeGlare.ONE -> {
                GlareOval(
                    modifier = Modifier.align(Alignment.TopStart),
                    color = lightColor,
                    blackoutColor = blackoutColor,
                    width = 14.dp, height = 8.dp, offsetX = 0.dp, offsetY = (-2).dp
                )
            }

            TypeGlare.TWO -> {
                GlareOval(
                    modifier = Modifier.align(Alignment.TopStart),
                    color = lightColor, blackoutColor = blackoutColor,
                    width = 16.dp, height = 10.dp, offsetX = 0.dp, offsetY = (-2).dp
                )
                GlareOval(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    color = lightColor2, blackoutColor = blackoutColor,
                    width = 10.dp, height = 6.dp, offsetX = (-6).dp, offsetY = (0).dp
                )
            }

            TypeGlare.NONE -> {}
        }
        iconId?.let {
            Icon(
                painter = painterResource(id = it),
                tint = iconColor ?: Color.Unspecified,
                contentDescription = contentDescription,
                modifier = Modifier.size(size.width / 2 - 2.dp, size.height / 2 - 2.dp)
            )
        }
        content()
    }
}

@Composable
fun GlareOval(
    modifier: Modifier,
    color: Color,
    blackoutColor: Color,
    width: Dp,
    height: Dp,
    offsetX: Dp,
    offsetY: Dp
) {
    Canvas(
        modifier = modifier
            .size(width = width, height = height)
    ) {
        drawOval(
            color = color, topLeft = Offset(x = offsetX.toPx(), offsetY.toPx()),
            size = Size(width.toPx(), height.toPx())
        )
        drawOval(
            color = blackoutColor, topLeft = Offset(x = offsetX.toPx(), offsetY.toPx()),
            size = Size(width.toPx(), height.toPx())
        )
    }
}

enum class TypeGlare {
    NONE, ONE, TWO
}


@Preview(showBackground = true)
@Composable
fun GameButtonPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(102, 102, 102)),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GameButton(
            size = DpSize(128.dp, 64.dp),
            colorBgr = Color(89, 172, 102),
            sizeBorder = 2.dp,
            colorBorder = Color.Black,
            cornerRadius = 12.dp,
            //text = "ВЫБРАТЬ",
            iconColor = Color.Black,
            onClick = {}
        ){
            OutlineText("ВЫБРАТЬ")
        }
    }
}