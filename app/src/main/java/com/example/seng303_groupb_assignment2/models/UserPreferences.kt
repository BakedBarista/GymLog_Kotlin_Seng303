package com.example.seng303_groupb_assignment2.models

data class UserPreferences(
    val darkMode: Boolean = false,
    val metricUnits: Boolean = true,
    val soundOn: Boolean = true
)