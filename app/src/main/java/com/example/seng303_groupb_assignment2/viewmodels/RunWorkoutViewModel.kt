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
import kotlinx.coroutines.delay
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
    var timerSeconds by mutableStateOf(0)
    var totalTime by mutableStateOf(0)
    private val _workoutWithExercises = MutableLiveData<WorkoutWithExercises?>()
    val workoutWithExercises: LiveData<WorkoutWithExercises?> = _workoutWithExercises
    var actualRepsList = mutableListOf<Int>()

    private fun calculateTotalTime() {
        totalTime = totalReps * 5 //Assuming each rep takes 5 secs, this should be an option for user to enter
        timerSeconds = totalTime
    }
    // Load workout with exercises
    fun loadWorkoutWithExercises(workoutId: Long) {
        viewModelScope.launch {
            _workoutWithExercises.value = workoutDao.getWorkoutWithExercises(workoutId)
            _workoutWithExercises.value?.let { initialize(it) }
        }
    }

    // Initialize workout state
    fun initialize(workoutWithExercises: WorkoutWithExercises) {
        Log.d("RunWorkoutViewModel", "Initializing with workout: ${workoutWithExercises.workout.name}")
        currentExerciseIndex = 0
        currentSetIndex = 0
        currentReps = 0
        val initialExercise = workoutWithExercises.exercises[currentExerciseIndex]
        totalReps = initialExercise.measurement1.values.getOrNull(currentSetIndex)?.toInt() ?: 0
        actualRepsList = initialExercise.getMutableActualReps()
        calculateTotalTime()

    }

    fun startTimer() {
        isPlaying = true
        viewModelScope.launch {
            while (isPlaying && timerSeconds > 0 && currentReps < totalReps) {
                delay(1000L)
                timerSeconds--

                if (timerSeconds % 5 == 0) {
                    incrementReps()
                }
            }
            if (currentReps >= totalReps || timerSeconds <= 0) {
                isPlaying = false
            }
        }
    }
    // Move to the next exercise
    fun nextExercise() {
        if (currentExerciseIndex < (workoutWithExercises.value?.exercises?.size ?: (0 - 1))) {
            // Save actual reps for the previous exercise
            workoutWithExercises.value?.exercises?.get(currentExerciseIndex)?.actualReps = actualRepsList.toList()

            currentExerciseIndex++
            currentSetIndex = 0
            currentReps = 0
            isPlaying = false

            val nextExercise = workoutWithExercises.value?.exercises?.get(currentExerciseIndex)
            totalReps = nextExercise?.measurement1?.values?.getOrNull(currentSetIndex)?.toInt() ?: 0
            actualRepsList = nextExercise?.getMutableActualReps() ?: mutableListOf()
            calculateTotalTime()
        }
    }

    // Move to the previous exercise
    fun previousExercise() {
        if (currentExerciseIndex > 0) {
            // Save actual reps for the previous exercise
            workoutWithExercises.value?.exercises?.get(currentExerciseIndex)?.actualReps = actualRepsList.toList()

            currentExerciseIndex--
            currentSetIndex = 0
            currentReps = 0
            isPlaying = false

            val previousExercise = workoutWithExercises.value?.exercises?.get(currentExerciseIndex)
            totalReps = previousExercise?.measurement1?.values?.getOrNull(currentSetIndex)?.toInt() ?: 0
            actualRepsList = previousExercise?.getMutableActualReps() ?: mutableListOf()
            calculateTotalTime()
        }
    }

    // Increment actual reps for the current exercise and set
    fun incrementReps() {
        if (currentReps < totalReps) {
            currentReps++
            actualRepsList[currentSetIndex] = currentReps
            workoutWithExercises.value?.exercises?.get(currentExerciseIndex)?.actualReps = actualRepsList.toList()
        }
    }

    // Move to the next set for the current exercise
    fun nextSet() {
        val exercise = workoutWithExercises.value?.exercises?.get(currentExerciseIndex)
        if (currentSetIndex < (exercise?.sets ?: 0) - 1) {
            currentSetIndex++
            currentReps = actualRepsList.getOrNull(currentSetIndex) ?: 0
            totalReps = exercise?.measurement1?.values?.getOrNull(currentSetIndex)?.toInt() ?: 0
            calculateTotalTime()
        }

    }

    // Move to the previous set for the current exercise
    fun previousSet() {
        if (currentSetIndex > 0) {
            currentSetIndex--
            val exercise = workoutWithExercises.value?.exercises?.get(currentExerciseIndex)
            currentReps = actualRepsList.getOrNull(currentSetIndex) ?: 0
            totalReps = exercise?.measurement1?.values?.getOrNull(currentSetIndex)?.toInt() ?: 0
            calculateTotalTime()
        }
    }
}
