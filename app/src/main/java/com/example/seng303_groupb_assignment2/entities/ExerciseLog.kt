package com.example.seng303_groupb_assignment2.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Stores the historical data for each exercise
 */
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("exerciseId")]
)
data class ExerciseLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val exerciseId: Long,
    val timestamp: Long,
    val record: MutableList<Pair<Float, Float>> // Reps | Distance / Weight | Time
)
