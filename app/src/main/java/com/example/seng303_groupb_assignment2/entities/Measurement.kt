package com.example.seng303_groupb_assignment2.entities

import kotlinx.serialization.Serializable

@Serializable
data class Measurement (
    var type: String,
    var values: List<Float>,
)