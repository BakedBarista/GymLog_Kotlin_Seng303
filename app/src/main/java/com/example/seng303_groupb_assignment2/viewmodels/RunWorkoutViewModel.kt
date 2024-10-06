package com.example.seng303_groupb_assignment2.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seng303_groupb_assignment2.daos.WorkoutDao
import com.example.seng303_groupb_assignment2.entities.WorkoutWithExercises
import kotlinx.coroutines.launch

class RunWorkoutViewModel(private val workoutDao: WorkoutDao) : ViewModel() {
    // Current state variables
    var currentExerciseIndex by mutableStateOf(0)
    var currentSetIndex by mutableStateOf(0)
    var currentReps by mutableStateOf(0)
    var totalReps by mutableStateOf(0)
    var isPlaying by mutableStateOf(false)
    val actualReps = mutableStateListOf<Int>()
    private val _workoutWithExercises = MutableLiveData<WorkoutWithExercises?>()
    val workoutWithExercises: LiveData<WorkoutWithExercises?> = _workoutWithExercises

    fun loadWorkoutWithExercises(workoutId: Int) {
        viewModelScope.launch {
            _workoutWithExercises.value = workoutDao.getWorkoutWithExercises(workoutId.toLong())
        }
    }

    fun initialize(workoutWithExercises: WorkoutWithExercises) {
        currentExerciseIndex = 0
        currentSetIndex = 0
        currentReps = 0
        totalReps = workoutWithExercises.exercises[currentExerciseIndex].reps?.getOrNull(currentSetIndex) ?: 0
        actualReps.clear()
    }
    // Move to the next exercise
    fun nextExercise(workoutWithExercises: WorkoutWithExercises) {
        if (currentExerciseIndex < workoutWithExercises.exercises.size - 1) {
            currentExerciseIndex++
            currentSetIndex = 0
            currentReps = 0
            totalReps = workoutWithExercises.exercises[currentExerciseIndex].reps?.getOrNull(currentSetIndex) ?: 0
        }
    }

    // Move to the previous exercise
    fun previousExercise(workoutWithExercises: WorkoutWithExercises) {
        if (currentExerciseIndex > 0) {
            currentExerciseIndex--
            currentSetIndex = 0
            currentReps = 0
            totalReps = workoutWithExercises.exercises[currentExerciseIndex].reps?.getOrNull(currentSetIndex) ?: 0
        }
    }

    // Increment actual reps for the current exercise
    fun incrementReps() {
        if (currentReps < totalReps) {
            actualReps[currentSetIndex]++
            currentReps++
        }
    }

    // Move to the next set for the current exercise
    fun nextSet() {
        if (currentSetIndex < actualReps.size - 1) {
            currentSetIndex++
            currentReps = 0
            totalReps = actualReps[currentSetIndex]
        }
    }

    fun previousSet() {
        if (currentSetIndex > 0) {
            currentSetIndex--
            currentReps = 0
            totalReps = actualReps[currentSetIndex]
        }
    }
    // Reset the workout state
    fun resetWorkout() {
        currentExerciseIndex = 0
        currentSetIndex = 0
        currentReps = 0
        actualReps.clear()
    }

    fun updateActualReps(index: Int, value: Int) {
        if (index in actualReps.indices) {
            actualReps[index] = value
        }
    }
}
