package com.example.seng303_groupb_assignment2.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.seng303_groupb_assignment2.entities.Preference
import kotlinx.coroutines.flow.Flow

@Dao
interface PreferenceDao {

    @Upsert
    suspend fun upsertPreference(preference: Preference)

    @Query("SELECT * FROM preferences WHERE id = 1")
    fun getPreferences(): Flow<Preference>
}
