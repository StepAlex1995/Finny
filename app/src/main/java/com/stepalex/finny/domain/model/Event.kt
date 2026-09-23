package com.stepalex.finny.domain.model

data class Event(
    val type: EvetType,
    val task: Task? = null,
    val workCashierSettings: WorkCashierSettings?,

)

data class WorkCashierSettings(
    val count: Int = 10,
)

enum class EvetType {
    Task,
    WorkCashier,
    EndEvent
}