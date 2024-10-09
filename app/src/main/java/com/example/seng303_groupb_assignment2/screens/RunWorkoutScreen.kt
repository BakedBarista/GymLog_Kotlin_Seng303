package com.example.seng303_groupb_assignment2.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.seng303_groupb_assignment2.R
import com.example.seng303_groupb_assignment2.entities.Exercise
import com.example.seng303_groupb_assignment2.viewmodels.RunWorkoutViewModel
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RunWorkout(
    viewModel: RunWorkoutViewModel,
    navController: NavController
) {
    val workoutWithExercises = viewModel.workoutWithExercises.value
    val exercises = workoutWithExercises?.exercises
    val currentExerciseIndex = viewModel.currentExerciseIndex

    // Retrieve current exercise and rest time
    val currentExercise = exercises?.getOrNull(currentExerciseIndex)
    val unit1Text = currentExercise?.measurement?.unit1
    val unit2Text = currentExercise?.measurement?.unit2
    val restTime = currentExercise?.restTime ?: 0

    var isTimerRunning by rememberSaveable { mutableStateOf(false) }
    var currentTime by rememberSaveable { mutableLongStateOf(restTime * 1000L) }
    var restartTimer by rememberSaveable { mutableStateOf(false) }
    val sets = viewModel.getSetsForCurrentExercise()

    val isPreviousEnabled = currentExerciseIndex > 0
    val isNextEnabled = currentExerciseIndex < (exercises?.size ?: 0) - 1

    fun onSaveSet(unit1: Float, unit2: Float) {
        viewModel.addSetToCurrentExercise(unit1, unit2)
        currentTime = restTime * 1000L
        restartTimer = true
        isTimerRunning = true
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Header(viewModel, onSave = {
                isTimerRunning = true
            }, onSaveSet = ::onSaveSet)
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = {
                    viewModel.previousExercise()
                    isTimerRunning = false
                },
                    enabled = isPreviousEnabled
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_skip_previous_24),
                        contentDescription = stringResource(id = R.string.next)
                    )
                }
                Timer(
                    totalTime = restTime * 1000L,
                    currentTime = currentTime,
                    isTimerRunning = isTimerRunning,
                    restartTimer = restartTimer,
                    onRestartHandled = { restartTimer = false },
                    handleColor = Color.Green,
                    inactiveBarColor = Color.DarkGray,
                    activeBarColor = Color(0xFF37B900),
                    modifier = Modifier.size(100.dp),
                    timerSize = 100.dp
                )
                IconButton(
                    onClick = {
                        viewModel.nextExercise()
                        isTimerRunning = false
                    },
                    enabled = isNextEnabled
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_skip_next_24),
                        contentDescription = stringResource(id = R.string.next)
                    )
                }
            }
        }

        itemsIndexed(sets) { index, set ->
            SetContainer(
                label1 = unit1Text ?: "Reps",
                label2 = unit2Text ?: "Weight",
                unit1 = set.first,
                unit2 = set.second,
                onDelete = {
                    viewModel.removeSetFromCurrentExercise(index)
                }
            )
        }

        item {
            Button(
                onClick = {
                    viewModel.saveLogs()
                    viewModel.clearWorkoutData()
                    navController.navigate("SelectWorkout")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Finish Workout")
            }
        }
    }
}

// Gotten from https://medium.com/@fahadhabib01/craft-a-captivating-animated-countdown-timer-with-jetpack-compose-0e2f16d64664
@Composable
private fun Header(
    viewModel: RunWorkoutViewModel,
    onSave: () -> Unit,
    onSaveSet: (Float, Float) -> Unit// Callback for when Save is clicked
) {
    val workoutWithExercises = viewModel.workoutWithExercises.value
    val exercises = workoutWithExercises?.exercises
    val currentExerciseIndex = viewModel.currentExerciseIndex

    val currentExercise = exercises?.getOrNull(currentExerciseIndex)

    val unit1Text = currentExercise?.measurement?.unit1
    val unit2Text = currentExercise?.measurement?.unit2

    var unit1Input by rememberSaveable { mutableStateOf("0") }
    var unit2Input by rememberSaveable { mutableStateOf("0") }

    var unit1Value by rememberSaveable { mutableFloatStateOf(0f) }
    var unit2Value by rememberSaveable { mutableFloatStateOf(0f) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager: FocusManager = LocalFocusManager.current

    val isValidInput = remember(unit1Input, unit2Input) {
        val unit1Valid = unit1Input.toFloatOrNull()?.let { it > 0 } ?: false
        val unit2Valid = unit2Input.toFloatOrNull()?.let { it > 0 } ?: false
        unit1Valid && unit2Valid
    }

    Column {
        Text(text = viewModel.workoutWithExercises.value?.exercises?.get(currentExerciseIndex)?.name ?: "Exercise", style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Button(onClick = {
                unit2Value = (unit2Value - 1).coerceAtLeast(0f)
                unit2Input = unit2Value.toString()
            }) {
                Text(text = "-")
            }
            TextField(
                value = unit2Input,
                onValueChange = { newValue ->
                    unit2Input = newValue.ifEmpty { "0" }
                    unit2Value = newValue.toFloatOrNull() ?: 0f
                },
                label = {
                    if (unit2Text != null) {
                        Text(text = unit2Text)
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                )
            )
            IconButton(onClick = {
                unit2Value += 1
                unit2Input = unit2Value.toString()
            }) {
                Icon(painter = painterResource(id = R.drawable.plus), contentDescription = stringResource(
                    id = R.string.plus
                ))
            }
        }

        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Button(onClick = {
                unit1Value = (unit1Value - 1).coerceAtLeast(0f)
                unit1Input = unit1Value.toString()
            }) {
                Text(text = "-")
            }
            TextField(
                value = unit1Input,
                onValueChange = { newValue ->
                    unit1Input = newValue.ifEmpty { "0" }
                    unit1Value = newValue.toFloatOrNull() ?: 0f
                },
                label = {
                    if (unit1Text != null) {
                        Text(text = unit1Text)
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                )
            )
            IconButton(onClick = {
                unit1Value += 1
                unit1Input = unit1Value.toString()
            }) {
                Icon(painter = painterResource(id = R.drawable.plus), contentDescription = stringResource(
                    id = R.string.plus
                ))
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Button(onClick = {
                onSaveSet(unit1Value, unit2Value)
                onSave()
            },
                enabled = isValidInput,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(text = "Save Set")
            }
            Button(onClick = {
                unit1Value = 0f
                unit2Value = 0f
                unit1Input = "0"
                unit2Input = "0"
            },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(text = "Clear")
            }
        }
    }
}

// Updated Timer composable to remove Start/Stop button
@Composable
fun Timer(
    totalTime: Long,
    currentTime: Long,
    isTimerRunning: Boolean,
    restartTimer: Boolean,  // Add a flag to handle timer reset
    onRestartHandled: () -> Unit,  // Callback to signal when restart is handled
    handleColor: Color,
    activeBarColor: Color,
    inactiveBarColor: Color,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 5.dp,
    timerSize: Dp = 120.dp  // Adjustable timer size, default to 120.dp
) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    var value by remember { mutableFloatStateOf(1f) }  // Start with 1f (100% of the total time)
    var internalCurrentTime by remember { mutableLongStateOf(currentTime) }

    // Reset timer logic when restart is triggered
    LaunchedEffect(key1 = restartTimer) {
        if (restartTimer) {
            internalCurrentTime = totalTime  // Reset the internal time
            value = 1f  // Reset the circle to full
            onRestartHandled()  // Notify that restart has been handled
        }
    }

    // Timer countdown logic
    LaunchedEffect(key1 = isTimerRunning, key2 = internalCurrentTime) {
        if (isTimerRunning) {
            while (internalCurrentTime > 0) {
                delay(100L)  // Update every 100 milliseconds
                internalCurrentTime -= 100L
                value = internalCurrentTime / totalTime.toFloat()
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.onSizeChanged { size = it }
    ) {
        // The circular timer
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(timerSize)  // Adjust the size of the timer here
        ) {
            Canvas(modifier = Modifier.size(timerSize)) {  // Use the same size for the canvas
                drawArc(
                    color = inactiveBarColor,
                    startAngle = -215f,
                    sweepAngle = 250f,
                    useCenter = false,
                    size = Size(size.width.toFloat(), size.height.toFloat()),
                    style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    color = activeBarColor,
                    startAngle = -215f,
                    sweepAngle = 250f * value,  // Draw the active arc based on the value
                    useCenter = false,
                    size = Size(size.width.toFloat(), size.height.toFloat()),
                    style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
                )
                val center = Offset(size.width / 2f, size.height / 2f)
                val beta = (250f * value + 145f) * (PI / 180f).toFloat()
                val r = size.width / 2f
                val a = cos(beta) * r
                val b = sin(beta) * r

                drawPoints(
                    listOf(Offset(center.x + a, center.y + b)),
                    pointMode = PointMode.Points,
                    color = handleColor,
                    strokeWidth = (strokeWidth * 3f).toPx(),
                    cap = StrokeCap.Round
                )
            }

            Text(
                text = (internalCurrentTime / 1000L).toString(),  // Display time in seconds
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface  // Set text color to black
            )
        }
    }
}

@Composable
fun SetContainer(
    label1: String,
    label2: String,
    unit1: Float,
    unit2: Float,
    onDelete: () -> Unit
) {

    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label2: $unit2 kg", // todo make this modular
            fontSize = 18.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "$label1: $unit1",
            fontSize = 18.sp,
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = onDelete,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Icon(painter = painterResource(id = R.drawable.delete),
                contentDescription = stringResource(id = R.string.delete))
        }
    }
}
