package com.stepalex.finny.presentation.task

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stepalex.finny.domain.model.TaskResult
import com.stepalex.finny.presentation.common.GameButton
import com.stepalex.finny.presentation.common.OutlineText
import com.stepalex.finny.presentation.common.food.Carrot
import com.stepalex.finny.presentation.home.HomeEvent
import com.stepalex.finny.presentation.home.HomeState
import com.stepalex.finny.presentation.home.OpenWindow
import com.stepalex.finny.presentation.pet_room_bgr.Money
import com.stepalex.finny.presentation.pet_room_bgr.MoodIndicator
import com.stepalex.finny.presentation.pet_room_bgr.MoodSmile
import com.stepalex.finny.utils.Fonts
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun ResultTaskScreenAnimatable(event: ((HomeEvent) -> Unit), state: HomeState) {
    AnimatedVisibility(
        visible = state.openWindow == OpenWindow.ShowResultTaskAnswer,
        // Анимация ПОЯВЛЕНИЯ: соединяем плавное увеличение (scaleIn) и появление (fadeIn)
        enter = scaleIn(
            animationSpec = tween(durationMillis = 300), initialScale = 0.8f
        ) + fadeIn(animationSpec = tween(durationMillis = 300)),
        // Анимация ИСЧЕЗНОВЕНИЯ: уменьшение (scaleOut) и затухание (fadeOut)
        exit = scaleOut(
            animationSpec = tween(durationMillis = 250), targetScale = 0.8f
        ) + fadeOut(animationSpec = tween(durationMillis = 250))
    ) {
        ResultTaskScreen(
            state = state,
            event = event,
        )
    }
}


@Composable
fun ResultTaskScreen(
    state: HomeState,
    event: (HomeEvent) -> Unit,
) {
    var isContentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200.milliseconds)
        isContentVisible = true
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(top = 250.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Тени нет
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = RoundedCornerShape(16.dp)),
                contentPadding = PaddingValues(
                    start = 24.dp,
                    top = 24.dp,
                    end = 24.dp,
                    bottom = 24.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. ТЕГ ПРЕДПОЧТИТЕЛЬНОГО ВАРИАНТА
                if (state.selectedTaskAnswer!!.isPrefer) {
                    item {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFE8F5E9))
                                .border(1.5.dp, Color(0xFF4CAF50), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Отличный выбор!",//"✨"
                                color = Color(0xFF2E7D32),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = Fonts.RegularTextFontFamily
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }

                // 2. ТЕКСТ ВЫБРАННОГО ОТВЕТА
                item {
                    Text(
                        text = "Ты выбрал:\n«${state.selectedTaskAnswer.text}»",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1C1E),
                        textAlign = TextAlign.Center,
                        lineHeight = 26.sp,
                        fontFamily = Fonts.RegularTextFontFamily
                    )
                }

                // 3. ОПИСАНИЕ ПОСЛЕДСТВИЙ
                item {
                    Text(
                        text = state.selectedTaskAnswer.description,
                        fontSize = 16.sp,
                        color = Color(0xFF42474E),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        fontFamily = Fonts.RegularTextFontFamily
                    )
                }

                // 4. РАЗДЕЛИТЕЛЬ И ЗАГОЛОВОК РЕСУРСОВ
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFEFF1F4), thickness = 2.dp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Влияние на ресурсы:",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF72777A),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start,
                        fontFamily = Fonts.RegularTextFontFamily
                    )
                }

                // 5. ДИНАМИЧЕСКИЙ СПИСОК ПЕРИОДОВ
                items(state.selectedTaskAnswer.taskResult) { result ->
                    PeriodResultRow(result = result)
                }
            }
        }


        // Оставляем место под нижнюю плавающую кнопку
        Spacer(modifier = Modifier.height(16.dp))
        // --- КНОПКА «ДАЛЕЕ» ЗАФИКСИРОВАНА В САМОМ НИЗУ ЭКРАНА ---
        Box(
            modifier = Modifier
                .padding(bottom = 24.dp)
        ) {
            GameButton(
                size = DpSize(240.dp, 60.dp),
                colorBgr = Color(249, 222, 91),
                sizeBorder = 2.dp,
                colorBorder = Color.Black,
                cornerRadius = 18.dp,
                onClick = { event(HomeEvent.CompleteQuizTask(state.selectedTaskAnswer!!)) }
            ) {
                OutlineText("Понятно")
            }
        }
    }

}


@Composable
fun PeriodResultRow(result: TaskResult) {
    val isImmediate = result.period == 0
    val periodText = if (isImmediate) "Сразу" else "Через ${result.period} период(а)"
    val backgroundColor = if (isImmediate) Color(0xFFF0F4F8) else Color(0xFFF7F9FA)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .padding(12.dp)
    ) {
        // Заголовок периода (Сразу / Будущее)
        Text(
            text = periodText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (isImmediate) Color(0xFF0061A4) else Color(0xFF535F66),
            fontFamily = Fonts.RegularTextFontFamily
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Ряд с тремя пулами ресурсов
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color(0xFFE1E3E5), RoundedCornerShape(12.dp))
                    .background(Color.White, shape = RoundedCornerShape(12.dp))
                    .padding(vertical = 6.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Money(modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(8.dp))
                val valueColor = when {
                    result.gold > 0 -> Color(0xFF2E7D32) // Зеленый для плюса
                    result.gold < 0 -> Color(0xFFC62828) // Красный для минуса
                    else -> Color(0xFF535F66)       // Серый для нуля
                }
                Text(
                    text = (if (result.gold > 0) "+" else "") + result.gold.toString(),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = valueColor,
                    fontFamily = Fonts.RegularTextFontFamily
                )
            }
            Row(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color(0xFFE1E3E5), RoundedCornerShape(12.dp))
                    .background(Color.White, shape = RoundedCornerShape(12.dp))
                    .padding(vertical = 6.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Carrot(modifier = Modifier.size(32.dp), isFilled = true)
                Spacer(modifier = Modifier.width(8.dp))
                val valueColor = when {
                    result.food > 0 -> Color(0xFF2E7D32) // Зеленый для плюса
                    result.food < 0 -> Color(0xFFC62828) // Красный для минуса
                    else -> Color(0xFF535F66)       // Серый для нуля
                }
                Text(
                    text = (if (result.food > 0) "+" else "") + result.food.toString(),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = valueColor,
                    fontFamily = Fonts.RegularTextFontFamily
                )
            }
            Row(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color(0xFFE1E3E5), RoundedCornerShape(12.dp))
                    .background(Color.White, shape = RoundedCornerShape(12.dp))
                    .padding(vertical = 6.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                val moodIndicator = when {
                    result.mood > 0 -> MoodIndicator.HAPPY
                    result.mood < 0 -> MoodIndicator.SAD
                    else -> MoodIndicator.NORMAL
                }
                val valueColor = when {
                    result.mood > 0 -> Color(0xFF2E7D32) // Зеленый для плюса
                    result.mood < 0 -> Color(0xFFC62828) // Красный для минуса
                    else -> Color(0xFF535F66)       // Серый для нуля
                }
                MoodSmile(
                    modifier = Modifier.size(32.dp),
                    isFilled = true,
                    moodIndicator = moodIndicator
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = (if (result.mood > 0) "+" else "") + result.mood.toString(),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = valueColor,
                    fontFamily = Fonts.RegularTextFontFamily
                )
            }
        }
    }
}
