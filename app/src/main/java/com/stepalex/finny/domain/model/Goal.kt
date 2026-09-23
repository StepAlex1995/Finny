package com.stepalex.finny.domain.model


data class Goal(
    val name: String,
    val description: String?,
    val cost: Int,
    val byParent: Boolean
)