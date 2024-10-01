package com.example.seng303_groupb_assignment2.datastore

import com.example.seng303_groupb_assignment2.viewmodels.ExerciseViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val dataAccessModule = module {
    viewModel { ExerciseViewModel(get()) }
}