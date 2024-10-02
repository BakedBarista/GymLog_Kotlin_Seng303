package com.example.seng303_groupb_assignment2.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.seng303_groupb_assignment2.database.AppDatabase
import com.example.seng303_groupb_assignment2.entities.Exercise
import com.example.seng303_groupb_assignment2.entities.WorkoutExerciseCrossRef
import kotlinx.coroutines.launch

class ExerciseViewModel(application: Application) : AndroidViewModel(application) {
    private val exerciseDao = AppDatabase.getDatabase(application).exerciseDao()
    private val workoutDao = AppDatabase.getDatabase(application).workoutDao()

    val allExercises: LiveData<List<Exercise>> = exerciseDao.getAllExercises().asLiveData()

    fun addExercise(workoutId: Long, exercise: Exercise) {
        viewModelScope.launch {
            exerciseDao.upsertExercise(exercise)
            val crossRef = WorkoutExerciseCrossRef(workoutId = workoutId, exerciseId = exercise.id)
            workoutDao.upsertWorkoutExerciseCrossRef(crossRef)
        }
    }
}