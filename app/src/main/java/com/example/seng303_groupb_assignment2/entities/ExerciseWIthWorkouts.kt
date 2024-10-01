package com.example.seng303_groupb_assignment2.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class ExerciseWithWorkouts(
    @Embedded val exercise: Exercise,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = WorkoutExerciseCrossRef::class,
            parentColumn = "exerciseId",
            entityColumn = "workoutId"
        )
    )
    val workouts: List<Workout>
)