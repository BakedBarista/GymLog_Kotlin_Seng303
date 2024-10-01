package com.example.seng303_groupb_assignment2.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.seng303_groupb_assignment2.entities.Exercise
import com.example.seng303_groupb_assignment2.entities.ExerciseWithWorkouts
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: Exercise)

    @Transaction
    @Query("SELECT * FROM Exercise WHERE id = :exerciseId")
    suspend fun getExerciseWithWorkouts(exerciseId: Long): ExerciseWithWorkouts

    @Transaction
    @Query("SELECT * FROM Exercise")
    suspend fun getAllExercisesWithWorkouts(): List<ExerciseWithWorkouts>

    @Transaction
    @Query("SELECT * FROM Exercise ORDER BY name ASC")
    fun getAllExercises(): Flow<List<Exercise>>
}