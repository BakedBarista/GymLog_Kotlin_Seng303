package com.example.seng303_groupb_assignment2.screens

import android.content.res.Configuration
import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
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
import androidx.compose.ui.platform.LocalContext


@Composable
fun SelectWorkout(
    navController: NavController,
    workoutViewModel: WorkoutViewModel = getViewModel(),
    exerciseViewModel: ExerciseViewModel = getViewModel()
) {
    val workouts by workoutViewModel.allWorkouts.observeAsState(initial = emptyList())
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val context = LocalContext.current

    if (isPortrait) {
        // Vertical scroll in portrait mode
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(workouts) { workoutWithExercises ->
                WorkoutItem(
                    workoutWithExercises = workoutWithExercises,
                    isPortrait = true,
                    onStartWorkout = { /* TODO - add a function to navigate to the run workout screen */ },
                    onEditWorkout = { workout ->
                        workoutViewModel.editWorkout(workout)
                    },
                    onDeleteWorkout = { workoutViewModel.deleteWorkout(workoutWithExercises.workout) },
                    onEditExercise = { exercise ->
                        exerciseViewModel.editExercise(exercise)
                    },
                    onDeleteExercise = { /* TODO - implement this */ },
                    onExportWorkout = {
                        workoutViewModel.exportWorkout(
                            context = context, // Pass the required context
                            workoutWithExercises = workoutWithExercises,
                            onSuccess = { filePath ->
                                Toast.makeText(context, "Workout exported to: $filePath", Toast.LENGTH_LONG).show()
                            },
                            onFailure = {
                                Toast.makeText(context, "Failed to export workout", Toast.LENGTH_LONG).show()
                            }
                        )
                    }
                )
            }
        }
    } else {
        // Horizontal scroll in landscape mode
        LazyRow(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(workouts) { workoutWithExercises ->
                WorkoutItem(
                    workoutWithExercises = workoutWithExercises,
                    isPortrait = false,
                    onStartWorkout = { /* TODO - add a function to navigate to the run workout screen */ },
                    onEditWorkout = { workout ->
                        workoutViewModel.editWorkout(workout)
                    },
                    onDeleteWorkout = { workoutViewModel.deleteWorkout(workoutWithExercises.workout) },
                    onEditExercise = { exercise ->
                        exerciseViewModel.editExercise(exercise)
                    },
                    onDeleteExercise = { /* TODO - implement this */ },
                    onExportWorkout = {
                        workoutViewModel.exportWorkout(
                            context = context,
                            workoutWithExercises = workoutWithExercises,
                            onSuccess = { filePath ->
                                Toast.makeText(context, "Workout exported to: $filePath", Toast.LENGTH_LONG).show()
                            },
                            onFailure = {
                                Toast.makeText(context, "Failed to export workout", Toast.LENGTH_LONG).show()
                            }
                        )
                    }
                )
            }
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
    onDeleteExercise: (Exercise) -> Unit,
    onExportWorkout: () -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var showEditDialog by rememberSaveable { mutableStateOf(false) }
    var showDropdownMenu by rememberSaveable { mutableStateOf(false) }

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

    val expandedState = if (isPortrait) expanded else true

    // This is really nasty. I am using this to calculate how much I need to offset the exercise column by
    val headerHeightPx = remember { mutableFloatStateOf(0f) }
    val density = LocalDensity.current

    Card(
        modifier = Modifier
            .let {
                if (isPortrait) {
                    it
                        .fillMaxWidth()
                        .padding(8.dp)
                } else {
                    it
                        .width(300.dp)
                        .fillMaxHeight()
                        .padding(8.dp)
                }
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .onGloballyPositioned { coordinates ->
                        // Getting the header height in pixels
                        headerHeightPx.floatValue = coordinates.size.height.toFloat()
                    }
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
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
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.export_workout))},
                            onClick = {
                                onExportWorkout()
                                showDropdownMenu = false })
                    }
                }
                ScheduleInformation(workoutWithExercises.workout.schedule)
                if (!isPortrait) {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .let {
                        if (isPortrait) {
                            val headerHeightDp = with(density) { headerHeightPx.value.toDp() }
                            it.padding(
                                top = headerHeightDp,
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 0.dp
                            )
                        } else {
                            val headerHeightDp = with(density) { headerHeightPx.value.toDp() }
                            it
                                .padding(
                                    top = headerHeightDp,
                                    start = 16.dp,
                                    end = 16.dp,
                                    bottom = 16.dp
                                )
                                .verticalScroll(rememberScrollState())
                        }
                    }
            ) {
                if (expandedState) {
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
                if (isPortrait) {
                    IconButton(
                        onClick = { expanded = !expanded },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 0.dp),
                    ) {
                        Icon(
                            painter = painterResource(id = if (expanded) R.drawable.expand_less else R.drawable.expand_more),
                            contentDescription = if (expanded) stringResource(R.string.collapse) else stringResource(
                                R.string.expand
                            )
                        )
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

// Have not styled this at all as the information present here will depend on what has been done in other tasks
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
            .padding(vertical = 2.dp)
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.small
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = exercise.name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
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