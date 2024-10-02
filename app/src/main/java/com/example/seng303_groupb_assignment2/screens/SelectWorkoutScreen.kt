package com.example.seng303_groupb_assignment2.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.seng303_groupb_assignment2.R
import com.example.seng303_groupb_assignment2.entities.Workout
import com.example.seng303_groupb_assignment2.entities.WorkoutWithExercises
import com.example.seng303_groupb_assignment2.enums.Days
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
                onEditWorkout = { workout ->
                    viewModel.editWorkout(workout)
                },
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
    onEditWorkout: (Workout) -> Unit,
    onDeleteWorkout: () -> Unit,
    onExpandWorkout: () -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var showEditDialog by rememberSaveable { mutableStateOf(false) }

    if (showEditDialog) {
        EditWorkoutDialog(
            workout = workoutWithExercises.workout,
            onDismiss = { showEditDialog = false},
            onSave = { updatedWorkout ->
                onEditWorkout(updatedWorkout)
                showEditDialog = false
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = workoutWithExercises.workout.name,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = workoutWithExercises.workout.description,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                IconButton(onClick = onStartWorkout) {
                    Icon(painter = painterResource(id = R.drawable.play_arrow), contentDescription = stringResource(R.string.start_workout))
                }
                IconButton(onClick = { showEditDialog = true }) {
                    Icon(painter = painterResource(id = R.drawable.edit), contentDescription = stringResource(R.string.edit_workout))
                }
                IconButton(onClick = onDeleteWorkout) {
                    Icon(painter = painterResource(id = R.drawable.delete), contentDescription = stringResource(R.string.delete_workout))
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        painter = painterResource(id = if (expanded) R.drawable.arrow_drop_up else R.drawable.arrow_drop_down),
                        contentDescription = if (expanded) stringResource(R.string.collapse) else stringResource(R.string.collapse)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            ScheduleInformation(workoutWithExercises.workout.schedule)

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

@Composable
fun ScheduleInformation(schedule: List<Days>) {
    val daysAbbreviations = listOf("S", "M", "T", "W", "T", "F", "S")
    val days = Days.entries.toTypedArray()

    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        days.forEachIndexed { index, day ->
            val isScheduled = schedule.contains(day)
            val backgroundColour = if (isScheduled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
            val textColour = if (isScheduled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .height(24.dp)
                    .width(24.dp)
                    .background(color = backgroundColour, shape = MaterialTheme.shapes.small),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = daysAbbreviations[index],
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColour
                )
            }
        }
    }
}

@Composable
fun EditWorkoutDialog(
    workout: Workout,
    onDismiss: () -> Unit,
    onSave: (Workout) -> Unit
) {
    var name by rememberSaveable { mutableStateOf(workout.name) }
    var description by rememberSaveable { mutableStateOf(workout.description) }
    var selectedDays by rememberSaveable { mutableStateOf(workout.schedule.toSet()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(workout.copy(name = name, description = description, schedule = selectedDays.toList()))
                }
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
        title = { Text(text = stringResource(R.string.edit_workout_title)) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.workout_name)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.description)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                Text(text = stringResource(R.string.schedule))
                Column {
                    Days.entries.forEach { day ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = selectedDays.contains(day),
                                onCheckedChange = { isChecked ->
                                    selectedDays = if (isChecked) {
                                        selectedDays + day
                                    } else {
                                        selectedDays - day
                                    }
                                }
                            )
                            Text(text = day.name)
                        }
                    }
                }
            }
        }
    )
}