package com.example.seng303_groupb_assignment2.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seng303_groupb_assignment2.daos.ExerciseDao
import com.example.seng303_groupb_assignment2.daos.WorkoutDao
import com.example.seng303_groupb_assignment2.entities.WorkoutWithExercises
import kotlinx.coroutines.launch

class RunWorkoutViewModel(
    private val workoutDao: WorkoutDao,
    private val exerciseDao: ExerciseDao
) : ViewModel() {
    var currentExerciseIndex by mutableStateOf(0)
    var currentSetIndex by mutableStateOf(0)
    var currentReps by mutableStateOf(0)
    var totalReps by mutableStateOf(0)
    var isPlaying by mutableStateOf(false)
    var actualReps by mutableStateOf(0)
    private val _workoutWithExercises = MutableLiveData<WorkoutWithExercises?>()
    val workoutWithExercises: LiveData<WorkoutWithExercises?> = _workoutWithExercises

    // Load workout with exercises
    fun loadWorkoutWithExercises(workoutId: Long) {
        viewModelScope.launch {
            _workoutWithExercises.value = workoutDao.getWorkoutWithExercises(workoutId)
            _workoutWithExercises.value?.let { initialize(it) }
        }
    }

    // Initialize workout state
    fun initialize(workoutWithExercises: WorkoutWithExercises) {
        currentExerciseIndex = 0
        currentSetIndex = 0
        currentReps = 0
        val initialExercise = workoutWithExercises.exercises[currentExerciseIndex]
        totalReps = initialExercise.measurement2.values.getOrNull(currentSetIndex)?.toInt() ?: 0
    }

    // Move to the next exercise
    fun nextExercise() {
        if (currentExerciseIndex < (workoutWithExercises.value?.exercises?.size ?: (0 - 1))) {
            currentExerciseIndex++
            currentSetIndex = 0
            currentReps = 0
            actualReps = 0
        }
    }

    // Move to the previous exercise
    fun previousExercise() {
        if (currentExerciseIndex > 0) {
            currentExerciseIndex--
            currentSetIndex = 0
            currentReps = 0
        }
    }


    // Increment actual reps for the current exercise and set
    fun incrementReps() {
        if (currentReps < totalReps) {
            currentReps++
            workoutWithExercises.value?.exercises?.get(currentExerciseIndex)?.actualReps = currentReps
        }
    }

    // Move to the next set for the current exercise
    fun nextSet() {
        if (currentSetIndex < workoutWithExercises.value?.exercises?.get(currentExerciseIndex)?.sets!!) {
            currentSetIndex++
            currentReps = 0
            totalReps = workoutWithExercises.value?.exercises?.get(currentExerciseIndex)?.measurement2?.values?.getOrNull(currentSetIndex)?.toInt() ?: 0
        }
    }

    // Move to the previous set for the current exercise
    fun previousSet() {
        if (currentSetIndex > 0) {
            currentSetIndex--
            currentReps = 0
            totalReps = workoutWithExercises.value?.exercises?.get(currentExerciseIndex)?.measurement2?.values?.getOrNull(currentSetIndex)?.toInt() ?: 0
        }
    }

    // Update actual reps for a specific set
    fun updateActualReps(value: Int) {
        actualReps = value
        Log.d("RunWorkoutViewModel", "Actual Reps Updated: $actualReps")
    }
}
