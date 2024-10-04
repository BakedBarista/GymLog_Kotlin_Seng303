package com.example.seng303_groupb_assignment2.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi

class NotificationReceiver: BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onReceive(context: Context, intent: Intent) {
        val notificationHandler = NotificationManager(context)

        // TODO - implement this to notify for scheduled workouts
        notificationHandler.sendWorkoutNotification("test")
    }
}