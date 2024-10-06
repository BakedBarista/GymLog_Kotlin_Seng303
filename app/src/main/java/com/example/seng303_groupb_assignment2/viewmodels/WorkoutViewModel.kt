package com.example.seng303_groupb_assignment2.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.seng303_groupb_assignment2.daos.ExerciseDao
import com.example.seng303_groupb_assignment2.daos.WorkoutDao
import com.example.seng303_groupb_assignment2.entities.Exercise
import com.example.seng303_groupb_assignment2.entities.Workout
import com.example.seng303_groupb_assignment2.entities.WorkoutExerciseCrossRef
import com.example.seng303_groupb_assignment2.entities.WorkoutWithExercises
import com.example.seng303_groupb_assignment2.enums.Days
import kotlinx.coroutines.launch

class WorkoutViewModel(
    private val workoutDao: WorkoutDao,
    private val exerciseDao: ExerciseDao
) : ViewModel() {
    val allWorkouts: LiveData<List<WorkoutWithExercises>> = workoutDao.getAllWorkoutsWithExercises().asLiveData()

    fun addWorkout(workout: Workout, onWorkoutAdded: (Long) -> Unit) {
        viewModelScope.launch {
            val workoutId = workoutDao.upsertWorkout(workout)
            onWorkoutAdded(workoutId)
        }
    }

    fun deleteWorkout(workout: Workout) {
        viewModelScope.launch {
            workoutDao.deleteWorkout(workout)
        }
    }

    fun editWorkout(workout: Workout) {
        viewModelScope.launch {
            workoutDao.upsertWorkout(workout)
        }
    }

}