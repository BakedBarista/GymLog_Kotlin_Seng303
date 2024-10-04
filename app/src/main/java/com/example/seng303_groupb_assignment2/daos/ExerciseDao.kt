package com.example.seng303_groupb_assignment2.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.seng303_groupb_assignment2.entities.Exercise
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    // Updates an exercise if the id exists, otherwise insert a new exercise
    @Upsert
    suspend fun upsertExercise(exercise: Exercise) : Long

    @Delete
    suspend fun deleteExercise(exercise: Exercise)

    @Transaction
    @Query("SELECT * FROM Exercise ORDER BY name ASC")
    fun getAllExercises(): Flow<List<Exercise>>
}