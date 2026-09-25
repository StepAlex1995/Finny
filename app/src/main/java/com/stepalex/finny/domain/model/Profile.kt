package com.stepalex.finny.domain.model

import com.stepalex.finny.presentation.common.pets.PetType
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val currentGoal: Goal,
    val countMoney: Int,
    val countFood: Int,
    val countMood: Int,
    val isSleep: Boolean,
    val foodInventory: List<FoodInventory>,
    val itemInventory: List<ItemInventory>,
    val petStyle: PetStyle
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
enum class TypeFood {
    Carrot
}

@Serializable
enum class ItemPosition {
    Top, Left, Right
}

@Serializable
enum class TypeItem {
    BowTie, Crown, Glass, HairBow,
    NeckTie, TopHat
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