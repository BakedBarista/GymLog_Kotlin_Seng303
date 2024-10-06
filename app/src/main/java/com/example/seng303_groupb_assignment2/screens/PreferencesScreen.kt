package com.example.seng303_groupb_assignment2.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.seng303_groupb_assignment2.viewmodels.PreferenceViewModel
import com.example.seng303_groupb_assignment2.R


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
            PreferenceToggleRow(
                title = stringResource(id = R.string.dark_mode),
                isChecked = darkMode,
                onCheckedChange = { preferenceViewModel.updateDarkMode(it) },
                startPadding = 16.dp
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.units),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            preferenceViewModel.updateMetricUnits(true)
                        }
                    ) {
                        RadioButton(
                            selected = metricUnits,
                            onClick = { preferenceViewModel.updateMetricUnits(true) }
                        )
                        Text(text = stringResource(id = R.string.metric))
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            preferenceViewModel.updateMetricUnits(false)
                        }
                    ) {
                        RadioButton(
                            selected = !metricUnits,
                            onClick = { preferenceViewModel.updateMetricUnits(false) }
                        )
                        Text(text = stringResource(id = R.string.imperial))
                    }
                }
            }
        }

        item {
            PreferenceToggleRow(
                title = stringResource(id = R.string.sound),
                isChecked = soundOn,
                onCheckedChange = { preferenceViewModel.updateSoundOn(it) },
                startPadding = 16.dp
            )
        }
    }
}

@Composable
fun PreferenceToggleRow(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    startPadding: Dp = 0.dp
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = startPadding)
        )
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}

