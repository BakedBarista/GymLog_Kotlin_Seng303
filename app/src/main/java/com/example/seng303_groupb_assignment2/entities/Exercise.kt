package com.example.seng303_groupb_assignment2.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Exercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val sets: Int? = null,
    val reps: List<Int>? = null,
    val weight: List<Float>? = null,
    val distance: Float? = null,
    val time: Float? = null,
    val restTime: Int
)
