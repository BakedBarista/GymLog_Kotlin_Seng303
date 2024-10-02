package com.example.seng303_groupb_assignment2.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.seng303_groupb_assignment2.daos.ExerciseDao
import com.example.seng303_groupb_assignment2.entities.Exercise
import kotlinx.coroutines.launch

class ExerciseViewModel(
    private val exerciseDao: ExerciseDao
) : ViewModel() {
    val allExercises: LiveData<List<Exercise>> = exerciseDao.getAllExercises().asLiveData()

    fun addExercise(exercise: Exercise) {
        viewModelScope.launch {
            exerciseDao.upsertExercise(exercise)
        }
    }

    fun deleteExercise(exercise: Exercise) {
        viewModelScope.launch {
            exerciseDao.deleteExercise(exercise)
        }
    }

    fun editExercise(exercise: Exercise) {
        viewModelScope.launch {
            exerciseDao.upsertExercise(exercise)
        }
    }

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
            exerciseDao.upsertExercise(newExercise)
        }
    }
}