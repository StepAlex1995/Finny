package com.stepalex.finny.presentation.pet_room

import androidx.compose.ui.graphics.Color


data class PetRoomColor(
    val wallColor: Color = Color(0xFFFFF2CC),               // стена
    val floorColor: Color = Color(0xFFFFE6C2),               // пол
    val floorLineColor: Color = Color(0xFFF1D3AC),           // линии пола
    val windowsFrameColor: Color = Color(0xFFFFFFFF),        // рамка окна
    val windowsFrameStrokeColor: Color = Color(0xFFe4ecec),        // огранка рамки окна
    val windowBgColor: Color = Color(0xFFBAE5FF),    // Вид из окна (небо)
    /*val curtainLineColor: Color = Color(0xFFbbd29e),    //Линии занавесок
    val curtainColor: Color = Color(0xFFd4e8ab),    //занавески
    val curtainHorizontalColor: Color = Color(0Xffaac578),    //цвет подвязки
    val curtainHorizontalStrokeColor: Color = Color(0xFF8cad68),    //цвет обводки подвязки
     */
    val curtainLineColor: Color = Color(0xFFcec6ff),    //Линии занавесок
    val curtainColor: Color = Color(0xFFe6e6fe),    //занавески
    val curtainHorizontalColor: Color = Color(0xFFc6c6ee),    //цвет подвязки
    val curtainHorizontalStrokeColor: Color = Color(0xFFb6b6ee),    //цвет обводки подвязки
)
