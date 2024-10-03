package com.example.seng303_groupb_assignment2.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "preferences")
data class Preference(
    @PrimaryKey val id: Long = 1, // Singleton ID for the only row of preferences
    val darkMode: Boolean = false,
    val metricUnits: Boolean = true,
    val soundOn: Boolean = true
)
