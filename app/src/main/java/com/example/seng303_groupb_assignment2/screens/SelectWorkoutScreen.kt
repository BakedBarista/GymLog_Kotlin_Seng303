package com.example.seng303_groupb_assignment2.screens

import ExerciseModalViewModel
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.core.content.FileProvider
import com.example.seng303_groupb_assignment2.enums.Measurement
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.io.File
import java.io.FileOutputStream
import com.example.seng303_groupb_assignment2.enums.UnitType
import com.example.seng303_groupb_assignment2.models.UserPreferences
import com.example.seng303_groupb_assignment2.viewmodels.PreferenceViewModel


@Composable
fun SelectWorkout(
    navController: NavController,
    workoutViewModel: WorkoutViewModel = getViewModel(),
    exerciseViewModel: ExerciseViewModel = getViewModel(),
    preferenceViewModel: PreferenceViewModel = getViewModel()
) {
    val userPreferences by preferenceViewModel.preferences.observeAsState(UserPreferences())
    val isMetric = userPreferences.metricUnits
    val workouts by workoutViewModel.allWorkouts.observeAsState(initial = emptyList())
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val context = LocalContext.current
    var qrCodeBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val modalViewModel: ExerciseModalViewModel = viewModel()
    var currentExerciseId: Long? by rememberSaveable { mutableStateOf(null) }
    var editExerciseModalOpen by rememberSaveable { mutableStateOf(false) }
    if (editExerciseModalOpen) {
        ManageExerciseModal(
            exerciseModel = modalViewModel,
            closeModal = { editExerciseModalOpen = false },
            submitModal = { name, restTime, measurement ->
                if (currentExerciseId != null) {
                    val currentExercise = Exercise(
                        id = currentExerciseId!!,
                        name = name,
                        restTime = restTime,
                        measurement = measurement
                    )
                    exerciseViewModel.editExercise(currentExercise)
                    currentExerciseId = null
                }
            })
    }

    var currentWorkoutId: Long? by rememberSaveable { mutableStateOf(null) }
    var addExerciseModalOpen by rememberSaveable { mutableStateOf(false) }
    if (addExerciseModalOpen) {
        ManageExerciseModal(
            exerciseModel = modalViewModel,
            closeModal = { addExerciseModalOpen = false },
            submitModal = { name, restTime, measurement ->
                if (currentWorkoutId != null) {
                    val newExercise = Exercise(
                        name = name,
                        restTime = restTime,
                        measurement = measurement
                    )
                    exerciseViewModel.addExercise(currentWorkoutId!!, newExercise)
                    currentWorkoutId = null
                }
            })
    }

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
                    onStartWorkout = {  if(workoutWithExercises.exercises.isNotEmpty())
                                        {navController.currentBackStackEntry?.savedStateHandle?.set("workoutId", workoutWithExercises.workout.id)
                                        navController.navigate("Run")} else {
                                            Toast.makeText(context, context.getString(R.string.no_exercises_toast), Toast.LENGTH_LONG).show()
                    }
                                     },
                    onEditWorkout = { workout ->
                        workoutViewModel.editWorkout(workout)
                    },
                    onDeleteWorkout = { workoutViewModel.deleteWorkout(workoutWithExercises.workout) },
                    onAddExercise = {
                        currentWorkoutId = workoutWithExercises.workout.id
                        modalViewModel.updateExerciseName("")
                        modalViewModel.updateMeasurement(Measurement.REPS_WEIGHT)
                        modalViewModel.updateRestTime("")
                        addExerciseModalOpen = true
                    },
                    onEditExercise = { exercise ->
                        modalViewModel.updateExerciseName(exercise.name)
                        modalViewModel.updateRestTime(exercise.restTime.toString())
                        modalViewModel.updateMeasurement(exercise.measurement)
                        currentExerciseId = exercise.id
                        editExerciseModalOpen = true
                    },
                    onDeleteExercise = { exercise ->
                        exerciseViewModel.deleteExercise(exercise)
                    },
                    onExportWorkout = {
                        exportWorkout(
                            context = context,
                            workoutWithExercises = workoutWithExercises,
                            onSuccess = { uri ->
                                // Open the QR code file
                                openFile(context, uri)
                                Toast.makeText(context, context.getString(R.string.qr_exported_toast), Toast.LENGTH_LONG).show()
                            },
                            onFailure = {
                                Toast.makeText(context, context.getString(R.string.workout_exported_failure_toast), Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    onExportWorkoutLog = {
                        workoutViewModel.exportWorkoutLog(
                            context = context,
                            workoutWithExercises = workoutWithExercises,
                            onSuccess = { filePath ->
                                Toast.makeText(context, context.getString(R.string.workout_logs_exported_toast, filePath), Toast.LENGTH_LONG).show()
                            },
                            onFailure = {
                                Toast.makeText(context, context.getString(R.string.workout_log_exported_failure_toast), Toast.LENGTH_LONG).show()
                            },
                            isMetric = isMetric
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
                    onStartWorkout = { if(workoutWithExercises.exercises.isNotEmpty())
                    {navController.currentBackStackEntry?.savedStateHandle?.set("workoutId", workoutWithExercises.workout.id)
                        navController.navigate("Run")} else {
                        Toast.makeText(context, context.getString(R.string.no_exercises_toast), Toast.LENGTH_LONG).show()
                    }},
                    onEditWorkout = { workout ->
                        workoutViewModel.editWorkout(workout)
                    },
                    onDeleteWorkout = { workoutViewModel.deleteWorkout(workoutWithExercises.workout) },
                    onEditExercise = { exercise ->
                        modalViewModel.updateExerciseName(exercise.name)
                        modalViewModel.updateRestTime(exercise.restTime.toString())
                        modalViewModel.updateMeasurement(exercise.measurement)
                        currentExerciseId = exercise.id
                        editExerciseModalOpen = true
                    },
                    onAddExercise = {
                        currentWorkoutId = workoutWithExercises.workout.id
                        modalViewModel.updateExerciseName("")
                        modalViewModel.updateRestTime("")
                        modalViewModel.updateMeasurement(Measurement.REPS_WEIGHT)
                        addExerciseModalOpen = true
                    },
                    onDeleteExercise = { exercise ->
                        exerciseViewModel.deleteExercise(exercise) },
                    onExportWorkout = {

                        exportWorkout(
                            context = context,
                            workoutWithExercises = workoutWithExercises,
                            onSuccess = { uri ->
                                // Open the QR code file
                                openFile(context, uri)
                                Toast.makeText(context, context.getString(R.string.qr_exported_toast), Toast.LENGTH_LONG).show()
                            },
                            onFailure = {
                                Toast.makeText(context, context.getString(R.string.workout_exported_failure_toast), Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    onExportWorkoutLog = {
                        workoutViewModel.exportWorkoutLog(
                            context = context,
                            workoutWithExercises = workoutWithExercises,
                            onSuccess = { filePath ->
                                Toast.makeText(context, context.getString(R.string.workout_logs_exported_toast, filePath), Toast.LENGTH_LONG).show()
                            },
                            onFailure = {
                                Toast.makeText(context, context.getString(R.string.workout_log_exported_failure_toast), Toast.LENGTH_LONG).show()
                            },
                            isMetric = isMetric
                        )
                    }
                )
            }
        }
    }
}
private fun openFile(context: Context, uri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "image/png")
        flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
    }
    context.startActivity(Intent.createChooser(intent, "Open QR Code"))
}
@Composable
fun WorkoutItem(
    workoutWithExercises: WorkoutWithExercises,
    isPortrait: Boolean,
    onStartWorkout: () -> Unit,
    onEditWorkout: (Workout) -> Unit,
    onDeleteWorkout: () -> Unit,
    onAddExercise: () -> Unit,
    onEditExercise: (Exercise) -> Unit,
    onDeleteExercise: (Exercise) -> Unit,
    onExportWorkout: () -> Unit,
    onExportWorkoutLog: () -> Unit
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
                    Box {
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
                                text = { Text(stringResource(id = R.string.export_workout)) },
                                onClick = {
                                    onExportWorkout()
                                    showDropdownMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.export_workout_log)) },
                                onClick = {
                                    onExportWorkoutLog()
                                    showDropdownMenu = false
                                }
                            )
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
                    Row(modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            text = workoutWithExercises.workout.description,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        IconButton(onClick = onAddExercise) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(id = R.drawable.plus),
                                contentDescription = stringResource(R.string.add)
                            )
                        }
                    }
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

fun convertWorkoutToJson(workoutWithExercises: WorkoutWithExercises): String {
    val gson = Gson()
    return gson.toJson(workoutWithExercises)
}

fun generateQRCode(data: String): Bitmap? {
    try {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 200, 200)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bmp
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

fun exportWorkout(
    context: Context,
    workoutWithExercises: WorkoutWithExercises,
    onSuccess: (Uri) -> Unit,
    onFailure: () -> Unit
) {
    // Convert workout to JSON
    val workoutJson = convertWorkoutToJson(workoutWithExercises)

    // Generate QR Code
    val qrCodeBitmap = generateQRCode(workoutJson)

    // Save QR Code to file
    if (qrCodeBitmap != null) {
        try {
            val uri = saveBitmapToFile(context, qrCodeBitmap)
            onSuccess(uri)
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure()
        }
    } else {
        onFailure()
    }
}


fun saveBitmapToFile(context: Context, bitmap: Bitmap?): Uri {
    val file = File(context.cacheDir, "qr_code_${System.currentTimeMillis()}.png")
    if (bitmap != null) {
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
    }
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}

