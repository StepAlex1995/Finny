package com.stepalex.finny.presentation.task

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stepalex.finny.R
import com.stepalex.finny.presentation.common.GameButton
import com.stepalex.finny.presentation.home.HomeEvent
import com.stepalex.finny.presentation.home.HomeState
import com.stepalex.finny.presentation.home.OpenWindow
import com.stepalex.finny.presentation.common.icons.IdeaBulb
import com.stepalex.finny.utils.Fonts
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun TaskQuestionScreenAnimatable(event: ((HomeEvent) -> Unit), state: HomeState) {
    AnimatedVisibility(
        visible = state.openWindow == OpenWindow.ShowTask,
        // Анимация ПОЯВЛЕНИЯ: соединяем плавное увеличение (scaleIn) и появление (fadeIn)
        enter = scaleIn(
            animationSpec = tween(durationMillis = 300), initialScale = 0.8f
        ) + fadeIn(animationSpec = tween(durationMillis = 300)),
        // Анимация ИСЧЕЗНОВЕНИЯ: уменьшение (scaleOut) и затухание (fadeOut)
        exit = scaleOut(
            animationSpec = tween(durationMillis = 250), targetScale = 0.8f
        ) + fadeOut(animationSpec = tween(durationMillis = 250))
    ) {
        TaskQuestionScreen(
            state = state,
            event = event,
        )
    }
}

@Composable
fun TaskQuestionScreen(
    state: HomeState,
    event: (HomeEvent) -> Unit,
) {
    // Состояние, показывающее, открыта ли подсказка
    var isHelpVisible by remember { mutableStateOf(false) }

    // Состояние для отложенного показа помощи
    var isDescriptionVisible by remember { mutableStateOf(false) }
    // Таймер на 5 секунд, который запускается при появлении экрана
    LaunchedEffect(state.currentTask?.id) { // Перезапустится, если id задачи изменится
        delay(5000.milliseconds) // Ждем 5000 мс (5 секунд)
        isDescriptionVisible = true
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.75f)
            .background(Color.Transparent)
            .padding(top = 150.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- КАРТОЧКА СИТУАЦИИ (ОПИСАНИЕ ЗАДАЧИ) ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 2.dp, Color.DarkGray, shape = RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(animationSpec = tween(durationMillis = 100))
                    .padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Заголовок квиза
                Text(
                    text = state.currentTask!!.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF1A1C1E),
                    fontFamily = Fonts.RegularTextFontFamily
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Описание ситуации понятным языком
                Text(
                    text = state.currentTask.description,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF42474E),
                    lineHeight = 24.sp,
                    fontFamily = Fonts.RegularTextFontFamily
                )

                Spacer(modifier = Modifier.height(12.dp))
                // --- БЛОК ПОДСКАЗКИ (HELP) ---
                if (!state.currentTask.help.isNullOrBlank()) {
                    AnimatedVisibility(
                        visible = isDescriptionVisible,
                        enter = fadeIn(animationSpec = tween(durationMillis = 500))
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            TextButton(onClick = { isHelpVisible = !isHelpVisible }) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IdeaBulb(
                                        modifier = Modifier.size(32.dp), isFilled = !isHelpVisible
                                    )
                                    Text(
                                        text = if (isHelpVisible) "Скрыть подсказку" else "Подсказка",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.Gray,
                                        fontFamily = Fonts.RegularTextFontFamily
                                    )
                                }
                            }

                            AnimatedVisibility(
                                visible = isHelpVisible,
                                // Слегка расширим анимацию для более мягкого эффекта появления
                                enter = fadeIn(animationSpec = tween(300)) + expandVertically(
                                    animationSpec = tween(300)
                                ), exit = fadeOut(animationSpec = tween(250)) + shrinkVertically(
                                    animationSpec = tween(250)
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFFE1F5FE))
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = state.currentTask.help,
                                        fontSize = 15.sp,
                                        color = Color(0xFF0288D1),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth(),
                                        fontFamily = Fonts.RegularTextFontFamily
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun TaskAnswersScreenAnimatable(event: ((HomeEvent) -> Unit), state: HomeState) {
    AnimatedVisibility(
        visible = state.openWindow == OpenWindow.ShowTask,
        // Анимация ПОЯВЛЕНИЯ: соединяем плавное увеличение (scaleIn) и появление (fadeIn)
        enter = scaleIn(
            animationSpec = tween(durationMillis = 300), initialScale = 0.8f
        ) + fadeIn(animationSpec = tween(durationMillis = 300)),
        // Анимация ИСЧЕЗНОВЕНИЯ: уменьшение (scaleOut) и затухание (fadeOut)
        exit = scaleOut(
            animationSpec = tween(durationMillis = 250), targetScale = 0.8f
        ) + fadeOut(animationSpec = tween(durationMillis = 250))
    ) {
        TaskAnswersScreen(
            state = state,
            event = event,
        )
    }
}


@Composable
fun TaskAnswersScreen(
    state: HomeState,
    event: (HomeEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.35f)
            .background(Color.Transparent)
            .padding(start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            state.currentTask!!.answers.forEach { answer ->
                GameButton(
                    size = DpSize(1000.dp, 64.dp),
                    colorBgr = Color(91, 222, 249),
                    sizeBorder = 2.dp,
                    colorBorder = Color.Black,
                    cornerRadius = 18.dp,
                    onClick = { event(HomeEvent.SelectTaskAnswer(answer)) }) {
                    Text(
                        text = answer.text,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        fontFamily = Fonts.RegularTextFontFamily
                    )
                }
            }
        }
        // Кнопка «Выйти», если ребёнок передумал решать прямо сейчас
        Spacer(modifier = Modifier.height(16.dp))
        GameButton(
            size = DpSize(160.dp, 48.dp),
            colorBgr = Color(213, 213, 213),
            sizeBorder = 2.dp,
            colorBorder = Color.Black,
            cornerRadius = 18.dp,
            onClick = { event(HomeEvent.ShowHomeWindow) }) {
            Text(
                text = "Вернуться позже",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                fontFamily = Fonts.RegularTextFontFamily
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

    }
}