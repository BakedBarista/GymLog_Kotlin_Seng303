package com.example.seng303_groupb_assignment2.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.seng303_groupb_assignment2.entities.ExerciseLog
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseLogDao {
    @Upsert
    suspend fun upsertExerciseLog(exerciseLog: ExerciseLog)

    @Query("SELECT * FROM ExerciseLog WHERE exerciseId = :exerciseId ORDER BY timestamp ASC")
    fun getExerciseLogsByExercise(exerciseId: Long): Flow<List<ExerciseLog>>
}