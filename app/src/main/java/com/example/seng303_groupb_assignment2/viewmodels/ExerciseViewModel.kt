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
import com.example.seng303_groupb_assignment2.entities.Measurement
import kotlinx.coroutines.launch
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
        return exerciseDao.getExercisesByName("%$name%").asLiveData()
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
            try {
                val count = exerciseDao.getExerciseCount()
                if(count == 0) {
                    val benchPress = Exercise(
                        name = "Bench Press",
                        sets = 4,
                        measurement1 = Measurement("Reps", listOf(10f, 10f, 10f)),
                        measurement2 = Measurement("Weight", listOf(100f, 100f, 100f)),
                        restTime = 90
                    )

                    val exerciseId = exerciseDao.upsertExercise(benchPress)
                    val logs = mutableListOf<ExerciseLog>()

                    for (i in 0 until 365) {
                        val timestamp = System.currentTimeMillis() - (i * 24 * 60 * 60 * 1000L)
                        val randomWeights = List(4) { Random.nextFloat() * 50 + 50 }

                        val log = ExerciseLog(
                            exerciseId = exerciseId,
                            timestamp = timestamp,
                            sets = 4,
                            measurement1 = Measurement("Reps", listOf(10f, 9f, 8f, 6f)),
                            measurement2 = Measurement("Weight", randomWeights)
                        )
                        logs.add(log)
                    }

                    for (log in logs) {
                        exerciseLogDao.upsertExerciseLog(log)
                    }
                }
            } catch (e: Exception) {
                Log.e("ExerciseViewModel", "Error creating sample exercise and logs: ${e.message}", e)
            }
        }
    }
}