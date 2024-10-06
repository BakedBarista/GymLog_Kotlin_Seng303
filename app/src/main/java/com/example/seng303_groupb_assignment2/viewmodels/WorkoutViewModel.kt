package com.example.seng303_groupb_assignment2.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.seng303_groupb_assignment2.daos.ExerciseDao
import com.example.seng303_groupb_assignment2.daos.WorkoutDao
import com.example.seng303_groupb_assignment2.entities.Workout
import com.example.seng303_groupb_assignment2.entities.WorkoutWithExercises
import com.example.seng303_groupb_assignment2.services.FileExportService
import kotlinx.coroutines.launch
import android.util.Log

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

    // Export workout to CSV
    fun exportWorkout(context: Context, workoutWithExercises: WorkoutWithExercises, onSuccess: (String) -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            val fileExportService = FileExportService(context)
            val headers = listOf("Workout Name", "Description", "Exercise Name", "Measurement1 Type", "Measurement1 Values", "Measurement2 Type", "Measurement2 Values", "Rest Time")
            val rows = workoutWithExercises.exercises.map { exercise ->
                val workoutName = workoutWithExercises.workout.name
                val workoutDescription = workoutWithExercises.workout.description
                val exerciseName = exercise.name
                val exerciseType = exercise.measurement1.type
                val exerciseValues = exercise.measurement1.values.joinToString(separator = "/") { it.toString() }
                val exerciseType2 = exercise.measurement2.type
                val exerciseValues2 = exercise.measurement2.values.joinToString(separator = "/") { it.toString() }
                val restTime = exercise.restTime

                listOf(
                    workoutName,
                    workoutDescription,
                    exerciseName,
                    exerciseType,
                    exerciseValues,
                    exerciseType2,
                    exerciseValues2,
                    restTime.toString() ?: "No rest time"
                )
            }

            val csvData = fileExportService.prepareCsvData(headers, rows)

            Log.d("WorkoutExport", "Exported Workout Content:\n$csvData")

            val filePath = fileExportService.exportToCsv("workout_${workoutWithExercises.workout.name}.csv", csvData)

            if (filePath != null) {
                onSuccess(filePath)
            } else {
                onFailure()
            }
        }
    }


}
