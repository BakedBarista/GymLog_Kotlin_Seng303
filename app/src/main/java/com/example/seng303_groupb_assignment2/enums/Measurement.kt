package com.example.seng303_groupb_assignment2.enums

enum class Measurement(val label: String, val unit1: String, val unit2: String, val measurement: List<String>) {
    REPS_WEIGHT("Reps / Weight", "Reps", "Weight", listOf("kg", "lbs")), DISTANCE_TIME("Distance / Time", "Distance", "Time", listOf("km", "mi"))
}