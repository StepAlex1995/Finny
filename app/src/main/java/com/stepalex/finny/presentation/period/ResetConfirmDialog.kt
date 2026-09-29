package com.stepalex.finny.presentation.period

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.stepalex.finny.presentation.common.GameButton
import com.stepalex.finny.presentation.common.OutlineText
import com.stepalex.finny.presentation.common.TypeGlare
import com.stepalex.finny.presentation.goals.GameDialog
import com.stepalex.finny.presentation.home.HomeEvent
import com.stepalex.finny.presentation.home.HomeState
import com.stepalex.finny.presentation.home.ShowDialog


@Composable
fun ResetConfirmDialogAnimatable(event: ((HomeEvent) -> Unit), state: HomeState) {
    val isDialogVisible = state.showDialog == ShowDialog.ResetConfirm
    // Анимируем прозрачность черного фона от 0f до 0.6f (60% затемнения)
    val backgroundAlpha by animateFloatAsState(
        targetValue = if (isDialogVisible) 0.6f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "DialogBackgroundDim"
    )

    if (backgroundAlpha > 0f) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                // Накладываем черный цвет с анимированной прозрачностью
                .background(Color.Black.copy(alpha = backgroundAlpha))
                // Блокируем клики по экрану сквозь диалог + закрываем по клику на фон
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null // Убираем стандартный эффект волны (ripple) при клике на фон
                ) {
                    //event(HomeEvent.ClearSelectGoal)
                },
            contentAlignment = Alignment.Center
        ) {
            // Анимация самого диалогового окна
            AnimatedVisibility(
                visible = isDialogVisible,
                enter = scaleIn(
                    animationSpec = tween(durationMillis = 300),
                    initialScale = 0.8f
                ) + fadeIn(animationSpec = tween(durationMillis = 300)),
                exit = scaleOut(
                    animationSpec = tween(durationMillis = 250),
                    targetScale = 0.8f
                ) + fadeOut(animationSpec = tween(durationMillis = 250))
            ) {
                GameDialog(
                    title = "Сбросить данные",
                    text = "После сброса данных приложение закроется, нужно будет открыть его снова.",
                    negativeBtn = {
                        GameButton(
                            size = DpSize(120.dp, 48.dp),
                            colorBgr = Color(0xFFE2E2E2),
                            colorBorder = Color.DarkGray,
                            sizeBorder = 2.dp,
                            cornerRadius = 14.dp,
                            //  text = "Отмена",
                            typeGlare = TypeGlare.NONE,
                            onClick = { event(HomeEvent.DismissDialog) }
                        ) {
                            OutlineText("Отмена")
                        }
                    },
                    positiveBtn = {
                        GameButton(
                            size = DpSize(140.dp, 48.dp),
                            colorBgr = Color(0xFFFFD54F),
                            colorBorder = Color.Black,
                            sizeBorder = 2.5.dp,
                            cornerRadius = 14.dp,
                            //text = "Начать",
                            typeGlare = TypeGlare.TWO,
                            onClick = { event(HomeEvent.ResetData) }
                        ) {
                            OutlineText("Сбросить")
                        }
                    }
                )
            }
        }
    }
}