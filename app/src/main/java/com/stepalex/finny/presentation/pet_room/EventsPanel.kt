package com.stepalex.finny.presentation.pet_room

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stepalex.finny.presentation.common.GameButton
import com.stepalex.finny.presentation.common.OutlineText
import com.stepalex.finny.presentation.home.HomeState
import com.stepalex.finny.presentation.home.OpenWindow
import com.stepalex.finny.presentation.home.PeriodState
import com.stepalex.finny.utils.Fonts


@Composable
fun EventsPanel(
    state: HomeState,
    onStartTaskClick: () -> Unit,
    onTaskClick: () -> Unit,
    onSkipTimer: () -> Unit
) {
    val colorBgr = Color(247, 243, 231)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorBgr, shape = RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        when {
            state.periodState is PeriodState.Locked -> {
                // Шаг 1: Доступна только обязательная задача (Работа)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Новый период начался!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontFamily = Fonts.RegularTextFontFamily,
                        color = Color.DarkGray
                    )
                    Text(
                        "Выполните вводное задание, чтобы открыть доступ к событиям.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = Fonts.RegularTextFontFamily,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    GameButton(
                        size = DpSize(220.dp, 64.dp),
                        colorBgr = Color(249, 222, 91),
                        sizeBorder = 2.dp,
                        colorBorder = Color.Black,
                        cornerRadius = 12.dp,
                        iconColor = Color.Black,
                        onClick = onStartTaskClick
                    ) {
                        OutlineText("Выполнить стартовое задание")
                    }
                }
            }

            state.periodState is PeriodState.InProgress && state.currentTask != null -> {
                // Шаг 2: Доступны квизы (показываем счетчик 0..4 из 5)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        state.profile!!.currentPeriodIndex.toString() + " период",
                        style = MaterialTheme.typography.headlineSmall,
                        fontFamily = Fonts.RegularTextFontFamily,
                        color = Color.DarkGray
                    )
                    Text(
                        "Событие ${state.periodState.completedCount + 1} из 5",
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = Fonts.RegularTextFontFamily,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    GameButton(
                        size = DpSize(220.dp, 64.dp),
                        colorBgr = Color(249, 222, 91),
                        sizeBorder = 2.dp,
                        colorBorder = Color.Black,
                        cornerRadius = 12.dp,
                        iconColor = Color.Black,
                        onClick = onTaskClick
                    ) {
                        OutlineText(state.currentTask.title)
                    }
                }
            }


            state.periodState is PeriodState.WaitingForNextPeriod -> {
                // Шаг 3: Все 5 решено, тикает таймер
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Все события пройдены!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontFamily = Fonts.RegularTextFontFamily,
                        color = Color.DarkGray
                    )
                    Text(
                        "Следующий период откроется через:",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = Fonts.RegularTextFontFamily,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = state.periodState.remainingTime,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 48.sp,
                        fontFamily = Fonts.RegularTextFontFamily,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Bold, color = Color(0xFF6E4E37)//0xFFAC8A64)
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // КНОПКА МГНОВЕННОГО СБРОСА ТАЙМЕРА
                    Button(
                        onClick = onSkipTimer,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFAC8A64)
                        )
                    ) {
                        Text("Пропустить (Для ДЕМО)")
                    }
                }
            }
        }
    }
}
