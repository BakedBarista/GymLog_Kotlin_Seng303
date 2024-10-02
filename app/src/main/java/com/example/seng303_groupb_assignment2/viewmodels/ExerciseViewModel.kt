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

    fun addExercise(workoutViewModel: ManageWorkoutViewModel) {
        viewModelScope.launch {
            val newExercise = Exercise(
                name = workoutViewModel.name,
                sets = workoutViewModel.sets,
                measurement1 = workoutViewModel.measurement1,
                measurement2 = workoutViewModel.measurement2,
                restTime = workoutViewModel.restTime
            )
            exerciseDao.upsertExercise(newExercise)
        }
    }
}