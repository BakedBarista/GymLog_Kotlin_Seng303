package com.example.seng303_groupb_assignment2.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.seng303_groupb_assignment2.R
import com.example.seng303_groupb_assignment2.entities.Measurement
import com.example.seng303_groupb_assignment2.viewmodels.ManageWorkoutViewModel

@Composable
fun AddWorkout(
    navController: NavController,
    viewModel: ManageWorkoutViewModel,
) {
    // TODO - replace ui text with string resources
    // TODO - user rememberBy to handle orientation changes

    var modalOpen by rememberSaveable { mutableStateOf(false) }
    AddExerciseModal(
        modalOpen,
        closeModal = { modalOpen = false },
        { name, sets, m1, m2, restTime -> viewModel.addExercise(name, sets, m1, m2, restTime) }
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

        AddExerciseRow(openAddExerciseModal = { modalOpen = true } )

        Spacer(modifier = Modifier.padding(10.dp))

        // List of exercises that have been added to the workout
        DisplayExerciseList()

        Spacer(modifier = Modifier.padding(10.dp))

        CancelAndSaveRow()
    }
}

@Composable
private fun AddExerciseModal(
    modalOpen: Boolean,
    closeModal: () -> Unit,
    addExercise: (String, Int, Measurement, Measurement, Int) -> Unit
) {
    var exerciseName by rememberSaveable { mutableStateOf("") }
    var sets by rememberSaveable { mutableStateOf("") }
    var measurementType1 by rememberSaveable { mutableStateOf("") }
    var measurementValues1 by rememberSaveable { mutableStateOf(listOf<Float>()) }
    var measurementType2 by rememberSaveable { mutableStateOf("") }
    val measurementValues2 by rememberSaveable { mutableStateOf(listOf<Float>()) }

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
                    Text(text = "Add Exercise", style = MaterialTheme.typography.bodyLarge)

                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = exerciseName,
                        onValueChange = { exerciseName = it },
                        label = { Text("Exercise name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = sets,
                        onValueChange =
                        {
                            if (sets.isBlank() || sets.toIntOrNull() != null) {
                                sets = it
                            }

                            if (sets.isNotBlank()) {
                                for (i in 0 until sets.toInt()) {
//                                    if (measurementValues1.size < sets) {
//                                        measurementValues1.add("0")
//                                    }
                                }
                            }
                        },
                        label = { Text("Sets") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    MeasurementSelection(
                        options = listOf("Reps", "Time"),
                        updateOption = { measurementType1 = it },
                        sets = sets,
                        values = measurementValues1,
                        updateValue = { index, newValue ->
                            if (index in measurementValues1.indices) {
                                val updatedList = measurementValues1.toMutableList().apply {
                                    this[index] = newValue.toFloat()
                                }
                                measurementValues1 = updatedList
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    MeasurementSelection(
                        options = listOf("Weight", "Distance"),
                        updateOption = { measurementType2 = it },
                        sets = sets,
                        values = measurementValues2,
                        updateValue = { index, newValue ->
                            if (index in measurementValues1.indices) {
                                val updatedList = measurementValues1.toMutableList().apply {
                                    this[index] = newValue.toFloat()
                                }
                                measurementValues1 = updatedList
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = {
                        closeModal()
                        // TODO wait time and error checking

                        val measurement1 = Measurement(
                            type = measurementType1,
                            values = measurementValues1
                        )
                        val measurement2 = Measurement(
                            type = measurementType1,
                            values = measurementValues2
                        )
                        addExercise(
                            exerciseName,
                            sets.toInt(),
                            measurement1,
                            measurement2,
                            0
                        )
                        // TODO reset values to defaults
                    }) {
                        Text("Add")
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

) {
    // TODO give this a fixed height
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight(0.75f)
            .fillMaxWidth(0.9f)
    ) {
        // TODO for each exercise added
    }
}

@Composable
private fun DisplayExerciseCard(

) {

}

@Composable
private fun CancelAndSaveRow (

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
            onClick = { /*TODO cancel */ },
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
    values: List<Float>,
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

    LazyColumn(
        modifier = Modifier.padding(16.dp)
    ) {
        if (sets.isNotBlank()) {
            items(sets.toInt()) { index ->
                TextField(
                    value = values[index].toString(),
                    onValueChange = { it: String -> updateValue(index, it) },
                    label = { Text("Set $index") },
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
            }
        }
    }
}