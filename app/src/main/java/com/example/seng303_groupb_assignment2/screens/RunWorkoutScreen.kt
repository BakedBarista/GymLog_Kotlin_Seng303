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
import androidx.compose.runtime.livedata.observeAsState
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
import com.example.seng303_groupb_assignment2.entities.WorkoutWithExercises
import com.example.seng303_groupb_assignment2.viewmodels.RunWorkoutViewModel

@Composable
fun RunWorkout(
    navController: NavController,
    workoutWithExercises: WorkoutWithExercises,
    viewModel: RunWorkoutViewModel
) {
    val workoutId = navController.currentBackStackEntry?.savedStateHandle?.get<Int>("workoutId")
    workoutId?.let {
        LaunchedEffect(it) {
            viewModel.loadWorkoutWithExercises(it) // Make sure to create this function in your ViewModel
        }
    }
    val workoutWithExercises by viewModel.workoutWithExercises.observeAsState()
    workoutWithExercises ?.let { workout ->
        LaunchedEffect(workout) {
            viewModel.initialize(workout)
        }

        val currentExercise = workoutWithExercises!!.exercises[viewModel.currentExerciseIndex]

        LaunchedEffect(viewModel.isPlaying) {
            if (viewModel.isPlaying) {
                viewModel.currentReps = 0
                while (viewModel.isPlaying && viewModel.currentReps < viewModel.totalReps) {
                    kotlinx.coroutines.delay(1000L) // 1-second delay simulating a timer
                    viewModel.currentReps++ // Count up each second
                }
                if (viewModel.currentReps >= viewModel.totalReps) {
                    viewModel.isPlaying = false
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Exercise: ${currentExercise.name}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(text = "Weight", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Text(text = "Rep Goal", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Text(text = "Actual", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                currentExercise.reps?.let { reps ->
                    items(reps.size) { index ->
                        val weight = currentExercise.measurement1
                        val repGoal = reps[index]
                        val actual = viewModel.actualReps.getOrNull(index) ?: 0

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Text(text = "$weight kg", fontSize = 16.sp)
                            Text(text = "$repGoal reps", fontSize = 16.sp)

                            var actualRepsInput by remember { mutableStateOf(actual.toString()) }

                            androidx.compose.material3.TextField(
                                value = actualRepsInput,
                                onValueChange = {
                                    actualRepsInput = it
                                    // Ensure we only update with valid numbers
                                    val newActual = it.toIntOrNull() ?: 0
                                    viewModel.updateActualReps(index, newActual)
                                },
                                modifier = Modifier
                                    .height(20.dp)
                                    .width(50.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Current Rep: ${viewModel.currentReps} / ${viewModel.totalReps}",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { viewModel.isPlaying = true }) { Text("Start") }
                Button(onClick = { viewModel.isPlaying = false }) { Text("Pause") }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (viewModel.currentExerciseIndex > 0) {
                    Button(onClick = {
                        viewModel.previousExercise(workoutWithExercises!!)
                    }) {
                        Text("Previous Exercise")
                    }
                }

                if (viewModel.currentExerciseIndex < workoutWithExercises!!.exercises.size - 1) {
                    Button(onClick = {
                        viewModel.nextExercise(workoutWithExercises!!)
                    }) {
                        Text("Next Exercise")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (viewModel.currentSetIndex > 0) {
                    Button(onClick = {
                        viewModel.previousSet()
                    }) {
                        Text("Previous Set")
                    }
                }

                if (viewModel.currentSetIndex < (currentExercise.reps?.size ?: 1) - 1) {
                    Button(onClick = {
                        viewModel.nextSet()
                    }) {
                        Text("Next Set")
                    }
                }
            }
        }
    } ?: run {
        Text(text = "Loading...")
    }
}
