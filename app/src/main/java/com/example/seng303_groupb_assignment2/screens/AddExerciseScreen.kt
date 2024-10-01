package com.example.seng303_groupb_assignment2.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.seng303_groupb_assignment2.viewmodels.ExerciseViewModel

@Composable
fun AddExerciseScreen(viewModel: ExerciseViewModel) {
    var name by remember { mutableStateOf("") }
    var sets by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var distance by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var restTime by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Exercise Name") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = sets,
            onValueChange = { sets = it },
            label = { Text("Sets") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = reps,
            onValueChange = { reps = it },
            label = { Text("Reps (optional)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = weight,
            onValueChange = { weight = it },
            label = { Text("Weight (optional)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = distance,
            onValueChange = { distance = it },
            label = { Text("Distance (optional)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = time,
            onValueChange = { time = it },
            label = { Text("Time (optional)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = restTime,
            onValueChange = { restTime = it },
            label = { Text("Rest time") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                val setsInt = sets.toIntOrNull() ?: 0
                val repsInt = reps.toIntOrNull()
                val weightFloat = weight.toFloatOrNull()
                val distanceFloat = distance.toFloatOrNull()
                val timeFloat = time.toFloatOrNull()
                val restTimeInt = restTime.toIntOrNull() ?: 0

                viewModel.addExercise(name, setsInt, repsInt, weightFloat, distanceFloat, timeFloat, restTimeInt)
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Add Exercise")
        }
    }
}