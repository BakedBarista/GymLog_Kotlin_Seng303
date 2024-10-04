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
import com.example.seng303_groupb_assignment2.enums.Days
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import kotlin.random.Random


class NotificationManager(private val context: Context) {
    private val notificationManager = context.getSystemService(NotificationManager::class.java)
    private val notificationChannelID = "schedule_channel_id"
    private val repeatNotification: List<Long> = listOf()

    @RequiresApi(Build.VERSION_CODES.S)
    fun sendNotification(intent: Intent) {
        val workoutName = intent.getStringExtra("workoutName")
        val notification = NotificationCompat.Builder(context, notificationChannelID)
            .setContentTitle(context.getString(R.string.notification_schedule_title))
            .setContentText(context.getString(R.string.notification_schedule_content, workoutName))
            .setSmallIcon(R.drawable.run)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(Random.nextInt(), notification)

        scheduleNotificationOnDay(workoutName = workoutName ?: "", day = getCurrentDay())
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun scheduleNotificationOnDay(workoutName: String, day: Days) {
        scheduleNotification(workoutName, timeInMillsForDaysAtHour(day))
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun scheduleNotificationTest(workoutName: String) {
        scheduleNotification(workoutName, System.currentTimeMillis())
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun scheduleNotification(workoutName: String, timeInMillis: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (!alarmManager.canScheduleExactAlarms()) {
            context.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
            return
        }

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("workoutName", workoutName)
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
        val nextDateForDay: LocalDateTime = dateTime.with(TemporalAdjusters.next(day.toDayOfWeek())).withHour(4)
        val zoneId = ZoneId.systemDefault()
        val timeInMillis: Long = nextDateForDay.atZone(zoneId).toInstant().toEpochMilli()
        return timeInMillis
    }

    private fun getCurrentDay(): Days {
        val currentDateTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(System.currentTimeMillis()),
            ZoneId.systemDefault()
        )
        val dayOfWeek = currentDateTime.dayOfWeek

        return when (dayOfWeek) {
            DayOfWeek.SUNDAY -> Days.SUNDAY
            DayOfWeek.MONDAY -> Days.MONDAY
            DayOfWeek.TUESDAY -> Days.TUESDAY
            DayOfWeek.WEDNESDAY -> Days.WEDNESDAY
            DayOfWeek.THURSDAY -> Days.THURSDAY
            DayOfWeek.FRIDAY -> Days.FRIDAY
            DayOfWeek.SATURDAY -> Days.SATURDAY
        }
    }
}