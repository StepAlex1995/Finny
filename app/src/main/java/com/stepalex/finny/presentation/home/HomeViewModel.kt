package com.stepalex.finny.presentation.home

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
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
import com.stepalex.finny.domain.use_cases.period.GetCurrentTaskUseCase
import com.stepalex.finny.domain.use_cases.period.GetHistoryByPeriodIdUseCase
import com.stepalex.finny.domain.use_cases.period.StartNewPeriodUseCase
import com.stepalex.finny.domain.use_cases.period.SubmitTaskAnswerUseCase
import com.stepalex.finny.domain.use_cases.profile.CompleteGoalsUseCase
import com.stepalex.finny.domain.use_cases.profile.GetGoalsUseCase
import com.stepalex.finny.domain.use_cases.profile.GetProfileUseCase
import com.stepalex.finny.domain.use_cases.profile.UpdateProfileUseCase
import com.stepalex.finny.nvgraph.HomeUIEvent
import com.stepalex.finny.presentation.common.pets.PetType
import com.stepalex.finny.presentation.home.PeriodState.*
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
    private val application: Application,
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val getGoalsUseCase: GetGoalsUseCase,
    private val getPeriodStartTimeUseCase: GetPeriodStartTimeUseCase,
    private val savePeriodStartTimeUseCase: SavePeriodStartTimeUseCase,
    private val getCompletedTasksCountUseCase: GetCompletedTasksCountUseCase,
    private val saveCompletedTaskCountUseCase: SaveCompletedTaskCountUseCase,
    private val saveStartTaskCompletedUseCase: SaveStartTaskCompletedUseCase,
    private val checkStartTaskCompletedUseCase: CheckStartTaskCompletedUseCase,
    private val startNewPeriodUseCase: StartNewPeriodUseCase,
    private val submitTaskAnswerUseCase: SubmitTaskAnswerUseCase,
    private val getCurrentTaskUseCase: GetCurrentTaskUseCase,
    private val getHistoryByPeriodIdUseCase: GetHistoryByPeriodIdUseCase,
    private val completeGoalsUseCase: CompleteGoalsUseCase,
) : AndroidViewModel(application) {
    private val _uiEvent = Channel<HomeUIEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var timerJob: Job? = null

    var homeState by mutableStateOf(
        HomeState(
            openWindow = OpenWindow.None,
            showDialog = ShowDialog.None,
            profile = null,
            goals = emptyList(),
            selectGoal = null,
            selectedTaskAnswer = null,
            periodHistory = null
        )
    )

    init {
        viewModelScope.launch {
            val goals = getGoalsUseCase()
            val profile = getProfileUseCase()
            if (profile == null || profile.currentGoal == null) {
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
                homeState = homeState.copy(periodState = Locked)
            } else {
                homeState = homeState.copy(
                    periodState = InProgress(completedCount),
                    currentTask = getCurrentTaskUseCase().getOrNull()
                )
            }

        }
    }

    // Инициализация нового периода
    private suspend fun startNewPeriod() {//startTimeMs: Long) {
        savePeriodStartTimeUseCase(0L)//Обнуляем таймер //startTimeMs)
        saveCompletedTaskCountUseCase(0)
        saveStartTaskCompletedUseCase(false)
        homeState = homeState.copy(periodState = Locked)
        timerJob?.cancel()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {/*is HomeEvent.OpenQuiz -> {
                viewModelScope.launch {
                    _uiEvent.send(HomeUIEvent.OpenQuiz)
                }
            }*/

            is HomeEvent.GetProfile -> {
                viewModelScope.launch {
                    val profile = getProfileUseCase()
                    Log.i("TEST", "profile = $profile")
                    val goals = getGoalsUseCase()
                    Log.i("TEST", "goals = $goals")
                }
            }

            is HomeEvent.DismissDialog -> {
                homeState = homeState.copy(showDialog = ShowDialog.None)
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
                        countMoney = 0,
                        countFood = 4,
                        countMood = 4,
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
                        petStyle = PetStyle(PetType.BUNNY, petColor = PetColorType.White)
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
                    // Вызывается ПОСЛЕ КАЖДОГО квиза. Прогресс мгновенно сохраняется в базу!
                    submitTaskAnswerUseCase(
                        homeState.currentTask!!, event.taskAnswer
                    ).onSuccess { updatedProfile ->
                        homeState = homeState.copy(
                            profile = updatedProfile.first,
                            periodHistory = updatedProfile.second
                        )
                    }
                    val newCount = getCompletedTasksCountUseCase() + 1
                    saveCompletedTaskCountUseCase(newCount)

                    if (newCount >= TASK_PER_PERIOD) {
                        //Выполнили последнюю задачу, запускаем таймер
                        //val startTime = getPeriodStartTimeUseCase()
                        //startCountdownTimer(startTime)

                        // ПОЛЬЗОВАТЕЛЬ ЗАКОНЧИЛ ВСЁ! Засекаем время строго от ТЕКУЩЕГО момента
                        val finishTime = System.currentTimeMillis()
                        savePeriodStartTimeUseCase(finishTime) // Перезаписываем точку отсчета!
                        startCountdownTimer(finishTime)
                        //Закрывать окно
                        //homeState = homeState.copy(openWindow = OpenWindow.None)
                        //Показывать окно с результатами периода
                        homeState = homeState.copy(openWindow = OpenWindow.ShowPeriodResult)
                    } else {
                        homeState = homeState.copy(
                            periodState = InProgress(newCount),
                            currentTask = getCurrentTaskUseCase().getOrNull(),
                            openWindow = OpenWindow.None
                        )
                        //homeState = homeState.copy(periodState = InProgress(newCount))
                    }
                }
            }

            is HomeEvent.CompleteStartTask -> {//Выполнил стартовую работу
                if(!homeState.profile!!.showStartInfo){//Показать стартовую информацию
                    val updatedProfile  = homeState.profile!!.copy(showStartInfo = true)
                    viewModelScope.launch {
                        updateProfileUseCase(updatedProfile)
                    }
                    homeState = homeState.copy(profile =  updatedProfile, showDialog = ShowDialog.StartInfo)
                }else {
                    viewModelScope.launch {
                        //запоминание в преференс что начался период
                        saveStartTaskCompletedUseCase(true)
                        homeState = homeState.copy(periodState = InProgress(completedCount = 0))

                        // Запускает период, применяет эффекты и генерирует задачи ОДНОВРЕМЕННО
                        startNewPeriodUseCase().onSuccess { periodInfo ->  //(profile, effects) ->
                            homeState = homeState.copy(
                                profile = periodInfo.profile,
                                appliedEffects = periodInfo.scheduledEffects,
                                openWindow = OpenWindow.ShowStartPeriodInfo,
                                //showEffectsDialog = periodInfo.scheduledEffects.isNotEmpty(),
                                currentTask = periodInfo.currentTask
                            )
                            Log.i(
                                "TEST",
                                "CURRENT TASKS: ${periodInfo.profile.currentPeriodTaskIds}"
                            )
                            Log.i(
                                "TEST",
                                "CURRENT TASK number: ${periodInfo.profile.currentPeriodChoices.size}"
                            )
                            Log.i("TEST", "CURRENT TASK current : ${periodInfo.currentTask}")
                        }
                    }
                }
            }

            is HomeEvent.SkipTimer -> {
                viewModelScope.launch {
                    timerJob?.cancel()
                    startNewPeriod()
                }
            }

            HomeEvent.ShowTask -> {
                homeState = homeState.copy(openWindow = OpenWindow.ShowTask)
            }

            is HomeEvent.SelectTaskAnswer -> {//выбрал ответ на задание
                homeState = homeState.copy(
                    openWindow = OpenWindow.ShowResultTaskAnswer,
                    selectedTaskAnswer = event.taskAnswer
                )
            }

            is HomeEvent.ShowPeriodHistory -> {//Просмотреть историю по периодам
                viewModelScope.launch {
                    val periodHistory = getHistoryByPeriodIdUseCase(event.periodId).getOrNull()
                    homeState = homeState.copy(
                        openWindow = OpenWindow.ShowPeriodHistory,
                        periodHistory = periodHistory
                    )
                }
            }

            is HomeEvent.ShowPreviewsPeriodHistory -> {//Просмотреть историю предыдущего
                viewModelScope.launch {
                    val periodHistory =
                        getHistoryByPeriodIdUseCase(homeState.periodHistory!!.periodIndex - 1).getOrNull()
                    homeState = homeState.copy(
                        openWindow = OpenWindow.ShowPeriodHistory,
                        periodHistory = periodHistory
                    )
                }
            }

            is HomeEvent.ShowNextPeriodHistory -> {//Просмотреть историю следующего периода
                viewModelScope.launch {
                    val periodHistory =
                        getHistoryByPeriodIdUseCase(homeState.periodHistory!!.periodIndex + 1).getOrNull()
                    homeState = homeState.copy(
                        openWindow = OpenWindow.ShowPeriodHistory,
                        periodHistory = periodHistory
                    )
                }
            }

            is HomeEvent.ShowResetConfirmDialog -> {    //Показать окно с предупреждением о сбросе
                homeState = homeState.copy(showDialog = ShowDialog.ResetConfirm)
            }

            is HomeEvent.ResetData -> { // Сброс данных для теста
                viewModelScope.launch {
                    val activityManager =
                        application.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                    activityManager.clearApplicationUserData()
                }
            }

            is HomeEvent.ShowCompleteGoalDialog -> { // Показать диалог достижения цели
                homeState = homeState.copy(showDialog = ShowDialog.CompleteGoal)
            }

            is HomeEvent.CompleteGoal -> { // достигнуть цели
                viewModelScope.launch {
                    val updatedProfile = completeGoalsUseCase().getOrNull()
                    val goals = getGoalsUseCase()

                    homeState = homeState.copy(showDialog = ShowDialog.None, openWindow = OpenWindow.Goals, goals = goals)
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
                        homeState.copy(periodState = WaitingForNextPeriod(timeString))
                }
                delay(1000.milliseconds)
            }
        }
    }

    companion object {
        const val PERIOD_DURATION_MS =
            24 * 60 * 60 * 1000L // 4 часа в мс todo времененное решение, потом читать с конфигов

        const val TASK_PER_PERIOD =
            5// задач за период todo тоже сделать в дальнейшем чтение из конфигов
    }
}
