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
    var reps: List<Int>? = null,
    var actualReps: List<Int>? = null,
    var measurement1: Measurement,
    var measurement2: Measurement,
    var restTime: Int?
) {
    fun getMutableActualReps(): MutableList<Int> {
        return actualReps?.toMutableList() ?: MutableList(sets ?: 0) { 0 }
    }

    fun toExerciseLog(): ExerciseLog {
        return ExerciseLog(
            exerciseId = this.id,
            timestamp = System.currentTimeMillis(),
            sets = this.sets!!,
            measurement1 = this.measurement1,
            measurement2 = this.measurement2
        )
    }
}
