package com.example.seng303_groupb_assignment2.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.seng303_groupb_assignment2.entities.Workout
import com.example.seng303_groupb_assignment2.entities.WorkoutExerciseCrossRef
import com.example.seng303_groupb_assignment2.entities.WorkoutWithExercises

@Dao
interface WorkoutDao {
    @Upsert
    suspend fun upsertWorkout(workout: Workout): Long

    @Upsert
    suspend fun upsertWorkoutExerciseCrossRef(crossRef: WorkoutExerciseCrossRef)

    @Transaction
    @Query("SELECT * FROM Workout WHERE id = :workoutId")
    suspend fun getWorkoutWithExercises(workoutId: Long): WorkoutWithExercises
}