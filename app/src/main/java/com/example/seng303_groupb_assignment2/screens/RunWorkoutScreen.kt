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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seng303_groupb_assignment2.entities.Exercise
import com.example.seng303_groupb_assignment2.viewmodels.RunWorkoutViewModel
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RunWorkout(
    viewModel: RunWorkoutViewModel
) {
    val workoutWithExercises = viewModel.workoutWithExercises.value
    val exercises = workoutWithExercises?.exercises
    val currentExerciseIndex = viewModel.currentExerciseIndex

    // Retrieve current exercise and rest time
    val currentExercise = exercises?.getOrNull(currentExerciseIndex)
    val restTime = currentExercise?.restTime ?: 0 // Default to 0 if no restTime is available

    var isTimerRunning by rememberSaveable { mutableStateOf(false) }
    var currentTime by rememberSaveable { mutableLongStateOf(restTime * 1000L) } // Timer starts with the total rest time
    var restartTimer by rememberSaveable { mutableStateOf(false) } // Flag to reset the timer
    var sets = viewModel.getSetsForCurrentExercise()

    // Function to reset the timer when "Save Set" is pressed
    fun onSaveSet(unit1: Float, unit2: Float) {
        viewModel.addSetToCurrentExercise(unit1, unit2)
        currentTime = restTime * 1000L  // Reset the timer
        restartTimer = true             // Signal to LaunchedEffect to restart the timer
        isTimerRunning = true           // Ensure the timer starts running again
    }

    Column {
        Header(viewModel, onSave = {
            isTimerRunning = true // Start the timer when "Save" is clicked
        }, onSaveSet = ::onSaveSet)

        // Timer Box
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(onClick = {
                viewModel.previousExercise()
                isTimerRunning = false  // Reset timer on exercise change
            }) {
                Text("Previous")
            }
            Timer(
                totalTime = restTime * 1000L,
                currentTime = currentTime,  // Pass currentTime as a parameter
                isTimerRunning = isTimerRunning,
                restartTimer = restartTimer,  // Flag to trigger timer reset
                onRestartHandled = { restartTimer = false },  // Reset flag once handled
                handleColor = Color.Green,
                inactiveBarColor = Color.DarkGray,
                activeBarColor = Color(0xFF37B900),
                modifier = Modifier.size(100.dp),  // Set the size smaller, or use the new parameter
                timerSize = 100.dp  // Set this to a smaller value to shrink the timer
            )
            // Next Button
            Button(onClick = {
                viewModel.nextExercise()
                isTimerRunning = false  // Reset timer on exercise change
            }) {
                Text("Next")
            }
        }

        // Display saved sets in LazyColumn
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            itemsIndexed(sets) { index, set ->
                SetContainer(
                    unit1 = set.first,
                    unit2 = set.second,
                    onDelete = {
                        viewModel.removeSetFromCurrentExercise(index)
                    }
                )
            }
        }
        Button(onClick = {
            viewModel.saveLogs() // Persist the workout progress
        }) {
            Text("Finish Workout")
        }
    }
}

/**
 * Rounds a float value to the nearest 0.5.
 */
fun roundToNearestHalf(value: Float): Float {
    return (Math.round(value * 2) / 2.0).toFloat()
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

    Column {
        Text(text = viewModel.workoutWithExercises.value?.exercises?.get(currentExerciseIndex)?.name ?: "Exercise")
        if (unit2Text != null) {
            Text(text = unit2Text)
        }
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
                    unit2Input = newValue
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
            Button(onClick = {
                unit2Value += 1
                unit2Input = unit2Value.toString()
            }) {
                Text(text = "+")
            }
        }
        if (unit1Text != null) {
            Text(text = unit1Text)
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
                    unit1Input = newValue
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
            Button(onClick = {
                unit1Value += 1
                unit1Input = unit1Value.toString()
            }) {
                Text(text = "+")
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Button(onClick = {
                onSaveSet(unit1Value, unit2Value)
                onSave()
                unit1Value = 0f
                unit2Value = 0f
                unit1Input = "0"
                unit2Input = "0"
            }) {
                Text(text = "Save Set")
            }
            Button(onClick = {
                unit1Value = 0f
                unit2Value = 0f
                unit1Input = "0"
                unit2Input = "0"
            }) {
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
            text = "Weight: $unit2 kg", // todo make this modular
            fontSize = 18.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "Reps: $unit1",
            fontSize = 18.sp,
            modifier = Modifier.weight(1f)
        )
        Button(
            onClick = onDelete,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text(text = "Delete", color = Color.White)
        }
    }
}
