package com.example.seng303_groupb_assignment2.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seng303_groupb_assignment2.daos.ExerciseDao
import com.example.seng303_groupb_assignment2.entities.Exercise
import com.example.seng303_groupb_assignment2.enums.Days
import com.example.seng303_groupb_assignment2.enums.Measurement
import kotlinx.coroutines.launch

class ManageWorkoutViewModel(
    private val exerciseDao: ExerciseDao,
    ): ViewModel() {
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
    // take the name and search for an exercise that matches all fields EXACTLY if it does then upsert otherwise add new
    fun addExercise(
        name: String,
        restTime: Int?,
        measurement: Measurement
    ) {
        viewModelScope.launch {
            val tempExercise =
                exerciseDao.getAllMatchingExercises(name, restTime ?: 0, measurement.label)
            if (tempExercise.isEmpty()) {
                val exercise = Exercise(
                    name = name,
                    restTime = restTime,
                    measurement = measurement
                )
                exercises.add(exercise)
            } else {
                Log.d("ADD existing", "Existing found")
                Log.d("EX", exercises.toString())
                exercises.add(tempExercise[0])
                Log.d("EX2", exercises.toString())
            }
        }
    }

    fun deleteExercise(index: Int) {
        exercises.removeAt(index)
    }

    fun validName(): Boolean {
        return name.isNotBlank()
    }

    fun moveExercise(start: Int, end: Int) {
        exercises.add(end, exercises.removeAt(start))
    }

    fun clear() {
        name = ""
        description = ""
        schedule = mutableStateListOf()
        exercises = mutableStateListOf()
    }
}