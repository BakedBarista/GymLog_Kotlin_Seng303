package com.example.seng303_groupb_assignment2.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.seng303_groupb_assignment2.enums.Measurement
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class Exercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var name: String,
    var restTime: Int?,
    var measurement: Measurement // distance / time OR weight / reps
)
