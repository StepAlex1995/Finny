package com.stepalex.finny.presentation.period

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stepalex.finny.domain.model.FinancialRank
import com.stepalex.finny.domain.model.HistoryTaskSnapshot
import com.stepalex.finny.domain.model.PeriodHistory
import com.stepalex.finny.presentation.common.GameButton
import com.stepalex.finny.presentation.common.OutlineText
import com.stepalex.finny.presentation.home.HomeEvent
import com.stepalex.finny.presentation.home.HomeState
import com.stepalex.finny.presentation.home.OpenWindow
import com.stepalex.finny.presentation.pet_room_bgr.Money
import com.stepalex.finny.utils.Fonts


@Composable
fun PeriodResultScreenAnimatable(
    event: ((HomeEvent) -> Unit),
    state: HomeState,
) {
    AnimatedVisibility(
        visible = state.openWindow == OpenWindow.ShowPeriodResult && state.periodHistory != null,
        enter = scaleIn(
            animationSpec = tween(durationMillis = 300), initialScale = 0.8f
        ) + fadeIn(animationSpec = tween(durationMillis = 300)),
        exit = scaleOut(
            animationSpec = tween(durationMillis = 250), targetScale = 0.8f
        ) + fadeOut(animationSpec = tween(durationMillis = 250))
    ) {
        if (state.periodHistory != null) {
            PeriodResultScreen(
                history = state.periodHistory, event = event
            )
        }
    }
}

@Composable
fun PeriodResultScreen(
    history: PeriodHistory,
    event: (HomeEvent) -> Unit,
) {
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
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = RoundedCornerShape(16.dp)),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. ШАПКА: НОМЕР ПЕРИОДА И ФИНАНСОВОЕ ЗВАНИЕ
                item {
                    Text(
                        text = "Итоги периода ${history.periodIndex}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0061A4),
                        fontFamily = Fonts.RegularTextFontFamily
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    // Динамический цвет плашки в зависимости от звания
                    val rankColor = when (history.rankAwarded) {
                        FinancialRank.MASTER -> Color(0xFFE8F5E9)
                        FinancialRank.SMART_BUYER -> Color(0xE3FFF8E1)
                        FinancialRank.SPENDER -> Color(0xFFFFEBEE)
                        FinancialRank.SCAM_VICTIM -> Color(0xFFECEFF1)
                    }
                    val rankBorderColor = when (history.rankAwarded) {
                        FinancialRank.MASTER -> Color(0xFF4CAF50)
                        FinancialRank.SMART_BUYER -> Color(0xFFFFFFB300)
                        FinancialRank.SPENDER -> Color(0xFFEF5350)
                        FinancialRank.SCAM_VICTIM -> Color(0xFF78909C)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(rankColor)
                            .border(1.5.dp, rankBorderColor, RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = history.rankAwarded.title,
                                color = Color.Black,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                fontFamily = Fonts.RegularTextFontFamily
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = history.rankAwarded.description,
                                color = Color(0xFF42474E),
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp,
                                fontFamily = Fonts.RegularTextFontFamily
                            )
                        }
                    }
                }

                // 2. ПОКАЗАТЕЛИ НА КОНЕЦ РАУНДА
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF7F9FA))
                            .border(1.dp, Color(0xFFEFF1F4), RoundedCornerShape(16.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Монетки за работу:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1A1C1E),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                fontFamily = Fonts.RegularTextFontFamily
                            )
                            Row(
                                modifier = Modifier
                                    .width(100.dp)
                                    .border(1.dp, Color(0xFFE1E3E5), RoundedCornerShape(12.dp))
                                    .background(Color.White, shape = RoundedCornerShape(12.dp))
                                    .padding(vertical = 6.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Money(modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(6.dp))

                                Text(
                                    text = history.workIncome.toString(),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32),
                                    fontFamily = Fonts.RegularTextFontFamily
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = "Эффекты предыдущих периодов:",
                                fontSize = 13.sp,
                                modifier = Modifier.weight(1f),
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1A1C1E),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                fontFamily = Fonts.RegularTextFontFamily
                            )
                            Row(
                                modifier = Modifier
                                    .width(100.dp)
                                    .border(1.dp, Color(0xFFE1E3E5), RoundedCornerShape(12.dp))
                                    .background(Color.White, shape = RoundedCornerShape(12.dp))
                                    .padding(vertical = 6.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Money(modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                val valueColor = when {
                                    history.pendingGoldEffect > 0 -> Color(0xFF2E7D32)
                                    history.pendingGoldEffect < 0 -> Color(0xFFC62828)
                                    else -> Color(0xFF535F66)
                                }
                                Text(
                                    text = (if (history.pendingGoldEffect > 0) "+" else "") + history.pendingGoldEffect,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = valueColor,
                                    fontFamily = Fonts.RegularTextFontFamily
                                )
                            }
                        }
                    }
                }

                // 3. РАЗДЕЛИТЕЛЬ И АНАЛИТИКА ТРАТ ПО ТИПАМ
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color(0xFFEFF1F4), thickness = 2.dp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Расходы за период:",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF72777A),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start,
                        fontFamily = Fonts.RegularTextFontFamily
                    )
                }

                // Список категорий расходов
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ExpenseStatRow(
                            title = "Необходимые (Нужды):",
                            amount = history.spentOnNecessity,
                            color = Color(0xFF0061A4)
                        )
                        ExpenseStatRow(
                            title = "Необязательные (Хотелки):",
                            amount = history.spentOnOptional,
                            color = Color(0xFFE65100)
                        )
                        ExpenseStatRow(
                            title = "Направлено на накопления:",
                            amount = history.spentOnAccumulation,
                            color = Color(0xFF2E7D32)
                        )
                        if (history.lostToScam > 0) {
                            ExpenseStatRow(
                                title = "Потери от мошенников:",
                                amount = history.lostToScam,
                                color = Color(0xFFC62828)
                            )
                        }
                    }
                }

                // 4. КНОПКА-СПОЙЛЕР ДЛЯ РАЗВЕРТЫВАНИЯ ИСТОРИИ РЕШЕНИЙ
                item {
                    var isHistoryVisible by remember { mutableStateOf(false) } // Состояние раскрытия всей истории

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color(0xFFEFF1F4), thickness = 2.dp)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Интерактивная кнопка-шапка
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF0F4F8)) // Выделяем плашку мягким цветом
                            .clickable { isHistoryVisible = !isHistoryVisible }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = (if (isHistoryVisible) "Скрыть " else "Показать ") + "историю решений",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF72777A),
                                fontFamily = Fonts.RegularTextFontFamily
                            )
                        }
                        // Динамическая стрелочка-индикатор
                        Text(
                            text = if (isHistoryVisible) "▲" else "▼",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF535F66),
                            fontFamily = Fonts.RegularTextFontFamily
                        )
                    }

                    // Внутри LazyColumn элементы развертки анимируются через AnimatedVisibility
                    // Мы дублируем Column внутри AnimatedVisibility, чтобы запустить прокрутку вложенных элементов
                    AnimatedVisibility(
                        visible = isHistoryVisible,
                        enter = expandVertically(
                            animationSpec = tween(
                                300
                            )
                        ) + fadeIn(),
                        exit = shrinkVertically(animationSpec = tween(250)) + fadeOut()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            history.chosenTasks.forEach { snapshot ->
                                HistoryTaskCollapseRow(snapshot = snapshot)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // КНОПКА ЗАВЕРШЕНИЯ: ОТПРАВЛЯЕТ СОБЫТИЕ ДЛЯ ПЕРЕХОДА НА ТАЙМЕР / СЛЕДУЮЩИЙ ШАГ
        Box(modifier = Modifier.padding(bottom = 24.dp)) {
            GameButton(
                size = DpSize(240.dp, 60.dp),
                colorBgr = Color(249, 222, 91),
                sizeBorder = 2.dp,
                colorBorder = Color.Black,
                cornerRadius = 18.dp,
                onClick = { event(HomeEvent.ShowHomeWindow) }
            ) {
                OutlineText("Далее")
            }
        }
    }

}

@Composable
fun ExpenseStatRow(title: String, amount: Int, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF7F9FA))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1A1C1E),
            fontFamily = Fonts.RegularTextFontFamily
        )
        Row(
            modifier = Modifier
                //.weight(1f)
                .border(1.dp, Color(0xFFE1E3E5), RoundedCornerShape(12.dp))
                .background(Color.White, shape = RoundedCornerShape(12.dp))
                .padding(vertical = 6.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Money(modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = amount.toString(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                fontFamily = Fonts.RegularTextFontFamily
            )
        }
    }

}


@Composable
fun HistoryTaskCollapseRow(snapshot: HistoryTaskSnapshot) {
    var isExpanded by remember { mutableStateOf(false) }
    // Определяем, правильный ли ответ выбрал ребенок
    val chosenAnswerObject =
        snapshot.task.answers.firstOrNull { it.text == snapshot.chosenAnswerText }
    val isPrefer = chosenAnswerObject?.isPrefer == true
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFE1E3E5), RoundedCornerShape(12.dp))
            .background(if (isPrefer) Color(0xFFF9FBF9) else Color(0xFFFDF9F9))
            .clickable { isExpanded = !isExpanded }
            .padding(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = snapshot.task.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1C1E),
                    fontFamily = Fonts.RegularTextFontFamily
                )
                Text(
                    text = if (isPrefer) "Правильный выбор" else "Были варианты лучше",
                    fontSize = 12.sp,
                    color = if (isPrefer) Color(0xFF2E7D32) else Color(0xFFC62828),
                    fontWeight = FontWeight.Medium,
                    fontFamily = Fonts.RegularTextFontFamily
                )
            }
            Text(text = if (isExpanded) "▲" else "▼", fontSize = 12.sp, color = Color.Gray)
        }
        if (isExpanded) {
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(
                color = Color(0xFFEFF1F4), thickness = 1.dp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Ситуация: ${snapshot.task.description}",
                fontSize = 13.sp,
                color = Color(0xFF535F66),
                lineHeight = 18.sp,
                fontFamily = Fonts.RegularTextFontFamily
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Выбранный ответ: «${snapshot.chosenAnswerText}»",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1C1E),
                fontFamily = Fonts.RegularTextFontFamily
            )
            if (chosenAnswerObject != null) {
                Spacer(
                    modifier = Modifier.height(4.dp)
                )
                Text(
                    text = "Последствия: ${chosenAnswerObject.description}",
                    fontSize = 13.sp,
                    //color = Color(0xFF42474E),
                    color = if (isPrefer) Color(0xFF2E7D32) else Color(0xFFC62828),
                    lineHeight = 18.sp,
                    fontFamily = Fonts.RegularTextFontFamily
                )
            }
        }
    }
}