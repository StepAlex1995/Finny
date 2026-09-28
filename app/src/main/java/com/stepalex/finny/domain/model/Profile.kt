package com.stepalex.finny.domain.model

import com.stepalex.finny.presentation.common.pets.PetType
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val currentPeriodIndex: Int = 1,
    val currentGoal: Goal,
    val countMoney: Int,
    val countFood: Int,
    val countMood: Int,
    val isSleep: Boolean,
    val foodInventory: List<FoodInventory>,
    val itemInventory: List<ItemInventory>,
    val petStyle: PetStyle,
    // Поля для сохранения состояния раунда:
    val currentPeriodTaskIds: List<Long> = emptyList(), // ID 5 задач, сгенерированных на этот период
    val currentPeriodChoices: List<HistoryChoice> = emptyList(), // Сюда по очереди пишем выбранные ответы: {taskId, answerText}
    val moneyEffectByPreviewsPeriod:Int = 0  //эффект изменение денег за предыдущий период
)

@Serializable
data class PetStyle(
    val petType: PetType,
    val petColor: PetColorType,
)

@Serializable
enum class PetColorType {
    White, Peach, Chocolate, CuberPurple, Tangerine, Aqua, Indigo, TeddyBear
}

@Serializable
data class FoodInventory(
    val typeFood: TypeFood,
    val count: Int
)

@Serializable
data class ItemInventory(
    val typeItem: TypeItem,
    val itemColor: ItemColor,
    val position: ItemPosition,
    val isUsing: Boolean,
    val isAvailable: Boolean
)

@Serializable
enum class TypeFood(val text: String) {
    Carrot("Морковка"),
    Grapes("Виноград"),
    Cherry("Вишня"),
    Apple("Яблоко"),
    Cabbage("Капуста"),
    Pear("Груша"),
}

@Serializable
enum class ItemPosition {
    Top, Left, Right
}

@Serializable
enum class TypeItem(val text: String) {
    BowTie("Бабочка"), Crown("Короона"), Glass("Очки"), HairBow("Бант"),
    NeckTie("Галстук"), TopHat("Шляпка")
}

enum class ItemColor {
    Black,
    Red,
    Blue,
    Gold,
    Purple,
    Lime,
    Fuchsia,
    Orange,
    Teal,
    Pearl,
    Emerald,
    ElectricBlue,
    Watermelon,
    Chocolate,
    Marshmallow
}