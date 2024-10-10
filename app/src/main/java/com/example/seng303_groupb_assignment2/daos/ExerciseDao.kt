package com.example.seng303_groupb_assignment2.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.seng303_groupb_assignment2.entities.Exercise
import com.example.seng303_groupb_assignment2.enums.Measurement
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    // Updates an exercise if the id exists, otherwise insert a new exercise
    @Upsert
    suspend fun upsertExercise(exercise: Exercise) : Long

    @Delete
    suspend fun deleteExercise(exercise: Exercise)

    @Query("SELECT COUNT(*) FROM Exercise")
    suspend fun getExerciseCount(): Int

    @Transaction
    @Query("SELECT * FROM Exercise ORDER BY name ASC")
    fun getAllExercises(): Flow<List<Exercise>>

    @Query("SELECT * FROM Exercise WHERE name LIKE :name")
    fun getAllExercisesByName(name: String): Flow<List<Exercise>>

    @Query("SELECT * FROM Exercise WHERE name = :name AND restTime = :restTime AND measurement = :measurementLabel")
    suspend fun getAllMatchingExercises(name: String, restTime: Int, measurementLabel: String): List<Exercise>

}