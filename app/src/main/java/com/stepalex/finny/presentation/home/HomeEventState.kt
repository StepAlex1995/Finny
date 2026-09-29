package com.stepalex.finny.presentation.home

import com.stepalex.finny.domain.model.Goal
import com.stepalex.finny.domain.model.ItemInventory
import com.stepalex.finny.domain.model.PeriodHistory
import com.stepalex.finny.domain.model.PetStyle
import com.stepalex.finny.domain.model.Profile
import com.stepalex.finny.domain.model.ScheduledEffect
import com.stepalex.finny.domain.model.Task
import com.stepalex.finny.domain.model.TaskAnswer

sealed class HomeEvent {
    //data class OpenQuiz(val taskId: Int) : HomeEvent()
    object ShowHomeWindow : HomeEvent()
    object DismissDialog : HomeEvent()
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

    //Показать очередную задачу
    data object ShowTask : HomeEvent()

    //Выбрать вариант ответа на событие
    data class SelectTaskAnswer(val taskAnswer: TaskAnswer) : HomeEvent()

    //Ответить по очередной задаче
    data class CompleteQuizTask(val taskAnswer: TaskAnswer) : HomeEvent()

    //Пропуск ожидания таймера для тестирования
    data object SkipTimer : HomeEvent()

    data class ShowPeriodHistory(val periodId: Int) : HomeEvent()  //показать результаты по периодам
    data object ShowPreviewsPeriodHistory : HomeEvent()  //показать результаты Предыдущему периоду
    data object ShowNextPeriodHistory : HomeEvent()  //показать результаты Следующему периоду


    data object ShowResetConfirmDialog : HomeEvent()  //Показать окно с предупреждением о сбросе
    data object ResetData : HomeEvent()  //Полный сброс всех данных для теста

    data object ShowCompleteGoalDialog : HomeEvent()  //Показать диалог о достижении цели
    data object CompleteGoal : HomeEvent()  //Обновляем информацию о достижении цели
}

data class HomeState(
    val openWindow: OpenWindow,
    val showDialog: ShowDialog,//пока можно удалить
    val profile: Profile?,
    val goals: List<Goal>,
    val selectGoal: Goal?,
    val periodState: PeriodState = PeriodState.Locked,

    // Отображение итогов прошлого периода:
    val appliedEffects: List<ScheduledEffect> = emptyList(),
    //val showEffectsDialog: Boolean = false,             //Показать результаты за прошедший период
    val currentTask: Task? = null,
    val selectedTaskAnswer: TaskAnswer?,
    val periodHistory: PeriodHistory? = null
)

enum class OpenWindow {
    None,   //Домашняя страница
    Goals,  //Просмотр и выбор цели
    CreatePet,  //Создание питомца
    ShowTask,   //Показать событие
    ShowResultTaskAnswer,    //Показать результат выбора ответа по событию
    ShowStartPeriodInfo,    //Показать результаты за прошедший период
    ShowPeriodResult,    //Показать результаты по текущему периоду
    ShowPeriodHistory,    //Показать результаты по периодам, то же окно что и ShowPeriodResult, ток можно переключаться между периодами
}

enum class ShowDialog {
    None, SelectGoal, SelectPet,ResetConfirm,CompleteGoal,StartInfo
}

// Состояние периода
sealed interface PeriodState {
    data object Locked : PeriodState // Нужно выполнить стартовую задачу
    data class InProgress(val completedCount: Int) : PeriodState // Доступны квизы (0..4)
    data class WaitingForNextPeriod(val remainingTime: String) : PeriodState // Показываем таймер
}