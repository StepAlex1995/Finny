package com.stepalex.finny.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.stepalex.finny.presentation.common.GameButton
import com.stepalex.finny.presentation.goals.GameDialog
import com.stepalex.finny.presentation.common.TypeGlare
import com.stepalex.finny.presentation.goals.GameDialogAnimatable
import com.stepalex.finny.presentation.goals.GoalScreenAnimatable
import com.stepalex.finny.presentation.pet_room.PetRoom

@Composable
fun HomeScreen(event: ((HomeEvent) -> Unit), state: HomeState) {
    Box(modifier = Modifier.fillMaxSize()) {
        PetRoom(modifier = Modifier.fillMaxSize())

        GoalScreenAnimatable(event, state)
        GameDialogAnimatable(event, state)

    }

    /* var type by remember { mutableStateOf(PetType.BUNNY) }
     var state by remember { mutableStateOf(PetStage.Baby) }
     var mood by remember { mutableStateOf(PetMood.Normal) }
     var action by remember { mutableStateOf(PetAction.Play) }
     var touchOffset by remember { mutableStateOf<Offset?>(null) }

     var testCount by remember { mutableStateOf(1) }
     var goalMoney by remember { mutableFloatStateOf(1000f) }
     var currentMoney by remember { mutableFloatStateOf(300f) }
     var progress by remember { mutableStateOf(0.0f) }

     var petItems by remember {
         mutableStateOf(
             PetItems(
                 topHat = null,
                 neckTie = null,
                 hairBow = null,
                 glass = null,
                 crown = null,
                 bowTie = null
             )
         )
     }
     Box(
         modifier = Modifier
             .fillMaxSize()
             .background(Color.White)
             //.background(Color(0xFFF3F4F6))
             //.padding(all = 32.dp)
             .pointerInput(mood) { // Перезапускаем PointerInput при смене настроения
                 if (mood == PetMood.Sleep) {
                     // Если кролик спит, жесты полностью игнорируются
                     return@pointerInput
                 }
                 awaitEachGesture {
                     // 1. Фаза Press: ловим самое первое касание мгновенно
                     val down = awaitFirstDown(requireUnconsumed = false)
                     touchOffset = down.position

                     // 2. Фаза Move/Release: отслеживаем палец, пока он на экране
                     while (true) {
                         val event = awaitPointerEvent()

                         when (event.type) {
                             PointerEventType.Move -> {
                                 // Палец движется — обновляем координаты без задержек slop
                                 val pointer = event.changes.firstOrNull()
                                 if (pointer != null && pointer.pressed) {
                                     touchOffset = pointer.position
                                 }
                             }

                             PointerEventType.Release -> {
                                 // 3. Фаза Release: палец оторвали — мгновенный сброс
                                 touchOffset = null
                                 break // Выходим из цикла этого жеста
                             }
                         }
                     }
                 }
             }
     ) {
         PetRoom(
             modifier = Modifier
                 .fillMaxWidth()
                 .fillMaxHeight(1f)
         )

         Column(
             modifier = Modifier.fillMaxSize(),
             horizontalAlignment = Alignment.CenterHorizontally,
             //verticalArrangement = Arrangement.SpaceBetween
         ) {
             Column(
                 modifier = Modifier
                     .fillMaxWidth(0.55f)
                     .align(alignment = Alignment.CenterHorizontally)
                     .padding(top = 25.dp),
                 //.height(100.dp),
                 horizontalAlignment = Alignment.CenterHorizontally
             ) {
                 //Индикатор денег
                 Row(
                     verticalAlignment = Alignment.Bottom,
                     horizontalArrangement = Arrangement.Center,
                     modifier = Modifier
                         .fillMaxWidth()
                         .height(70.dp)
                     //.background(Color.Green)
                 ) {
                     Money(
                         modifier = Modifier
                             .width(40.dp)
                             .height(40.dp)
                     )//.padding(bottom = 10.dp))
                     CountMoneyOutlineText(
                         "123",
                         modifier = Modifier,
                         fontSize = 40.sp,
                         strokeSize = 10f,
                         alignment = Alignment.BottomStart,
                         fontFamily = Fonts.CountMoneyFontFamily
                     )
                 }

                 FinnyProgressBar(
                     0.4f,
                     trackStyle = FinnyProgressStyle(
                         color = Color.LightGray,
                         cornerRadius = 4.dp
                     ),
                     progressStyle = FinnyProgressStyle(
                         //color = Color(0xFFAF4FAF),
                         color = Color(0xFF7000FF),
                         //color = Color(0xFFB380FF),
                         cornerRadius = 4.dp
                     ),
                     segmentsCount = 3,
                     tickStyle = FinnyTickStyle(
                         inactiveColor = Color.LightGray,
                         //activeColor = Color(0xFFAF4FAF),
                         //activeColor = Color(0xFFB380FF),
                         activeColor = Color(0xFF1A004D),
                         extraHeight = 3.dp
                     ),
                     modifier = Modifier//.padding(16.dp)
                 )
                 Row(
                     modifier = Modifier
                         .fillMaxWidth()
                         .padding(top = 4.dp),
                     horizontalArrangement = Arrangement.SpaceBetween,
                     verticalAlignment = Alignment.CenterVertically
                 ) {
                     Silhouette(
                         modifier = Modifier.size(10.dp),
                         petType = PetType.BUNNY,
                         stage = PetStage.Baby,
                         petColor = PetColor.CyberPurple,
                     )
                     Silhouette(
                         modifier = Modifier.size(10.dp),
                         petType = PetType.BUNNY,
                         stage = PetStage.Teenager,
                         petColor = PetColor.CyberPurple,
                     )
                     Silhouette(
                         modifier = Modifier.size(10.dp),
                         petType = PetType.BUNNY,
                         stage = PetStage.Adult,
                         petColor = PetColor.SilverGrey,
                     )
                     Start(modifier = Modifier.size(10.dp))
                 }
                 Row(
                     modifier = Modifier
                         .fillMaxWidth()
                         .padding(bottom = 8.dp),
                     horizontalArrangement = Arrangement.SpaceBetween
                 ) {
                     CountMoneyOutlineText(
                         "Цель:",
                         fontFamily = Fonts.RegularTextFontFamily,
                         strokeSize = 5f
                     )
                     CountMoneyOutlineText(
                         "1000",
                         fontFamily = Fonts.RegularTextFontFamily,
                         strokeSize = 5f
                     )
                 }

                 //Индикатор еды
                 Row(
                     verticalAlignment = Alignment.CenterVertically,
                     modifier = Modifier
                         .fillMaxWidth()
                         .height(50.dp)
                         .padding(top = 4.dp)
                 ) {
                     for (i in 0..4) {
                         Food(
                             modifier = Modifier
                                 .weight(0.2f)
                                 .aspectRatio(1f), isFilled = i < 3
                         )
                     }
                 }
                 //Индикатор настроения
                 Row(
                     verticalAlignment = Alignment.Top,
                     modifier = Modifier
                         .fillMaxWidth(0.8f)
                         .height(50.dp)
                 ) {
                     val count = testCount
                     val moodIndicator =
                         if (count < 2) MoodIndicator.SAD else if (count < 4) MoodIndicator.NORMAL else MoodIndicator.HAPPY
                     for (i in 0..4) {
                         MoodSmile(
                             modifier = Modifier
                                 .weight(0.2f)
                                 .padding(4.dp)
                                 .aspectRatio(1f), isFilled = i < count, moodIndicator
                         )
                     }
                 }
             }
         }

         Pet(
             modifier = Modifier
                 .size(300.dp)
                 .align(alignment = Alignment.Center),
             //.padding(bottom = 100.dp),
             stage = state,
             mood = mood,
             action = action,
             touchOffset = touchOffset,
             petColor = PetColor.White,
             petItems = petItems,
             petType = type,
             petListener = object : PetListener {
                 override fun updatePetMod(newMode: PetMood) {
                     mood = newMode
                 }
             }
         )
        /* Box(
             modifier = Modifier
                 .fillMaxWidth()
                 .fillMaxHeight(0.8f)
         ) {
             Button(
                 modifier = Modifier
                     //.fillMaxWidth(0.8f)
                     .padding(bottom = 16.dp, start = 24.dp, end = 24.dp)
                     .align(alignment = Alignment.BottomCenter),
                 onClick = { /*event?.let { it(HomeEvent.OpenQuiz(0)) }*/
                     event?.let { it(HomeEvent.GetProfile) }
                 }) {
                 OutlineText(
                     "Название события",
                     modifier = Modifier.padding(horizontal = 24.dp)
                 )
             }
         }
         // ИНСТРУКЦИЯ ДЛЯ ТЕСТИРОВАНИЯ
         Column(modifier = Modifier.fillMaxWidth()) {
             Button(onClick = {
                 testCount = if ((testCount + 1) % 6 == 0) 1 else (testCount + 1) % 6
                 state = when (state) {
                     PetStage.Baby -> PetStage.Teenager
                     PetStage.Teenager -> PetStage.Adult
                     PetStage.Adult -> PetStage.Baby
                 }
             }) { Text("Current stage = $state") }
         }*/
         /*Row(modifier = Modifier.fillMaxWidth()) {
         Column(modifier = Modifier.weight(0.5f)) {
             Button(onClick = {
                 mood = PetMood.Sleep
             }) { Text("SLEEP") }
             Button(onClick = {
                 mood = PetMood.Normal
             }) { Text("NORMAL") }
             Button(onClick = {
                 mood = PetMood.Sad
             }) { Text("SAD") }
             Button(onClick = {
                 mood = PetMood.Happy
             }) { Text("HAPPY") }
         }
         Column(modifier = Modifier.weight(0.5f)) {
             Button(onClick = {
                 action = PetAction.Pet
             }) { Text("PET") }
             Button(onClick = {
                 action = PetAction.Eat
             }) { Text("EAT") }
             Button(onClick = {
                 action = PetAction.Play
             }) { Text("PLAY") }
             Button(onClick = {
                 type = if(type == PetType.BUNNY){
                     PetType.BEAR
                 }else {
                     PetType.BUNNY
                 }
             }) { Text("PLAY") }
         }
     }
 }

 Column(
     modifier = Modifier
         .fillMaxWidth()
         .align(Alignment.BottomCenter)
 ) {
     Button(onClick = {
         petItems = petItems.copy(
             topHat = null,
             hairBow = PetItemParam(
                 isSparkles = false,
                 position = PetItemPosition.TOP,
                 petItemColor = PetItemColor.Fuchsia
             )
         )
     }) { Text("Корона TOP Fuchsia") }
     Button(onClick = {
         petItems = petItems.copy(
             topHat = null,
             hairBow = PetItemParam(
                 isSparkles = true,
                 position = PetItemPosition.LEFT,
                 petItemColor = PetItemColor.Orange
             )
         )
     }) { Text("Корона LEFT Fuchsia") }
    /* Button(onClick = {
         petItems = petItems.copy(
             topHat = null,
             hairBow = PetItemParam(
                 isSparkles = true,
                 position = PetItemPosition.LEFT,
                 petItemColor = PetItemColor.Blue
             )
         )
     }) { Text("Корона LEFT Blue") }

     Button(onClick = {
         petItems = petItems.copy(
             crown = null,
             bowTie = PetItemParam(
                 isSparkles = true,
                 position = PetItemPosition.TOP,
                 petItemColor = PetItemColor.Emerald
             )
         )
     }) { Text("Корона TOP Fuchsia") }
     Button(onClick = {
         petItems = petItems.copy(
             crown = null,
             topHat = PetItemParam(
                 isSparkles = true,
                 position = PetItemPosition.LEFT,
                 petItemColor = PetItemColor.Fuchsia
             )
         )
     }) { Text("Корона LEFT Fuchsia") }
     Button(onClick = {
         petItems = petItems.copy(
             crown = null,
             topHat = PetItemParam(
                 isSparkles = false,
                 position = PetItemPosition.LEFT,
                 petItemColor = PetItemColor.Blue
             )
         )
     }) { Text("Корона LEFT Blue") }
     Button(onClick = {
         petItems = petItems.copy(
             crown = null,
             glass = PetItemParam(
                 isSparkles = true,
                 position = PetItemPosition.RIGHT,
                 petItemColor = PetItemColor.Blue
             )
         )
     }) { Text("Корона LEFT Blue") }*/
     Button(onClick = {
         petItems = petItems.copy(
             crown = null,
             topHat = null,
             hairBow = null,
             bowTie = null,
             neckTie = null,
             glass = null
         )
     }) { Text("CLEAT") }
 }*/
     }*/

}

@Preview
@Composable
fun HomeScreenPreview() {
    Box(modifier = Modifier.fillMaxSize()) {

        HomeScreen(
            event = object : Function1<HomeEvent, Unit> {
                override fun invoke(p1: HomeEvent) {

                }
            },
            state = HomeState(
                openWindow = OpenWindow.None,
                showDialog = ShowDialog.None,
                profile = null,
                goals = emptyList(),
                selectGoal = null
            ),
        )
    }
}