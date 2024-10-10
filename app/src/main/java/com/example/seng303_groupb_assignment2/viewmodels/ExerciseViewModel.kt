package com.example.seng303_groupb_assignment2.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.seng303_groupb_assignment2.daos.ExerciseDao
import com.example.seng303_groupb_assignment2.daos.WorkoutDao
import com.example.seng303_groupb_assignment2.daos.ExerciseLogDao
import com.example.seng303_groupb_assignment2.entities.Exercise
import com.example.seng303_groupb_assignment2.entities.WorkoutExerciseCrossRef
import com.example.seng303_groupb_assignment2.entities.ExerciseLog
import com.example.seng303_groupb_assignment2.entities.Workout
import com.example.seng303_groupb_assignment2.enums.Days
import com.example.seng303_groupb_assignment2.enums.Measurement
import kotlinx.coroutines.launch
import kotlin.random.Random

class ExerciseViewModel(
    private val exerciseDao: ExerciseDao,
    private val workoutDao: WorkoutDao,
    private val exerciseLogDao: ExerciseLogDao
) : ViewModel() {

    fun deleteExercise(exercise: Exercise) {
        viewModelScope.launch {
            exerciseDao.deleteExercise(exercise)
        }
    }

    fun addExerciseToWorkout(workoutId: Long, exercise: Exercise) {
        viewModelScope.launch {
            val existingExercises = exerciseDao.getAllMatchingExercises(
                name = exercise.name,
                restTime = exercise.restTime ?: 0,
                measurementLabel = exercise.measurement.label
            )

            val exerciseId = if (existingExercises.isNotEmpty()) {
                existingExercises[0].id
            } else {
                exerciseDao.upsertExercise(exercise)
            }

            val crossRef = WorkoutExerciseCrossRef(workoutId = workoutId, exerciseId = exerciseId)

            workoutDao.upsertWorkoutExerciseCrossRef(crossRef)
        }
    }

    fun getExercisesByName(name: String): LiveData<List<Exercise>> {
        return exerciseDao.getAllExercisesByName("%$name%").asLiveData()
    }

    fun getExercisesByNameOrEmpty(name: String): LiveData<List<Exercise>> {
        return if (name.isEmpty()) {
            MutableLiveData(emptyList())
        } else {
            exerciseDao.getAllExercisesByName("%$name%").asLiveData()
        }
    }

    fun getExerciseLogsByExercise(exerciseId: Long): LiveData<List<ExerciseLog>> {
        return exerciseLogDao.getExerciseLogsByExerciseId(exerciseId).asLiveData()
    }

    fun addExercise(workoutId: Long, exercise: Exercise) {
        viewModelScope.launch {
            Log.d("UPSErt", "log")
            val exerciseId: Long = if (exercise.id == 0L) {
                exerciseDao.upsertExercise(exercise)
            } else {
                exercise.id
            }
            Log.d("id", exerciseId.toString())
            if (exerciseId != -1L) {
                val crossRef = WorkoutExerciseCrossRef(workoutId = workoutId, exerciseId = exerciseId)
                workoutDao.upsertWorkoutExerciseCrossRef(crossRef)
            } else {
                Log.e("AddExerciseError", "Failed to upsert exercise. Invalid ID returned.")
            }
        }
    }

    fun createSampleExerciseAndLogs() {
        viewModelScope.launch {
            try {
                val workoutCount = workoutDao.getWorkoutCount()
                val workoutOne : Workout
                val workoutOneId : Long
                if (workoutCount == 0) {
                    workoutOne = Workout(
                        name = "My First Exercise",
                        description = "Bench press and a run",
                        schedule = listOf(Days.MONDAY, Days.WEDNESDAY, Days.FRIDAY)
                    )
                    workoutOneId = workoutDao.upsertWorkout(workoutOne)
                    val count = exerciseDao.getExerciseCount()
                    if (count == 0) {
                        val benchPress = Exercise(
                            name = "Bench Press",
                            restTime = 90,
                            measurement = Measurement.REPS_WEIGHT
                        )

                        val run = Exercise(
                            name = "Run",
                            restTime = 0,
                            measurement = Measurement.DISTANCE_TIME
                        )

                        val exerciseId = exerciseDao.upsertExercise(benchPress)
                        val exerciseIdTwo = exerciseDao.upsertExercise(run)

                        val workoutWithExercise =
                            WorkoutExerciseCrossRef(workoutOneId, exerciseId)

                        val workoutWithExercise2 = WorkoutExerciseCrossRef(workoutOneId, exerciseIdTwo)

                        workoutDao.upsertWorkoutExerciseCrossRef(workoutWithExercise)
                        workoutDao.upsertWorkoutExerciseCrossRef(workoutWithExercise2)

                        val logsBench = mutableListOf<ExerciseLog>()
                        val logsRun = mutableListOf<ExerciseLog>()

                        for (i in 0 until 365) {
                            val timestamp = System.currentTimeMillis() - (i * 24 * 60 * 60 * 1000L)

                            val log = ExerciseLog(
                                exerciseId = exerciseId,
                                timestamp = timestamp,
                                record = mutableListOf(Pair(3f, Random.nextFloat() * 50 + 50), Pair(3f, Random.nextFloat() * 50 + 50), Pair(3f, Random.nextFloat() * 50 + 50))
                            )
                            logsBench.add(log)
                        }

                        for (i in 0 until 20) {
                            val timestamp = System.currentTimeMillis() - (i * 24 * 60 * 60 * 1000L)

                            val log = ExerciseLog(
                                exerciseId = exerciseIdTwo,
                                timestamp = timestamp,
                                record = mutableListOf(Pair(Random.nextFloat() * 10 + 10, Random.nextFloat() * 30 + 10))
                            )
                            logsRun.add(log)
                        }

                        for (log in logsBench) {
                            exerciseLogDao.upsertExerciseLog(log)
                        }

                        for (log in logsRun) {
                            exerciseLogDao.upsertExerciseLog(log)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ExerciseViewModel", "Error creating sample exercise and logs: ${e.message}", e)
            }
        }
    }
}