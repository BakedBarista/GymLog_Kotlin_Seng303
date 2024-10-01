package com.example.seng303_groupb_assignment2.daos

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.example.seng303_groupb_assignment2.entities.Exercise
import com.example.seng303_groupb_assignment2.entities.Workout
import com.example.seng303_groupb_assignment2.entities.WorkoutExerciseCrossRef
import com.example.seng303_groupb_assignment2.entities.WorkoutWithExercises

@Dao
interface WorkoutDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: Workout): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutExerciseCrossRef(crossRef: WorkoutExerciseCrossRef)

    @Transaction
    @Query("SELECT * FROM Workout WHERE id = :workoutId")
    suspend fun getWorkoutWithExercises(workoutId: Long): WorkoutWithExercises

    @Transaction
    @Query("SELECT * FROM Workout")
    suspend fun getAllWorkoutsWithExercises(): List<WorkoutWithExercises>
}