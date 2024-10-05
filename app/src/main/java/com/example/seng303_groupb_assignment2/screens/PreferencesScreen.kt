package com.example.seng303_groupb_assignment2.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.seng303_groupb_assignment2.viewmodels.PreferenceViewModel

import org.koin.androidx.compose.koinViewModel

@Composable
fun ViewPreferences(
    navController: NavController,
    preferenceViewModel: PreferenceViewModel = koinViewModel()
) {
    val preferences = preferenceViewModel.preferences.observeAsState(null).value

    val darkMode = preferences?.darkMode ?: false
    val metricUnits = preferences?.metricUnits ?: false
    val soundOn = preferences?.soundOn ?: false

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Dark Mode toggle
            PreferenceToggleRow(
                title = "Dark Mode",
                isChecked = darkMode,
                onCheckedChange = { preferenceViewModel.updateDarkMode(it) }
            )
        }

        item {
            // Units toggle
            PreferenceToggleRow(
                title = "Units (Metric / Imperial)",
                isChecked = metricUnits,
                onCheckedChange = { preferenceViewModel.updateMetricUnits(it) }
            )
        }

        item {
            // Sound On/Off toggle
            PreferenceToggleRow(
                title = "Sound",
                isChecked = soundOn,
                onCheckedChange = { preferenceViewModel.updateSoundOn(it) }
            )
        }
    }
}


@Composable
fun PreferenceToggleRow(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title)
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}
