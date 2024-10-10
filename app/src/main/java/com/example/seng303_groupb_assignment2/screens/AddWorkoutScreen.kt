package com.example.seng303_groupb_assignment2.screens

import ExerciseModalViewModel
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.seng303_groupb_assignment2.R
import com.example.seng303_groupb_assignment2.entities.Exercise
import com.example.seng303_groupb_assignment2.entities.Workout
import com.example.seng303_groupb_assignment2.enums.ChartOption
import com.example.seng303_groupb_assignment2.enums.Days
import com.example.seng303_groupb_assignment2.enums.Measurement
import com.example.seng303_groupb_assignment2.notifications.NotificationManager
import com.example.seng303_groupb_assignment2.viewmodels.ExerciseViewModel
import com.example.seng303_groupb_assignment2.viewmodels.ManageWorkoutViewModel
import com.example.seng303_groupb_assignment2.viewmodels.PreferenceViewModel
import com.example.seng303_groupb_assignment2.viewmodels.WorkoutViewModel
import org.koin.androidx.compose.getViewModel
import kotlin.math.roundToInt

@Composable
fun AddWorkout(
    navController: NavController,
    manageViewModel: ManageWorkoutViewModel,
    workoutViewModel: WorkoutViewModel,
    exerciseViewModel: ExerciseViewModel,
    preferenceViewModel: PreferenceViewModel
) {
    var manageExerciseModalOpen by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val exercises by exerciseViewModel.getExercisesByNameOrEmpty(searchQuery).observeAsState(emptyList())


    if (manageExerciseModalOpen) {
        ManageExerciseModal(
            closeModal = { manageExerciseModalOpen = false },
            submitModal = { name, restTime, measurement -> manageViewModel.addExercise(name, restTime, measurement) },
            exercises = exercises,
            searchQueryChanged = { searchQuery = it },
            onExerciseSelected = { exercise ->
                manageViewModel.addExercise(exercise.name, exercise.restTime, exercise.measurement)
                manageExerciseModalOpen = false
            }
        )
    }

    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    if (isPortrait) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                WorkoutNameTextBox(
                    name = manageViewModel.name,
                    updateName = { manageViewModel.updateName(it) },
                    isError = { !manageViewModel.validName() }
                )
            }

            item {
                DescriptionTextBox(
                    description = manageViewModel.description,
                    updateDescription = { manageViewModel.updateDescription(it) },
                )
                Spacer(modifier = Modifier.padding(15.dp))
            }

            item {
                AddExerciseRow(modifier = Modifier.fillMaxWidth(0.8f),
                        openAddExerciseModal = { manageExerciseModalOpen = true })
                Spacer(modifier = Modifier.padding(10.dp))
            }

            item {
                DisplayExerciseList(manageViewModel)
                Spacer(modifier = Modifier.padding(10.dp))
            }

            item {
                EditableScheduleInformation(
                    schedule = manageViewModel.schedule,
                    toggleDay = { day: Days -> manageViewModel.toggleDay(day) }
                )
                Spacer(modifier = Modifier.padding(5.dp))
            }

            item {
                CancelAndSaveRow(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    cancel = { navController.navigate("Home") },
                    manageViewModel = manageViewModel,
                    workoutViewModel = workoutViewModel,
                    exerciseViewModel = exerciseViewModel,
                    navController = navController,
                    preferenceViewModel = preferenceViewModel
                )
            }
        }
    } else {
        Row(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)) {
            Column(
                Modifier
                    .fillMaxWidth(0.5f)
                    .fillMaxHeight(), verticalArrangement = Arrangement.Center) {
                WorkoutNameTextBox(
                    name = manageViewModel.name,
                    updateName = { manageViewModel.updateName(it) },
                    isError = { !manageViewModel.validName() }
                )
                DescriptionTextBox(
                    description = manageViewModel.description,
                    updateDescription = { manageViewModel.updateDescription(it) },
                )
            }
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                AddExerciseRow(modifier = Modifier.fillMaxWidth(),
                    openAddExerciseModal = { manageExerciseModalOpen = true })
                Spacer(modifier = Modifier.padding(5.dp))
                DisplayExerciseList(manageViewModel)
                Spacer(modifier = Modifier.padding(5.dp))
                EditableScheduleInformation(
                    schedule = manageViewModel.schedule,
                    toggleDay = { day: Days -> manageViewModel.toggleDay(day) }
                )
                Spacer(modifier = Modifier.padding(5.dp))
                CancelAndSaveRow(
                    modifier = Modifier.fillMaxWidth(0.95f),
                    cancel = { navController.navigate("Home") },
                    manageViewModel = manageViewModel,
                    workoutViewModel = workoutViewModel,
                    exerciseViewModel = exerciseViewModel,
                    navController = navController,
                    preferenceViewModel = preferenceViewModel
                )
            }
        }
    }
}

@Composable
private fun WorkoutNameTextBox(
    name: String,
    updateName: (String) -> Unit,
    isError: () -> Boolean
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        TextField(
            value = name,
            onValueChange = { updateName(it) },
            textStyle = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.primaryContainer),
            isError = isError(),
            label = { Text(context.getString(R.string.workout_name_label)) },
        )
    }
}

@Composable
private fun DescriptionTextBox(
    description: String,
    updateDescription: (String) -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        TextField(
            value = description,
            onValueChange = { updateDescription(it) },
            textStyle = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.primaryContainer),
            label = { Text(context.getString(R.string.description_label)) },
        )
    }
}

@Composable
private fun AddExerciseRow (
    modifier: Modifier,
    openAddExerciseModal: () -> Unit
) {
    val context = LocalContext.current

    Row(modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Text(text = context.getString(R.string.exercises_title), style = MaterialTheme.typography.titleLarge)
        IconButton(onClick = { openAddExerciseModal() }) {
            Icon(
                painter = painterResource(id = R.drawable.plus),
                contentDescription = context.getString(R.string.plus)
            )
        }
    }
}

@Composable
private fun DisplayExerciseList (
    viewModel: ManageWorkoutViewModel
) {
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val height = if (isPortrait) 210.dp else 180.dp

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(height),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        viewModel.exercises.forEachIndexed { index, exercise ->
            item {
                DisplayExerciseCard(
                    startIndex = index,
                    viewModel = viewModel,
                    exercise = exercise,
                    delete = { viewModel.deleteExercise(index) }
                )
            }
        }
    }
}

@Composable
private fun DisplayExerciseCard(
    startIndex: Int,
    viewModel: ManageWorkoutViewModel,
    exerciseViewModel: ExerciseViewModel = getViewModel(),
    exercise: Exercise,
    delete: () -> Unit,
) {
    val context = LocalContext.current
    var manageExerciseModalOpen by rememberSaveable { mutableStateOf(false) }
    val exerciseModel: ExerciseModalViewModel = viewModel()
    val itemHeight = 100
    val spacing = 10

    var searchQuery by rememberSaveable { mutableStateOf("") }
    val exercises by exerciseViewModel.getExercisesByNameOrEmpty(searchQuery).observeAsState(emptyList())
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var selectedOption by rememberSaveable { mutableStateOf<ChartOption?>(null) }

    if (manageExerciseModalOpen) {
        ManageExerciseModal(
            closeModal = { manageExerciseModalOpen = false },
            submitModal = { name, restTime, measurement -> viewModel.addExercise(name, restTime, measurement) },
            exercises = exercises,
            searchQueryChanged = { searchQuery = it },
            onExerciseSelected = { exercise ->
                exerciseModel.updateExerciseName(exercise.name)
                exerciseModel.updateRestTime(exercise.restTime?.toString() ?: "")
                selectedOption = when (exercise.measurement) {
                    Measurement.REPS_WEIGHT -> ChartOption.MaxWeight
                    Measurement.DISTANCE_TIME -> ChartOption.MaxDistance
                }
                searchQuery = ""
                showDialog = false
            }
        )
    }

    var offsetY by remember { mutableFloatStateOf(0f) }
    var dragging by remember { mutableStateOf(false) }
    Card(modifier = Modifier
        .offset(y = offsetY.dp)
    ) {
        Row(modifier = Modifier
            .fillMaxWidth(0.9f)
            .fillMaxHeight()
            .height(itemHeight.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(28.dp))
            Text(text = exercise.name,
                style = MaterialTheme.typography.bodyLarge)
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End) {
                IconButton(onClick = { delete() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.delete),
                        contentDescription = context.getString(R.string.delete)
                    )
                }
                Spacer(modifier = Modifier.width(25.dp))
                Icon(
                    modifier = Modifier.pointerInput(key1 = viewModel.exercises) {
                        detectDragGestures (
                            onDragStart = {
                                dragging = true
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                offsetY += dragAmount.y * 0.3f
                            },
                            onDragEnd = {
                                dragging = false
                                if (offsetY.roundToInt() != 0) {
                                    val endIndex = (startIndex + (offsetY.roundToInt() / (itemHeight + spacing)))
                                        .coerceIn(0, viewModel.exercises.size - 1)
                                    viewModel.moveExercise(startIndex, endIndex)
                                }

                                offsetY = 0f
                            },
                            onDragCancel = {
                                dragging = false
                                offsetY = 0f
                            },
                        )
                    },
                    painter = painterResource(id = R.drawable.reorder),
                    contentDescription = context.getString(R.string.reorder)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
        }
    }
    Spacer(modifier = Modifier.height(spacing.dp))
}

@Composable
private fun CancelAndSaveRow (
    modifier: Modifier,
    cancel: () -> Unit,
    manageViewModel: ManageWorkoutViewModel,
    workoutViewModel: WorkoutViewModel,
    exerciseViewModel: ExerciseViewModel,
    navController: NavController,
    preferenceViewModel: PreferenceViewModel

) {
    val context = LocalContext.current

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End
    ) {
        val buttonColors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )

        Button(
            onClick = { cancel() },
            colors = buttonColors,
            shape = RectangleShape
        ) {
            Text(text = context.getString(R.string.cancel), style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            onClick = {
                if (manageViewModel.validName()) {
                    val workout = Workout(
                        name = manageViewModel.name,
                        description = manageViewModel.description,
                        schedule = manageViewModel.schedule
                    )
                    workoutViewModel.addWorkout(workout) { workoutId ->
                        manageViewModel.exercises.forEach {
                            exerciseViewModel.addExercise(workoutId, it)
                        }
                    }

                    if (manageViewModel.schedule.isNotEmpty()) {
                        val notificationHandler = NotificationManager(context, preferenceViewModel.preferenceStorage)
                        val nextDayFormatted = getNextDay(manageViewModel.schedule)
                        notificationHandler.sendNewWorkoutNotification(
                            nextDayFormatted,
                            workout.name
                        )
                    }


                    navController.navigate("SelectWorkout")
                }
            },
            colors = buttonColors,
            shape = RectangleShape
        ) {
            Text(text = context.getString(R.string.save), style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun ManageExerciseModal(
    exerciseModel: ExerciseModalViewModel = viewModel(),
    closeModal: () -> Unit,
    submitModal: (String, Int?, Measurement) -> Unit,
    exercises: List<Exercise>,
    searchQueryChanged: (String) -> Unit,
    onExerciseSelected: (Exercise) -> Unit
) {
    val measurements = Measurement.entries.toTypedArray()
    val context = LocalContext.current
    var open by rememberSaveable { mutableStateOf(false) }
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }

    Dialog(onDismissRequest = {
        closeModal()
        exerciseModel.clearSavedInfo()
    }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RectangleShape)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
                item {
                    Text(
                        text = context.getString(R.string.exercises_modal_title),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    TextField(
                        value = exerciseModel.exerciseName,
                        onValueChange = {
                            exerciseModel.updateExerciseName(it)
                            searchQueryChanged(it) },
                        label = { Text(context.getString(R.string.exercise_name_label)) },
                        isError = !exerciseModel.validExerciseName(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                item {
                    if (exerciseModel.exerciseName.isNotEmpty() && exercises.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 200.dp)
                        ) {
                            LazyColumn {
                                items(exercises) { exercise ->
                                    Text(
                                        text = exercise.name,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                onExerciseSelected(exercise)
                                            }
                                            .padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    TextField(
                        value = exerciseModel.restTime,
                        onValueChange = { exerciseModel.updateRestTime(it) },
                        label = { Text(context.getString(R.string.rest_time_label)) },
                        isError = !exerciseModel.validRestTime(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.inverseOnSurface, shape = MaterialTheme.shapes.small)
                            .clickable { open = true }
                            .padding(16.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = measurements[selectedIndex].label,
                                modifier = Modifier.weight(1f),
                            )
                            Icon(
                                painter = painterResource(id = R.drawable.dropdown),
                                contentDescription = "Measurements"
                            )
                        }
                        DropdownMenu(
                            expanded = open,
                            onDismissRequest = { open = false }
                        ) {
                            measurements.forEachIndexed { index, measurement ->
                                DropdownMenuItem(
                                    text = { Text(measurement.label) },
                                    onClick = {
                                        selectedIndex = index
                                        open = false
                                        exerciseModel.updateMeasurement(measurement)
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    val buttonColors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth(0.9f)) {
                        Button(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            colors = buttonColors,
                            shape = RectangleShape,
                            onClick = {
                                closeModal()
                                exerciseModel.clearSavedInfo()
                            }
                        ) {
                            Text(context.getString(R.string.cancel), style = MaterialTheme.typography.bodyLarge)
                        }
                        Button(
                            colors = buttonColors,
                            shape = RectangleShape,
                            onClick = {
                                if (exerciseModel.validRestTime() && exerciseModel.validExerciseName()) {
                                    var restTime: Int? = null
                                    if (exerciseModel.restTime.isNotBlank()) {
                                        restTime = exerciseModel.restTime.toInt()
                                    }

                                    submitModal(
                                        exerciseModel.exerciseName,
                                        restTime,
                                        exerciseModel.measurement
                                    )

                                    closeModal()
                                    exerciseModel.clearSavedInfo()
                                }
                            }
                        ) {
                            Text(context.getString(R.string.add), style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun EditableScheduleInformation(
    schedule: List<Days>,
    toggleDay: (Days) -> Unit
) {
    val context = LocalContext.current

    val abbreviationEnumPair: List<Pair<String, Days>> = remember {
        listOf(
            Pair(context.getString(R.string.sunday_abbreviation), Days.SUNDAY),
            Pair(context.getString(R.string.monday_abbreviation), Days.MONDAY),
            Pair(context.getString(R.string.tuesday_abbreviation), Days.TUESDAY),
            Pair(context.getString(R.string.wednesday_abbreviation), Days.WEDNESDAY),
            Pair(context.getString(R.string.thursday_abbreviation), Days.THURSDAY),
            Pair(context.getString(R.string.friday_abbreviation), Days.FRIDAY),
            Pair(context.getString(R.string.saturday_abbreviation), Days.SATURDAY)
        )
    }
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
                    .background(color = backgroundColour, shape = MaterialTheme.shapes.small)
                    .clickable(onClick = { toggleDay(abbreviationEnumPair[index].second) }),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = abbreviationEnumPair[index].first,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColour
                )
            }
        }
    }
}

private fun getNextDay(schedule: List<Days>): String {
    val currentOrdinal = Days.getCurrentDay().ordinal
    val nextDays = (currentOrdinal + 1 until Days.entries.size)
        .map { Days.entries[it % Days.entries.size] }
        .plus(Days.entries.take(currentOrdinal + 1))
    return nextDays
        .firstOrNull { it in schedule }
        .toString()
        .lowercase()
        .replaceFirstChar { it.uppercaseChar() }
}