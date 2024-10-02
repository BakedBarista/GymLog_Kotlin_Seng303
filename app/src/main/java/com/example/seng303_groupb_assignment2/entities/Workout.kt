package com.example.seng303_groupb_assignment2.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Workout(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val schedule: String? // Not sure what data type to put here for the schedule, keeping it as a string for now
)
