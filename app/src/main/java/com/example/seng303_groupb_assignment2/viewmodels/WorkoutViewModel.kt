package com.example.seng303_groupb_assignment2.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.seng303_groupb_assignment2.daos.WorkoutDao
import com.example.seng303_groupb_assignment2.entities.Workout
import com.example.seng303_groupb_assignment2.entities.WorkoutWithExercises
import com.example.seng303_groupb_assignment2.services.FileExportService
import kotlinx.coroutines.launch
import android.util.Log
import com.example.seng303_groupb_assignment2.daos.ExerciseLogDao
import com.example.seng303_groupb_assignment2.datastore.PreferencePersistentStorage
import com.example.seng303_groupb_assignment2.entities.Exercise
import com.example.seng303_groupb_assignment2.entities.ExerciseLog
import com.example.seng303_groupb_assignment2.models.UserPreferences
import com.example.seng303_groupb_assignment2.services.MeasurementConverter
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import org.koin.androidx.compose.getViewModel
import org.koin.androidx.compose.koinViewModel
import java.io.File.separator


class WorkoutViewModel(
    private val workoutDao: WorkoutDao,
    private val exerciseLogDao: ExerciseLogDao
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
    fun exportWorkout(context: Context, workoutWithExercises: WorkoutWithExercises, onSuccess: (String) -> Unit, onFailure: () -> Unit, isMetric: Boolean) {
        viewModelScope.launch {
            val measurementConverter = MeasurementConverter(isMetric)

            val fileExportService = FileExportService(context)
            val headers = listOf("Workout Name", "Description", "Rest Time", "Exercise Name")
            val rows = workoutWithExercises.exercises.map { exercise ->
                val workoutName = workoutWithExercises.workout.name
                val workoutDescription = workoutWithExercises.workout.description
                val restTime = exercise.restTime
                val exerciseName = exercise.name
                val exerciseMeasurement = exercise.measurement

                listOf(
                    workoutName,
                    workoutDescription,
                    restTime.toString(),
                    exerciseName,
                    exerciseMeasurement.label
                )
            }

            val csvData = fileExportService.prepareCsvData(headers, rows)
            Log.d("Data", csvData)
            val filePath = fileExportService.exportToCsv("${workoutWithExercises.workout.name}.csv", csvData)

            if (filePath != null) {
                onSuccess(filePath)
            } else {
                onFailure()
            }
        }
    }

    fun exportWorkoutLog(context: Context, workoutWithExercises: WorkoutWithExercises, onSuccess: (String) -> Unit, onFailure: () -> Unit, isMetric: Boolean) {
        viewModelScope.launch {
            val measurementConverter = MeasurementConverter(isMetric)
            val exercises: List<Exercise> = workoutWithExercises.exercises
            val headers = listOf("Exercise Name", "Timestamp", "Sets")
            val allExerciseLogRows = mutableListOf<List<String>>()

            val deferredLogs = exercises.map { exercise ->
                async {
                    val logs = exerciseLogDao.getExerciseLogsByExerciseId(exercise.id).first()
                    logs.map { log : ExerciseLog ->
                        val exerciseName = exercise.name
                        val logTimestamp = log.timestamp
                        val logSets = log.record.size
                        val firstValues = log.record.joinToString ( separator = "," ) {
                            val convertedValue = measurementConverter.convertToImperial(it.first, isMetric)
                            convertedValue.toString()
                        }
                        val secondValues = log.record.joinToString ( separator = "," ) {
                            val convertedValue = measurementConverter.convertToImperial(it.second, isMetric) }

                        listOf(
                            exerciseName,
                            logTimestamp.toString(),
                            logSets.toString(),
                            firstValues,
                            secondValues
                        )
                    }
                }
            }

            val allLogs = deferredLogs.awaitAll().flatten()
            allExerciseLogRows.addAll(allLogs)

            val fileExportService = FileExportService(context)
            val csvData = fileExportService.prepareCsvData(headers, allExerciseLogRows)
            Log.d("exported exercise logs", csvData)
            val filePath = fileExportService.exportToCsv("${workoutWithExercises.workout.name}_log.csv", csvData)

            if (filePath != null) {
                onSuccess(filePath)
            } else {
                onFailure()
            }
        }
    }
}