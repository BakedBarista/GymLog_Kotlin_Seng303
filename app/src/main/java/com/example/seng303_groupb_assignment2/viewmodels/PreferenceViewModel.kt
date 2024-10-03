package com.example.seng303_groupb_assignment2.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.seng303_groupb_assignment2.database.AppDatabase
import com.example.seng303_groupb_assignment2.entities.Preference
import kotlinx.coroutines.launch

class PreferenceViewModel(application: Application) : AndroidViewModel(application) {

    private val preferenceDao = AppDatabase.getDatabase(application).preferenceDao()

    val preferences = preferenceDao.getPreferences().asLiveData()

    // Update preferences
    fun setPreferences(darkMode: Boolean, metricUnits: Boolean, soundOn: Boolean) {
        viewModelScope.launch {
            val newPreference = Preference(id = 1, darkMode = darkMode, metricUnits = metricUnits, soundOn = soundOn)
            preferenceDao.upsertPreference(newPreference)
        }
    }
}
