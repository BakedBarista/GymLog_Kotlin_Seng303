package com.example.seng303_groupb_assignment2.screens

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text

import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.example.seng303_groupb_assignment2.R
import com.example.seng303_groupb_assignment2.entities.WorkoutWithExercises
import com.example.seng303_groupb_assignment2.viewmodels.RunWorkoutViewModel

@Composable
fun RunWorkout(
    navController: NavController,
    workoutWithExercises: WorkoutWithExercises,
    viewModel: RunWorkoutViewModel
) {
    workoutWithExercises.let { workout ->
        LaunchedEffect(workout) {
            viewModel.initialize(workout)
        }

        val currentExercise = workoutWithExercises.exercises[viewModel.currentExerciseIndex]
        Log.d("RunWorkout", "Current Exercise: $currentExercise")

        LaunchedEffect(viewModel.isPlaying) {
            if (viewModel.isPlaying) {
                viewModel.currentReps = 0
                while (viewModel.isPlaying && viewModel.currentReps < viewModel.totalReps) {
                    kotlinx.coroutines.delay(1000L) // 1-second delay simulating a timer
                    viewModel.incrementReps()
                }
                if (viewModel.currentReps >= viewModel.totalReps) {
                    viewModel.isPlaying = false
                }
            }
        }

        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        if (isLandscape) {
            // Only show timer and control buttons in landscape mode
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Timer display
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.circle_arrow),
                        contentDescription = "Circle Background",
                        modifier = Modifier.fillMaxSize()
                    )
                    // Overlay text for current reps
                    Text(
                        text = "${viewModel.currentReps} / ${viewModel.totalReps}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }

                // Control buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    IconButton(onClick = { viewModel.previousSet() }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_skip_previous_24),
                            contentDescription = "Previous"
                        )
                    }

                    if (viewModel.isPlaying) {
                        IconButton(onClick = { viewModel.isPlaying = false }) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_pause_24),
                                contentDescription = "Pause"
                            )
                        }
                    } else {
                        IconButton(onClick = { viewModel.isPlaying = true }) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_play_arrow_24),
                                contentDescription = "Play"
                            )
                        }
                    }

                    IconButton(onClick = { viewModel.nextSet() }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_skip_next_24),
                            contentDescription = "Next"
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // Exercise name display
                Text(
                    text = "Exercise: ${currentExercise.name}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Header for the table
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text(text = "Weight", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = "Rep Goal", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = "Actual", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Exercise details
                LazyColumn(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)) {
                    items(currentExercise.measurement1.values.size) { index ->
                        val weight = currentExercise.measurement2.values.getOrNull(index) ?: 0
                        val repGoal = currentExercise.measurement1.values[index]
                        val actual = viewModel.actualRepsList.getOrNull(index) ?: 0

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Text(text = "$weight kg", fontSize = 18.sp)
                            Text(text = "$repGoal reps", fontSize = 18.sp)
                            Text(
                                text = "$actual reps",
                                fontSize = 18.sp,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Current rep display
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.circle_arrow),
                        contentDescription = "Circle Background",
                        modifier = Modifier.fillMaxSize()
                    )

                    // Overlay text
                    Text(
                        text = "${viewModel.currentReps} / ${viewModel.totalReps}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }

                // Control buttons (Start and Pause)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    IconButton(onClick = { viewModel.previousSet() }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_skip_previous_24),
                            contentDescription = "Previous"
                        )
                    }

                    if (viewModel.isPlaying) {
                        IconButton(onClick = { viewModel.isPlaying = false }) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_pause_24),
                                contentDescription = "Pause"
                            )
                        }
                    } else {
                        IconButton(onClick = { viewModel.isPlaying = true }) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_play_arrow_24),
                                contentDescription = "Play"
                            )
                        }
                    }

                    IconButton(onClick = { viewModel.nextSet() }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_skip_next_24),
                            contentDescription = "Next"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Navigation buttons for exercises
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (viewModel.currentExerciseIndex > 0) {
                        Button(onClick = { viewModel.previousExercise() }) {
                            Text("Previous Exercise")
                        }
                    }

                    if (viewModel.currentExerciseIndex < workoutWithExercises.exercises.size - 1) {
                        Button(onClick = { viewModel.nextExercise() }) {
                            Text("Next Exercise")
                        }
                    }
                }
            }
        }
    }
}

