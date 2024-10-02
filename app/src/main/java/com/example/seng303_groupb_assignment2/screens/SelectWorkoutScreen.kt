package com.example.seng303_groupb_assignment2.screens

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.navigation.NavController
import com.example.seng303_groupb_assignment2.R
import com.example.seng303_groupb_assignment2.entities.Exercise
import com.example.seng303_groupb_assignment2.entities.Workout
import com.example.seng303_groupb_assignment2.entities.WorkoutWithExercises
import com.example.seng303_groupb_assignment2.enums.Days
import com.example.seng303_groupb_assignment2.viewmodels.ExerciseViewModel
import com.example.seng303_groupb_assignment2.viewmodels.WorkoutViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun SelectWorkout(
    navController: NavController,
    workoutViewModel: WorkoutViewModel = getViewModel(),
    exerciseViewModel: ExerciseViewModel = getViewModel()
) {
    val workouts by workoutViewModel.allWorkouts.observeAsState(initial = emptyList())
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    val columns = if (isPortrait) 1 else 2

    LazyVerticalGrid (
        columns = if (isPortrait) GridCells.Fixed(1) else GridCells.Adaptive(minSize = 300.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(workouts) { workoutWithExercises ->
            WorkoutItem(
                workoutWithExercises = workoutWithExercises,
                isPortrait = isPortrait,
                onStartWorkout = { /* TODO - add a function to navigate to the run workout screen */ },
                onEditWorkout = { workout ->
                    workoutViewModel.editWorkout(workout)
                },
                onDeleteWorkout = { workoutViewModel.deleteWorkout(workoutWithExercises.workout) },
                onEditExercise = { exercise ->
                    exerciseViewModel.editExercise(exercise)
                },
                onDeleteExercise = { /* TODO - implement this */ }
            )
        }
    }
}

@Composable
fun WorkoutItem(
    workoutWithExercises: WorkoutWithExercises,
    isPortrait: Boolean,
    onStartWorkout: () -> Unit,
    onEditWorkout: (Workout) -> Unit,
    onDeleteWorkout: () -> Unit,
    onEditExercise: (Exercise) -> Unit,
    onDeleteExercise: (Exercise) -> Unit
) {
    var isFlipped by rememberSaveable { mutableStateOf(false) }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var showEditDialog by rememberSaveable { mutableStateOf(false) }
    var showDropdownMenu by rememberSaveable { mutableStateOf(false) }

    val frontAlpha by animateFloatAsState(targetValue = if (isFlipped) 0f else 1f)
    val backAlpha by animateFloatAsState(targetValue = if (isFlipped) 1f else 0f)

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
            .let {
                if (isPortrait) it.padding(vertical = 8.dp) else it.fillMaxHeight().padding(8.dp)
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.heightIn(max = 300.dp)) {
            if (isPortrait) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = workoutWithExercises.workout.name,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        IconButton(onClick = onStartWorkout) {
                            Icon(
                                painter = painterResource(id = R.drawable.play_arrow),
                                contentDescription = stringResource(R.string.start_workout)
                            )
                        }
                        IconButton(onClick = { showDropdownMenu = true }) {
                            Icon(
                                painter = painterResource(id = R.drawable.more_vert),
                                contentDescription = stringResource(R.string.more_options)
                            )
                        }
                        DropdownMenu(
                            expanded = showDropdownMenu,
                            onDismissRequest = { showDropdownMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.edit_workout)) },
                                onClick = {
                                    showEditDialog = true
                                    showDropdownMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.delete_workout)) },
                                onClick = {
                                    onDeleteWorkout()
                                    showDropdownMenu = false
                                }
                            )
                        }
                    }

                    ScheduleInformation(workoutWithExercises.workout.schedule)

                    if (expanded) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = workoutWithExercises.workout.description,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        workoutWithExercises.exercises.forEach { exercise ->
                            ExerciseItem(
                                exercise = exercise,
                                onEditExercise = { onEditExercise(exercise) },
                                onDeleteExercise = { onDeleteExercise(exercise) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    IconButton(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Icon(
                            painter = painterResource(id = if (expanded) R.drawable.expand_less else R.drawable.expand_more),
                            contentDescription = if (expanded) stringResource(R.string.collapse) else stringResource(R.string.expand)
                        )
                    }
                }
            } else {
                if (!isFlipped) {
                    Column(
                        modifier = Modifier
                            .graphicsLayer { alpha = frontAlpha }
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = workoutWithExercises.workout.name,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(8.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            IconButton(onClick = onStartWorkout) {
                                Icon(
                                    painter = painterResource(id = R.drawable.play_arrow),
                                    contentDescription = stringResource(R.string.start_workout)
                                )
                            }
                            IconButton(onClick = { showDropdownMenu = true }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.more_vert),
                                    contentDescription = stringResource(R.string.more_options)
                                )
                            }
                            DropdownMenu(
                                expanded = showDropdownMenu,
                                onDismissRequest = { showDropdownMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.edit_workout)) },
                                    onClick = {
                                        showEditDialog = true
                                        showDropdownMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.delete_workout)) },
                                    onClick = {
                                        onDeleteWorkout()
                                        showDropdownMenu = false
                                    }
                                )
                            }
                        }

                        ScheduleInformation(workoutWithExercises.workout.schedule)
                        Spacer(modifier = Modifier.height(8.dp))
                        IconButton(
                            onClick = { isFlipped = !isFlipped },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.flip),
                                contentDescription = if (isFlipped) stringResource(R.string.view_workout) else stringResource(R.string.view_details)
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .graphicsLayer { alpha = backAlpha }
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = workoutWithExercises.workout.description,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        workoutWithExercises.exercises.forEach { exercise ->
                            ExerciseItem(
                                exercise = exercise,
                                onEditExercise = { onEditExercise(exercise) },
                                onDeleteExercise = { onDeleteExercise(exercise) }
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        IconButton(
                            onClick = { isFlipped = !isFlipped },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.flip),
                                contentDescription = if (isFlipped) stringResource(R.string.view_workout) else stringResource(R.string.view_details)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScheduleInformation(schedule: List<Days>) {
    val daysAbbreviations = listOf(
        stringResource(R.string.sunday_abbreviation),
        stringResource(R.string.monday_abbreviation),
        stringResource(R.string.tuesday_abbreviation),
        stringResource(R.string.wednesday_abbreviation),
        stringResource(R.string.thursday_abbreviation),
        stringResource(R.string.friday_abbreviation),
        stringResource(R.string.saturday_abbreviation)
    )
    val days = Days.entries.toTypedArray()
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        days.forEachIndexed { index, day ->
            val isScheduled = schedule.contains(day)
            val backgroundColour = if (isScheduled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
            val textColour = if (isScheduled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .weight(1f)
                    .aspectRatio(1f)
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

@Composable
fun ExerciseItem(
    exercise: Exercise,
    onEditExercise: () -> Unit,
    onDeleteExercise: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = exercise.name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Row {
            IconButton(onClick = onEditExercise) {
                Icon(
                    painter = painterResource(id = R.drawable.edit),
                    contentDescription = stringResource(R.string.edit_exercise)
                )
            }
            IconButton(onClick = onDeleteExercise) {
                Icon(
                    painter = painterResource(id = R.drawable.delete),
                    contentDescription = stringResource(R.string.delete_exercise)
                )
            }
        }
    }
}