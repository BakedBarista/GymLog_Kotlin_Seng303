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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.example.seng303_groupb_assignment2.R
import com.example.seng303_groupb_assignment2.entities.WorkoutWithExercises
import com.example.seng303_groupb_assignment2.viewmodels.RunWorkoutViewModel
import java.util.Locale

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
        val listState = rememberLazyListState()
        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        LaunchedEffect(viewModel.currentSetIndex) {
            listState.animateScrollToItem(viewModel.currentSetIndex)
        }
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
                        contentDescription = stringResource(R.string.circle_background),
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
                            contentDescription = stringResource(R.string.previous)
                        )
                    }

                    if (viewModel.isPlaying) {
                        IconButton(onClick = { viewModel.isPlaying = false }) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_pause_24),
                                contentDescription = stringResource(R.string.pause)
                            )
                        }
                    } else {
                        IconButton(onClick = { viewModel.startTimer() }) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_play_arrow_24),
                                contentDescription = stringResource(R.string.play)
                            )
                        }
                    }

                    IconButton(onClick = { viewModel.nextSet() }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_skip_next_24),
                            contentDescription = stringResource(R.string.next)
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
                    text = stringResource(R.string.exercise_name, currentExercise.name),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Header for the table
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text(text = stringResource(R.string.weight,""), fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = stringResource(R.string.rep_goal,""), fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = stringResource(R.string.actual,""), fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Exercise details
                LazyColumn(state = listState,
                    modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(vertical = 16.dp)) {
                    items(currentExercise.measurement1.values.size) { index ->
                        val weight = currentExercise.measurement2.values.getOrNull(index) ?: 0
                        val repGoal = currentExercise.measurement1.values[index]
                        val actual = viewModel.actualRepsList.getOrNull(index) ?: 0
                        val isCurrentSet = index == viewModel.currentSetIndex
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                                .background(
                                    color = if (isCurrentSet) Color.Yellow else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Text(text = stringResource(R.string.measurement1_label, "$weight"), fontSize = 18.sp)
                            Text(text = stringResource(R.string.measurement2_label, "$repGoal"), fontSize = 18.sp)
                            Text(
                                text = stringResource(R.string.measurement2_label, "$actual"), fontSize = 18.sp,
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
                        contentDescription = stringResource(R.string.circle_background),
                        modifier = Modifier.fillMaxSize()
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Timer text
                        Text(
                            text = String.format(Locale.getDefault(), ":%02d", viewModel.timerSeconds),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Reps count text
                        Text(
                            text = "${viewModel.currentReps} / ${viewModel.totalReps}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Control buttons (Start and Pause)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    IconButton(onClick = { viewModel.previousSet() }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_skip_previous_24),
                            contentDescription = stringResource(R.string.previous)
                        )
                    }

                    if (viewModel.isPlaying) {
                        IconButton(onClick = { viewModel.isPlaying = false }) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_pause_24),
                                contentDescription = stringResource(R.string.pause)
                            )
                        }
                    } else {
                        IconButton(onClick = { viewModel.startTimer() }) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_play_arrow_24),
                                contentDescription = stringResource(R.string.play)
                            )
                        }
                    }

                    IconButton(onClick = { viewModel.nextSet() }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_skip_next_24),
                            contentDescription = stringResource(R.string.next)
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
                            Text(stringResource(R.string.previous_exercise))
                        }
                    }

                    if (viewModel.currentExerciseIndex < workoutWithExercises.exercises.size - 1) {
                        Button(onClick = { viewModel.nextExercise() }) {
                            Text(stringResource(R.string.next_exercise))
                        }
                    }
                }
            }
        }
    }
}
