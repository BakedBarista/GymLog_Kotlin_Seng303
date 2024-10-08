package com.example.seng303_groupb_assignment2.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.seng303_groupb_assignment2.models.UserPreferences
import com.google.gson.Gson
import org.koin.dsl.module

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

val dataStoreModule = module {

    single {
        Gson()
    }
    single {
        val context: Context = get()
        context.dataStore
    }

    single {
        val preferencesKey = stringPreferencesKey("user_preferences")
        val defaultPreferences = UserPreferences()

        PreferencePersistentStorage(
            gson = get(),
            dataStore = get(),
            preferenceKey = preferencesKey,
            defaultValue = defaultPreferences
        )
    }
}
