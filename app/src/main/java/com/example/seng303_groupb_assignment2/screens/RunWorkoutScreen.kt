package com.example.seng303_groupb_assignment2.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavController
import com.example.seng303_groupb_assignment2.viewmodels.WorkoutViewModel

@Composable
fun RunWorkout(
    navController: NavController,
    workoutViewModel: WorkoutViewModel
) {
    val workouts by workoutViewModel.allWorkouts.observeAsState(emptyList())

    // just here for testing
    Column {
        Text(text = "Run Workouts")
        workouts.forEach { workout ->
            Text(text = workout.name)
        }
    }
}