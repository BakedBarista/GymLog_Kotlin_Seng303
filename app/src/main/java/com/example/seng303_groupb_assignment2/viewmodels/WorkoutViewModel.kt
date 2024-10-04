package com.example.seng303_groupb_assignment2.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.seng303_groupb_assignment2.daos.ExerciseDao
import com.example.seng303_groupb_assignment2.daos.WorkoutDao
import com.example.seng303_groupb_assignment2.entities.Exercise
import com.example.seng303_groupb_assignment2.entities.Workout
import com.example.seng303_groupb_assignment2.entities.WorkoutExerciseCrossRef
import com.example.seng303_groupb_assignment2.entities.WorkoutWithExercises
import com.example.seng303_groupb_assignment2.enums.Days
import kotlinx.coroutines.launch

class WorkoutViewModel(
    private val workoutDao: WorkoutDao,
    private val exerciseDao: ExerciseDao
) : ViewModel() {
    val allWorkouts: LiveData<List<WorkoutWithExercises>> = workoutDao.getAllWorkoutsWithExercises().asLiveData()

    init {
        viewModelScope.launch {
            checkAndAddSampleData()
        }
    }

    fun addWorkout(workout: Workout) {
        viewModelScope.launch {
            workoutDao.upsertWorkout(workout)
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

    // TODO DELETE THESE TWO FUNCTIONS WHEN WE NO LONGER NEED SAMPLE DATA
    private suspend fun checkAndAddSampleData() {
        // Check if there are any workouts in the database
        val workouts = workoutDao.getWorkoutCount()
        if (workouts == 0) {
            addSampleData()
        }
    }

    private suspend fun addSampleData() {
        val exercise1 = Exercise(
            name = "Push Ups",
            sets = 3,
            reps = listOf(10, 12, 15),
            restTime = 60
        )

        val exercise2 = Exercise(
            name = "Squats",
            sets = 4,
            reps = listOf(12, 15, 15, 18),
            weight = listOf(20f, 25f, 25f, 30f),
            restTime = 90
        )

        val exercise3 = Exercise(
            name = "Running",
            sets = 1,
            distance = 5.0f,
            time = 30.0f,
            restTime = 0
        )

        val exerciseIds = mutableListOf<Long>()
        exerciseIds.add(exerciseDao.upsertExercise(exercise1))
        exerciseIds.add(exerciseDao.upsertExercise(exercise2))
        exerciseIds.add(exerciseDao.upsertExercise(exercise3))

        val workout = Workout(
            name = "Morning Routine",
            description = "A full-body workout to start the day",
            schedule = listOf(Days.MONDAY, Days.WEDNESDAY, Days.FRIDAY)
        )

        val workoutTwo = Workout(
            name = "Weekend Run",
            description = "Quick weekend run",
            schedule = listOf(Days.SATURDAY)
        )

        val workoutId = workoutDao.upsertWorkout(workout)
        val workoutIdTwo = workoutDao.upsertWorkout(workoutTwo)

        exerciseIds.forEach { exerciseId ->
            workoutDao.upsertWorkoutExerciseCrossRef(
                WorkoutExerciseCrossRef(workoutId = workoutId, exerciseId = exerciseId)
            )
        }

        workoutDao.upsertWorkoutExerciseCrossRef(
            WorkoutExerciseCrossRef(workoutId = workoutIdTwo, exerciseId = exerciseIds[2])
        )
    }
}