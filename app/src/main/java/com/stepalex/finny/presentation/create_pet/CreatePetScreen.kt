package com.stepalex.finny.presentation.create_pet

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stepalex.finny.domain.model.ItemColor
import com.stepalex.finny.domain.model.ItemInventory
import com.stepalex.finny.domain.model.ItemPosition
import com.stepalex.finny.domain.model.PetColorType
import com.stepalex.finny.domain.model.PetColorType.*
import com.stepalex.finny.domain.model.PetStyle
import com.stepalex.finny.domain.model.TypeItem
import com.stepalex.finny.presentation.common.OutlineText
import com.stepalex.finny.presentation.home.HomeEvent
import com.stepalex.finny.presentation.home.HomeState
import com.stepalex.finny.presentation.home.OpenWindow
import com.stepalex.finny.presentation.home.ShowDialog
import com.stepalex.finny.presentation.common.items.BowTie
import com.stepalex.finny.presentation.common.items.Crown
import com.stepalex.finny.presentation.common.items.Glasses
import com.stepalex.finny.presentation.common.items.HairBow
import com.stepalex.finny.presentation.common.items.NeckTie
import com.stepalex.finny.presentation.common.items.PetItemColor
import com.stepalex.finny.presentation.common.items.PetItemParam
import com.stepalex.finny.presentation.common.items.PetItemPosition
import com.stepalex.finny.presentation.common.items.PetItems
import com.stepalex.finny.presentation.common.items.TopHat
import com.stepalex.finny.presentation.common.items.convertItemColor
import com.stepalex.finny.presentation.common.items.getPetItems
import com.stepalex.finny.presentation.common.pets.Pet
import com.stepalex.finny.presentation.common.pets.PetAction
import com.stepalex.finny.presentation.common.pets.PetColor
import com.stepalex.finny.presentation.common.pets.PetListener
import com.stepalex.finny.presentation.common.pets.PetMood
import com.stepalex.finny.presentation.common.pets.PetStage
import com.stepalex.finny.presentation.common.pets.PetType
import com.stepalex.finny.presentation.common.pets.getPetColorScheme


@Composable
fun CreatePetScreenAnimatable(event: ((HomeEvent) -> Unit), state: HomeState) {
    AnimatedVisibility(
        visible = state.openWindow == OpenWindow.CreatePet,
        // Анимация ПОЯВЛЕНИЯ: соединяем плавное увеличение (scaleIn) и появление (fadeIn)
        enter = scaleIn(
            animationSpec = tween(durationMillis = 300), initialScale = 0.8f
        ) + fadeIn(animationSpec = tween(durationMillis = 300)),
        // Анимация ИСЧЕЗНОВЕНИЯ: уменьшение (scaleOut) и затухание (fadeOut)
        exit = scaleOut(
            animationSpec = tween(durationMillis = 250), targetScale = 0.8f
        ) + fadeOut(animationSpec = tween(durationMillis = 250))
    ) {
        CreatePetScreen(
            modifier = Modifier.fillMaxSize(),
            event = event,
            state = state,
            onBack = { event(HomeEvent.ShowHomeWindow) },
            createPet = {})
    }
}

private fun convertItemPosition(petItemPosition: PetItemParam?): ItemPosition {
    return when (petItemPosition?.position) {
        PetItemPosition.LEFT -> ItemPosition.Left
        PetItemPosition.TOP -> ItemPosition.Top
        PetItemPosition.RIGHT -> ItemPosition.Right
        null -> ItemPosition.Top
    }
}

private fun getItemInventory(petItems: PetItems): List<ItemInventory> {
    return listOf(
        ItemInventory(
            typeItem = TypeItem.TopHat,
            position = convertItemPosition(petItems.topHat),
            isUsing = petItems.topHat != null,
            isAvailable = petItems.topHat != null,
            itemColor = if (petItems.topHat?.petItemColor != null) {
                convertItemColor(petItems.topHat.petItemColor)
            } else ItemColor.Black
        ),
        ItemInventory(
            typeItem = TypeItem.Crown,
            position = convertItemPosition(petItems.crown),
            isUsing = petItems.crown != null,
            isAvailable = petItems.crown != null,
            itemColor = if (petItems.crown?.petItemColor != null) {
                convertItemColor(petItems.crown.petItemColor)
            } else ItemColor.Gold
        ),
        ItemInventory(
            typeItem = TypeItem.HairBow,
            position = convertItemPosition(petItems.hairBow),
            isUsing = petItems.hairBow != null,
            isAvailable = petItems.hairBow != null,
            itemColor = if (petItems.hairBow?.petItemColor != null) {
                convertItemColor(petItems.hairBow.petItemColor)
            } else ItemColor.Blue
        ),
        ItemInventory(
            typeItem = TypeItem.Glass,
            position = convertItemPosition(petItems.glass),
            isUsing = petItems.glass != null,
            isAvailable = petItems.glass != null,
            itemColor = if (petItems.glass?.petItemColor != null) {
                convertItemColor(petItems.glass.petItemColor)
            } else ItemColor.Purple
        ),
        ItemInventory(
            typeItem = TypeItem.NeckTie,
            position = convertItemPosition(petItems.neckTie),
            isUsing = petItems.neckTie != null,
            isAvailable = petItems.neckTie != null,
            itemColor = if (petItems.neckTie?.petItemColor != null) {
                convertItemColor(petItems.neckTie.petItemColor)
            } else ItemColor.Blue
        ),
        ItemInventory(
            typeItem = TypeItem.BowTie,
            position = convertItemPosition(petItems.bowTie),
            isUsing = petItems.bowTie != null,
            isAvailable = petItems.bowTie != null,
            itemColor = if (petItems.bowTie?.petItemColor != null) {
                convertItemColor(petItems.bowTie.petItemColor)
            } else ItemColor.Red
        ),
    )
}

@Composable
fun CreatePetScreen(
    modifier: Modifier,
    event: (HomeEvent) -> Unit,
    state: HomeState,
    onBack: () -> Unit,
    createPet: () -> Unit
) {
    val CreatePetBg = Color(0xFFFFF6E5)
    val CreatePetCardBg = Color(0xFFFFFFFF)
    val CreatePetAccent = Color(0xFFFFD54F)

    // Состояние экрана (выбранный питомец, аксессуар и его позиция)
    val selectedItem = getPetItems(state.profile!!.itemInventory)

    var accessoryPosition by remember { mutableStateOf(PetItemPosition.TOP) }
    var activeCategory by remember { mutableStateOf<ItemCategory?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CreatePetBg)
            .padding(top = 16.dp)
    ) {
        // --- ШАПКА ЭКРАНА ---
        OutlineText(
            text = "Создай питомца",
            fontSize = 24.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )
        // --- ПРЕВЬЮ ПИТОМЦА ПО ЦЕНТРУ ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(), contentAlignment = Alignment.Center
        ) {
            Pet(
                modifier = Modifier
                    .size(300.dp)
                    .align(alignment = Alignment.Center),
                stage = PetStage.Adult,
                mood = PetMood.Normal,
                action = PetAction.Play,
                touchOffset = null,
                petColor = getPetColorScheme(state.profile!!.petStyle.petColor),
                petItems = selectedItem,
                petType = state.profile.petStyle.petType,
                petListener = object : PetListener {
                    override fun updatePetMod(newMode: PetMood) {

                    }
                })
        }
        // --- ВЫБОР ПИТОМЦА (ТАБЫ ПЕРЕКЛЮЧЕНИЯ) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(3.dp, Color.Black, RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            PetType.entries.forEach { pet ->
                val isSelected = state.profile!!.petStyle.petType == pet
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(if (isSelected) CreatePetAccent else Color.Transparent)
                        .clickable {
                            event(
                                HomeEvent.UpdatePetStyle(
                                    PetStyle(
                                        petType = pet, petColor = state.profile.petStyle.petColor
                                    )
                                )
                            )
                        }
                        .padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (pet == PetType.BUNNY) "Кроля" else "Мишка",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                }
            }
        }

        // --- НИЖНЯЯ ПАНЕЛЬ НАСТРОЕК ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(4.dp, Color.Black, RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(Color.White)
                .padding(20.dp)
        ) {
            // ВЫБОР ЦВЕТА ПИТОМЦА
            Text(text = "Цвет шёрстки:", fontSize = 14.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                // Перебираем все созданные вами PetColor
                items(entries) { colorConfig ->
                    val isSelected = state.profile!!.petStyle.petColor == colorConfig
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            // Если цвет выбран, делаем внешнюю черную рамку
                            .border(
                                3.dp,
                                if (isSelected) Color.Black else Color.Transparent,
                                CircleShape
                            )
                            .padding(3.dp)
                            .clip(CircleShape)
                            .border(2.dp, getPetColorScheme(colorConfig).outlineColor, CircleShape)
                            .background(getPetColorScheme(colorConfig).fillColor)
                            .clickable {
                                event(
                                    HomeEvent.UpdatePetStyle(
                                        PetStyle(
                                            petType = state.profile.petStyle.petType,
                                            petColor = colorConfig
                                        )
                                    )
                                )
                            })
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // ВЫБОР КАТЕГОРИИ ВЕЩЕЙ
            Text(text = "Выбери один предмет:", fontSize = 14.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(ItemCategory.values()) { category ->
                    val isCurrent = activeCategory == category
                    val hasItemOn = when (category) {
                        ItemCategory.NONE -> false
                        ItemCategory.TOP_HAT -> selectedItem.topHat != null
                        ItemCategory.CROWN -> selectedItem.crown != null
                        ItemCategory.HAIR_BOW -> selectedItem.hairBow != null
                        ItemCategory.GLASS -> selectedItem.glass != null
                        ItemCategory.NECK_TIE -> selectedItem.neckTie != null
                        ItemCategory.BOW_TIE -> selectedItem.bowTie != null
                    }

                    Box(
                        modifier = Modifier
                            .width(75.dp)
                            .border(
                                3.dp,
                                if (isCurrent) Color.Black else Color(0xFFE2E2E2),
                                RoundedCornerShape(16.dp)
                            )
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isCurrent) CreatePetAccent.copy(alpha = 0.3f) else if (hasItemOn) CreatePetAccent.copy(
                                    alpha = 0.1f
                                ) else Color(0xFFFAFAFA)
                            )
                            .clickable {
                                activeCategory = category
                                accessoryPosition = PetItemPosition.TOP
                                event(
                                    HomeEvent.UpdatePetItems(
                                        petItems = getItemInventory(
                                            updateItemInState(
                                                category,
                                                PetItemParam(true, accessoryPosition, null)
                                            )
                                        )
                                    )
                                )
                            }
                            .padding(8.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            when (category) {
                                ItemCategory.NONE -> Box(modifier = Modifier.size(64.dp, 37.dp))
                                ItemCategory.TOP_HAT -> TopHat(
                                    modifier = Modifier.size(
                                        width = 64.dp, height = 48.dp
                                    )
                                )

                                ItemCategory.CROWN -> Crown(
                                    modifier = Modifier.size(
                                        width = 64.dp, height = 48.dp
                                    )
                                )

                                ItemCategory.HAIR_BOW -> HairBow(
                                    modifier = Modifier.size(width = 64.dp, height = 48.dp),
                                    rotationDegrees = PetItemPosition.TOP.degrees
                                )

                                ItemCategory.GLASS -> Glasses(
                                    modifier = Modifier.size(
                                        width = 64.dp, height = 48.dp
                                    )
                                )

                                ItemCategory.NECK_TIE -> NeckTie(
                                    modifier = Modifier.size(
                                        width = 64.dp, height = 48.dp
                                    )
                                )

                                ItemCategory.BOW_TIE -> BowTie(
                                    modifier = Modifier.size(
                                        width = 64.dp, height = 48.dp
                                    )
                                )
                            }
                            Text(
                                text = category.displayName,
                                textAlign = TextAlign.Center,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            // ПОДМЕНЮ НАСТРОЙКИ ВЫБРАННОГО ПРЕДМЕТА
            if (activeCategory != null && activeCategory != ItemCategory.NONE) {
                activeCategory?.let { category ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color(0xFFE2E2E2), thickness = 2.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Извлекаем или создаем базовый параметр для модификации
                    val currentParam = when (category) {
                        ItemCategory.NONE -> null
                        ItemCategory.TOP_HAT -> selectedItem.topHat
                        ItemCategory.CROWN -> selectedItem.crown
                        ItemCategory.HAIR_BOW -> selectedItem.hairBow
                        ItemCategory.GLASS -> selectedItem.glass
                        ItemCategory.NECK_TIE -> selectedItem.neckTie
                        ItemCategory.BOW_TIE -> selectedItem.bowTie
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Настройка: ${category.displayName}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    val supportsPosition =
                        category == ItemCategory.TOP_HAT || category == ItemCategory.CROWN || category == ItemCategory.HAIR_BOW
                    if (supportsPosition) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            PetItemPosition.values().forEach { pos ->
                                val isPosSelected =
                                    currentParam?.position == pos || (currentParam == null && pos == PetItemPosition.TOP)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(2.dp, Color.Black, RoundedCornerShape(12.dp))
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (isPosSelected && currentParam != null) CreatePetAccent else Color(
                                                0xFFF5F5F5
                                            )
                                        )
                                        .clickable {
                                            val newParam =
                                                currentParam?.copy(position = pos) ?: PetItemParam(
                                                    isSparkles = false,
                                                    position = pos,
                                                    petItemColor = PetItemColor.Gold
                                                )
                                            event(
                                                HomeEvent.UpdatePetItems(
                                                    petItems = getItemInventory(
                                                        updateItemInState(category, newParam)
                                                    )
                                                )
                                            )

                                        }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center) {
                                    Text(
                                        text = pos.text,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(PetItemColor.values()) { itemColor ->
                            val isColorSelected = currentParam?.petItemColor == itemColor
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .border(
                                        2.dp,
                                        if (isColorSelected) Color.Black else Color.Transparent,
                                        CircleShape
                                    )
                                    .padding(2.dp)
                                    .clip(CircleShape)
                                    .background(itemColor.base)
                                    .clickable {
                                        val newParam = currentParam?.copy(petItemColor = itemColor)
                                            ?: PetItemParam(
                                                isSparkles = false,
                                                position = PetItemPosition.TOP,
                                                petItemColor = itemColor
                                            )
                                        // Передаем в функцию только текущую категорию и новый параметр
                                        val testSelectedItem = updateItemInState(category, newParam)
                                        event(
                                            HomeEvent.UpdatePetItems(
                                                petItems = getItemInventory(
                                                    updateItemInState(category, newParam)
                                                )
                                            )
                                        )
                                    })
                        }
                    }

                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    event(HomeEvent.SelectPet)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .border(3.dp, Color.Black, RoundedCornerShape(16.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = CreatePetAccent),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Выбрать этого питомца!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            }
        }
    }
}




fun updateItemInState(category: ItemCategory, param: PetItemParam?): PetItems {
    // Если мы снимаем предмет (param == null), возвращаем полностью пустой PetItems
    if (param == null) {
        return PetItems(
            topHat = null, neckTie = null, hairBow = null, glass = null, crown = null, bowTie = null
        )
    }

    // Если надеваем новый предмет, то ВСЕ остальные поля пишем как null,
    // а активным делаем только выбранный
    return when (category) {
        ItemCategory.NONE -> PetItems(
            topHat = null, neckTie = null, hairBow = null, glass = null, crown = null, bowTie = null
        )

        ItemCategory.TOP_HAT -> PetItems(
            topHat = param,
            neckTie = null,
            hairBow = null,
            glass = null,
            crown = null,
            bowTie = null
        )

        ItemCategory.CROWN -> PetItems(
            topHat = null,
            neckTie = null,
            hairBow = null,
            glass = null,
            crown = param,
            bowTie = null
        )

        ItemCategory.HAIR_BOW -> PetItems(
            topHat = null,
            neckTie = null,
            hairBow = param,
            glass = null,
            crown = null,
            bowTie = null
        )

        ItemCategory.GLASS -> PetItems(
            topHat = null,
            neckTie = null,
            hairBow = null,
            glass = param,
            crown = null,
            bowTie = null
        )

        ItemCategory.NECK_TIE -> PetItems(
            topHat = null,
            neckTie = param,
            hairBow = null,
            glass = null,
            crown = null,
            bowTie = null
        )

        ItemCategory.BOW_TIE -> PetItems(
            topHat = null,
            neckTie = null,
            hairBow = null,
            glass = null,
            crown = null,
            bowTie = param
        )
    }
}

// Вспомогательный enum для категорий вещей, чтобы удобно строить UI
enum class ItemCategory(val displayName: String) {
    NONE("Без предмета"), TOP_HAT("Шляпа"), CROWN("Корона"), HAIR_BOW("Бантик"), GLASS("Очки"), NECK_TIE(
        "Галстук"
    ),
    BOW_TIE("Бабочка")
}


@Preview
@Composable
fun CreatePetScreenPreview() {
    CreatePetScreen(
        modifier = Modifier.fillMaxSize(), onBack = {}, event = {}, state = HomeState(
            openWindow = OpenWindow.None,
            showDialog = ShowDialog.None,
            profile = null,
            goals = emptyList(),
            selectGoal = null,
            selectedTaskAnswer = null,
            periodHistory = null
        )
    ) { }
}