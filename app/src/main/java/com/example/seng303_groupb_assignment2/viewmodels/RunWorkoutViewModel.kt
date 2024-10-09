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
import com.example.seng303_groupb_assignment2.daos.ExerciseLogDao
import com.example.seng303_groupb_assignment2.daos.WorkoutDao
import com.example.seng303_groupb_assignment2.entities.ExerciseLog
import com.example.seng303_groupb_assignment2.entities.WorkoutWithExercises
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RunWorkoutViewModel(
    private val workoutDao: WorkoutDao,
    private val exerciseLogDao: ExerciseLogDao,
) : ViewModel() {
    var currentExerciseIndex by mutableStateOf(0)
    var currentSetIndex by mutableStateOf(0)
    var currentReps by mutableStateOf(0)
    var totalReps by mutableStateOf(0)
    var isResting by mutableStateOf(false)
    var timerSeconds by mutableStateOf(0)
    private val _workoutWithExercises = MutableLiveData<WorkoutWithExercises?>()
    val workoutWithExercises: LiveData<WorkoutWithExercises?> = _workoutWithExercises
    var logsList = mutableListOf<ExerciseLog>()
    var currentActualRepsInput by mutableStateOf("")

    fun updateCurrentActualRepsInput(newValue: String) {
        currentActualRepsInput = newValue
    }

    fun getActualValue(index: Int): Int {
        return logsList[currentExerciseIndex].measurement1.values[index].toInt()
    }

    fun updateExerciseLog() {
        if (currentActualRepsInput.isNotBlank()) {
            val values = logsList[currentExerciseIndex].measurement1.values.toMutableList()
            values[currentSetIndex] = currentActualRepsInput.toFloat()
            logsList[currentExerciseIndex].measurement1.values = values
        }
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
        timerSeconds = initialExercise.restTime ?: 0
        totalReps = initialExercise.measurement1.values.getOrNull(currentSetIndex)?.toInt() ?: 0
        isResting = false

        logsList.clear()
        logsList.addAll(workoutWithExercises.exercises.map { exercise -> exercise.toExerciseLog() })
        currentActualRepsInput = logsList[currentExerciseIndex].measurement1.values[currentSetIndex].toInt().toString()
    }

    fun startTimer(callback: () -> Unit) {
        val exercise = workoutWithExercises.value?.exercises?.get(currentExerciseIndex)!!
        if (exercise.restTime != null) {
            isResting = true
            timerSeconds = exercise.restTime!!
            viewModelScope.launch {
                while (isResting && timerSeconds > 0) {
                    delay(1000L)
                    timerSeconds--
                }
                isResting = false
                callback()
            }
        } else {
            callback()
        }
    }

    // Move to the next exercise
    fun nextExercise() {
        if (currentExerciseIndex < (workoutWithExercises.value?.exercises?.size ?: (0 - 1))) {
            // Save actual reps for the previous exercise

            currentExerciseIndex++
            currentSetIndex = 0
            currentReps = 0
            isResting = false

            val nextExercise = workoutWithExercises.value?.exercises?.get(currentExerciseIndex)
            totalReps = nextExercise?.measurement1?.values?.getOrNull(currentSetIndex)?.toInt() ?: 0
        }
    }

    // Move to the previous exercise
    fun previousExercise() {
        if (currentExerciseIndex > 0) {
            // Save actual reps for the previous exercise

            currentExerciseIndex--
            currentSetIndex = 0
            currentReps = 0
            isResting = false

            val previousExercise = workoutWithExercises.value?.exercises?.get(currentExerciseIndex)
            totalReps = previousExercise?.measurement1?.values?.getOrNull(currentSetIndex)?.toInt() ?: 0
        }
    }

    // Move to the next set for the current exercise
    fun nextSet(nextExerciseAvailable: Boolean) {
        startTimer() {
            val exercise = workoutWithExercises.value?.exercises?.get(currentExerciseIndex)
            if (canGoNext()) {
                updateExerciseLog()
                currentSetIndex++
                totalReps = exercise?.measurement1?.values?.getOrNull(currentSetIndex)?.toInt() ?: 0
            } else if (nextExerciseAvailable){
                updateExerciseLog()
                nextExercise()
            }
            currentActualRepsInput = logsList[currentExerciseIndex].measurement1.values[currentSetIndex].toInt().toString()
        }
    }

    // Move to the previous set for the current exercise
    fun previousSet(previousExerciseAvailable: Boolean) {

        if (canGoPrevious()) {
            updateExerciseLog()
            currentSetIndex--
            val exercise = workoutWithExercises.value?.exercises?.get(currentExerciseIndex)
            totalReps = exercise?.measurement1?.values?.getOrNull(currentSetIndex)?.toInt() ?: 0
        } else if (previousExerciseAvailable) {
            updateExerciseLog()
            previousExercise()
        }
        currentActualRepsInput = logsList[currentExerciseIndex].measurement1.values[currentSetIndex].toInt().toString()
    }

    fun canGoPrevious(): Boolean {
        return currentSetIndex > 0
    }

    fun canGoNext(): Boolean {
        val exercise = workoutWithExercises.value?.exercises?.get(currentExerciseIndex)
        return currentSetIndex < (exercise?.sets ?: 0) - 1
    }

    fun endRestEarly() {
        timerSeconds = 0
    }

    fun saveLogs() {
        viewModelScope.launch {
            logsList.forEach { exerciseLog ->
                exerciseLogDao.upsertExerciseLog(exerciseLog = exerciseLog)
            }
        }
    }
}
