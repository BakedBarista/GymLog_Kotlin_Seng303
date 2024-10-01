package com.example.seng303_groupb_assignment2.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.seng303_groupb_assignment2.database.AppDatabase
import com.example.seng303_groupb_assignment2.entities.Exercise
import kotlinx.coroutines.launch

class ExerciseViewModel(application: Application) : AndroidViewModel(application) {
    private val exerciseDao = AppDatabase.getDatabase(application).exerciseDao()

    val allExercises: LiveData<List<Exercise>> = exerciseDao.getAllExercises().asLiveData()

    fun addExercise(name: String, sets: Int, reps: Int?, weight: Float?, distance: Float?, time: Float?, restTime: Int) {
        viewModelScope.launch {
            val newExercise = Exercise(
                name = name,
                sets = sets,
                reps = reps?.let { listOf(it) },
                weight = weight?.let { listOf(it) },
                distance = distance,
                time = time,
                restTime = restTime
            )
            exerciseDao.insertExercise(newExercise)
        }
    }
}