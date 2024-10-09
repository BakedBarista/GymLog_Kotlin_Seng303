package com.example.seng303_groupb_assignment2

import android.Manifest
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.seng303_groupb_assignment2.datastore.dataAccessModule
import com.example.seng303_groupb_assignment2.datastore.dataStoreModule
import org.koin.android.ext.koin.androidContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import org.koin.core.context.startKoin

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MainApplication)
            modules(listOf(dataAccessModule, dataStoreModule))
        }
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val notificationChannel = NotificationChannel(
            "schedule_channel_id",
            "Schedule channel",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationChannel.description = "Channel for notifying user of upcoming scheduled workouts"

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }
}