package com.example.seng303_groupb_assignment2.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.seng303_groupb_assignment2.enums.TimeRange
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
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.Scroll
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.component.Component
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import org.koin.androidx.compose.getViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
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
    var selectedTimeRange by rememberSaveable { mutableStateOf(TimeRange.ALL) }

    val exercises by viewModel.getExercisesByName(searchQuery).observeAsState(emptyList())
    val exerciseLogs by viewModel.getExerciseLogsByExercise(selectedExercise?.id ?: 0L)
        .observeAsState(emptyList())

    val filteredExerciseLogs = remember(exerciseLogs, selectedTimeRange) {
        val now = System.currentTimeMillis()
        val cutoffTime = selectedTimeRange.days?.let { now - it * 86400000 }

        if (cutoffTime != null) {
            exerciseLogs.filter { log -> log.timestamp >= cutoffTime }
        } else {
            exerciseLogs
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {

        ExerciseHeader(
            selectedExercise = selectedExercise,
            onHeaderClick = { showDialog = true },
            onOptionSelected = { selectedOption = it }
        )

        if (selectedExercise != null && exerciseLogs.isNotEmpty()) {
            TimeRangeSelector(
                selectedTimeRange = selectedTimeRange,
                onTimeRangeSelected = { timeRange ->
                    selectedTimeRange = timeRange
                }
            )
        }

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

        if (selectedExercise != null && filteredExerciseLogs.isNotEmpty() && selectedOption != null) {
            ExerciseProgressGraph(filteredExerciseLogs, selectedOption)
        } else if (selectedExercise != null) {
            Text(
                text = stringResource(R.string.no_logs_in_timeframe),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
        } else {
            Text(
                text = stringResource(R.string.no_logs_available),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
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
        Box {
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
}

@Composable
fun TimeRangeSelector(
    selectedTimeRange: TimeRange,
    onTimeRangeSelected: (TimeRange) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TimeRange.entries.forEach { timeRange ->
            val isSelected = timeRange == selectedTimeRange
            val backgroundColour = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
            val textColour = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .weight(1f)
                    .background(color = backgroundColour, shape = MaterialTheme.shapes.small)
                    .clickable(onClick = { onTimeRangeSelected(timeRange) }),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = timeRange.label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColour
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
                    onDispose { }
                }

                TextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChanged,
                    label = { Text(stringResource(R.string.search_exercise)) },
                    modifier = Modifier
                        .fillMaxWidth()
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

    val xToDateMapKey = ExtraStore.Key<Map<Float, Long>>()

    LaunchedEffect(exerciseLogs, selectedOption) {
        val dataSeries = when (selectedOption) {
            ChartOption.MaxWeight -> {
                exerciseLogs.map { log ->
                    val epochDay = Instant.ofEpochMilli(log.timestamp)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .toEpochDay()
                    epochDay.toDouble() to (log.measurement2.values.maxOrNull() ?: 0f)
                }
            }
            ChartOption.TotalWorkoutVolume -> {
                exerciseLogs.map { log ->
                    val epochDay = Instant.ofEpochMilli(log.timestamp)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .toEpochDay()
                    val totalVolume = log.measurement1.values.zip(log.measurement2.values)
                        .sumOf { (reps, weight) -> reps * weight.toDouble() }
                    epochDay.toDouble() to totalVolume.toFloat()
                }
            }
            ChartOption.MaxDistance -> {
                exerciseLogs.map { log ->
                    val epochDay = Instant.ofEpochMilli(log.timestamp)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .toEpochDay()
                    epochDay.toDouble() to (log.measurement1.values.maxOrNull() ?: 0f) }
            }
            ChartOption.TotalDistance -> {
                exerciseLogs.map { log ->
                    val epochDay = Instant.ofEpochMilli(log.timestamp)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .toEpochDay()
                    epochDay.toDouble() to log.measurement1.values.sum() }
            }

            null -> {
                emptyList()
            }
        }
        if (dataSeries.isNotEmpty()) {
            val xToTimestamps = dataSeries.associate { it.first to it.first.toLong() }

            modelProducer.runTransaction {
                lineSeries {
                    series(
                        x = dataSeries.map { it.first },
                        y = dataSeries.map { it.second }
                    )
                }
            }
        } else {
            modelProducer.runTransaction {
                lineSeries { series(emptyList(), emptyList()) }
                extras { extraStore ->
                    extraStore[xToDateMapKey] = emptyMap()
                }
            }
        }
    }

    val dateTimeFormatter = DateTimeFormatter.ofPattern("d MMM")

    val dateValueFormatter = CartesianValueFormatter { _, x, _ ->
        val date = LocalDate.ofEpochDay(x.toLong())
        date.format(dateTimeFormatter)
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
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = dateValueFormatter
            ),
            marker = rememberDefaultCartesianMarker(
                label = rememberTextComponent(),
                indicator = pointIndicator
            )
        ),
        modelProducer = modelProducer,
        scrollState = rememberVicoScrollState(true, Scroll.Absolute.End),
        zoomState = rememberVicoZoomState(zoomEnabled = true, initialZoom = Zoom.x(10.0/* Make this a variable that changes when the user selects a different time period*/)),
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp),
    )
}

private val pointIndicator: (Color) -> Component = { color ->
    CircleComponent(colour = color, radius = 10f)
}

// This is how we set the Y axis > than the max val of the Y in out dataset
private val rangeProvider =
    object : CartesianLayerRangeProvider {
        override fun getMaxY(minY: Double, maxY: Double, extraStore: ExtraStore) = ceil(1.1 * maxY)
    }