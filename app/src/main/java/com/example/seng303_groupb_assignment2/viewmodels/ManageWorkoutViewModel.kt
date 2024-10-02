package com.example.seng303_groupb_assignment2.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.seng303_groupb_assignment2.entities.Measurement

class ManageWorkoutViewModel: ViewModel() {
    var name by mutableStateOf("")
        private set

    fun updateName(newName: String) {
        name = newName
    }

    var sets by mutableIntStateOf(0)
        private set

    var measurement1: Measurement by mutableStateOf(Measurement(type = "", value = listOf()))
        private set

    var measurement2: Measurement by mutableStateOf(Measurement(type = "", value = listOf()))
        private set

    var restTime by mutableIntStateOf(0)
        private set
}