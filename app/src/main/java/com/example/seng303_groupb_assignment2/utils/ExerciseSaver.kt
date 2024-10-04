package com.example.seng303_groupb_assignment2.utils

import androidx.compose.runtime.saveable.Saver
import com.example.seng303_groupb_assignment2.entities.Exercise
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * I need to implement this custom saveable because rememberSaveable does not work for complex data like an Exercise class
 */
val exerciseSaver = Saver<Exercise?, String>(
    save = { exercise ->
        exercise?.let {
            Json.encodeToString(it)
        } ?: ""
    },
    restore = { jsonString ->
        if (jsonString.isNotEmpty()) {
            Json.decodeFromString<Exercise>(jsonString)
        } else {
            null
        }
    }
)