package com.example.seng303_groupb_assignment2.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.seng303_groupb_assignment2.entities.Workout
import com.example.seng303_groupb_assignment2.entities.WorkoutWithExercises

@Composable
fun RunWorkout(
    navController: NavController,
    workoutWithExercises: WorkoutWithExercises
) {
    var currentExerciseIndex by remember { mutableIntStateOf(0) }
    val currentExercise = workoutWithExercises.exercises[currentExerciseIndex]

    var currentSetIndex by remember { mutableIntStateOf(0) }
    var currentReps by remember { mutableIntStateOf(0) }
    var totalReps by remember { mutableIntStateOf(currentExercise.reps?.getOrNull(currentSetIndex) ?: 0) }
    var isPlaying by remember { mutableStateOf(false) }
    var actualReps = remember { mutableStateOf(currentExercise.reps?.map { 0 }?.toMutableList()) }

    // Simulate countdown timer (for tracking purposes only)
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            currentReps = 0
            while (isPlaying && currentReps < totalReps) {
                kotlinx.coroutines.delay(1000L) // 1-second delay simulating a timer
                currentReps++ // Count up each second
            }
            // Stop when total reps are reached or timer is paused
            if (currentReps >= totalReps) {
                isPlaying = false
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Exercise: ${currentExercise.name}",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            Text(text = "Weight", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Text(text = "Rep Goal", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Text(text = "Actual", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            currentExercise.reps?.let { reps ->
                items(reps.size) { index ->
                    val weight = currentExercise.weight?.getOrNull(index) ?: 0f
                    val repGoal = reps[index]
                    val actual = actualReps.value?.get(index) ?: 0

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {

                        Text(text = "$weight kg", fontSize = 16.sp)

                        Text(text = "$repGoal reps", fontSize = 16.sp)

                        var actualRepsInput by remember { mutableStateOf(actual.toString()) }

                        androidx.compose.material3.TextField(
                            value = actualRepsInput,
                            onValueChange = {
                                actualRepsInput = it
                                actualReps.value?.set(index, it.toIntOrNull() ?: 0)
                            },
                            modifier = Modifier.height(20.dp).width(50.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Timer for tracking reps
        Text(
            text = "Current Rep: $currentReps / $totalReps",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Play, Pause buttons
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { isPlaying = true }) { Text("Start") }
            Button(onClick = { isPlaying = false }) { Text("Pause") }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation buttons to switch between exercises
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (currentExerciseIndex > 0) {
                Button(onClick = {
                    currentExerciseIndex--
                    currentSetIndex = 0
                    currentReps = 0
                    totalReps = workoutWithExercises.exercises[currentExerciseIndex].reps?.getOrNull(currentSetIndex) ?: 0
                }) {
                    Text("Previous Exercise")
                }
            }

            if (currentExerciseIndex < workoutWithExercises.exercises.size - 1) {
                Button(onClick = {
                    currentExerciseIndex++
                    currentSetIndex = 0
                    currentReps = 0
                    totalReps = workoutWithExercises.exercises[currentExerciseIndex].reps?.getOrNull(currentSetIndex) ?: 0
                }) {
                    Text("Next Exercise")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation for sets within the current exercise
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (currentSetIndex > 0) {
                Button(onClick = {
                    currentSetIndex--
                    currentReps = 0
                    totalReps = currentExercise.reps?.getOrNull(currentSetIndex) ?: 0
                }) {
                    Text("Previous Set")
                }
            }

            if (currentSetIndex < (currentExercise.reps?.size ?: 1) - 1) {
                Button(onClick = {
                    currentSetIndex++
                    currentReps = 0
                    totalReps = currentExercise.reps?.getOrNull(currentSetIndex) ?: 0
                }) {
                    Text("Next Set")
                }
            }
        }
    }
}





@Composable
fun RunWorkoutPreview(navController: NavController) {
    // Hardcoded workout and exercises
    val workoutWithExercises = WorkoutWithExercises(
        workout = Workout(id = 1, name = "Lower Body Workout", description = "Lower body exercises", schedule = null),
        exercises = listOf(
            Exercise(
                id = 1,
                name = "Squat",
                sets = 3,
                reps = listOf(10, 12, 15),
                weight = listOf(60f, 65f, 70f),
                restTime = 90
            ),
            Exercise(
                id = 2,
                name = "Leg Press",
                sets = 4,
                reps = listOf(10, 12, 15, 15),
                weight = listOf(100f, 110f, 120f, 130f),
                restTime = 90
            ),
            Exercise(
                id = 3,
                name = "Deadlift",
                sets = 3,
                reps = listOf(8, 10, 10),
                weight = listOf(80f, 85f, 90f),
                restTime = 120
            )
        )
    )

    // Call the actual RunWorkout composable with this hardcoded data
    RunWorkout(navController = navController, workoutWithExercises = workoutWithExercises)
}

