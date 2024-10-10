package com.example.seng303_groupb_assignment2.datastore

import androidx.lifecycle.SavedStateHandle
import com.example.seng303_groupb_assignment2.daos.ExerciseDao
import com.example.seng303_groupb_assignment2.daos.ExerciseLogDao
import com.example.seng303_groupb_assignment2.daos.WorkoutDao
import com.example.seng303_groupb_assignment2.database.AppDatabase
import com.example.seng303_groupb_assignment2.models.UserPreferences
import com.example.seng303_groupb_assignment2.viewmodels.ExerciseViewModel
import com.example.seng303_groupb_assignment2.viewmodels.PreferenceViewModel
import com.example.seng303_groupb_assignment2.viewmodels.RunWorkoutViewModel
import com.example.seng303_groupb_assignment2.viewmodels.WorkoutViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val dataAccessModule = module {
    single { AppDatabase.getDatabase(androidContext()) }
    single { get<AppDatabase>().workoutDao() }
    single { get<AppDatabase>().exerciseDao() }
    single { get<AppDatabase>().exerciseLogDao() }

    viewModel { ExerciseViewModel(get<ExerciseDao>(), get<WorkoutDao>(),  get<ExerciseLogDao>()) }
    viewModel { WorkoutViewModel(get<WorkoutDao>(), get<ExerciseLogDao>()) }
    viewModel { (handle: SavedStateHandle) -> RunWorkoutViewModel(get<WorkoutDao>(), get<ExerciseLogDao>(), handle)}
    viewModel { PreferenceViewModel(get<PreferencePersistentStorage<UserPreferences>>()) }
}