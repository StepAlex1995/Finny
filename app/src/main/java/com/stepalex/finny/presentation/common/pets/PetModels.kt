package com.stepalex.finny.presentation.common.pets


enum class PetStage {
    Baby,
    Teenager,
    Adult
}

enum class PetMood {
    Sad,
    Normal,
    Happy,
    Sleep
}

enum class PetAction {
    Pet,    //Гладить
    Eat,    //Кормить
    Play    //Играть
}

interface PetListener {
    fun updatePetMod(newMode: PetMood)
}
