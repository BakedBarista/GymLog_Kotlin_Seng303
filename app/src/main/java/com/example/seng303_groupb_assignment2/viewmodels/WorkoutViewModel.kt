package com.example.seng303_groupb_assignment2.viewmodels

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.seng303_groupb_assignment2.database.AppDatabase
import com.example.seng303_groupb_assignment2.entities.Workout
import kotlinx.coroutines.launch

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {
    private val workoutDao = AppDatabase.getDatabase(application).workoutDao()
    val allWorkouts: LiveData<List<Workout>> = workoutDao.getAllWorkouts().asLiveData()

    fun addWorkout(workout: Workout) {
        viewModelScope.launch {
            workoutDao.upsertWorkout(workout)
        }
    }
}