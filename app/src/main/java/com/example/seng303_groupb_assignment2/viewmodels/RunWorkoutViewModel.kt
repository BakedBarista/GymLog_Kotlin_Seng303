package com.example.seng303_groupb_assignment2.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
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

    var currentExerciseIndex by mutableIntStateOf(0)
    private val _workoutWithExercises = MutableLiveData<WorkoutWithExercises?>()
    val workoutWithExercises: LiveData<WorkoutWithExercises?> = _workoutWithExercises

    // Map from exercise index to list of sets (unit1, unit2)
    private var exerciseSets = mutableMapOf<Int, SnapshotStateList<Pair<Float, Float>>>()

    fun clearWorkoutData() {
        currentExerciseIndex = 0
        exerciseSets.clear()
        _workoutWithExercises.value = null  // Clear the loaded workout data
    }

    // Function to get sets for the current exercise
    fun getSetsForCurrentExercise(): SnapshotStateList<Pair<Float, Float>> {
        return exerciseSets.getOrPut(currentExerciseIndex) { mutableStateListOf() }
    }

    // Add a set to the current exercise
    fun addSetToCurrentExercise(unit1: Float, unit2: Float) {
        val sets = getSetsForCurrentExercise()
        sets.add(Pair(unit1, unit2))
    }

    // Remove a set from the current exercise
    fun removeSetFromCurrentExercise(index: Int) {
        val sets = getSetsForCurrentExercise()
        if (index >= 0 && index < sets.size) {
            sets.removeAt(index)
        }
    }

    // Save logs to the database
    fun saveLogs() {
        viewModelScope.launch {
            exerciseSets.forEach { (exerciseIndex, sets) ->
                val exercise = workoutWithExercises.value?.exercises?.get(exerciseIndex)
                if (exercise != null && sets.isNotEmpty()) {
                    val exerciseLog = ExerciseLog(
                        exerciseId = exercise.id,
                        record = sets.toMutableList(),
                        timestamp = System.currentTimeMillis()
                    )
                    exerciseLogDao.upsertExerciseLog(exerciseLog)
                }
            }
        }
    }

    // Load workout with exercises
    fun loadWorkoutWithExercises(workoutId: Long) {
        viewModelScope.launch {
            _workoutWithExercises.value = workoutDao.getWorkoutWithExercises(workoutId)
        }
    }

    // Navigate to the next exercise
    fun nextExercise() {
        updateExerciseLog()
        if (currentExerciseIndex < (workoutWithExercises.value?.exercises?.size ?: 0) - 1) {
            currentExerciseIndex++
        }
    }

    // Navigate to the previous exercise
    fun previousExercise() {
        updateExerciseLog()
        if (currentExerciseIndex > 0) {
            currentExerciseIndex--
        }
    }

    // Update the exercise log with the current sets
    private fun updateExerciseLog() {
        // This function can be used to perform any additional actions when navigating between exercises
    }
}


