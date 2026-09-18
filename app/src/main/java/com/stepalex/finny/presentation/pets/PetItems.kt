package com.stepalex.finny.presentation.pets

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import com.stepalex.finny.presentation.items.PetItemPosition
import com.stepalex.finny.presentation.items.PetItems
import com.stepalex.finny.presentation.items.drawBowTie
import com.stepalex.finny.presentation.items.drawCrown
import com.stepalex.finny.presentation.items.drawGlasses
import com.stepalex.finny.presentation.items.drawHairBow
import com.stepalex.finny.presentation.items.drawNeckTie
import com.stepalex.finny.presentation.items.drawTopHat


fun DrawScope.drawPetItems(
    w: Float,
    h: Float,
    interactions: PetInteractionsOut,
    petItems: PetItems?,
    sparkleAnimationTime: Float,
    stageScale: Float
) {
    // --- ПРЕДМЕТ: Корона ---
    if (interactions.crownScale > 0f) {
        val item = petItems?.crown

        // Цвет берем из кэша, если реальный предмет стал null
        val finalColor = item?.petItemColor ?: interactions.cachedCrownColor
        val currentPos = item?.position ?: interactions.cachedCrownPos

        // Логика волнового переноса позиций
        val renderedPosition = if (interactions.crownScale < 0.15f) {
            currentPos
        } else {
            interactions.prevCrownPos ?: currentPos
        }

        val cX = when (renderedPosition) {
            PetItemPosition.TOP -> w * 0.50f
            PetItemPosition.RIGHT -> w * 0.66f
            else -> w * 0.34f
        }
        val cY = if (renderedPosition == PetItemPosition.TOP) h * 0.32f else h * 0.34f

        withTransform({
            scale(
                scaleX = interactions.crownScale,
                scaleY = interactions.crownScale,
                pivot = Offset(cX, cY)
            )
        }) {
            drawCrown(
                centerX = cX, centerY = cY, size = w * 0.18f,
                rotationDegrees = renderedPosition?.degrees ?: 0f,
                colorTheme = finalColor, // ЮВЕЛИРНО: цвет не прыгнет на красный!
                sparklePhase = if (item?.isSparkles == true) sparkleAnimationTime else 0f
            )
        }
    }

    // --- ПРЕДМЕТ: Шляпка цилиндр ---
    if (interactions.topHatScale > 0f) {
        val item = petItems?.topHat

        val finalColor = item?.petItemColor ?: interactions.cachedTopHatColor
        val currentPos = item?.position ?: interactions.cachedTopHatPos

        val renderedPosition = if (interactions.topHatScale < 0.15f) {
            currentPos
        } else {
            interactions.prevTopHatPos ?: currentPos
        }

        val cX = when (renderedPosition) {
            PetItemPosition.TOP -> w * 0.50f
            PetItemPosition.RIGHT -> w * 0.68f
            else -> w * 0.32f
        }
        val cY = if (renderedPosition == PetItemPosition.TOP) h * 0.32f else h * 0.34f

        withTransform({
            scale(
                scaleX = interactions.topHatScale,
                scaleY = interactions.topHatScale,
                pivot = Offset(cX, cY)
            )
        }) {
            drawTopHat(
                centerX = cX, centerY = cY, size = w * 0.22f,
                colorTheme = finalColor,
                rotationDegrees = renderedPosition?.degrees ?: 0f,
                sparklePhase = if (item?.isSparkles == true) sparkleAnimationTime else 0f
            )
        }
    }
    // --- ПРЕДМЕТ: Бант для девочки ---
    if (interactions.hairBowScale > 0f) {
        val item = petItems?.hairBow

        // Забираем параметры из кэша, если бант резко стал null в инвентаре
        val finalColor = item?.petItemColor ?: interactions.cachedHairBowColor
        val currentPos = item?.position ?: interactions.cachedHairBowPos

        // Логика волнового переноса позиций
        val renderedPosition = if (interactions.hairBowScale < 0.15f) {
            currentPos
        } else {
            interactions.prevHairBowPos ?: currentPos
        }

        // Твои оригинальные координаты центров для бантика
        val cX = when (renderedPosition) {
            PetItemPosition.TOP -> w * 0.50f
            PetItemPosition.LEFT -> w * 0.28f  // Смещен к левому уху кролика
            PetItemPosition.RIGHT -> w * 0.72f // Смещен к правому уху кролика
            else -> w * 0.50f
        }
        val cY = if (renderedPosition == PetItemPosition.TOP) h * 0.82f else h * 0.35f

        // Твои оригинальные углы: левый наклоняем на 25f, правый на -25f
        val rotationDegrees = when (renderedPosition) {
            PetItemPosition.LEFT -> 25f
            PetItemPosition.RIGHT -> -25f
            else -> 0f
        }

        withTransform({
            // Плавно сдуваем бантик прямо в его центр, предотвращая прыжки
            scale(
                scaleX = interactions.hairBowScale,
                scaleY = interactions.hairBowScale,
                pivot = Offset(cX, cY)
            )
        }) {
            drawHairBow(
                centerX = cX,
                centerY = cY,
                size = w * 0.22f,
                rotationDegrees = rotationDegrees,
                colorTheme = finalColor,
                sparklePhase = if (item?.isSparkles == true) sparkleAnimationTime else 0f
            )
        }
    }
    // --- ПРЕДМЕТ: Бабочка на шею (Чистый вертикальный Scale-эффект) ---
    if (interactions.bowTieScale > 0f) {
        val item = petItems?.bowTie

        // Забираем цвет из кэша, если в инвентаре бабочка резко стала null
        val finalColor = item?.petItemColor ?: interactions.cachedBowTieColor

        val cX = w * 0.50f
        val cY = h * 0.82f // Твоя оригинальная высота посадки на воротник

        withTransform({
            // Плавно раздуваем и сдуваем бабочку из её собственного центра шеи
            scale(
                scaleX = interactions.bowTieScale,
                scaleY = interactions.bowTieScale,
                pivot = Offset(cX, cY)
            )
        }) {
            drawBowTie(
                centerX = cX,
                centerY = cY,
                size = w * 0.18f,
                colorTheme = finalColor, // Цвет железно зафиксирован, пока масштаб тухнет
                sparklePhase = if (item?.isSparkles == true) sparkleAnimationTime else 0f
            )
        }
    }
    // --- ПРЕДМЕТ: Галстук (Адаптивный Scale-эффект под возраст кролика) ---
    if (interactions.neckTieScale > 0f) {
        val item = petItems?.neckTie

        // Забираем цвет из кэша, если в инвентаре галстук резко стал null
        val finalColor = item?.petItemColor ?: interactions.cachedNeckTieColor

        val cX = w * 0.50f
        // Твоя оригинальная адаптивная формула высоты посадки на шею
        val cY = h * 0.62f + h * stageScale * 0.25f

        withTransform({
            // Плавно раздуваем и сдуваем галстук из его собственного центра шеи
            scale(
                scaleX = interactions.neckTieScale,
                scaleY = interactions.neckTieScale,
                pivot = Offset(cX, cY)
            )
        }) {
            drawNeckTie(
                centerX = cX,
                centerY = cY,
                // Твоя оригинальная формула динамического размера галстука
                size = w * 0.15f * stageScale * 1.2f,
                colorTheme = finalColor, // Цвет зафиксирован, пока галстук исчезает
                sparklePhase = if (item?.isSparkles == true) sparkleAnimationTime else 0f
            )
        }
    }
    // --- ПРЕДМЕТ: Очки (Чистый центральный Scale-эффект) ---
    if (interactions.glassScale > 0f) {
        val item = petItems?.glass

        // Забираем цвет из кэша, если в инвентаре очки резко стали null
        val finalColor = item?.petItemColor ?: interactions.cachedGlassColor

        val cX = w * 0.50f
        val cY = h * 0.6f // Твоя оригинальная высота посадки на глаза

        withTransform({
            // Плавно раздуваем и сдуваем очки из центра лица
            scale(
                scaleX = interactions.glassScale,
                scaleY = interactions.glassScale,
                pivot = Offset(cX, cY)
            )
        }) {
            // Вызываем твою оригинальную функцию очков
            drawGlasses(
                centerX = cX,
                centerY = cY,
                size = w * 0.45f, // Твой оригинальный размер очков
                colorTheme = finalColor, // Цвет зафиксирован, пока очки сжимаются
                sparklePhase = if (item?.isSparkles == true) sparkleAnimationTime else 0f
            )
        }
    }
}