package com.stepalex.finny.domain.model

data class Task(
    val id: Long,
    val title: String,
    val description: String,
    val help: String?,
    val answers: List<TaskAnswer>,
    val byParent: Boolean,
    val type: TaskType,
    val complexity: Int,
    val frequency: Int
)

data class TaskAnswer(
    val text: String,
    val description: String,
    val taskResult: List<TaskResult>,   //результат на последующие периоды
    val isPrefer: Boolean               //предпочтительный вариант
)

data class TaskResult(
    val period: Int,//на какой период по счеты от текущего будет происходить результат, если 0 - значит сразу
    val gold: Int,  //Сумма за весь период - 100 монет
    val food: Int,  //пул - 5 штук
    val mood: Int   //пул - 5 штук
)

enum class TaskType {
    Offline,            // Офлайн задание от родителей
    Accumulation,       // Накопления
    Necessity,          // Необходимые траты
    Optional,           // Хотелки
    Scam                // Мошенники
}



enum class FinancialRank(val title: String, val description: String) {
    MASTER("Магистр Экономики", "Идеальный баланс! Ты отлично планируешь траты и думаешь о будущем."),
    SMART_BUYER("Разумный Покупатель", "Хороший результат! Ты обеспечил себя важным и почти не тратил деньги на ерунду."),
    SPENDER("Импульсивный Транжира", "Ой! Похоже, «хотелки» победили разум. Попробуй сначала покупать то, что необходимо."),
    SCAM_VICTIM("Урок Бдительности", "В этот раз мошенники перехитрили тебя. Ошибаться в игре — нормально, теперь ты будешь осторожнее!")
}


// Результат расчёта раунда
sealed class PeriodEvaluationResult {
    data class Success(val profile: Profile, val rank: FinancialRank) : PeriodEvaluationResult()
    object GameOverFoodZero : PeriodEvaluationResult()
    object GameOverMoodZero : PeriodEvaluationResult()
}