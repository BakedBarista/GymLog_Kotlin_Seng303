package com.example.seng303_groupb_assignment2.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.seng303_groupb_assignment2.entities.Workout
import com.example.seng303_groupb_assignment2.entities.WorkoutExerciseCrossRef
import com.example.seng303_groupb_assignment2.entities.WorkoutWithExercises
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Upsert
    suspend fun upsertWorkout(workout: Workout): Long

    @Upsert
    suspend fun upsertWorkoutExerciseCrossRef(crossRef: WorkoutExerciseCrossRef)

    @Delete
    suspend fun deleteWorkout(workout: Workout)

    @Transaction
    @Query("SELECT * FROM Workout")
    fun getAllWorkoutsWithExercises(): Flow<List<WorkoutWithExercises>>

    @Transaction
    @Query("SELECT * FROM Workout ORDER BY name ASC")
    fun getAllWorkouts(): Flow<List<Workout>>

    @Transaction
    @Query("SELECT * FROM Workout WHERE id = :workoutId")
    suspend fun getWorkoutWithExercises(workoutId: Long): WorkoutWithExercises

    @Query("SELECT COUNT(*) FROM Workout")
    suspend fun getWorkoutCount(): Int

    @Query("DELETE FROM WorkoutExerciseCrossRef WHERE workoutId = :workoutId AND exerciseId = :exerciseId")
    suspend fun deleteWorkoutExerciseCrossRef(workoutId: Long, exerciseId: Long)
}