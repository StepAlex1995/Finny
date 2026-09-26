package com.stepalex.finny.presentation.pet_room

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stepalex.finny.domain.model.FoodInventory
import com.stepalex.finny.domain.model.ItemInventory
import com.stepalex.finny.domain.model.Profile
import com.stepalex.finny.domain.model.TypeFood
import com.stepalex.finny.domain.model.TypeItem
import com.stepalex.finny.presentation.common.food.Apple
import com.stepalex.finny.presentation.common.food.Cabbage
import com.stepalex.finny.presentation.common.items.BowTie
import com.stepalex.finny.presentation.common.items.Crown
import com.stepalex.finny.presentation.common.items.Glasses
import com.stepalex.finny.presentation.common.items.HairBow
import com.stepalex.finny.presentation.common.items.NeckTie
import com.stepalex.finny.presentation.common.items.PetItemPosition
import com.stepalex.finny.presentation.common.items.TopHat
import com.stepalex.finny.presentation.common.food.Carrot
import com.stepalex.finny.presentation.common.food.Cherry
import com.stepalex.finny.presentation.common.food.Grapes
import com.stepalex.finny.presentation.common.food.Pear
import com.stepalex.finny.presentation.home.PeriodState
import com.stepalex.finny.utils.Fonts


enum class PetRoomTab(val title: String) {
    Events("События"), Food("Еда"), Items("Предметы")
}


@Composable
fun BottomPetRoomPanel(
    profile: Profile,
    periodState: PeriodState,
    modifier: Modifier = Modifier,
    onFoodClick: (FoodInventory) -> Unit,
    onItemClick: (ItemInventory) -> Unit,
    onStartTaskClick: () -> Unit,
    onQuizClick: () -> Unit,
    onSkipTimer: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(PetRoomTab.Food) }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // 1. Ряд вкладок (Коричневые переключатели)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            PetRoomTab.values().forEach { tab ->
                val isSelected = selectedTab == tab

                // Кастомная вкладка со скруглением верхних углов (как на скрине)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 8.dp, start = 4.dp, end = 4.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                        .background(if (isSelected) Color(0xFF6E4E37) else Color(0xFFAC8A64))
                        .clickable { selectedTab = tab }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center) {
                    Text(
                        text = tab.title,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontFamily = Fonts.RegularTextFontFamily
                    )
                }
            }
        }

        // 2. Контейнер сетки (фиксированная высота под контент)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp) // Общая высота нижней секции контента
                .background(Color(0xFFFFFDF6))
                .padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            when (selectedTab) {
                PetRoomTab.Food -> {
                    // Один ряд из 4-х элементов для Еды
                    FixedTwoRowItemsGrid(
                        items = profile.foodInventory, itemContent = { foodItem ->
                            FoodCard(foodItem = foodItem, onClick = { onFoodClick(foodItem) })
                        })
                }

                PetRoomTab.Items -> {
                    // Два ряда по 3 элемента для Предметов (одежды)
                    FixedTwoRowItemsGrid(
                        items = profile.itemInventory, itemContent = { itemInventory ->
                            ItemCard(
                                itemInventory = itemInventory,
                                onClick = { onItemClick(itemInventory) })
                        })
                }

                PetRoomTab.Events -> {
                    Box(
                        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                    ) {
                        EventsPanel(
                            periodState = periodState,
                            onStartTaskClick = onStartTaskClick,
                            onQuizClick = onQuizClick,
                            onSkipTimer = onSkipTimer
                        )/*Text(
                            "Игрушек пока нет",
                            color = Color.Gray,
                            fontFamily = Fonts.RegularTextFontFamily
                        )*/
                    }
                }
            }
        }
    }

}

@Composable
fun EventsPanel(
    periodState: PeriodState,
    onStartTaskClick: () -> Unit,
    onQuizClick: () -> Unit,
    onSkipTimer: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        when (periodState) {
            is PeriodState.Locked -> {
                // Шаг 1: Доступна только обязательная задача (Работа)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Новый период начался!", style = MaterialTheme.typography.headlineSmall)
                    Text("Выполните вводное задание, чтобы открыть доступ к квизам.")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onStartTaskClick) {
                        Text("Выполнить стартовое задание")
                    }
                }
            }

            is PeriodState.InProgress -> {
                // Шаг 2: Доступны квизы (показываем счетчик 0..4 из 5)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Доступны задания периода", style = MaterialTheme.typography.headlineSmall)
                    Text(
                        "Выполнено задач: ${periodState.completedCount} / 5",
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = onQuizClick) {
                        Text("Пройти квиз")
                    }
                }
            }

            is PeriodState.WaitingForNextPeriod -> {
                // Шаг 3: Все 5 решено, тикает таймер
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Все задачи выполнены!", style = MaterialTheme.typography.headlineSmall)
                    Text("Следующий период откроется через:")
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = periodState.remainingTime,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    // КНОПКА МГНОВЕННОГО СБРОСА ТАЙМЕРА
                    Button(
                        onClick = onSkipTimer,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error // Сделаем её приметной
                        )
                    ) {
                        Text("Сбросить время (Пропустить)")
                    }
                }
            }
        }
    }
}


// РАЗМЕТКА 1: Один горизонтальный ряд для 4-х элементов еды
@Composable
fun <T> FixedOneRowFoodGrid(
    items: List<T>, itemContent: @Composable (T) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Жестко фиксируем 3 слота
        for (i in 0..2) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(0.8f)
            ) { // Немного сужаем по высоте для красоты
                if (i < items.size) {
                    itemContent(items[i])
                }
            }
        }
    }
}


// РАЗМЕТКА 2: Два ряда по 3 элемента для 6 предметов
@Composable
fun <T> FixedTwoRowItemsGrid(
    items: List<T>, itemContent: @Composable (T) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Первый ряд (индексы 0, 1, 2)
        Row(
            modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (i in 0..2) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    if (i < items.size) {
                        itemContent(items[i])
                    }
                }
            }
        }

        // Второй ряд (индексы 3, 4, 5)
        Row(
            modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (i in 3..5) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    if (i < items.size) {
                        itemContent(items[i])
                    }
                }
            }
        }
    }
}

@Composable
fun FoodCard(foodItem: FoodInventory, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFF5E4))
            .clickable { onClick() }
            .padding(4.dp),
        contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            when (foodItem.typeFood) {
                TypeFood.Carrot -> Carrot(modifier = Modifier.size(48.dp), isFilled = true)
                TypeFood.Grapes -> Grapes(modifier = Modifier.size(48.dp), isFilled = true)
                TypeFood.Cherry -> Cherry(modifier = Modifier.size(48.dp), isFilled = true)
                TypeFood.Apple -> Apple(modifier = Modifier.size(48.dp), isFilled = true)
                TypeFood.Cabbage -> Cabbage(modifier = Modifier.size(48.dp), isFilled = true)
                TypeFood.Pear -> Pear(modifier = Modifier.size(48.dp), isFilled = true)
            }
            Text(
                text = foodItem.typeFood.text,
                fontSize = 12.sp,
                fontFamily = Fonts.RegularTextFontFamily
            )
            Text(
                text = "x${foodItem.count}",
                fontSize = 14.sp,
                color = Color.DarkGray,
                fontFamily = Fonts.RegularTextFontFamily
            )
        }
    }
}

@Composable
fun ItemCard(itemInventory: ItemInventory, onClick: () -> Unit) {
    val isSelected = itemInventory.isUsing
    val isAvailable = itemInventory.isAvailable

    val backgroundColor = when {
        !isAvailable -> Color(0xFFE2E2E2)
        isSelected -> Color(0xFFE6D5C3)
        else -> Color(0xFFFFF5E4)
    }
    val contentAlpha = if (isAvailable) 1f else 0.5f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(enabled = itemInventory.isAvailable) { onClick() }
            .padding(4.dp),
        contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.graphicsLayer(alpha = contentAlpha)
        ) {
            when (itemInventory.typeItem) {
                TypeItem.TopHat -> TopHat(
                    modifier = Modifier.size(
                        width = 64.dp, height = 48.dp
                    )
                )

                TypeItem.Crown -> Crown(
                    modifier = Modifier.size(
                        width = 64.dp, height = 48.dp
                    )
                )

                TypeItem.HairBow -> HairBow(
                    modifier = Modifier.size(width = 48.dp, height = 48.dp),
                    rotationDegrees = PetItemPosition.TOP.degrees
                )

                TypeItem.Glass -> Glasses(
                    modifier = Modifier.size(
                        width = 64.dp, height = 48.dp
                    )
                )

                TypeItem.NeckTie -> NeckTie(
                    modifier = Modifier.size(
                        width = 64.dp, height = 48.dp
                    )
                )

                TypeItem.BowTie -> BowTie(
                    modifier = Modifier.size(
                        width = 64.dp, height = 48.dp
                    )
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = itemInventory.typeItem.text,
                    fontSize = 11.sp,
                    fontFamily = Fonts.RegularTextFontFamily
                )/*Text(
                text = itemInventory.itemColor.name,
                fontSize = 9.sp,
                color = Color.Gray,
                fontFamily = Fonts.RegularTextFontFamily
            )*/
                //Spacer(modifier = Modifier.height(8.dp))
                if (!itemInventory.isAvailable) {
                    Text(text = "🔒", fontSize = 10.sp, modifier = Modifier.padding(start = 4.dp))
                }
            }
        }
    }
}
