package com.stepalex.finny.presentation.goals

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stepalex.finny.domain.model.Goal
import com.stepalex.finny.domain.model.GoalState
import com.stepalex.finny.domain.model.GoalType
import com.stepalex.finny.presentation.pet_room_bgr.Money
import com.stepalex.finny.utils.Fonts.RegularTextFontFamily

@Composable
fun GoalCardItem(goal: Goal, onClick: () -> Unit) {
    val GameCardBg = Color(0xFFFFFFFF)    // Белый фон для карточек игр
    val GameProgressBarBg = Color(0xFFE2E2E2) // Серый цвет для неактивных шкал
    val GamePrimaryBg = Color(0xFFFFF6E5) // Мягкий бежевый фон, как стены в комнате
    val GameTextDark = Color(0xFF2C2C2C)   // Темно-серый/черный для текста

    val isAvailable = goal.status != GoalState.NOT_AVAILABLE

    // Состояние нажата ли карточка в данный момент
    var isPressed by remember { mutableStateOf(false) }
    // Анимация масштаба: при нажатии уменьшаем до 96%
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "CardScale"
    )
    // Анимация сдвига вниз: создает иллюзию "утапливания" кнопки
    val translationY by animateFloatAsState(
        targetValue = if (isPressed) 4f else 0f,
        animationSpec = tween(durationMillis = 100),
        label = "CardTranslation"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            // Применяем игровые анимации нажатия
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.translationY = translationY.dp.toPx()
            }
            .border(2.dp, Color.DarkGray, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            // Если игра недоступна, делаем карточку слегка прозрачной
            .background(if (isAvailable) GameCardBg else GameCardBg.copy(alpha = 0.6f))
            //.clickable { onClick() }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        try {
                            awaitRelease() // Ждем, пока пользователь отпустит палец
                        } finally {
                            isPressed = false // Возвращаем в исходное состояние
                        }
                        onClick() // Вызываем клик только после успешного отпускания
                    }
                )
            }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // --- МЕСТО ПОД КАРТИНКУ ИГРЫ ---
        Box(
            modifier = Modifier
                .size(80.dp)
                .border(2.dp, Color.DarkGray, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                // Здесь будет ваша картинка (Image). Пока сделаем цветной плейсхолдер
                .background(if (isAvailable) Color(0xFFFFD54F) else GameProgressBarBg),
            contentAlignment = Alignment.Center
        ) {
            // Тут можно использовать Иконку или Image() в зависимости от GoalType
            Text(
                text = if (goal.goalType == GoalType.BY_PARENT) "\uD83C\uDFC6" else "🎮", // Иконка игры по умолчанию
                fontSize = 32.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // --- ИНФОРМАЦИЯ ОБ ИГРЕ ---
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = goal.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                color = if (isAvailable) GameTextDark else Color.Gray,
                fontFamily = RegularTextFontFamily
            )

            goal.description?.let { desc ->
                Text(
                    text = desc,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Панель стоимости и статуса доступа
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isAvailable) {
                    // Плашка с ценой монеток (в стилистике верхней панели вашего скрина)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp))
                            .background(Color(0xFFFFFDF6))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Money(modifier = Modifier.size(24.dp))
                        Text(
                            text = "${goal.cost}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = GameTextDark
                        )
                    }
                } else {
                    if (goal.goalType == GoalType.BY_PARENT) {
                        Text(
                            text = "Получить",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2FD32F), // Красный цвет ошибки/блокировки
                            modifier = Modifier
                                .border(1.5.dp, Color(0xFF2FD32F), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun GoalCardItemPreview() {
    Column {
        GoalCardItem(
            Goal(
                name = "Шахта сокровищ",
                description = "Один богач закопал свои сокровища в этой шахте, но чтоб их получить, нужно решить задачку.",
                cost = 1200,
                goalType = GoalType.MINE,
                status = GoalState.NOT_AVAILABLE
            )
        ) {}
        GoalCardItem(
            Goal(
                name = "Шахта сокровищ",
                description = "Один богач закопал свои сокровища в этой шахте, но чтоб их получить, нужно решить задачку.",
                cost = 1200,
                goalType = GoalType.MINE,
                status = GoalState.AVAILABLE
            )
        ) {}
        GoalCardItem(
            Goal(
                name = "Шахта сокровищ",
                description = "Один богач закопал свои сокровища в этой шахте, но чтоб их получить, нужно решить задачку.",
                cost = 1200,
                goalType = GoalType.BY_PARENT,
                status = GoalState.AVAILABLE
            )
        ) {}
    }
}