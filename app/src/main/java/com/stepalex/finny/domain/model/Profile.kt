package com.stepalex.finny.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val currentGoal: Goal,
    val countMoney: Int,
    val countFood: Int,
    val countMood: Int,
    val isSleep: Boolean,
    val foodInventory: List<FoodInventory>,
    val itemInventory: List<ItemInventory>
)

@Serializable
data class FoodInventory(
    val typeFood: TypeFood,
    val count: Int
)

@Serializable
data class ItemInventory(
    val typeItem: TypeItem,
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
