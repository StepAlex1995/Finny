package com.stepalex.finny.presentation.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuizScreen(onDismiss: () -> Unit) {
// Внешний контейнер на весь экран с полупрозрачным черным фоном
    // Полупрозрачный черный задний фон, блокирующий клики по HomeScreen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() }, // Закрыть при клике мимо карточки
        contentAlignment = Alignment.Center
    ) {
        // Белая карточка самого квиза
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f) // Занимает 90% ширины экрана
                .wrapContentHeight()
                .padding(16.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { /* Защита от закрытия при клике на саму карточку */ },
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Заголовок задачи (title)
                Text(
                    text = "Подозрительная ссылка", // task.title
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1C1B1F)
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 2. Описание ситуации (description)
                Text(
                    text = "Вам пришло SMS от незнакомца с предложением выиграть приз.", // task.description
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color(0xFF49454F),
                        lineHeight = 22.sp
                    ),
                    textAlign = TextAlign.Center
                )

                // 3. Подсказка (help), если она есть в объекте JSON
                // task.help?.let { helpText ->
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = Color(0xFFF3EDF7),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Подсказка: не переходите по незнакомым ссылкам.", // helpText
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF65558F)),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }
                // }

                Spacer(modifier = Modifier.height(28.dp))

                // 4. Варианты ответов (Перебор списка task.answers)
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Вариант 1 (Нежелательный)
                    AnswerButton(
                        text = "Перейти по ссылке", // answer.text
                        onClick = {
                            // Тут обрабатываешь последствия (gold, mood...) и закрываешь/меняешь экран
                            onDismiss()
                        }
                    )

                    // Вариант 2 (Предпочтительный)
                    AnswerButton(
                        text = "Заблокировать номер", // answer.text
                        onClick = {
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AnswerButton(
    text: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color(0xFF6750A4)
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            // Тонкая стильная рамка фиолетового оттенка Material3
            width = 1.dp
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(vertical = 6.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
fun QuizScreenPreview(){
    QuizScreen {  }
}