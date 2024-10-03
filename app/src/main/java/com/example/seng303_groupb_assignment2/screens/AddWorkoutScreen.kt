package com.example.seng303_groupb_assignment2.screens

import ExerciseModalViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.seng303_groupb_assignment2.R
import com.example.seng303_groupb_assignment2.entities.Exercise
import com.example.seng303_groupb_assignment2.entities.Measurement
import com.example.seng303_groupb_assignment2.entities.Workout
import com.example.seng303_groupb_assignment2.viewmodels.ExerciseViewModel
import com.example.seng303_groupb_assignment2.viewmodels.ManageWorkoutViewModel
import com.example.seng303_groupb_assignment2.viewmodels.WorkoutViewModel
import kotlinx.coroutines.Deferred

@Composable
fun AddWorkout(
    navController: NavController,
    manageViewModel: ManageWorkoutViewModel,
    workoutViewModel: WorkoutViewModel,
    exerciseViewModel: ExerciseViewModel,
) {
    var manageExerciseModalOpen by rememberSaveable { mutableStateOf(false) }
    ManageExerciseModal(
        modalOpen = manageExerciseModalOpen,
        closeModal = { manageExerciseModalOpen = false },
        submitModal = { name, sets, m1, m2, restTime -> manageViewModel.addExercise(name, sets, m1, m2, restTime) }
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        WorkoutNameTextBox(
            name = manageViewModel.name,
            updateName = { manageViewModel.updateName(it) },
            isError = { !manageViewModel.validName() }
        )

        Spacer(modifier = Modifier.padding(15.dp))

        AddExerciseRow(openAddExerciseModal = { manageExerciseModalOpen = true } )

        Spacer(modifier = Modifier.padding(10.dp))

        DisplayExerciseList(manageViewModel)

        Spacer(modifier = Modifier.padding(10.dp))

        CancelAndSaveRow(
            cancel = { navController.navigate("Home") },
            manageViewModel = manageViewModel,
            workoutViewModel =  workoutViewModel,
            exerciseViewModel = exerciseViewModel,
            navController = navController
        )
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
private fun AddExerciseRow (
    openAddExerciseModal: () -> Unit
) {
    val context = LocalContext.current

    Row(modifier = Modifier.fillMaxWidth(0.8f),
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
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight(0.75f)
            .fillMaxWidth(0.9f),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 5.dp)
    ) {
        viewModel.exercises.forEachIndexed { index, exercise ->
            item {
                DisplayExerciseCard(
                    exercise = exercise,
                    edit = { name, sets, m1, m2, restTime -> viewModel.updateExercise(index, name, sets, m1, m2, restTime) },
                    delete = { viewModel.deleteExercise(index) }
                )
            }
        }
    }
}

@Composable
private fun DisplayExerciseCard(
    exercise: Exercise,
    edit: (String, Int, Measurement, Measurement, Int?) -> Unit,
    delete: () -> Unit
) {
    val context = LocalContext.current

    var manageExerciseModalOpen by rememberSaveable { mutableStateOf(false) }
    val exerciseModel: ExerciseModalViewModel = viewModel()

    ManageExerciseModal(
        modalOpen = manageExerciseModalOpen,
        closeModal = { manageExerciseModalOpen = false },
        submitModal = edit,
        exerciseModel = exerciseModel
    )

    Card {
        Row(modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(100.dp)
            .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = exercise.name,
                style = MaterialTheme.typography.bodyLarge)
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End) {
                IconButton(onClick = { delete() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.delete),
                        contentDescription = context.getString(R.string.delete)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                IconButton(onClick = {
                    manageExerciseModalOpen = true
                    exerciseModel.updateExerciseName(exercise.name)
                    exerciseModel.updateSets(exercise.sets.toString())
                    if (exercise.restTime != null) {
                        exerciseModel.updateRestTime(exercise.restTime.toString())
                    }
                    exerciseModel.updateMeasurementType1(exercise.measurement1.type)
                    exerciseModel.updateMeasurementType2(exercise.measurement2.type)
                    exerciseModel.updateMeasurementValues1(exercise.measurement1.values.map { it.toString() })
                    exerciseModel.updateMeasurementValues2(exercise.measurement2.values.map { it.toString() })
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.edit),
                        contentDescription = context.getString(R.string.edit)
                    )
                }
            }
        }
    }
}

@Composable
private fun CancelAndSaveRow (
    cancel: () -> Unit,
    manageViewModel: ManageWorkoutViewModel,
    workoutViewModel: WorkoutViewModel,
    exerciseViewModel: ExerciseViewModel,
    navController: NavController
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth(0.8f),
        horizontalArrangement = Arrangement.End
    ) {
        val buttonColors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )

        // cancel
        Button(
            onClick = { cancel() },
            colors = buttonColors,
            shape = RectangleShape
        ) {
            Text(text = context.getString(R.string.cancel), style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.width(8.dp))

        // save
        Button(
            onClick = {
                val workout = Workout(name = manageViewModel.name, description = "", schedule = "")
                workoutViewModel.addWorkout(workout)

                manageViewModel.exercises.forEach {
                    exerciseViewModel.addExercise(workout.id, it)
                }

                navController.navigate("Run")
            },
            colors = buttonColors,
            shape = RectangleShape
        ) {
            Text(text = context.getString(R.string.save), style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun MeasurementSelection(
    options: List<String>,
    updateOption: (String) -> Unit,
    sets: String,
    values: List<String>,
    updateValue: (Int, String) -> Unit
) {
    val context = LocalContext.current

    var open by rememberSaveable { mutableStateOf(false) }
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Gray, shape = MaterialTheme.shapes.small)
            .clickable { open = true }
            .padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = options[selectedIndex],
                modifier = Modifier.weight(1f),
            )
            Icon(
                painter = painterResource(id = R.drawable.dropdown),
                contentDescription =  context.getString(R.string.leaderboard),
            )
        }

        DropdownMenu(
            expanded = open,
            onDismissRequest = { open = false }
        ) {
            options.forEachIndexed { index, option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        selectedIndex = index
                        open = false
                        updateOption(option)
                    }
                )
            }
        }
    }

    if (sets.isNotBlank()) {
        LazyColumn(
            modifier = Modifier
                .padding(8.dp)
                .heightIn(max = 100.dp)
        ) {
            items(sets.toInt()) { index ->
                val displayText = context.getString(R.string.measurement_text,
                    index + 1, options[selectedIndex])

                TextField(
                    value = values[index],
                    onValueChange = { it: String -> updateValue(index, it) },
                    isError = values[index].toFloatOrNull() == null,
                    label = { Text(displayText) },
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .padding(vertical = 3.dp)
                )
            }
        }
    }
}

@Composable
private fun ManageExerciseModal(
    exerciseModel: ExerciseModalViewModel = viewModel(),
    modalOpen: Boolean,
    closeModal: () -> Unit,
    submitModal: (String, Int, Measurement, Measurement, Int?) -> Unit
) {
    val context = LocalContext.current

    if (modalOpen) {
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
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = context.getString(R.string.exercises_modal_title),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black)

                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = exerciseModel.exerciseName,
                        onValueChange = { exerciseModel.updateExerciseName(it) },
                        label = { Text(context.getString(R.string.exercise_name_label)) },
                        isError = !exerciseModel.validExerciseName(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    TextField(
                        value = exerciseModel.sets,
                        onValueChange = {
                            if (it.isBlank() || it.toIntOrNull() != null) {
                                exerciseModel.updateSets(it)
                            }
                        },
                        label = { Text(context.getString(R.string.sets_label)) },
                        isError = !exerciseModel.validSetValue(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    MeasurementSelection(
                        options = listOf("Reps", "Time"),
                        updateOption = { exerciseModel.updateMeasurementType1(it) },
                        sets = exerciseModel.sets,
                        values = exerciseModel.measurementValues1,
                        updateValue = { index, newValue ->
                            val measurementValues1 = exerciseModel.measurementValues1.toMutableList().apply {
                                this[index] = newValue
                            }
                            exerciseModel.updateMeasurementValues1(measurementValues1)
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    MeasurementSelection(
                        options = listOf("Weight", "Distance"),
                        updateOption = { exerciseModel.updateMeasurementType2(it) },
                        sets = exerciseModel.sets,
                        values = exerciseModel.measurementValues2,
                        updateValue = { index, newValue ->
                            val measurementValues2 = exerciseModel.measurementValues2.toMutableList().apply {
                                this[index] = newValue
                            }
                            exerciseModel.updateMeasurementValues2(measurementValues2)
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = exerciseModel.restTime,
                        onValueChange = { exerciseModel.updateRestTime(it) },
                        label = { Text(context.getString(R.string.rest_time_label)) },
                        isError = !exerciseModel.validRestTime(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val buttonColors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth(0.9f)) {
                        Button(
                            modifier = Modifier.padding(paddingValues = PaddingValues(horizontal = 8.dp)),
                            colors = buttonColors,
                            shape = RectangleShape,
                            onClick = {
                                closeModal()
                                exerciseModel.clearSavedInfo()
                            }
                        ) { Text(context.getString(R.string.cancel), style = MaterialTheme.typography.bodyLarge) }
                        Button(
                            colors = buttonColors,
                            shape = RectangleShape,
                            onClick =
                            {
                                if (exerciseModel.validMeasurementValues()
                                    && exerciseModel.validSetValue()
                                    && exerciseModel.validRestTime()) {
                                    val measurement1 = Measurement(
                                        type = exerciseModel.measurementType1,
                                        values = exerciseModel.measurementValues1.toList().map { it.toFloat() }
                                    )
                                    val measurement2 = Measurement(
                                        type = exerciseModel.measurementType1,
                                        values = exerciseModel.measurementValues2.toList().map { it.toFloat() }
                                    )

                                    var restTime: Int? = null;
                                    if (exerciseModel.restTime.isNotBlank()) {
                                        restTime = exerciseModel.restTime.toInt()
                                    }

                                    submitModal(
                                        exerciseModel.exerciseName,
                                        exerciseModel.sets.toInt(),
                                        measurement1,
                                        measurement2,
                                        restTime
                                    )

                                    closeModal()
                                    exerciseModel.clearSavedInfo()
                                }
                            }) {
                            Text(context.getString(R.string.add), style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}