package com.example.seng303_groupb_assignment2

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.example.seng303_groupb_assignment2.datastore.dataAccessModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MainApplication)
            modules(dataAccessModule)
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