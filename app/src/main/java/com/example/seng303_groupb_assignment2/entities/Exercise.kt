package com.example.seng303_groupb_assignment2.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class Exercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var name: String,
    var sets: Int? = null,
    var measurement1: Measurement,
    var measurement2: Measurement,
    var restTime: Int?
)
