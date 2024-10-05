package com.example.seng303_groupb_assignment2.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.seng303_groupb_assignment2.models.UserPreferences
import com.google.gson.Gson
import org.koin.dsl.module

// Import preferencesDataStore delegate
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

val dataStoreModule = module {

    // Provide Gson instance for dependency injection
    single {
        Gson()
    }

    // Provide the DataStore instance
    single {
        val context: Context = get()
        context.dataStore
    }

    // Provide PreferencePersistentStorage with Gson and other dependencies
    single {
        val preferencesKey = stringPreferencesKey("user_preferences")
        val defaultPreferences = UserPreferences()

        PreferencePersistentStorage(
            gson = get(),            // Inject Gson instance
            dataStore = get(),       // Inject DataStore instance
            preferenceKey = preferencesKey,
            defaultValue = defaultPreferences
        )
    }
}
