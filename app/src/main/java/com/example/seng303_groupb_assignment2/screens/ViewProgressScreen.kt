package com.example.seng303_groupb_assignment2.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.seng303_groupb_assignment2.entities.Exercise
import com.example.seng303_groupb_assignment2.utils.exerciseSaver
import com.example.seng303_groupb_assignment2.viewmodels.ExerciseViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import org.koin.androidx.compose.getViewModel

@Composable
fun ViewProgress(
    navController: NavController,
    viewModel: ExerciseViewModel = getViewModel()
) {
    viewModel.createSampleExerciseAndLogs()
    var selectedExercise by rememberSaveable(stateSaver = exerciseSaver) { mutableStateOf<Exercise?>(null) }
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val exercises by viewModel.getExercisesByName(searchQuery).observeAsState(emptyList())
    val exerciseLogs by viewModel.getExerciseLogsByExercise(selectedExercise?.id ?: 0L)
        .observeAsState(emptyList())

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier.padding(16.dp)) {

        Text(
            text = selectedExercise?.name ?: "Tap to select an exercise",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDialog = true }
                .padding(16.dp),
            color = MaterialTheme.colorScheme.primary
        )

        if (showDialog) {
            Dialog(onDismissRequest = { showDialog = false }) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = MaterialTheme.shapes.medium,
                    tonalElevation = 8.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DisposableEffect(Unit) {
                            focusRequester.requestFocus()
                            keyboardController?.show()
                            onDispose { }
                        }
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = { Text("Search for an exercise") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyColumn {
                            if (exercises.isEmpty()) {
                                item {
                                    Text(
                                        text = "No exercises found.",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            } else {
                                items(exercises) { exercise ->
                                    Text(
                                        text = exercise.name,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                selectedExercise = exercise
                                                showDialog = false
                                                focusManager.clearFocus()
                                            }
                                            .padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (selectedExercise != null && exerciseLogs.isNotEmpty()) {
            val modelProducer = remember { CartesianChartModelProducer() }
            LaunchedEffect(exerciseLogs) {
                val dataSeries = exerciseLogs.map { log ->
                    log.measurement2.values.maxOrNull() ?: 0f
                }

                modelProducer.runTransaction {
                    lineSeries {
                        series(dataSeries)
                    }
                }
            }

            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberLineCartesianLayer(),
                    startAxis = VerticalAxis.rememberStart(),
                    bottomAxis = HorizontalAxis.rememberBottom(),
                    marker = rememberDefaultCartesianMarker(
                        label = rememberTextComponent()
                    )
                ),
                modelProducer = modelProducer,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp),
            )
        }
    }
}