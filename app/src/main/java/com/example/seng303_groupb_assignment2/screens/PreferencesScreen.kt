package com.example.seng303_groupb_assignment2.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.seng303_groupb_assignment2.entities.Preference
import com.example.seng303_groupb_assignment2.viewmodels.PreferenceViewModel

@Composable
fun ViewPreferences(
    navController: NavController,
    preferenceViewModel: PreferenceViewModel = viewModel()
) {
    val preferences = preferenceViewModel.preferences.observeAsState(
        null
    ).value

    val darkMode = preferences?.darkMode ?: false
    val metricUnits = preferences?.metricUnits ?: false
    val soundOn = preferences?.soundOn ?: false

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Dark Mode toggle
        PreferenceToggleRow(
            title = "Dark Mode",
            isChecked = darkMode,
            onCheckedChange = { preferenceViewModel.setPreferences(it, metricUnits, soundOn) }
        )

        // Units toggle
        PreferenceToggleRow(
            title = "Units (Metric / Imperial)",
            isChecked = metricUnits,
            onCheckedChange = { preferenceViewModel.setPreferences(darkMode, it, soundOn) }
        )

        // Sound On/Off toggle
        PreferenceToggleRow(
            title = "Sound",
            isChecked = soundOn,
            onCheckedChange = { preferenceViewModel.setPreferences(darkMode, metricUnits, it) }
        )
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
