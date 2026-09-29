package com.stepalex.finny.presentation.common.pets

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.stepalex.finny.domain.model.PetColorType
import com.stepalex.finny.domain.model.PetColorType.Aqua
import com.stepalex.finny.domain.model.PetColorType.Chocolate
import com.stepalex.finny.domain.model.PetColorType.Indigo
import com.stepalex.finny.domain.model.PetColorType.Peach
import com.stepalex.finny.domain.model.PetColorType.Tangerine
import com.stepalex.finny.domain.model.PetColorType.TeddyBear
import com.stepalex.finny.domain.model.PetColorType.White

@Immutable
data class PetColor(
    val fillColor: Color,    // Цвет шерстки (тела)
    val outlineColor: Color,  // Цвет контура (обводки)
    val blushColor: Color,    // Цвет румяных щечек
    val bearEarsColor: Color  // Цвет ушей у медведя
) {
    companion object {
        // 1. Классический Белый кролик
        val White = PetColor(
            fillColor = Color.White,
            outlineColor = Color.Black,
            blushColor = Color(0xFFFEE1E1),
            bearEarsColor = Color(0xFFC6BCB4)
        )

        // 2. Милый Персиковый/Рыжий кролик (Кавайный пастельный скин)
        val Peach = /*PetColor(
            fillColor = Color(0xFFFFEAA7),   // Мягкий персиково-кремовый
            outlineColor = Color(0xFF6C4030), // Благородный шоколадно-коричневый контур
            blushColor = Color(0xFFFFB6C1),// Нежно-розовые щечки
            bearEarsColor = Color(0xFFC6BCB4)
        )*/PetColor(
            fillColor = Color(0xFFC3B1E1),     // Плотный, сочный лавандовый
            outlineColor = Color(0xFF2E1A47),   // Очень темный, глубокий черничный
            blushColor = Color(0xFFFFB7D5),     // Яркие неоново-розовые щечки
            bearEarsColor = Color(0xFFC6BCB4)   // Фирменные ушки
        )

        // 3. Шоколадный кролик (Пасхальный заяц)
        val Chocolate = PetColor(
            fillColor = Color(0xFFD7A17C),   // Вкусный молочный шоколад
            outlineColor = Color(0xFF4A2711), // Глубокий кофейный контур
            blushColor = Color(0xFFFFC0CB),   // Выразительные зефирные щечки
            bearEarsColor = Color(0xFFC6BCB4)
        )

        // 1. 🦩 Неоновый Фламинго (Кислотно-розовый)
        // Безумно яркий девчачий скин. Контур глубокого черничного цвета делает его очень стильным
        val Flamingo = PetColor(
            fillColor = Color(0xFFFF4081),
            outlineColor = Color(0xFF3F0015),
            blushColor = Color(0xFFFF80AB),
            bearEarsColor = Color(0xFFC6BCB4)
        )

        // 2. 🧬 Кислотный Лайм (Энергия неона)
        // Максимально заряженный, ядовито-салатовый скин. Идеален для брутальных очков
        val NeonLime = PetColor(
            fillColor = Color(0xFF00FF87),
            outlineColor = Color(0xFF003311),
            blushColor = Color(0xFF60FFA3),
            bearEarsColor = Color(0xFFC6BCB4)
        )

        // 3. 🧪 Кибер-Панк Фиолетовый (Ультрафиолет)
        // Глубокий, насыщенный неоновый фиолетовый скин, на котором золотая корона засияет как космос
        val CyberPurple = /*PetColor(
            fillColor = Color(0xFF7000FF),
            outlineColor = Color(0xFF1A004D),
            blushColor = Color(0xFFB380FF),
            bearEarsColor = Color(0xFFC6BCB4)
        )*/PetColor(
            fillColor = Color(0xFFFBC02D),     // Глубокий, насыщенный желтый
            outlineColor = Color(0xFF3E2723),   // Темно-коричневый контрастный контур
            blushColor = Color(0xFFFFE082),     // Мягкие желто-оранжевые щечки
            bearEarsColor = Color(0xFFC6BCB4)
        )

        // 4. 🍊 Сочный Мандарин (Яркий цитрус)
        // Взрывной оранжевый цвет шёрстки. Тёмно-терракотовый контур подчёркивает теплоту мультяшной формы
        val Tangerine = /*PetColor(
            fillColor = Color(0xFFFF6F00),
            outlineColor = Color(0xFF3E1300),
            blushColor = Color(0xFFFFAB40),
            bearEarsColor = Color(0xFFC6BCB4)
        )*/PetColor(
            fillColor = Color(0xFFFF8A65),     // Плотный и яркий морковно-оранжевый
            outlineColor = Color(0xFF4E1505),   // Экстра-темный терракотовый контур
            blushColor = Color(0xFFFFCCBC),     // Пастельно-оранжевые щечки
            bearEarsColor = Color(0xFFC6BCB4)
        )

        // 5. 🧊 Электрический Аквамарин (Ледяной нео)
        // Насыщенная светящаяся бирюза. Выглядит свежо, футуристично и контрастно на любом экране
        val Aqua = /*PetColor(
            fillColor = Color(0xFF00E5FF),
            outlineColor = Color(0xFF003C45),
            blushColor = Color(0xFF80F3FF),
            bearEarsColor = Color(0xFFC6BCB4)
        )*/PetColor(
            fillColor = Color(0xFFD48C98),     // Плотный благородный розовый
            outlineColor = Color(0xFF3A1F24),   // Глубокий бордово-черный контур
            blushColor = Color(0xFFF8BBD0),     // Зефирные щечки
            bearEarsColor = Color(0xFFC6BCB4)
        )

        // 6. 🍌 Банановый Шейк (Летнее солнце)
        // Насыщенный канарский жёлтый цвет. Скин выглядит празднично, а контур тёмного янтаря сохраняет чёткость глаз
        val Banana = PetColor(
            fillColor = Color(0xFFFFEA00),
            outlineColor = Color(0xFF514100),
            blushColor = Color(0xFFFFFA80),
            bearEarsColor = Color(0xFFC6BCB4)
        )

        // 7. 🔮 Космический Черничный (Индиго неон)
        // Насыщенный сине-фиолетовый электрик. Идеальный скин для создания «ночного» или магического питомца
        val Indigo = /*PetColor(
            fillColor = Color(0xFF2979FF),
            outlineColor = Color(0xFF002280),
            blushColor = Color(0xFF82B1FF),
            bearEarsColor = Color(0xFFC6BCB4)
        )*/ PetColor(
            fillColor = Color(0xFFE0A96D),     // Насыщенная карамель
            outlineColor = Color(0xFF4C2C16),   // Темно-кофейный контур
            blushColor = Color(0xFFF8BBD0),     // Выразительные розовые щечки
            bearEarsColor = Color(0xFFC6BCB4)
        )

        // 8. 🍓 Клубничная Конфета (Коралловый взрыв)
        // Сладкий, насыщенный клубнично-красный цвет шерсти. Смотрится невероятно аппетитно и сочно
        val CandyRed = PetColor(
            fillColor = Color(0xFFFF1744),
            outlineColor = Color(0xFF4D000C),
            blushColor = Color(0xFFFF80AB),
            bearEarsColor = Color(0xFFC6BCB4)
        )

        // 9. 🍏 Зелёное Яблоко (Свежая мята)
        // Насыщенный пастельно-зелёный цвет, очень приятный для глаз при долговременной игре
        val AppleGreen = PetColor(
            fillColor = Color(0xFF00E676),
            outlineColor = Color(0xFF004D26),
            blushColor = Color(0xFFB9F6CA),
            bearEarsColor = Color(0xFFC6BCB4)
        )

        // 10. 🪵 Плюшевый Мишка (Каштановый кавай)
        // Тёплый, выразительный тёмно-бежевый скин. Превращает кролика в дорогую коллекционную игрушку
        val TeddyBear = PetColor(
            fillColor = Color(0xFFFFAB91),
            outlineColor = Color(0xFF4E1D0F),
            blushColor = Color(0xFFFFCCBC),
            bearEarsColor = Color(0xFFC6BCB4)
        )

        val SilverGrey = PetColor(
            fillColor = Color(0xFFD2D7DF),
            outlineColor = Color(0xFF28313B),
            blushColor = Color(0xFFFFD4C4),
            bearEarsColor = Color(0xFFC6BCB4)
        )
        val allColors = listOf(
            White, Peach, Chocolate, /*Flamingo, NeonLime,*/
            CyberPurple, Tangerine, Aqua, /*Banana,*/ Indigo,
            /*CandyRed, AppleGreen,*/ TeddyBear/*, SilverGrey*/
        )
        val CompletedGoal = PetColor(
            fillColor = Color(0xFF7000FF),
            outlineColor = Color(0xFF1A004D),
            blushColor = Color(0xFFB380FF),
            bearEarsColor = Color(0xFFC6BCB4)
        )
    }
}

fun getPetColorScheme(petColorType: PetColorType): PetColor {
    return when (petColorType) {
        White -> PetColor.White//
        Peach -> PetColor.Peach
        Chocolate -> PetColor.Chocolate//
        // CuberPurple -> PetColor.CyberPurple
        Tangerine -> PetColor.Tangerine
        Aqua -> PetColor.Aqua
        Indigo -> PetColor.Indigo
        TeddyBear -> PetColor.TeddyBear//
    }
}
