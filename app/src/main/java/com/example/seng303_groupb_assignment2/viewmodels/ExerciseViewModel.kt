package com.example.seng303_groupb_assignment2.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.seng303_groupb_assignment2.daos.ExerciseDao
import com.example.seng303_groupb_assignment2.daos.WorkoutDao
import com.example.seng303_groupb_assignment2.entities.Exercise
import com.example.seng303_groupb_assignment2.entities.WorkoutExerciseCrossRef
import kotlinx.coroutines.launch

class ExerciseViewModel(
    private val exerciseDao: ExerciseDao,
    private val workoutDao: WorkoutDao
) : ViewModel() {
    val allExercises: LiveData<List<Exercise>> = exerciseDao.getAllExercises().asLiveData()

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

    fun addExercise(workoutId: Long, exercise: Exercise) {
        viewModelScope.launch {
            exerciseDao.upsertExercise(exercise)
            val crossRef = WorkoutExerciseCrossRef(workoutId = workoutId, exerciseId = exercise.id)
            workoutDao.upsertWorkoutExerciseCrossRef(crossRef)
        }
    }
}