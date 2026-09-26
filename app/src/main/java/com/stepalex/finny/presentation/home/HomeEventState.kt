package com.stepalex.finny.presentation.home

import com.stepalex.finny.domain.model.Goal
import com.stepalex.finny.domain.model.ItemInventory
import com.stepalex.finny.domain.model.PetStyle
import com.stepalex.finny.domain.model.Profile

sealed class HomeEvent {
    data class OpenQuiz(val taskId: Int) : HomeEvent()
    object ShowHomeWindow : HomeEvent()
    data class OnGoalClick(val goal: Goal) : HomeEvent()
    data class SelectGoal(val goal: Goal) : HomeEvent()
    object ClearSelectGoal : HomeEvent()
    object GetProfile : HomeEvent()
    data class UpdatePetStyle(val petStyle: PetStyle) : HomeEvent()
    data object SelectPet : HomeEvent()
    data object HideDialogSelectPet : HomeEvent()
    data object SaveProfile : HomeEvent()
    data class UpdatePetItems(val petItems: List<ItemInventory>) : HomeEvent()
    data class UpdateProfile(val profile: Profile) : HomeEvent()

    //выполнена стартовая задача(работа)
    data class CompleteStartTask(val todoPrams: Int) : HomeEvent()

    //Пройден очередной квиз
    data class CompleteQuizTask(val todoParams: Int) : HomeEvent()

    //Пропуск ожидания таймера для тестирования
    data object SkipTimer : HomeEvent()
}

data class HomeState(
    val openWindow: OpenWindow,
    val showDialog: ShowDialog,//пока можно удалить
    val profile: Profile?,
    val goals: List<Goal>,
    val selectGoal: Goal?,
    val periodState: PeriodState = PeriodState.Locked,
)

enum class OpenWindow {
    None,
    Goals,
    CreatePet
}

enum class ShowDialog {
    None,
    SelectGoal,
    SelectPet
}

// Состояние периода
sealed interface PeriodState {
    data object Locked : PeriodState // Нужно выполнить стартовую задачу
    data class InProgress(val completedCount: Int) : PeriodState // Доступны квизы (0..4)
    data class WaitingForNextPeriod(val remainingTime: String) : PeriodState // Показываем таймер
}