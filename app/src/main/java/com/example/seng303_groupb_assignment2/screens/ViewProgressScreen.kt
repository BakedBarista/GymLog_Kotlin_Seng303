package com.example.seng303_groupb_assignment2.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.seng303_groupb_assignment2.R
import com.example.seng303_groupb_assignment2.entities.Exercise
import com.example.seng303_groupb_assignment2.entities.ExerciseLog
import com.example.seng303_groupb_assignment2.enums.ChartOption
import com.example.seng303_groupb_assignment2.graphcomponents.CircleComponent
import com.example.seng303_groupb_assignment2.utils.exerciseSaver
import com.example.seng303_groupb_assignment2.viewmodels.ExerciseViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.Scroll
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.component.Component
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import org.koin.androidx.compose.getViewModel
import kotlin.math.ceil

@Composable
fun ViewProgress(
    navController: NavController,
    viewModel: ExerciseViewModel = getViewModel()
) {
    // TODO Remove this when we don't need sample data anymore
    viewModel.createSampleExerciseAndLogs()
    var selectedExercise by rememberSaveable(stateSaver = exerciseSaver) { mutableStateOf<Exercise?>(null) }
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedOption by rememberSaveable { mutableStateOf<ChartOption?>(null) }

    val exercises by viewModel.getExercisesByName(searchQuery).observeAsState(emptyList())
    val exerciseLogs by viewModel.getExerciseLogsByExercise(selectedExercise?.id ?: 0L)
        .observeAsState(emptyList())

    Column(modifier = Modifier.padding(16.dp)) {

        ExerciseHeader(
            selectedExercise = selectedExercise,
            onHeaderClick = { showDialog = true },
            onOptionSelected = { selectedOption = it }
        )

        if (showDialog) {
            ExerciseSelectionDialog(
                exercises = exercises,
                searchQuery = searchQuery,
                onSearchQueryChanged = { searchQuery = it },
                onExerciseSelected = { exercise ->
                    selectedExercise = exercise
                    selectedOption = when (exercise.measurement1.type) {
                        "Reps" -> ChartOption.MaxWeight
                        "Distance" -> ChartOption.MaxDistance
                        else -> null
                    }
                    showDialog = false
                },
                onDismissRequest = { showDialog = false }
            )
        }

        if (selectedExercise != null && exerciseLogs.isNotEmpty()) {
            ExerciseProgressGraph(exerciseLogs, selectedOption)
        }
    }
}

@Composable
fun ExerciseHeader(
    selectedExercise: Exercise?,
    onHeaderClick: () -> Unit,
    onOptionSelected: (ChartOption) -> Unit
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }

    val relevantOptions = remember(selectedExercise) {
        when (selectedExercise?.measurement1?.type) {
            "Reps" -> listOf(ChartOption.MaxWeight, ChartOption.TotalWorkoutVolume)
            "Distance" -> listOf(ChartOption.MaxDistance, ChartOption.TotalDistance)
            else -> emptyList()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = selectedExercise?.name ?: stringResource(R.string.tap_select),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .clickable { onHeaderClick() },
            color = MaterialTheme.colorScheme.primary
        )

        IconButton(onClick = { isDropdownExpanded = true }) {
            Icon(
                painter = painterResource(id = R.drawable.more_vert),
                contentDescription = stringResource(R.string.more_options)
            )
        }

        DropdownMenu(
            expanded = isDropdownExpanded,
            onDismissRequest = { isDropdownExpanded = false }
        ) {
            relevantOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label) },
                    onClick = {
                        onOptionSelected(option)
                        isDropdownExpanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ExerciseSelectionDialog(
    exercises: List<Exercise>,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onExerciseSelected: (Exercise) -> Unit,
    onDismissRequest: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Dialog(onDismissRequest = onDismissRequest) {
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
                    onValueChange = onSearchQueryChanged,
                    label = { Text(stringResource(R.string.search_exercise)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn {
                    if (exercises.isEmpty()) {
                        item {
                            Text(
                                text = stringResource(R.string.no_exercises),
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
                                        onExerciseSelected(exercise)
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

@Composable
fun ExerciseProgressGraph(exerciseLogs: List<ExerciseLog>, selectedOption: ChartOption?) {
    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(exerciseLogs, selectedOption) {
        val dataSeries = when (selectedOption) {
            ChartOption.MaxWeight -> {
                exerciseLogs.map { log -> log.measurement2.values.maxOrNull() ?: 0f }
            }

            ChartOption.TotalWorkoutVolume -> {
                exerciseLogs.map { log ->
                    log.measurement1.values.zip(log.measurement2.values)
                        .sumOf { (reps, weight) -> reps * weight.toDouble() }
                }
            }
            ChartOption.MaxDistance -> {
                exerciseLogs.map { log -> log.measurement1.values.maxOrNull() ?: 0f }
            }
            ChartOption.TotalDistance -> {
                exerciseLogs.map { log ->
                    log.measurement1.values.sum()
                }
            }

            null -> {
                emptyList()
            }
        }
        if (dataSeries.isNotEmpty()) {
            modelProducer.runTransaction {
                lineSeries {
                    series(dataSeries)
                }
            }
        }
    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                LineCartesianLayer.LineProvider.series(
                    LineCartesianLayer.rememberLine(
                        fill = remember { LineCartesianLayer.LineFill.single(fill(Color.DarkGray)) },
                        pointConnector = remember { LineCartesianLayer.PointConnector.cubic(curvature = 0f) },
                    )
                ),
                rangeProvider = rangeProvider
            ),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(),
            marker = rememberDefaultCartesianMarker(
                label = rememberTextComponent(),
                indicator = pointIndicator
            )
        ),
        modelProducer = modelProducer,
        scrollState = rememberVicoScrollState(true, Scroll.Absolute.End),
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp),
    )
}

val pointIndicator: (Color) -> Component = { color ->
    CircleComponent(colour = color, radius = 10f) // Adjust the radius as needed
}

// This is how we set the Y axis > than the max val of the Y in out dataset
private val rangeProvider =
    object : CartesianLayerRangeProvider {
        override fun getMaxY(minY: Double, maxY: Double, extraStore: ExtraStore) = ceil(1.1 * maxY)
    }