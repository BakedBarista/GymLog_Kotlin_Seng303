package com.example.seng303_groupb_assignment2.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
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
import java.time.LocalDate
import java.time.ZoneId
import kotlin.random.Random

class ExerciseViewModel(
    private val exerciseDao: ExerciseDao,
    private val workoutDao: WorkoutDao,
    private val exerciseLogDao: ExerciseLogDao
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

    fun getExercisesByName(name: String): LiveData<List<Exercise>> {
        return exerciseDao.getAllExercisesByName("%$name%").asLiveData()
    }

    fun getExerciseLogsByExercise(exerciseId: Long): LiveData<List<ExerciseLog>> {
        return exerciseLogDao.getExerciseLogsByExerciseId(exerciseId).asLiveData()
    }

    fun addExercise(workoutId: Long, exercise: Exercise) {
        viewModelScope.launch {
            val exerciseId = exerciseDao.upsertExercise(exercise)
            val crossRef = WorkoutExerciseCrossRef(workoutId = workoutId, exerciseId = exerciseId)
            workoutDao.upsertWorkoutExerciseCrossRef(crossRef)
        }
    }

    // TODO DELETE THIS WHEN WE NO LONGER NEED SAMPLE DATA
    fun createSampleExerciseAndLogs() {
        viewModelScope.launch {
            Log.d("DBINIT", "INSERTING WORKOUT")
            try {
                Log.d("DBINIT", "INSERTING WORKOUT")
                val workoutCount = workoutDao.getWorkoutCount()
                val workoutOne : Workout
                val workoutOneId : Long
                if (workoutCount == 0) {
                    workoutOne = Workout(
                        name = "WorkoutOne",
                        description = "Test workout",
                        schedule = listOf(Days.MONDAY, Days.WEDNESDAY, Days.FRIDAY)
                    )
                    Log.d("DBINIT", "INSERT WORKOUT")
                    workoutOneId = workoutDao.upsertWorkout(workoutOne)
                    val count = exerciseDao.getExerciseCount()
                    if (count == 0) {
                        Log.d("DBINIT", "INSERT EXERCISE")
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

                        val noLogs = Exercise(
                            name = "No logs",
                            restTime = 0,
                            measurement = Measurement.DISTANCE_TIME
                        )

                        val exerciseId = exerciseDao.upsertExercise(benchPress)
                        val exerciseIdTwo = exerciseDao.upsertExercise(run)
                        val exerciseIdThree = exerciseDao.upsertExercise(noLogs)

                        val workoutWithExercise =
                            WorkoutExerciseCrossRef(workoutOneId, exerciseId)

                        workoutDao.upsertWorkoutExerciseCrossRef(workoutWithExercise)

                        for (i in 0 until 100) {
                            exerciseDao.upsertExercise(
                                Exercise(
                                    name = "Exercise $i",
                                    restTime = 60,
                                    measurement = Measurement.REPS_WEIGHT
                                )
                            )
                        }

                        val logsBench = mutableListOf<ExerciseLog>()
                        val logsRun = mutableListOf<ExerciseLog>()

                        for (i in 0 until 365) {
                            val date = LocalDate.of(2020, 1, 1).plusDays(i.toLong())
                            val timestamp =
                                date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

                            val log = ExerciseLog(
                                exerciseId = exerciseId,
                                timestamp = timestamp,
                                record = listOf(Pair(3f, Random.nextFloat() * 50 + 50), Pair(3f, Random.nextFloat() * 50 + 50), Pair(3f, Random.nextFloat() * 50 + 50))
                            )
                            logsBench.add(log)
                        }

                        for (i in 0 until 20) {
                            val timestamp = System.currentTimeMillis() - (i * 24 * 60 * 60 * 1000L)
                            val randomDistance = List(1) { Random.nextFloat() * 10 + 10 }

                            val log = ExerciseLog(
                                exerciseId = exerciseIdTwo,
                                timestamp = timestamp,
                                record = listOf(Pair(Random.nextFloat() * 10 + 10, Random.nextFloat() * 30 + 10))
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