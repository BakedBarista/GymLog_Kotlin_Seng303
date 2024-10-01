package com.example.seng303_groupb_assignment2.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ManageWorkoutViewModel: ViewModel() {
    var name by mutableStateOf("")
        private set

    fun updateName(newName: String) {
        name = newName
    }
}