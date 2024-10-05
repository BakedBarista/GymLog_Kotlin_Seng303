package com.example.seng303_groupb_assignment2.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.seng303_groupb_assignment2.datastore.PreferencePersistentStorage
import com.example.seng303_groupb_assignment2.models.UserPreferences
import kotlinx.coroutines.launch

class PreferenceViewModel(
    private val preferenceStorage: PreferencePersistentStorage<UserPreferences>
) : ViewModel() {

    val preferences = preferenceStorage.get().asLiveData()

    fun updateDarkMode(darkMode: Boolean) {
        viewModelScope.launch {
            preferenceStorage.update { it.copy(darkMode = darkMode) }
        }
    }

    fun updateMetricUnits(metricUnits: Boolean) {
        viewModelScope.launch {
            preferenceStorage.update { it.copy(metricUnits = metricUnits) }
        }
    }

    fun updateSoundOn(soundOn: Boolean) {
        viewModelScope.launch {
            preferenceStorage.update { it.copy(soundOn = soundOn) }
        }
    }
}

