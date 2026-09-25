package com.stepalex.finny.presentation.common.items

import androidx.compose.ui.graphics.Color
import com.stepalex.finny.domain.model.ItemColor
import com.stepalex.finny.domain.model.ItemInventory
import com.stepalex.finny.domain.model.ItemPosition
import com.stepalex.finny.domain.model.TypeItem

data class PetItems(
    val topHat: PetItemParam?,
    val neckTie: PetItemParam?,
    val hairBow: PetItemParam?,
    val glass: PetItemParam?,
    val crown: PetItemParam?,
    val bowTie: PetItemParam?,
)

data class PetItemParam(
    val isSparkles: Boolean,
    val position: PetItemPosition?,
    val petItemColor: PetItemColor?
)

enum class PetItemPosition(val text: String, val degrees: Float) {
    LEFT("Слева", -19f), TOP("Центр", 0f),
    RIGHT("Справа", 19f)
}

enum class PetItemColor(val base: Color, val shadow: Color) {
    Black(Color(0xFF000000), Color(0xFF990000)),       // Красная
    Red(Color(0xFFFF0000), Color(0xFF990000)),       // Красная
    Blue(Color(0xFF4DCEF6), Color(0xFF0099CC)),      // Мятно-голубая
    Gold(Color(0xFFFFD400), Color(0xFFD4A000)),      // Жёлто-золотая
    Purple(Color(0xFFB566FF), Color(0xFF7A24D4)),     // Фиолетовая

    // --- ДОПОЛНИТЕЛЬНЫЕ ПОДОБРАННЫЕ ЦВЕТА ПОД КОНКРЕТНЫЕ ПРЕДМЕТЫ ---
    //  Кислотный Лайм
    // Очень яркий, светящийся цвет, супер-контрастный для очков и бабочек
    Lime(Color(0xFFADFF2F), Color(0xFF66B300)),

    // Дерзкая Фуксия (Яркий девчачий)
    // Глубокий розово-пурпурный цвет, сочнее обычного розового, идеален для Бантика принцессы
    Fuchsia(Color(0xFFFF007F), Color(0xFFB30047)),

    // Неоновый Оранжевый (Вкусная хурма)
    // Перекликается с морковкой из режима Eat, смотрится празднично и тепло
    Orange(Color(0xFFFF6B00), Color(0xFFB33600)),

    // Насыщенный Бирюзовый (Морская волна)
    // Более глубокий и плотный, чем мятный. Безумно стильно смотрится в оправе очков
    Teal(Color(0xFF00F5D4), Color(0xFF00B395)),

    // Жемчужно-Белый (Снежный люкс)
    // Для создания белых шелковых аксессуаров, которые выделяются за счет мягких серых теней
    Pearl(Color(0xFFF9F9FB), Color(0xFFB0B3C4)),

    // Изумрудный Неон (Ядовитый зеленый)
    // Цвет дорогого мультяшного кристалла, круто красит драгоценные камни на Короне
    Emerald(Color(0xFF00E676), Color(0xFF00994D)),

    // Электрический Синий (Глубокий космос)
    // Насыщенный ультрамарин, очень благородный джентльменский цвет для Галстука или Цилиндра
    ElectricBlue(Color(0xFF2979FF), Color(0xFF0045A6)),

    // Спелый Арбуз (Коралловый неон)
    // Нечто среднее между розовым и красным. Невероятно сочный летний оттенок
    Watermelon(Color(0xFFFF5252), Color(0xFFC62828)),

    // Горячий Шоколад (Винтажный стиль)
    // Придаёт Цилиндру или Галстуку приятный ретро-стиль дорогой кожи или вельвета
    Chocolate(Color(0xFF8D6E63), Color(0xFF4E342E)),

    // Клубничный Зефир (Нежная пастель)
    // Мягкий, йогуртово-розовый оттенок для милых и кавайных комбинаций бантиков
    Marshmallow(Color(0xFFFFC0CB), Color(0xFFD87093))
}


fun getPetItems(items: List<ItemInventory>): PetItems {
    return PetItems(
        topHat = mapItem(TypeItem.TopHat, items),
        neckTie = mapItem(TypeItem.NeckTie, items),
        hairBow = mapItem(TypeItem.HairBow, items),
        glass = mapItem(TypeItem.Glass, items),
        crown = mapItem(TypeItem.Crown, items),
        bowTie = mapItem(TypeItem.BowTie, items)
    )
}

private fun mapItem(type: TypeItem, items: List<ItemInventory>): PetItemParam? {
    val inventoryItem = items.find { it.typeItem == type && it.isUsing } ?: return null

    // Маппим позицию из инвентаря в позицию для питомца
    val petPosition = when (inventoryItem.position) {
        ItemPosition.Left -> PetItemPosition.LEFT
        ItemPosition.Top -> PetItemPosition.TOP
        ItemPosition.Right -> PetItemPosition.RIGHT
    }

    return PetItemParam(
        isSparkles = true, // Задайте true/false на основе вашей логики (например, если предмет эпический)
        position = petPosition, petItemColor = convertPetItemColor(inventoryItem.itemColor)
    )
}

private fun convertPetItemColor(itemColor: ItemColor): PetItemColor {
    return when (itemColor) {
        ItemColor.Black -> PetItemColor.Black
        ItemColor.Red -> PetItemColor.Red
        ItemColor.Blue -> PetItemColor.Blue
        ItemColor.Gold -> PetItemColor.Gold
        ItemColor.Purple -> PetItemColor.Purple
        ItemColor.Lime -> PetItemColor.Lime
        ItemColor.Fuchsia -> PetItemColor.Fuchsia
        ItemColor.Orange -> PetItemColor.Orange
        ItemColor.Teal -> PetItemColor.Teal
        ItemColor.Pearl -> PetItemColor.Pearl
        ItemColor.Emerald -> PetItemColor.Emerald
        ItemColor.ElectricBlue -> PetItemColor.ElectricBlue
        ItemColor.Watermelon -> PetItemColor.Watermelon
        ItemColor.Chocolate -> PetItemColor.Chocolate
        ItemColor.Marshmallow -> PetItemColor.Marshmallow
    }
}

fun convertItemColor(itemColor: PetItemColor): ItemColor {
    return when (itemColor) {
        PetItemColor.Black -> ItemColor.Black
        PetItemColor.Red -> ItemColor.Red
        PetItemColor.Blue -> ItemColor.Blue
        PetItemColor.Gold -> ItemColor.Gold
        PetItemColor.Purple -> ItemColor.Purple
        PetItemColor.Lime -> ItemColor.Lime
        PetItemColor.Fuchsia -> ItemColor.Fuchsia
        PetItemColor.Orange -> ItemColor.Orange
        PetItemColor.Teal -> ItemColor.Teal
        PetItemColor.Pearl -> ItemColor.Pearl
        PetItemColor.Emerald -> ItemColor.Emerald
        PetItemColor.ElectricBlue -> ItemColor.ElectricBlue
        PetItemColor.Watermelon -> ItemColor.Watermelon
        PetItemColor.Chocolate -> ItemColor.Chocolate
        PetItemColor.Marshmallow -> ItemColor.Marshmallow
    }
}