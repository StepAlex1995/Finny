package com.stepalex.finny.presentation.pets

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class PetColor(
    val fillColor: Color,    // Цвет шерстки (тела)
    val outlineColor: Color,  // Цвет контура (обводки)
    val blushColor: Color    // Цвет румяных щечек
) {
    companion object {
        // 1. Классический Белый кролик
        val White = PetColor(
            fillColor = Color.White,
            outlineColor = Color.Black,
            blushColor = Color(0xFFFEE1E1)
        )

        // 2. Милый Персиковый/Рыжий кролик (Кавайный пастельный скин)
        val Peach = PetColor(
            fillColor = Color(0xFFFFEAA7),   // Мягкий персиково-кремовый
            outlineColor = Color(0xFF6C4030), // Благородный шоколадно-коричневый контур
            blushColor = Color(0xFFFFB6C1)   // Нежно-розовые щечки
        )

        // 3. Шоколадный кролик (Пасхальный заяц)
        val Chocolate = PetColor(
            fillColor = Color(0xFFD7A17C),   // Вкусный молочный шоколад
            outlineColor = Color(0xFF4A2711), // Глубокий кофейный контур
            blushColor = Color(0xFFFFC0CB)   // Выразительные зефирные щечки
        )

        // 1. 🦩 Неоновый Фламинго (Кислотно-розовый)
        // Безумно яркий девчачий скин. Контур глубокого черничного цвета делает его очень стильным
        val Flamingo = PetColor(
            fillColor = Color(0xFFFF4081),
            outlineColor = Color(0xFF3F0015),
            blushColor = Color(0xFFFF80AB)
        )

        // 2. 🧬 Кислотный Лайм (Энергия неона)
        // Максимально заряженный, ядовито-салатовый скин. Идеален для брутальных очков
        val NeonLime = PetColor(
            fillColor = Color(0xFF00FF87),
            outlineColor = Color(0xFF003311),
            blushColor = Color(0xFF60FFA3)
        )

        // 3. 🧪 Кибер-Панк Фиолетовый (Ультрафиолет)
        // Глубокий, насыщенный неоновый фиолетовый скин, на котором золотая корона засияет как космос
        val CyberPurple = PetColor(
            fillColor = Color(0xFF7000FF),
            outlineColor = Color(0xFF1A004D),
            blushColor = Color(0xFFB380FF)
        )

        // 4. 🍊 Сочный Мандарин (Яркий цитрус)
        // Взрывной оранжевый цвет шёрстки. Тёмно-терракотовый контур подчёркивает теплоту мультяшной формы
        val Tangerine = PetColor(
            fillColor = Color(0xFFFF6F00),
            outlineColor = Color(0xFF3E1300),
            blushColor = Color(0xFFFFAB40)
        )

        // 5. 🧊 Электрический Аквамарин (Ледяной нео)
        // Насыщенная светящаяся бирюза. Выглядит свежо, футуристично и контрастно на любом экране
        val Aqua = PetColor(
            fillColor = Color(0xFF00E5FF),
            outlineColor = Color(0xFF003C45),
            blushColor = Color(0xFF80F3FF)
        )

        // 6. 🍌 Банановый Шейк (Летнее солнце)
        // Насыщенный канарский жёлтый цвет. Скин выглядит празднично, а контур тёмного янтаря сохраняет чёткость глаз
        val Banana = PetColor(
            fillColor = Color(0xFFFFEA00),
            outlineColor = Color(0xFF514100),
            blushColor = Color(0xFFFFFA80)
        )

        // 7. 🔮 Космический Черничный (Индиго неон)
        // Насыщенный сине-фиолетовый электрик. Идеальный скин для создания «ночного» или магического питомца
        val Indigo = PetColor(
            fillColor = Color(0xFF2979FF),
            outlineColor = Color(0xFF002280),
            blushColor = Color(0xFF82B1FF)
        )

        // 8. 🍓 Клубничная Конфета (Коралловый взрыв)
        // Сладкий, насыщенный клубнично-красный цвет шерсти. Смотрится невероятно аппетитно и сочно
        val CandyRed = PetColor(
            fillColor = Color(0xFFFF1744),
            outlineColor = Color(0xFF4D000C),
            blushColor = Color(0xFFFF80AB)
        )

        // 9. 🍏 Зелёное Яблоко (Свежая мята)
        // Насыщенный пастельно-зелёный цвет, очень приятный для глаз при долговременной игре
        val AppleGreen = PetColor(
            fillColor = Color(0xFF00E676),
            outlineColor = Color(0xFF004D26),
            blushColor = Color(0xFFB9F6CA)
        )

        // 10. 🪵 Плюшевый Мишка (Каштановый кавай)
        // Тёплый, выразительный тёмно-бежевый скин. Превращает кролика в дорогую коллекционную игрушку
        val TeddyBear = PetColor(
            fillColor = Color(0xFFFFAB91),
            outlineColor = Color(0xFF4E1D0F),
            blushColor = Color(0xFFFFCCBC)
        )

        val SilverGrey = PetColor(
            fillColor = Color(0xFFD2D7DF),
            outlineColor = Color(0xFF28313B),
            blushColor = Color(0xFFFFD4C4)     
        )
    }
}