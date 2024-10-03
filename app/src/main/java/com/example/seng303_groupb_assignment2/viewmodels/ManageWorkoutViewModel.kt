package com.example.seng303_groupb_assignment2.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.seng303_groupb_assignment2.entities.Exercise
import com.example.seng303_groupb_assignment2.entities.Measurement

class ManageWorkoutViewModel(): ViewModel() {
    var name by mutableStateOf("")
        private set

    fun updateName(newName: String) {
        name = newName
    }

    var exercises = mutableStateListOf<Exercise>()
        private set

    fun addExercise(
        name: String,
        sets: Int,
        measurement1: Measurement,
        measurement2: Measurement,
        restTime: Int?
    ) {
        val exercise = Exercise(
            name = name,
            sets = sets,
            measurement1 = measurement1,
            measurement2 = measurement2,
            restTime = restTime
        )

        exercises.add(exercise)
    }

    fun deleteExercise(index: Int) {
        exercises.removeAt(index)
    }

    fun updateExercise(index: Int, name: String, sets: Int, m1: Measurement,
                       m2: Measurement, restTime: Int?) {
        val exercise = exercises[index]
        exercise.name = name
        exercise.sets = sets
        exercise.measurement1 = m1
        exercise.measurement2 = m2

        if (restTime != null) {
            exercise.restTime = restTime
        }
    }

    fun validName(): Boolean {
        return name.isNotBlank()
    }
}