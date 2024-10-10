package com.example.seng303_groupb_assignment2.screens

import android.content.res.Configuration
import android.text.Layout
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.seng303_groupb_assignment2.R
import com.example.seng303_groupb_assignment2.entities.Exercise
import com.example.seng303_groupb_assignment2.entities.ExerciseLog
import com.example.seng303_groupb_assignment2.enums.ChartOption
import com.example.seng303_groupb_assignment2.enums.Measurement
import com.example.seng303_groupb_assignment2.enums.TimeRange
import com.example.seng303_groupb_assignment2.enums.UnitType
import com.example.seng303_groupb_assignment2.graphcomponents.CircleComponent
import com.example.seng303_groupb_assignment2.models.UserPreferences
import com.example.seng303_groupb_assignment2.services.MeasurementConverter
import com.example.seng303_groupb_assignment2.utils.exerciseSaver
import com.example.seng303_groupb_assignment2.viewmodels.ExerciseViewModel
import com.example.seng303_groupb_assignment2.viewmodels.PreferenceViewModel
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
import org.koin.androidx.compose.koinViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.ceil

@Composable
fun ViewProgress(
    navController: NavController,
    viewModel: ExerciseViewModel = getViewModel(),
    preferenceViewModel: PreferenceViewModel = getViewModel()
) {
    // TODO Remove this when we don't need sample data anymore
    viewModel.createSampleExerciseAndLogs()
    val userPreferences by preferenceViewModel.preferences.observeAsState(UserPreferences())
    val unitType = if (userPreferences.metricUnits) UnitType.METRIC else UnitType.IMPERIAL
    var selectedExercise by rememberSaveable(stateSaver = exerciseSaver) { mutableStateOf<Exercise?>(null) }
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedOption by rememberSaveable { mutableStateOf<ChartOption?>(null) }
    var selectedTimeRange by rememberSaveable { mutableStateOf(TimeRange.LAST_MONTH) }

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
            selectedOption = selectedOption,
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
                    selectedOption = when (exercise.measurement) {
                        Measurement.REPS_WEIGHT -> ChartOption.MaxWeight
                        Measurement.DISTANCE_TIME -> ChartOption.MaxDistance
                    }
                    showDialog = false
                },
                onDismissRequest = { showDialog = false }
            )
        }

        if (selectedExercise != null && filteredExerciseLogs.isNotEmpty() && selectedOption != null) {
            val measurementType = if (selectedExercise!!.measurement.unit1 == "Distance") {
                "Distance"
            } else {
                "Weight"
            }
            ExerciseProgressGraph(filteredExerciseLogs, selectedOption, unitType, measurementType)
        } else if (selectedExercise != null) {
            Text(
                text = stringResource(R.string.no_logs_in_timeframe),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseHeader(
    selectedExercise: Exercise?,
    selectedOption: ChartOption?,
    onHeaderClick: () -> Unit,
    onOptionSelected: (ChartOption) -> Unit
) {
    var isModalVisible by remember { mutableStateOf(false) }

    val relevantOptions = remember(selectedExercise) {
        when {
            selectedExercise?.measurement?.equals(Measurement.REPS_WEIGHT) == true -> {
                listOf(ChartOption.MaxWeight, ChartOption.TotalWorkoutVolume)
            }
            selectedExercise?.measurement?.equals(Measurement.DISTANCE_TIME) == true -> {
                listOf(ChartOption.MaxDistance, ChartOption.TotalDistance)
            }
            else -> emptyList()
        }
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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

            if (relevantOptions.isNotEmpty()) {
                Text(
                    text = selectedOption?.label ?: stringResource(id = R.string.select_option),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable { isModalVisible = true }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = selectedExercise?.name ?: stringResource(R.string.tap_select),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .clickable { onHeaderClick() },
                color = MaterialTheme.colorScheme.primary,
                overflow = TextOverflow.Ellipsis
            )

            if (relevantOptions.isNotEmpty()) {
                Row (
                    modifier = Modifier
                        .padding(8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.graph),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = selectedOption?.label ?: stringResource(id = R.string.select_option),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clickable { isModalVisible = true }
                            .padding(start = 2.dp)
                    )
                }
            }
        }
    }
    if (isModalVisible) {
        ChartOptionSelectionDialog(
            selectedOption = selectedOption,
            relevantOptions = relevantOptions,
            onOptionSelected = { selected ->
                onOptionSelected(selected)
            },
            onDismissRequest = { isModalVisible = false }
        )
    }
}

@Composable
fun ChartOptionSelectionDialog(
    selectedOption: ChartOption?,
    relevantOptions: List<ChartOption>,
    onOptionSelected: (ChartOption) -> Unit,
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
                Text(
                    text = stringResource(id = R.string.select_option),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                relevantOptions.forEach { option ->
                    Text(
                        text = option.label,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (option == selectedOption) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onOptionSelected(option)
                                onDismissRequest()
                            }
                            .padding(vertical = 8.dp)
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
                    .padding(horizontal = 2.dp)
                    .weight(1f)
                    .background(color = backgroundColour, shape = MaterialTheme.shapes.small)
                    .clickable(onClick = { onTimeRangeSelected(timeRange) }),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier
                        .padding(vertical = 4.dp),
                    text = timeRange.label,
                    style = MaterialTheme.typography.bodyLarge,
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
fun ExerciseProgressGraph(
    exerciseLogs: List<ExerciseLog>,
    selectedOption: ChartOption?,
    unitType: UnitType,
    measurementType: String,
    preferenceViewModel: PreferenceViewModel = koinViewModel()
) {
    val preferences = preferenceViewModel.preferences.observeAsState(null).value
    val metricUnits = preferences?.metricUnits ?: false
    val measurementConverter = MeasurementConverter(metricUnits)
    val modelProducer = remember { CartesianChartModelProducer() }

    val xToDateMapKey = ExtraStore.Key<Map<Float, Long>>()

    LaunchedEffect(exerciseLogs, selectedOption) {
        val dataSeries = when (selectedOption) {
            ChartOption.MaxWeight -> {
                exerciseLogs.mapNotNull { log ->
                    val epochDay = Instant.ofEpochMilli(log.timestamp)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .toEpochDay()
                    val maxWeight = log.record.maxOfOrNull { it.second }?.takeIf { !it.isNaN() } ?: 0f
                    if (maxWeight > 0f) epochDay.toDouble() to measurementConverter.convertToImperial(maxWeight, measurementType) else null
                }
            }
            ChartOption.TotalWorkoutVolume -> {
                exerciseLogs.mapNotNull { log ->
                    val epochDay = Instant.ofEpochMilli(log.timestamp)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .toEpochDay()
                    val totalVolume = log.record.sumOf { (reps, weight) -> reps * weight.toDouble() }
                    if (!totalVolume.isNaN() && totalVolume > 0) {
                        epochDay.toDouble() to measurementConverter.convertToImperial(totalVolume.toFloat(), measurementType)
                    } else {
                        null
                    }
                }
            }
            ChartOption.MaxDistance -> {
                exerciseLogs.mapNotNull { log ->
                    val epochDay = Instant.ofEpochMilli(log.timestamp)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .toEpochDay()
                    val maxDistance = log.record.maxOfOrNull { it.first }?.takeIf { !it.isNaN() } ?: 0f
                    if (maxDistance > 0f) epochDay.toDouble() to measurementConverter.convertToImperial(maxDistance, measurementType) else null
                }
            }
            ChartOption.TotalDistance -> {
                exerciseLogs.mapNotNull { log ->
                    val epochDay = Instant.ofEpochMilli(log.timestamp)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .toEpochDay()
                    val totalDistance = log.record.sumOf { it.first.toDouble() }
                    if (!totalDistance.isNaN() && totalDistance > 0) {
                        epochDay.toDouble() to measurementConverter.convertToImperial(totalDistance.toFloat(), measurementType)
                    } else {
                        null
                    }
                }
            }

            null -> {
                emptyList()
            }
        }
        if (dataSeries.isNotEmpty()) {

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

    val dateTimeFormatter = DateTimeFormatter.ofPattern("d-MMM")

    val dateValueFormatter = CartesianValueFormatter { _, x, _ ->
        val date = LocalDate.ofEpochDay(x.toLong())
        date.format(dateTimeFormatter)
    }

    val yAxisLabel = when (selectedOption) {
        ChartOption.MaxWeight -> if (unitType == UnitType.METRIC) stringResource(id = R.string.weight_kg) else stringResource(id = R.string.weight_lb)
        ChartOption.TotalWorkoutVolume -> if (unitType == UnitType.METRIC) stringResource(id = R.string.total_volume_kg) else stringResource(id = R.string.total_volume_lbs)
        ChartOption.MaxDistance -> if (unitType == UnitType.METRIC) stringResource(id = R.string.distance_km) else stringResource(id = R.string.distance_miles)
        ChartOption.TotalDistance -> if (unitType == UnitType.METRIC) stringResource(id = R.string.total_distance_km) else stringResource(id = R.string.total_distance_miles)
        else -> ""
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
            startAxis = VerticalAxis.rememberStart(
                titleComponent = rememberTextComponent(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                title = yAxisLabel,
                label = rememberTextComponent(
                    color = MaterialTheme.colorScheme.onSurface
                )
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = dateValueFormatter,
                label = rememberTextComponent(
                    color = MaterialTheme.colorScheme.onSurface
                )
            ),
            marker = rememberDefaultCartesianMarker(
                label = rememberTextComponent(),
                indicator = pointIndicator,

            )
        ),
        modelProducer = modelProducer,
        scrollState = rememberVicoScrollState(true, initialScroll = Scroll.Absolute.End),
        zoomState = rememberVicoZoomState(zoomEnabled = true, initialZoom = Zoom.Content),
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