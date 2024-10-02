package com.example.seng303_groupb_assignment2.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

// used to load the related data (workouts and exercises together)
data class WorkoutWithExercises(
    @Embedded val workout: Workout,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = WorkoutExerciseCrossRef::class,
            parentColumn = "workoutId",
            entityColumn = "exerciseId")
    )
    val exercises: List<Exercise>
)