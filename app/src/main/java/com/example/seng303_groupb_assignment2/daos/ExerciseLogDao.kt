package com.example.seng303_groupb_assignment2.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.seng303_groupb_assignment2.entities.ExerciseLog

@Dao
interface ExerciseLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseLog(exerciseLog: ExerciseLog)

    @Query("SELECT * FROM ExerciseLog WHERE exerciseId = :exerciseId ORDER BY timestamp ASC")
    suspend fun getExerciseLogsByExercise(exerciseId: Long): List<ExerciseLog>
}