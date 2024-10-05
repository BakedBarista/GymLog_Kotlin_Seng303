package com.example.seng303_groupb_assignment2.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencePersistentStorage<T : Any>(
    private val gson: Gson,
    private val dataStore: DataStore<Preferences>,
    private val preferenceKey: Preferences.Key<String>,
    private val defaultValue: T
) : PreferenceStorage<T> {

    override fun get(): Flow<T> {
        return dataStore.data.map { preferences ->
            val jsonString = preferences[preferenceKey]
            if (jsonString != null) {
                gson.fromJson(jsonString, defaultValue.javaClass)
            } else {
                defaultValue
            }
        }
    }

    override suspend fun set(data: T) {
        dataStore.edit { preferences ->
            val jsonString = gson.toJson(data)
            preferences[preferenceKey] = jsonString
        }
    }

    override suspend fun update(updateFunction: (T) -> T) {
        dataStore.edit { preferences ->
            val currentJsonString = preferences[preferenceKey]
            val currentData = if (currentJsonString != null) {
                gson.fromJson(currentJsonString, defaultValue.javaClass)
            } else {
                defaultValue
            }
            val updatedData = updateFunction(currentData)
            val updatedJsonString = gson.toJson(updatedData)
            preferences[preferenceKey] = updatedJsonString
        }
    }
}

interface PreferenceStorage<T : Any> {
    fun get(): Flow<T>
    suspend fun set(data: T)
    suspend fun update(updateFunction: (T) -> T)
}