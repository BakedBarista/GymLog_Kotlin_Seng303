package com.example.seng303_groupb_assignment2.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.seng303_groupb_assignment2.entities.Exercise
import com.example.seng303_groupb_assignment2.enums.Days
import com.example.seng303_groupb_assignment2.enums.Measurement

class ManageWorkoutViewModel(): ViewModel() {
    var name by mutableStateOf("")
        private set

    var description by mutableStateOf("")
        private set

    var exercises = mutableStateListOf<Exercise>()
        private set

    var schedule = mutableStateListOf<Days>()
        private set

    fun updateName(newName: String) {
        name = newName
    }

    fun updateDescription(newDescription: String) {
        description = newDescription
    }

    fun toggleDay(day: Days) {
        if (schedule.contains(day)) {
            schedule.remove(day)
        } else {
            schedule.add(day)
        }
    }

    fun addExercise(
        name: String,
        restTime: Int?,
        measurement: Measurement
    ) {
        val exercise = Exercise(
            name = name,
            restTime = restTime,
            measurement = measurement
        )

        exercises.add(exercise)
    }

    fun deleteExercise(index: Int) {
        exercises.removeAt(index)
    }

    fun updateExercise(index: Int, name: String,
                       restTime: Int?, measurement: Measurement) {
        val exercise = exercises[index]
        exercise.name = name
        if (restTime != null) {
            exercise.restTime = restTime
        }
        exercise.measurement = measurement
    }

    fun validName(): Boolean {
        return name.isNotBlank()
    }

    fun moveExercise(start: Int, end: Int) {
        exercises.add(end, exercises.removeAt(start))
    }
}