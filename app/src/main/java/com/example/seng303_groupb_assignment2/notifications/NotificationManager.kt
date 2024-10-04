package com.example.seng303_groupb_assignment2.notifications

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.seng303_groupb_assignment2.R
import com.example.seng303_groupb_assignment2.entities.Workout
import com.example.seng303_groupb_assignment2.enums.Days
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters
import kotlin.random.Random


class NotificationManager(private val context: Context) {
    private val notificationManager = context.getSystemService(NotificationManager::class.java)
    private val notificationChannelID = "schedule_channel_id"

    @RequiresApi(Build.VERSION_CODES.S)
    fun sendNotification() {
        val notification = NotificationCompat.Builder(context, notificationChannelID)
            .setContentTitle(context.getString(R.string.notification_schedule_title))
            .setContentText(context.getString(R.string.notification_schedule_content, "Bench"))
            .setSmallIcon(R.drawable.run)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(Random.nextInt(), notification)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun scheduleNotificationOnDay(workout: Workout, day: Days) {
        scheduleNotification(workout, timeInMillsForDaysAtHour(day))
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun scheduleNotificationTest(workout: Workout) {
        scheduleNotification(workout, System.currentTimeMillis())
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun scheduleNotification(workout: Workout, timeInMillis: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (!alarmManager.canScheduleExactAlarms()) {
            context.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
            return
        }

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("workoutName", workout.name)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            pendingIntent
        )
    }

    private fun timeInMillsForDaysAtHour(day: Days): Long {
        val dateTime: LocalDateTime = LocalDateTime.now()
        val nextMonday: LocalDateTime = dateTime.with(TemporalAdjusters.next(DayOfWeek.MONDAY))
        return 1L;
    }
}