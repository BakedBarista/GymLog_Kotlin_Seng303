package com.example.seng303_groupb_assignment2.screens

import android.media.MediaPlayer
import android.util.Log
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.seng303_groupb_assignment2.R
import com.example.seng303_groupb_assignment2.viewmodels.PreferenceViewModel
import com.example.seng303_groupb_assignment2.viewmodels.RunWorkoutViewModel
import org.koin.androidx.compose.koinViewModel
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RunWorkout(
    viewModel: RunWorkoutViewModel,
    navController: NavController,
    preferenceViewModel: PreferenceViewModel
) {
    val buttonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    )

    val workoutWithExercises = viewModel.workoutWithExercises.value
    val exercises = workoutWithExercises?.exercises
    val currentExerciseIndex = viewModel.currentExerciseIndex

    val currentExercise = exercises?.getOrNull(currentExerciseIndex)
    val unit1Text = currentExercise?.measurement?.unit1
    val unit2Text = currentExercise?.measurement?.unit2
    val unitMeasurement = currentExercise?.measurement?.measurement
    val restTime = currentExercise?.restTime ?: 0

    var isTimerRunning by rememberSaveable {
        mutableStateOf(false)
    }
    var currentTime by rememberSaveable {
        mutableLongStateOf(restTime * 1000L)
    }
    var restartTimer by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(currentExerciseIndex) {
        currentTime = restTime * 1000L
    }

    val sets = viewModel.getSetsForCurrentExercise()

    val isPreviousEnabled = currentExerciseIndex > 0
    val isNextEnabled = currentExerciseIndex < (exercises?.size ?: 0) - 1

    fun onSaveSet(unit1: Float, unit2: Float) {
        viewModel.addSetToCurrentExercise(unit1, unit2)
        currentTime = restTime * 1000L
        restartTimer = true
        isTimerRunning = true
    }

    fun stopTimer() {
        currentTime = restTime * 1000L
        restartTimer = true
        isTimerRunning = false
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Header(viewModel, onSave = {
                viewModel.isTimerRunning = true
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
                    stopTimer()
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
                    initialTime = currentTime,
                    isTimerRunning = isTimerRunning,
                    restartTimer = restartTimer,
                    onRestartHandled = { restartTimer = false },
                    handleColor = Color.Green,
                    inactiveBarColor = Color.DarkGray,
                    activeBarColor = Color(0xFF37B900),
                    modifier = Modifier.size(100.dp),
                    timerSize = 100.dp,
                    preferenceViewModel = preferenceViewModel,
                    currentExerciseIndex = currentExerciseIndex
                )
                IconButton(
                    onClick = {
                        viewModel.nextExercise()
                        stopTimer()
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
                measurement = unitMeasurement,
                onDelete = {
                    viewModel.removeSetFromCurrentExercise(index)
                }
            )
        }

        item {
            Button(
                onClick = {
                    viewModel.saveLogs {
                        viewModel.clearWorkoutData()
                        navController.navigate("SelectWorkout")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = buttonColors,
                shape = RectangleShape
            ) {
                Text("Finish Workout")
            }
        }
    }
}

@Composable
private fun Header(
    viewModel: RunWorkoutViewModel,
    onSave: () -> Unit,
    onSaveSet: (Float, Float) -> Unit
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

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = viewModel.workoutWithExercises.value?.exercises?.get(currentExerciseIndex)?.name ?: "Exercise", style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            IconButton(onClick = {
                unit2Value = (unit2Value - 1).coerceAtLeast(0f)
                unit2Input = unit2Value.toString()
            }) {
                Icon(modifier = Modifier.size(48.dp),
                    painter = painterResource(id = R.drawable.remove),
                    contentDescription = stringResource(id = R.string.remove
                ))
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
            IconButton(onClick = {
                unit1Value = (unit1Value - 1).coerceAtLeast(0f)
                unit1Input = unit1Value.toString()
            }) {
                Icon(modifier = Modifier.size(48.dp),
                    painter = painterResource(id = R.drawable.remove),
                    contentDescription = stringResource(id = R.string.remove
                    ))
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
            val buttonColors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Button(onClick = {
                Log.d("Status before", "currentTime: " + viewModel.currentTime + " restartTimer: " + viewModel.restartTimer + " isTimerRunning: " + viewModel.isTimerRunning)
                onSaveSet(unit1Value, unit2Value)
                Log.d("Status mid", "currentTime: " + viewModel.currentTime + " restartTimer: " + viewModel.restartTimer + " isTimerRunning: " + viewModel.isTimerRunning)
                onSave()
                Log.d("Status", "currentTime: " + viewModel.currentTime + " restartTimer: " + viewModel.restartTimer + " isTimerRunning: " + viewModel.isTimerRunning)
            },
                colors = buttonColors,
                shape = RectangleShape,
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
                colors = buttonColors,
                shape = RectangleShape,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(text = "Clear")
            }
        }
    }
}

// Gotten from https://medium.com/@fahadhabib01/craft-a-captivating-animated-countdown-timer-with-jetpack-compose-0e2f16d64664
@Composable
fun Timer(
    totalTime: Long,
    initialTime: Long,
    isTimerRunning: Boolean,
    restartTimer: Boolean,
    onRestartHandled: () -> Unit,
    handleColor: Color,
    activeBarColor: Color,
    inactiveBarColor: Color,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 5.dp,
    preferenceViewModel: PreferenceViewModel,
    timerSize: Dp = 120.dp,
    currentExerciseIndex: Int
) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    var value by rememberSaveable(currentExerciseIndex) { mutableFloatStateOf(1f) }
    var internalCurrentTime by rememberSaveable(currentExerciseIndex) { mutableLongStateOf(initialTime) }
    var lastFrameTimeNanos by rememberSaveable(currentExerciseIndex) { mutableLongStateOf(0L) }

    val context = LocalContext.current
    val preferences by preferenceViewModel.preferences.observeAsState()
    val isSoundOn = preferences?.soundOn ?: true

    val mediaPlayer = remember {
        MediaPlayer.create(context, R.raw.timer_finish)
    }

    var soundPlayed by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(isTimerRunning, restartTimer, currentExerciseIndex) {
        if (restartTimer) {
            internalCurrentTime = totalTime
            value = 1f
            lastFrameTimeNanos = 0L
            soundPlayed = false
            onRestartHandled()
        }

        if (isTimerRunning) {
            lastFrameTimeNanos = 0L

            while (internalCurrentTime > 0 && isTimerRunning) {
                withFrameNanos { frameTimeNanos ->
                    if (lastFrameTimeNanos > 0L) {
                        val deltaNanos = frameTimeNanos - lastFrameTimeNanos
                        val deltaMillis = deltaNanos / 1_000_000L

                        internalCurrentTime = (internalCurrentTime - deltaMillis).coerceAtLeast(0L)
                        value = internalCurrentTime / totalTime.toFloat()
                    }
                    lastFrameTimeNanos = frameTimeNanos
                }
            }

            if (internalCurrentTime <= 0 && !soundPlayed) {
                soundPlayed = true
                if (isSoundOn) {
                    mediaPlayer.start()
                    Log.d("Timer", "Sound played at the end of timer.")
                } else {
                    Log.d("Timer", "Sound muted (sound is off in preferences).")
                }
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.onSizeChanged { size = it }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(timerSize)
        ) {
            Canvas(modifier = Modifier.size(timerSize)) {
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
                    sweepAngle = 250f * value,
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
                text = (internalCurrentTime / 1000L).toString(),
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }
}




@Composable
fun SetContainer(
    label1: String,
    label2: String,
    unit1: Float,
    unit2: Float,
    measurement: List<String>?,
    onDelete: () -> Unit,
    preferenceViewModel: PreferenceViewModel = koinViewModel()
) {
    val preferences = preferenceViewModel.preferences.observeAsState(null).value
    val metricUnits = preferences?.metricUnits ?: false

    fun requiresUnit(label: String): Boolean {
        return label == "Weight" || label == "Distance"
    }

    val measurementUnit = if (metricUnits) measurement?.get(0) else measurement?.get(1)

    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (requiresUnit(label2)) "$label2: $unit2 $measurementUnit" else "$label2: $unit2",
            fontSize = 18.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = if (requiresUnit(label1)) "$label1: $unit1 $measurementUnit" else "$label1: $unit1",
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
