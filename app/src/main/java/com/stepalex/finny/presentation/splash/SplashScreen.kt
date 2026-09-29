package com.stepalex.finny.presentation.splash


import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stepalex.finny.R
import com.stepalex.finny.presentation.common.pets.Pet
import com.stepalex.finny.presentation.common.pets.PetListener
import com.stepalex.finny.presentation.common.pets.PetMood
import com.stepalex.finny.presentation.common.pets.PetStage
import com.stepalex.finny.presentation.common.pets.PetType
import com.stepalex.finny.utils.Fonts.RegularTextFontFamily
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SplashScreen(
    onSyncComplete: () -> Unit,
    viewModel: SplashViewModel
) {
    // Слушаем событие завершения синхронизации
    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collectLatest {
            onSyncComplete()
        }
    }

    // 1. Создаем бесконечную анимацию
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_transition")

    // 2. Анимируем коэффициент масштаба от 1.0f до 1.2f и обратно
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse // Возврат в исходное состояние (уменьшение)
        ),
        label = "scale_animation"
    )

    // 3. Центрируем контейнер на весь экран
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column() {
            Pet(
                modifier = Modifier
                    .size(400.dp)
                    .padding(bottom = 100.dp),
                stage = PetStage.Baby,
                mood = PetMood.Happy,
                touchOffset = null,
                petType = PetType.BUNNY,
                petItems = null,
                petListener = object : PetListener {
                    override fun updatePetMod(newMode: PetMood) {

                    }
                })

            Text(
                text = "Загрузка...",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black, // Очень жирный шрифт для игр
                color = Color.DarkGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontFamily = RegularTextFontFamily
            )
        }
    }
}


@Preview
@Composable
fun GameDialogPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column() {
                Pet(
                    modifier = Modifier
                        .size(400.dp)
                        .padding(bottom = 100.dp),
                    stage = PetStage.Baby,
                    mood = PetMood.Happy,
                    touchOffset = null,
                    petType = PetType.BUNNY,
                    petItems = null,
                    petListener = object : PetListener {
                        override fun updatePetMod(newMode: PetMood) {

                        }
                    })

                Text(
                    text = "Загрузка...",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black, // Очень жирный шрифт для игр
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    fontFamily = RegularTextFontFamily
                )
            }
        }
    }

}