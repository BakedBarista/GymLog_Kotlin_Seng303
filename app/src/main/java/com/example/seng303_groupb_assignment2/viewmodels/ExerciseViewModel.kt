package com.example.seng303_groupb_assignment2.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.seng303_groupb_assignment2.database.AppDatabase
import com.example.seng303_groupb_assignment2.entities.Exercise
import com.example.seng303_groupb_assignment2.entities.WorkoutWithExercises
import kotlinx.coroutines.launch

class ExerciseViewModel(application: Application) : AndroidViewModel(application) {
    private val exerciseDao = AppDatabase.getDatabase(application).exerciseDao()
    private val workoutDao = AppDatabase.getDatabase(application).workoutDao()
    private val _workoutWithExercises = MutableLiveData<WorkoutWithExercises?>()
    val workoutWithExercises: LiveData<WorkoutWithExercises?> = _workoutWithExercises

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
            exerciseDao.upsertExercise(newExercise)
        }
    }

    fun loadWorkoutWithExercises(workoutId: Long) {
        viewModelScope.launch {
            val workout = workoutDao.getWorkoutWithExercises(workoutId)
            _workoutWithExercises.postValue(workout)
        }
    }
}