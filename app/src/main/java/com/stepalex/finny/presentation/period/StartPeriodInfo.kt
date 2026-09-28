package com.stepalex.finny.presentation.period

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
import androidx.compose.foundation.layout.fillMaxHeight
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
import com.stepalex.finny.domain.model.ScheduledEffect
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


@Composable
fun StartPeriodInfoScreenAnimatable(event: ((HomeEvent) -> Unit), state: HomeState) {
    AnimatedVisibility(
        visible = state.openWindow == OpenWindow.ShowStartPeriodInfo,
        // Анимация ПОЯВЛЕНИЯ: соединяем плавное увеличение (scaleIn) и появление (fadeIn)
        enter = scaleIn(
            animationSpec = tween(durationMillis = 300), initialScale = 0.8f
        ) + fadeIn(animationSpec = tween(durationMillis = 300)),
        // Анимация ИСЧЕЗНОВЕНИЯ: уменьшение (scaleOut) и затухание (fadeOut)
        exit = scaleOut(
            animationSpec = tween(durationMillis = 250), targetScale = 0.8f
        ) + fadeOut(animationSpec = tween(durationMillis = 250))
    ) {
        StartPeriodInfoScreen(
            state = state,
            event = event,
        )
    }
}

@Composable
fun StartPeriodInfoScreen(
    state: HomeState,
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
                // ГЛАВНЫЙ ТЕКСТ-ПРИВЕТСТВИЕ
                item {
                    Text(
                        text = "Наступил новый период.\nДавай посмотрим, что изменилось!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1C1E),
                        textAlign = TextAlign.Center,
                        lineHeight = 26.sp,
                        fontFamily = Fonts.RegularTextFontFamily
                    )
                }
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
                                color = Color(0xFF535F66),
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
                                    text = "+100",
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
                                text = "Долго не ел:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF535F66),
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
                                Carrot(
                                    modifier = Modifier.size(24.dp),
                                    isFilled = true
                                )
                                Spacer(modifier = Modifier.width(6.dp))

                                Text(
                                    text = "-2",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFC62828),
                                    fontFamily = Fonts.RegularTextFontFamily
                                )
                            }
                        }
                    }
                }

                // РАЗДЕЛИТЕЛЬ И ЗАГОЛОВОК ЭФФЕКТОВ
                if (state.appliedEffects.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Color(0xFFEFF1F4), thickness = 2.dp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (state.appliedEffects.isEmpty()) "Нет активных эффектов" else "Эффекты прошлых периодов:",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF72777A),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start,
                            fontFamily = Fonts.RegularTextFontFamily
                        )
                    }

                    // ДИНАМИЧЕСКИЙ СПИСОК ЭФФЕКТОВ ИЗ ПРОШЛОГО
                    items(state.appliedEffects) { effect ->
                        AppliedEffectRow(effect = effect)
                    }
                }
            }
        }

        // Пространство под кнопку
        Spacer(modifier = Modifier.height(16.dp))

        // --- КНОПКА «ПОНЯТНО» ЗАФИКСИРОВАНА В САМОМ НИЗУ ---
        Box(
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            GameButton(
                size = DpSize(240.dp, 60.dp),
                colorBgr = Color(249, 222, 91),
                sizeBorder = 2.dp,
                colorBorder = Color.Black,
                cornerRadius = 18.dp,
                onClick = {
                    event(HomeEvent.ShowHomeWindow)
                }
            ) {
                OutlineText("Понятно")
            }
        }
    }
}

@Composable
fun AppliedEffectRow(effect: ScheduledEffect) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF7F9FA))
            .border(1.dp, Color(0xFFEFF1F4), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        // Источник эффекта (Название задачи и ваше решение)
        Text(
            text = "Из-за решения: «${effect.chosenAnswerText}»",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF535F66),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            fontFamily = Fonts.RegularTextFontFamily
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Ряд с тремя пулами ресурсов
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // ЗОЛОТО
            Row(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color(0xFFE1E3E5), RoundedCornerShape(12.dp))
                    .background(Color.White, shape = RoundedCornerShape(12.dp))
                    .padding(vertical = 6.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Money(modifier = Modifier.size(24.dp)) // Чуть уменьшил иконку, чтобы текст не сжимался
                Spacer(modifier = Modifier.width(6.dp))
                val valueColor = when {
                    effect.gold > 0 -> Color(0xFF2E7D32)
                    effect.gold < 0 -> Color(0xFFC62828)
                    else -> Color(0xFF535F66)
                }
                Text(
                    text = (if (effect.gold > 0) "+" else "") + effect.gold.toString(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = valueColor,
                    fontFamily = Fonts.RegularTextFontFamily
                )
            }

            // ЕДА
            Row(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color(0xFFE1E3E5), RoundedCornerShape(12.dp))
                    .background(Color.White, shape = RoundedCornerShape(12.dp))
                    .padding(vertical = 6.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Carrot(modifier = Modifier.size(24.dp), isFilled = true)
                Spacer(modifier = Modifier.width(6.dp))
                val valueColor = when {
                    effect.food > 0 -> Color(0xFF2E7D32)
                    effect.food < 0 -> Color(0xFFC62828)
                    else -> Color(0xFF535F66)
                }
                Text(
                    text = (if (effect.food > 0) "+" else "") + effect.food.toString(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = valueColor,
                    fontFamily = Fonts.RegularTextFontFamily
                )
            }

            // НАСТРОЕНИЕ
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
                    effect.mood > 0 -> MoodIndicator.HAPPY
                    effect.mood < 0 -> MoodIndicator.SAD
                    else -> MoodIndicator.NORMAL
                }
                val valueColor = when {
                    effect.mood > 0 -> Color(0xFF2E7D32)
                    effect.mood < 0 -> Color(0xFFC62828)
                    else -> Color(0xFF535F66)
                }
                MoodSmile(
                    modifier = Modifier.size(24.dp),
                    isFilled = true,
                    moodIndicator = moodIndicator
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = (if (effect.mood > 0) "+" else "") + effect.mood.toString(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = valueColor,
                    fontFamily = Fonts.RegularTextFontFamily
                )
            }
        }
    }
}