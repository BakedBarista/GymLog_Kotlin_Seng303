package com.example.seng303_groupb_assignment2.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.seng303_groupb_assignment2.daos.ExerciseLogDao
import com.example.seng303_groupb_assignment2.daos.WorkoutDao
import com.example.seng303_groupb_assignment2.datastore.PreferencePersistentStorage
import com.example.seng303_groupb_assignment2.entities.ExerciseLog
import com.example.seng303_groupb_assignment2.entities.WorkoutWithExercises
import com.example.seng303_groupb_assignment2.models.UserPreferences
import com.example.seng303_groupb_assignment2.services.MeasurementConverter
import kotlinx.coroutines.launch

class RunWorkoutViewModel(
    private val workoutDao: WorkoutDao,
    private val exerciseLogDao: ExerciseLogDao,
    private val savedStateHandle: SavedStateHandle,
    private val preferenceStorage: PreferencePersistentStorage<UserPreferences>
) : ViewModel() {

    private val KEY_CURRENT_TIME = "current_time"
    private val KEY_IS_TIMER_RUNNING = "is_timer_running"
    private val KEY_RESTART_TIMER = "restart_timer"

    val preferences = preferenceStorage.get().asLiveData()
    private var isMetric = preferences.value?.metricUnits ?: true

    private lateinit var measurementConverter: MeasurementConverter


    var currentTime: Long
        get() = savedStateHandle.get<Long>(KEY_CURRENT_TIME) ?: 0L
        set(value) {
            savedStateHandle[KEY_CURRENT_TIME] = value
        }

    var isTimerRunning: Boolean
        get() = savedStateHandle.get<Boolean>(KEY_IS_TIMER_RUNNING) ?: false
        set(value) {
            savedStateHandle[KEY_IS_TIMER_RUNNING] = value
        }

    var restartTimer: Boolean
        get() = savedStateHandle.get<Boolean>(KEY_RESTART_TIMER) ?: false
        set(value) {
            savedStateHandle[KEY_RESTART_TIMER] = value
        }

    var currentExerciseIndex by mutableIntStateOf(0)
    private val _workoutWithExercises = MutableLiveData<WorkoutWithExercises?>()
    val workoutWithExercises: LiveData<WorkoutWithExercises?> = _workoutWithExercises

    private var exerciseSets = mutableMapOf<Int, SnapshotStateList<Pair<Float, Float>>>()

    init {
        preferences.observeForever { userPreferences ->
            userPreferences?.let {
                isMetric = it.metricUnits
                measurementConverter = MeasurementConverter(isMetric)
            }
        }
    }

    fun clearWorkoutData() {
        currentExerciseIndex = 0
        exerciseSets.clear()
        _workoutWithExercises.value = null
    }

    fun getSetsForCurrentExercise(): SnapshotStateList<Pair<Float, Float>> {
        return exerciseSets.getOrPut(currentExerciseIndex) { mutableStateListOf() }
    }

    fun addSetToCurrentExercise(unit1: Float, unit2: Float) {
        val sets = getSetsForCurrentExercise()
        sets.add(Pair(unit1, unit2))
    }

    fun removeSetFromCurrentExercise(index: Int) {
        val sets = getSetsForCurrentExercise()
        if (index >= 0 && index < sets.size) {
            sets.removeAt(index)
        }
    }

    fun saveLogs(onComplete: () -> Unit) {
        viewModelScope.launch {
            exerciseSets.forEach { (exerciseIndex, sets) ->
                val exercise = workoutWithExercises.value?.exercises?.get(exerciseIndex)
                Log.d("Prefs", isMetric.toString())
                if (exercise != null && sets.isNotEmpty()) {
                    val measurementType = if (exercise.measurement.unit1 == "Distance") {
                        "Distance"
                    } else {
                        "Weight"
                    }
                    Log.d("Prefs", isMetric.toString())
                    val convertedSets = measurementConverter.convertSetToMetric(sets, measurementType).toMutableList()
                    val exerciseLog = ExerciseLog(
                        exerciseId = exercise.id,
                        record = convertedSets,
                        timestamp = System.currentTimeMillis()
                    )
                    exerciseLogDao.upsertExerciseLog(exerciseLog)
                }
            }
            onComplete()
        }
    }


    fun loadWorkoutWithExercises(workoutId: Long) {
        viewModelScope.launch {
            _workoutWithExercises.value = workoutDao.getWorkoutWithExercises(workoutId)
        }
    }

    fun nextExercise() {
        if (currentExerciseIndex < (workoutWithExercises.value?.exercises?.size ?: 0) - 1) {
            currentExerciseIndex++
        }
    }

    fun previousExercise() {
        if (currentExerciseIndex > 0) {
            currentExerciseIndex--
        }
    }
}