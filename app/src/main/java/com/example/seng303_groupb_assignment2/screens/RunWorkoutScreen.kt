package com.example.seng303_groupb_assignment2.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.seng303_groupb_assignment2.entities.Exercise
import com.example.seng303_groupb_assignment2.entities.WorkoutWithExercises

@Composable
fun RunWorkout(
    navController: NavController,
    workoutWithExercises: WorkoutWithExercises
) {
    val workoutName = workoutWithExercises.workout.name
    val exercises = workoutWithExercises.exercises

    var currentExerciseIndex by remember { mutableStateOf(0) }
    var currentReps by remember { mutableStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }

    // Simulate countdown timer with LaunchedEffect
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (currentReps < (exercises[currentExerciseIndex].reps?.sum() ?: 0)) {
                kotlinx.coroutines.delay(1000L) // 1-second delay simulating a timer
                currentReps++
            }
            isPlaying = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Title
        Text(text = workoutName, fontSize = 24.sp, fontWeight = FontWeight.Bold)

        // Exercise list
        LazyColumn(
            modifier = Modifier.weight(1f).padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(exercises.size) { index ->
                ExerciseRow(exercises[index])
            }
        }

        // Countdown for current exercise
        val currentExercise = exercises[currentExerciseIndex]
        Text(
            text = "Exercise: ${currentExercise.name}",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Reps: $currentReps / ${currentExercise.reps?.sum() ?: 0}",
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Play, Pause, Stop buttons
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { isPlaying = true }) { Text("Play") }
            Button(onClick = { isPlaying = false }) { Text("Pause") }
            Button(onClick = {
                isPlaying = false
                currentReps = 0
            }) { Text("Stop") }
        }
    }
}

@Composable
fun ExerciseRow(exercise: Exercise) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = exercise.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)

        // Display sets, reps, and weight if they are available
        exercise.sets?.let {
            Text(text = "Sets: $it")
        }

        exercise.reps?.let {
            Text(text = "Reps: ${it.joinToString(", ")}")
        }

        exercise.weight?.let {
            Text(text = "Weight: ${it.joinToString(", ")} kg")
        }

        // Display distance or time for cardio exercises
        exercise.distance?.let {
            Text(text = "Distance: ${it} km")
        }

        exercise.time?.let {
            Text(text = "Time: ${it} min")
        }

        // Display rest time
        Text(text = "Rest: ${exercise.restTime} sec")
    }
}
