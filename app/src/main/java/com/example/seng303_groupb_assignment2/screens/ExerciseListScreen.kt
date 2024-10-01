package com.example.seng303_groupb_assignment2.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.seng303_groupb_assignment2.entities.Exercise
import com.example.seng303_groupb_assignment2.viewmodels.ExerciseViewModel

@Composable
fun ExerciseListScreen(viewModel: ExerciseViewModel) {
    // Collecting the list of exercises as a state
    val exercises by viewModel.allExercises.observeAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Saved Exercises",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn {
            items(exercises) { exercise ->
                ExerciseItem(exercise)
            }
        }
    }
}

@Composable
fun ExerciseItem(exercise: Exercise) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Name: ${exercise.name}", style = MaterialTheme.typography.labelSmall)
            Text(text = "Sets: ${exercise.sets}")
            exercise.reps?.let { Text(text = "Reps: ${it.joinToString()}") }
            exercise.weight?.let { Text(text = "Weight: ${it.joinToString()} kg") }
            exercise.distance?.let { Text(text = "Distance: $it m") }
            exercise.time?.let { Text(text = "Time: $it s") }
        }
    }
}
