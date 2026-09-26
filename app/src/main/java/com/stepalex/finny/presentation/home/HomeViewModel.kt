package com.stepalex.finny.presentation.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stepalex.finny.domain.model.FoodInventory
import com.stepalex.finny.domain.model.GoalState
import com.stepalex.finny.domain.model.ItemColor
import com.stepalex.finny.domain.model.ItemInventory
import com.stepalex.finny.domain.model.ItemPosition
import com.stepalex.finny.domain.model.PetColorType
import com.stepalex.finny.domain.model.PetStyle
import com.stepalex.finny.domain.model.Profile
import com.stepalex.finny.domain.model.TypeFood
import com.stepalex.finny.domain.model.TypeItem
import com.stepalex.finny.domain.use_cases.CheckStartTaskCompletedUseCase
import com.stepalex.finny.domain.use_cases.GetCompletedTasksCountUseCase
import com.stepalex.finny.domain.use_cases.GetPeriodStartTimeUseCase
import com.stepalex.finny.domain.use_cases.SaveCompletedTaskCountUseCase
import com.stepalex.finny.domain.use_cases.SavePeriodStartTimeUseCase
import com.stepalex.finny.domain.use_cases.SaveStartTaskCompletedUseCase
import com.stepalex.finny.domain.use_cases.profile.GetGoalsUseCase
import com.stepalex.finny.domain.use_cases.profile.GetProfileUseCase
import com.stepalex.finny.domain.use_cases.profile.UpdateProfileUseCase
import com.stepalex.finny.nvgraph.HomeUIEvent
import com.stepalex.finny.presentation.common.pets.PetType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val getGoalsUseCase: GetGoalsUseCase,
    private val getPeriodStartTimeUseCase: GetPeriodStartTimeUseCase,
    private val savePeriodStartTimeUseCase: SavePeriodStartTimeUseCase,
    private val getCompletedTasksCountUseCase: GetCompletedTasksCountUseCase,
    private val saveCompletedTaskCountUseCase: SaveCompletedTaskCountUseCase,
    private val saveStartTaskCompletedUseCase: SaveStartTaskCompletedUseCase,
    private val checkStartTaskCompletedUseCase: CheckStartTaskCompletedUseCase,

    ) : ViewModel() {
    private val _uiEvent = Channel<HomeUIEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var timerJob: Job? = null

    var homeState by mutableStateOf(
        HomeState(
            openWindow = OpenWindow.None,
            showDialog = ShowDialog.None,
            profile = null,
            goals = emptyList(),
            selectGoal = null
        )
    )

    init {
        viewModelScope.launch {
            val goals = getGoalsUseCase()
            val profile = getProfileUseCase()
            if (profile == null) {
                homeState = homeState.copy(openWindow = OpenWindow.Goals, goals = goals)
            } else {
                homeState = homeState.copy(goals = goals, profile = profile)
                updatePeriodStatus()
            }
        }
    }

    //Проверка статуса при старте приложения
    fun updatePeriodStatus() {
        viewModelScope.launch {
            val startTime = getPeriodStartTimeUseCase()
            val now = System.currentTimeMillis()
            val completedCount = getCompletedTasksCountUseCase()

            // Проверяем: если юзер уже выполнил все 5 задач, смотрим на таймер отдыха
            if (completedCount >= 5) {
                if (startTime == 0L || (now - startTime) >= PERIOD_DURATION_MS) {
                    // 4 часа отдыха ПРОШЛИ -> Открываем СЛЕДУЮЩИЙ игровой период (все сбрасываем)
                    startNewPeriod()
                } else {
                    // Отдых еще идет -> Продолжаем крутить таймер
                    startCountdownTimer(startTime)
                }
                return@launch
            }
            // Если 5 задач еще НЕ выполнено, мы просто даем их решать без всяких ограничений по времени
            val isStartTaskDone = checkStartTaskCompletedUseCase()
            if (!isStartTaskDone) {
                homeState = homeState.copy(periodState = PeriodState.Locked)
            } else {
                homeState = homeState.copy(periodState = PeriodState.InProgress(completedCount))
            }
            /*
            // ЕСЛИ ПРИЛОЖЕНИЕ ЗАПУЩЕНО ВПЕРВЫЕ (БАЗА СВЕЖАЯ)
            if (startTime == 0L) {
                startNewPeriod(now)
                return@launch
            }
            // ЕСЛИ N ЧАСОВ УЖЕ ПРОШЛИ — сбрасываем всё под НОВЫЙ период
            if ((now - startTime) >= PERIOD_DURATION_MS) {
                startNewPeriod(now)
                return@launch
            }
            // МЫ ВНУТРИ ТЕКУЩЕГО ПЕРИОДА (проверяем сохраненный прогресс)
            val isStartTaskDone = checkStartTaskCompletedUseCase()
            //val completedCount = getCompletedTasksCountUseCase()

            when {
                !isStartTaskDone -> {
                    homeState = homeState.copy(periodState = PeriodState.Locked)
                }

                completedCount < TASK_PER_PERIOD -> {
                    homeState =
                        homeState.copy(periodState = PeriodState.InProgress(completedCount))
                }

                else -> {
                    //Всё задачи выполнены - запускаем таймер обратного отсчета
                    startCountdownTimer(startTime)
                }
            }*/
            /*//Если прошло больше N часов с начала старта периода - сбрасываем все на новый цикл
            if (startTime == 0L || (now - startTime) >= PERIOD_DURATION_MS) {
                savePeriodStartTimeUseCase(now)
                saveCompletedTaskCountUseCase(0)
                saveStartTaskCompletedUseCase(false)
                homeState = homeState.copy(periodState = PeriodState.Locked)
                timerJob?.cancel()
            } else {
                //Сейчас текущий период
                val isStartTaskDone = checkStartTaskCompletedUseCase()
                val completedCount = getCompletedTasksCountUseCase()

                when {
                    !isStartTaskDone -> {
                        homeState = homeState.copy(periodState = PeriodState.Locked)
                    }

                    completedCount < TASK_PER_PERIOD -> {
                        homeState =
                            homeState.copy(periodState = PeriodState.InProgress(completedCount))
                    }

                    else -> {
                        //Всё задачи выполнены - запускаем таймер обратного отсчета
                        startCountdownTimer(startTime)
                    }
                }
            }*/
        }
    }

    // Инициализация нового периода
    private suspend fun startNewPeriod() {//startTimeMs: Long) {
        savePeriodStartTimeUseCase(0L)//Обнуляем таймер //startTimeMs)
        saveCompletedTaskCountUseCase(0)
        saveStartTaskCompletedUseCase(false)
        homeState = homeState.copy(periodState = PeriodState.Locked)
        timerJob?.cancel()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.OpenQuiz -> {
                viewModelScope.launch {
                    _uiEvent.send(HomeUIEvent.OpenQuiz)
                }
            }

            is HomeEvent.GetProfile -> {
                viewModelScope.launch {
                    val profile = getProfileUseCase()
                    Log.i("TEST", "profile = $profile")
                    val goals = getGoalsUseCase()
                    Log.i("TEST", "goals = $goals")
                }
            }

            is HomeEvent.ShowHomeWindow -> {
                homeState = homeState.copy(openWindow = OpenWindow.None)
            }

            is HomeEvent.OnGoalClick -> {
                if (event.goal.status == GoalState.NOT_AVAILABLE) {
                    homeState =
                        homeState.copy(selectGoal = event.goal, showDialog = ShowDialog.SelectGoal)
                    //homeState = homeState.copy(showDialog = ShowDialog.SelectGoal)
                } else {
                    //todo как быть если цель не недоступна и/или родительская цель
                }

            }

            HomeEvent.ClearSelectGoal -> {//отмена выбора цели через диалог
                homeState = homeState.copy(showDialog = ShowDialog.None)
                //homeState = homeState.copy(selectGoal = null)
            }

            is HomeEvent.SelectGoal -> {//Выбор цели через диалог
                homeState = homeState.copy(
                    openWindow = OpenWindow.CreatePet,
                    showDialog = ShowDialog.None,
                    profile = Profile(
                        currentGoal = event.goal,
                        countMoney = 100,
                        countFood = 2,
                        countMood = 3,
                        isSleep = false,
                        foodInventory = listOf(
                            FoodInventory(
                                typeFood = TypeFood.Carrot, count = 1
                            ), FoodInventory(
                                typeFood = TypeFood.Grapes, count = 0
                            ), FoodInventory(
                                typeFood = TypeFood.Cherry, count = 0
                            ), FoodInventory(
                                typeFood = TypeFood.Apple, count = 0
                            ), FoodInventory(
                                typeFood = TypeFood.Cabbage, count = 0
                            ), FoodInventory(
                                typeFood = TypeFood.Pear, count = 0
                            )
                        ),
                        itemInventory = listOf(
                            ItemInventory(
                                typeItem = TypeItem.TopHat,
                                position = ItemPosition.Top,
                                isUsing = false,
                                isAvailable = false,
                                itemColor = ItemColor.Black
                            ),
                            ItemInventory(
                                typeItem = TypeItem.Crown,
                                position = ItemPosition.Top,
                                isUsing = false,
                                isAvailable = false,
                                itemColor = ItemColor.Gold
                            ),
                            ItemInventory(
                                typeItem = TypeItem.HairBow,
                                position = ItemPosition.Top,
                                isUsing = false,
                                isAvailable = false,
                                itemColor = ItemColor.Blue
                            ),
                            ItemInventory(
                                typeItem = TypeItem.Glass,
                                position = ItemPosition.Top,
                                isUsing = false,
                                isAvailable = false,
                                itemColor = ItemColor.Purple
                            ),
                            ItemInventory(
                                typeItem = TypeItem.NeckTie,
                                position = ItemPosition.Top,
                                isUsing = false,
                                isAvailable = false,
                                itemColor = ItemColor.Blue
                            ),
                            ItemInventory(
                                typeItem = TypeItem.BowTie,
                                position = ItemPosition.Top,
                                isUsing = false,
                                isAvailable = false,
                                itemColor = ItemColor.Red
                            ),
                        ),
                        petStyle = PetStyle(PetType.BEAR, petColor = PetColorType.TeddyBear)
                    )
                )
            }

            is HomeEvent.UpdatePetStyle -> {//обновить стиль питомца
                homeState.profile?.let { profile ->
                    val updatedProfile = profile.copy(petStyle = event.petStyle)
                    homeState = homeState.copy(profile = updatedProfile)
                }
            }

            is HomeEvent.UpdatePetItems -> {//обновить кол-во предметов (выбор, что надето)
                homeState.profile?.let { profile ->
                    viewModelScope.launch {
                        val updatedProfile = profile.copy(itemInventory = event.petItems)
                        updateProfileUseCase(updatedProfile)
                        homeState = homeState.copy(profile = updatedProfile)
                    }
                }
            }

            HomeEvent.SelectPet -> {
                homeState = homeState.copy(showDialog = ShowDialog.SelectPet)
            }

            HomeEvent.HideDialogSelectPet -> {
                homeState = homeState.copy(showDialog = ShowDialog.None)
            }

            HomeEvent.SaveProfile -> {
                if (homeState.profile != null) {
                    viewModelScope.launch {
                        updateProfileUseCase(homeState.profile!!)
                        homeState = homeState.copy(
                            openWindow = OpenWindow.None, showDialog = ShowDialog.None
                        )
                    }
                }
            }

            is HomeEvent.UpdateProfile -> {
                homeState = homeState.copy(profile = event.profile)
            }

            is HomeEvent.CompleteQuizTask -> {
                viewModelScope.launch {
                    val newCount = getCompletedTasksCountUseCase() + 1
                    saveCompletedTaskCountUseCase(newCount)
                    if (newCount >= TASK_PER_PERIOD) {
                        //Выполнили последнюю задачу, запускаем таймер
                        //val startTime = getPeriodStartTimeUseCase()
                        //startCountdownTimer(startTime)

                        // ПОЛЬЗОВАТЕЛЬ ЗАКОНЧИЛ ВСЁ! Засекаем 4 часа отдыха строго от ТЕКУЩЕГО момента
                        val finishTime = System.currentTimeMillis()
                        savePeriodStartTimeUseCase(finishTime) // Перезаписываем точку отсчета!
                        startCountdownTimer(finishTime)

                    } else {
                        homeState = homeState.copy(periodState = PeriodState.InProgress(newCount))
                    }
                }
            }

            is HomeEvent.CompleteStartTask -> {
                viewModelScope.launch {
                    saveStartTaskCompletedUseCase(true)
                    homeState =
                        homeState.copy(periodState = PeriodState.InProgress(completedCount = 0))
                }
            }

            is HomeEvent.SkipTimer -> {
                viewModelScope.launch {
                    timerJob?.cancel()
                    startNewPeriod()
                }
            }
        }
    }

    private fun startCountdownTimer(startTime: Long) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                val now = System.currentTimeMillis()
                val timePassed = now - startTime
                val timeLeft = PERIOD_DURATION_MS - timePassed

                if (timeLeft <= 0) {
                    //время вышло, триггерим новый период
                    //updatePeriodStatus()
                    // ВРЕМЯ ВЫШЛО!
                    // Не перезаписываем startTime автоматически.
                    // Просто принудительно сбрасываем состояние в Locked для нового цикла
                    startNewPeriod()//System.currentTimeMillis())
                    break
                } else {
                    // Форматируем миллисекунды в строку "ЧЧ:ММ:СС"
                    val hours = TimeUnit.MILLISECONDS.toHours(timeLeft)
                    val minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeft) % 60
                    val seconds = TimeUnit.MILLISECONDS.toSeconds(timeLeft) % 60
                    val timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds)

                    homeState =
                        homeState.copy(periodState = PeriodState.WaitingForNextPeriod(timeString))
                }
                delay(1000.milliseconds)
            }
        }
    }

    companion object {
        const val PERIOD_DURATION_MS =
            /*4 * 60 * */20 * 1000L // 4 часа в мс todo времененное решение, потом читать с конфигов

        const val TASK_PER_PERIOD =
            5// задач за период todo тоже сделать в дальнейшем чтение из конфигов
    }
}
