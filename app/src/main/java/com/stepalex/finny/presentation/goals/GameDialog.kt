package com.stepalex.finny.presentation.goals

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stepalex.finny.presentation.common.GameButton
import com.stepalex.finny.presentation.common.OutlineText
import com.stepalex.finny.presentation.common.TypeGlare
import com.stepalex.finny.presentation.home.HomeEvent
import com.stepalex.finny.presentation.home.HomeState
import com.stepalex.finny.presentation.home.ShowDialog

@Composable
fun GameDialogAnimatable(event: ((HomeEvent) -> Unit), state: HomeState) {
    val isDialogVisible = state.showDialog == ShowDialog.SelectGoal//state.selectGoal != null
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
                    event(HomeEvent.ClearSelectGoal)
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
                    title = "Выбрать цель?",
                    text = "Хочешь начать копить монетки на игру \"${state.selectGoal?.name}\"?",
                    negativeBtn = {
                        GameButton(
                            size = DpSize(120.dp, 48.dp),
                            colorBgr = Color(0xFFE2E2E2),
                            colorBorder = Color.Black,
                            sizeBorder = 2.dp,
                            cornerRadius = 14.dp,
                            //  text = "Отмена",
                            typeGlare = TypeGlare.NONE,
                            onClick = { event(HomeEvent.ClearSelectGoal) }
                        ) {
                            OutlineText("Отмена")
                        }
                    },
                    positiveBtn = {
                        GameButton(
                            size = DpSize(140.dp, 48.dp),
                            colorBgr = Color(0xFFFFD54F),
                            colorBorder = Color.Black,
                            sizeBorder = 3.dp,
                            cornerRadius = 14.dp,
                            //text = "Начать",
                            typeGlare = TypeGlare.TWO,
                            onClick = { event(HomeEvent.SelectGoal(goal = state.selectGoal!!)) }
                        ) {
                            OutlineText("Начать")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun GameDialog(
    title: String,
    text: String,
    positiveBtn: @Composable () -> Unit,
    negativeBtn: @Composable () -> Unit,
) {
    val DialogBg = Color(0xFFFFFDF6)
    val TextDark = Color(0xFF2C2C2C)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(3.dp, Color.Black, RoundedCornerShape(28.dp)) // Жирный игровой контур
                .clip(RoundedCornerShape(28.dp))
                .background(DialogBg)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- ЗАГОЛОВОК ДИАЛОГА ---
            Text(
                text = title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black, // Очень жирный шрифт для игр
                color = TextDark,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // --- ОСНОВНОЙ ТЕКСТ ---
            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- КНОПКИ ДЕЙСТВИЯ ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Обернем каждую кнопку в weight(1f), чтобы они делили место поровну
                Row(modifier = Modifier.weight(1f)) {
                    negativeBtn()
                }

                Spacer(modifier = Modifier.width(12.dp))

                Row(modifier = Modifier.weight(1f)) {
                    positiveBtn()
                }
            }
        }
    }

}


@Preview
@Composable
fun GameDialogPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray)
    ) {
        GameDialog(
            title = "Разблокировать игру?",
            text = "Вы хотите потратить 600 монеток, чтобы открыть игру \"Хочу или надо\"?",
            negativeBtn = {
                GameButton(
                    size = DpSize(120.dp, 48.dp),
                    colorBgr = Color(0xFFE2E2E2),
                    colorBorder = Color.Black,
                    sizeBorder = 2.dp,
                    cornerRadius = 14.dp,
                    //text = "Отмена",
                    typeGlare = TypeGlare.NONE, // Для нейтральной кнопки можно отключить блик
                    onClick = { /* закрыть диалог */ }
                ){
                    OutlineText("Отмена")
                }
            },
            positiveBtn = {
                GameButton(
                    size = DpSize(140.dp, 48.dp),
                    colorBgr = Color(0xFFFFD54F), // Яркий желтый
                    colorBorder = Color.Black,
                    sizeBorder = 3.dp, // Жирный контур для акцента
                    cornerRadius = 14.dp,
                    //text = "Открыть",
                    typeGlare = TypeGlare.TWO, // Сочный двойной блик
                    onClick = { /* логика списания монет */ }
                ){
                    OutlineText("Открыть")
                }
            }
        )
    }
}