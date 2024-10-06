package com.example.seng303_groupb_assignment2.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.seng303_groupb_assignment2.enums.Days

@Entity
data class Workout(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val schedule: List<Days>
)
