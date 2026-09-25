package com.stepalex.finny.presentation.goals

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stepalex.finny.domain.model.Goal
import com.stepalex.finny.domain.model.GoalState
import com.stepalex.finny.domain.model.GoalType
import com.stepalex.finny.domain.model.Profile
import com.stepalex.finny.presentation.common.GameButton
import com.stepalex.finny.presentation.common.OutlineText
import com.stepalex.finny.presentation.home.HomeEvent
import com.stepalex.finny.presentation.home.HomeState
import com.stepalex.finny.presentation.home.OpenWindow
import com.stepalex.finny.presentation.pet_room_bgr.Money

@Composable
fun GoalScreenAnimatable(event: ((HomeEvent) -> Unit), state: HomeState) {
    AnimatedVisibility(
        visible = state.openWindow == OpenWindow.Goals,
        // Анимация ПОЯВЛЕНИЯ: соединяем плавное увеличение (scaleIn) и появление (fadeIn)
        enter = scaleIn(
            animationSpec = tween(durationMillis = 300),
            initialScale = 0.8f
        ) + fadeIn(animationSpec = tween(durationMillis = 300)),
        // Анимация ИСЧЕЗНОВЕНИЯ: уменьшение (scaleOut) и затухание (fadeOut)
        exit = scaleOut(
            animationSpec = tween(durationMillis = 250),
            targetScale = 0.8f
        ) + fadeOut(animationSpec = tween(durationMillis = 250))
    ) {
        GoalsScreen(
            state.goals,
            state.profile,
            modifier = Modifier.fillMaxSize(),
            onClose = { event(HomeEvent.ShowHomeWindow) },
            onGoalClick = { goal -> event(HomeEvent.OnGoalClick(goal)) })
    }
}

@Composable
fun GoalsScreen(
    goals: List<Goal>,
    profile: Profile?,
    modifier: Modifier,
    onGoalClick: (Goal) -> Unit,
    onClose: () -> Unit
) {

    val GamePrimaryBg = Color(0xFFFFF6E5) // Мягкий бежевый фон, как стены в комнате
    val GameTextDark = Color(0xFF2C2C2C)   // Темно-серый/черный для текста
    Box(
        modifier = modifier
            .fillMaxSize()
            // Затемняем задний фон (комнату питомца)
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClose() }, // Закрыть при тапе на пустую область вокруг карточки
        contentAlignment = Alignment.Center
    ) {
        // Главное диалоговое окно игры
        Column(
            modifier = Modifier
                .fillMaxWidth(1f)
                .fillMaxHeight(1f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {} // Защита от закрытия при клике внутри самого окна
                .background(GamePrimaryBg)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- ШАПКА ОКНА ---
            // Крупный мультяшный заголовок
            OutlineText(
                text = "Развлечения и цели",
                fontSize = 24.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp)
            )
            // Игровая плашка текущего баланса под заголовком
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    //.border(2.5.dp, Color.Black, RoundedCornerShape(12.dp))
                    //.background(Color(0xFFFFFDF6)) // Светло-желтый фон для золота
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Money(modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Всего: ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "" + (profile?.countMood ?: 100),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = GameTextDark
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- СПИСОК ИГР ---
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(if (profile == null) 0.9f else 1f),
                verticalArrangement = Arrangement.spacedBy(16.dp) // Отступы между карточками игр
            ) {
                items(items = goals, key = { goal -> goal.name }) { goal ->
                    GoalCardItem(goal = goal) {
                        onGoalClick(goal)
                    }
                }
            }
            if (profile == null) {
                GameButton(
                    size = DpSize(128.dp, 64.dp),
                    colorBgr = Color(249, 222, 91),
                    sizeBorder = 2.dp,
                    colorBorder = Color.Black,
                    cornerRadius = 12.dp,
                    text = "НАЗАД",
                    iconColor = Color.Black
                ) {
                    onClose()
                }
            }
        }
    }
}

@Preview
@Composable
fun GoalsScreenPreview() {
    val defaultGoals = listOf(
        Goal(
            name = "Хочу или надо",
            description = "Кто-то перемешал все хотелки и нужды. Распредели все по местам и получи монетки!",
            cost = 600,
            goalType = GoalType.WONT_AND_NEED,
            status = GoalState.AVAILABLE
        ),
        Goal(
            name = "Что это такое",
            description = "Найди среди всех терминов два одинаковых, покажи, что он значит и получай больше монеток!",
            cost = 800,
            goalType = GoalType.TERMS,
            status = GoalState.NOT_AVAILABLE
        ),
        Goal(
            name = "Шахта сокровищ",
            description = "Один богач закопал свои сокровища в этой шахте, но чтоб их получить, нужно решить задачку.",
            cost = 1200,
            goalType = GoalType.MINE,
            status = GoalState.NOT_AVAILABLE
        )
    )
    GoalsScreen(
        goals = defaultGoals,
        profile = null,
        modifier = Modifier.fillMaxSize(),
        onGoalClick = {}) { }
}