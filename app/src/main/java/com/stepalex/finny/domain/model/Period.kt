package com.stepalex.finny.domain.model

import kotlinx.serialization.Serializable


// Облегченная модель выбора, которую мы сохраняем/читаем
@Serializable
data class HistoryChoice(
    val taskId: Long,
    val answerText: String
)

// Развернутый слепок для UI (Задача + Текст выбранного ответа)
data class HistoryTaskSnapshot(
    val task: Task,               // Оригинальная задача со всей структурой
    val chosenAnswerText: String  // Текст ответа, который выбрал ребёнок
)

// Сырая модель истории периода (для внутреннего маппинга)
data class RawPeriodHistory(
    val periodIndex: Int,
    val workIncome: Int,
    val spentOnNecessity: Int,
    val spentOnOptional: Int,
    val spentOnAccumulation: Int,
    val lostToScam: Int,
    val pendingGoldEffect: Int,
    val finalFood: Int,
    val finalMood: Int,
    val rankAwarded: FinancialRank,
    val choices: List<HistoryChoice>
)

// Развернутая модель истории периода для UI
data class PeriodHistory(
    val periodIndex: Int,       // Номер завершённого периода
    val workIncome: Int,        // Сколько монет выдано за работу

    // Траты/доходы в текущем периоде по типам:
    val spentOnNecessity: Int,   // Траты на нужды
    val spentOnOptional: Int,    // Траты на хотелки
    val spentOnAccumulation: Int,// Сколько отложил/инвестировал
    val lostToScam: Int,         // Сколько украли мошенники

    // Результаты отложенных эффектов, которые пришли из прошлого:
    val pendingGoldEffect: Int,  // Прибыль по вкладам (+) или списание долгов (-)

    // Состояние жизненных показателей на конец раунда:
    val finalFood: Int,
    val finalMood: Int,
    val rankAwarded: FinancialRank, // Сильное типизированное звание периода

    // Список развёрнутых слепков задач с полным объектом Task внутри
    val chosenTasks: List<HistoryTaskSnapshot>
)