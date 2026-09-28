package com.stepalex.finny.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "period_history")
data class PeriodHistoryEntity(
    @PrimaryKey val periodIndex: Int, // Номер завершённого периода
    val workIncome: Int = 100,         // Сколько монет выдано за работу

    // Траты/доходы в текущем периоде по типам:
    val spentOnNecessity: Int = 0,    // Траты на нужды (всегда >= 0)
    val spentOnOptional: Int = 0,     // Траты на хотелки (всегда >= 0)
    val spentOnAccumulation: Int = 0, // Сколько отложил/инвестировал (всегда >= 0)
    val lostToScam: Int = 0,          // Сколько украли мошенники (всегда >= 0)

    // Результаты отложенных эффектов, которые ПРИШЛИ в этот период из прошлого:
    val pendingGoldEffect: Int = 0,   // Прибыль по вкладам (+) или списание долгов (-)

    // Состояние жизненных показателей на конец раунда:
    val finalFood: Int,
    val finalMood: Int,
    val rankAwarded: String,           // Звание ("MASTER", "SPENDER" и т.д.)

    // Храним облегчённый JSON-массив из id задач и выбранных ответов
    val choicesJson: String
)