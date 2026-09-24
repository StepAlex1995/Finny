package com.stepalex.finny.domain.model

import kotlinx.serialization.Serializable


@Serializable
data class Goal(
    val name: String,
    val description: String?,
    val cost: Int,
    val goalType: GoalType,
    val status: GoalState
)

@Serializable
enum class GoalType {
    MINE,           //шахта
    WONT_AND_NEED,  //хочу и надо
    TERMS,          //термины
    BY_PARENT,      //создана родителем
}

@Serializable
enum class GoalState {
    NOT_AVAILABLE,
    AVAILABLE,
    ON_CHECKING,
    IN_PROCESSING,
    ARCHIVE
}
