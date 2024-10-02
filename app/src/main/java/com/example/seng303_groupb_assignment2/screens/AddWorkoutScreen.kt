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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.seng303_groupb_assignment2.R
import com.example.seng303_groupb_assignment2.entities.Exercise
import com.example.seng303_groupb_assignment2.entities.Measurement
import com.example.seng303_groupb_assignment2.viewmodels.ManageWorkoutViewModel

@Composable
fun AddWorkout(
    navController: NavController,
    viewModel: ManageWorkoutViewModel,
) {
    // TODO - replace ui text with string resources
    var addModalOpen by rememberSaveable { mutableStateOf(false) }
    AddExerciseModal(
        modalOpen = addModalOpen,
        closeModal = { addModalOpen = false },
        addExercise = { name, sets, m1, m2, restTime -> viewModel.addExercise(name, sets, m1, m2, restTime) }
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        WorkoutNameTextBox(
            name = viewModel.name,
            updateName = { viewModel.updateName(it) }
        )

        Spacer(modifier = Modifier.padding(15.dp))

        AddExerciseRow(openAddExerciseModal = { addModalOpen = true } )

        Spacer(modifier = Modifier.padding(10.dp))

        DisplayExerciseList(viewModel)

        Spacer(modifier = Modifier.padding(10.dp))

        CancelAndSaveRow(cancel = { navController.navigate("Home") })
    }
}

@Composable
private fun AddExerciseModal(
    exerciseModel: ExerciseModalViewModel = viewModel(),
    modalOpen: Boolean,
    closeModal: () -> Unit,
    addExercise: (String, Int, Measurement, Measurement, Int?) -> Unit
) {
    if (modalOpen) {
        Dialog(onDismissRequest = { closeModal() }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = RectangleShape)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Add Exercise", style = MaterialTheme.typography.bodyLarge, color = Color.Black)

                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = exerciseModel.exerciseName,
                        onValueChange = { exerciseModel.updateExerciseName(it) },
                        label = { Text("Exercise name") },
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
                        label = { Text("Sets") },
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
                        label = { Text("Rest time") },
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
                        ) { Text("Cancel", style = MaterialTheme.typography.bodyLarge) }
                        Button(
                            colors = buttonColors,
                            shape = RectangleShape,
                            onClick =
                            {
                                if (exerciseModel.validMeasurementValues() && exerciseModel.validSetValue() && exerciseModel.validRestTime()) {
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

                                    addExercise(
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
                            Text("Add", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkoutNameTextBox(
    name: String,
    updateName: (String) -> Unit,
) {
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
            placeholder = {
                Text("Workout name...", style = MaterialTheme.typography.bodyLarge)
            },
        )
    }
}

@Composable
private fun AddExerciseRow (
    openAddExerciseModal: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(0.8f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Text(text = "Exercises ", style = MaterialTheme.typography.titleLarge)
        IconButton(onClick = { openAddExerciseModal() }) {
            Icon(
                painter = painterResource(id = R.drawable.plus),
                contentDescription = "Plus"
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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        viewModel.exercises.forEachIndexed { index, exercise ->
            item {
                DisplayExerciseCard(
                    exercise = exercise,
                    delete = { viewModel.deleteExercise(index) }
                )
            }
        }
    }
}

@Composable
private fun DisplayExerciseCard(
    exercise: Exercise,
    delete: () -> Unit
) {
    Card {
        Row(modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(100.dp)
            .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = exercise.name, style = MaterialTheme.typography.bodyLarge)
            IconButton(onClick = { delete() }) {
                Icon(
                    painter = painterResource(id = R.drawable.delete),
                    contentDescription = "Delete"
                )
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    painter = painterResource(id = R.drawable.edit),
                    contentDescription = "Edit"
                )
            }
        }
    }
}

@Composable
private fun CancelAndSaveRow (
    cancel: () -> Unit
) {
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
            Text(text = "Cancel", style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.width(8.dp))

        // save
        Button(
            onClick = { /*TODO save workout */ },
            colors = buttonColors,
            shape = RectangleShape
        ) {
            Text(text = "Save", style = MaterialTheme.typography.bodyLarge)
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
                contentDescription = "Leaderboard",
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
                TextField(
                    value = values[index],
                    onValueChange = { it: String -> updateValue(index, it) },
                    isError = values[index].toFloatOrNull() == null,
                    label = { Text("Set ${index + 1} (${options[selectedIndex]})") },
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .padding(vertical = 3.dp)
                )
            }
        }
    }
}