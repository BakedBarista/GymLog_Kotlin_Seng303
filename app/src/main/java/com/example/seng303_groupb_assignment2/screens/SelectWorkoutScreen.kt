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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.seng303_groupb_assignment2.R
import com.example.seng303_groupb_assignment2.entities.WorkoutWithExercises
import com.example.seng303_groupb_assignment2.viewmodels.WorkoutViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun SelectWorkout(
    navController: NavController,
    viewModel: WorkoutViewModel = getViewModel()
) {
    val workouts by viewModel.allWorkouts.observeAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(workouts) { workoutWithExercises ->
            WorkoutItem(
                workoutWithExercises = workoutWithExercises,
                onStartWorkout = { /* TODO - add a function to navigate to the run workout screen */ },
                onEditWorkout = { /* TODO - add a function to navigate to the edit screen */ },
                onDeleteWorkout = { viewModel.deleteWorkout(workoutWithExercises.workout) },
                onExpandWorkout = { /* Expand card to show details */ }
            )
        }
    }
}

@Composable
fun WorkoutItem(
    workoutWithExercises: WorkoutWithExercises,
    onStartWorkout: () -> Unit,
    onEditWorkout: () -> Unit,
    onDeleteWorkout: () -> Unit,
    onExpandWorkout: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = workoutWithExercises.workout.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onStartWorkout) {
                    Icon(painter = painterResource(id = R.drawable.play_arrow), contentDescription = "Start Workout")
                }
                IconButton(onClick = onEditWorkout) {
                    Icon(painter = painterResource(id = R.drawable.edit), contentDescription = "Edit Workout")
                }
                IconButton(onClick = onDeleteWorkout) {
                    Icon(painter = painterResource(id = R.drawable.delete), contentDescription = "Delete Workout")
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        painter = painterResource(id = if (expanded) R.drawable.arrow_drop_up else R.drawable.arrow_drop_down),
                        contentDescription = if (expanded) "Collapse" else "Expand"
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                workoutWithExercises.exercises.forEach { exercise ->
                    Text(
                        text = "${exercise.name} - Sets: ${exercise.sets}, Reps: ${exercise.reps?.joinToString()}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }
        }
    }
}