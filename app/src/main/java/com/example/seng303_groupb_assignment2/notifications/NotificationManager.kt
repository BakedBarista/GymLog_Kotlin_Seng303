package com.example.seng303_groupb_assignment2.notifications

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.seng303_groupb_assignment2.R
import com.example.seng303_groupb_assignment2.datastore.PreferencePersistentStorage
import com.example.seng303_groupb_assignment2.models.UserPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.random.Random

class NotificationManager(
    private val context: Context,
    private val preferenceStorage: PreferencePersistentStorage<UserPreferences>
) {
    private val notificationManager = context.getSystemService(NotificationManager::class.java)
    private val notificationChannelID = "schedule_channel_id"

    fun sendNewWorkoutNotification(day: String, workoutName: String) {
        sendNotification(
            context.getString(R.string.notification_new_workout_title),
            context.getString(R.string.notification_new_workout_content, day, workoutName)
        )
    }

    fun sendWorkoutNotification(workoutName: String) {
        sendNotification(
            context.getString(R.string.notification_schedule_title),
            context.getString(R.string.notification_schedule_content, workoutName)
        )
    }

    private fun sendNotification(title: String, content: String) {
        CoroutineScope(Dispatchers.IO).launch {
            preferenceStorage.get().collect { preferences ->
                val notificationBuilder = NotificationCompat.Builder(context, notificationChannelID)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setSmallIcon(R.drawable.run)
                    .setAutoCancel(true)

                if (preferences.soundOn) {
                    Log.d("Preferences", "Sound on")
                    notificationBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND)
                } else {
                    Log.d("Preferences", "Sound off")

                    notificationBuilder.setSound(null)
                }

                notificationManager.notify(Random.nextInt(), notificationBuilder.build())
            }
        }
    }

    fun setupDailyNotifications() {
        turnOffNotifications()
        turnOnNotifications()
        Log.i("NotificationManager", "Setup alarm")
    }

    private fun turnOnNotifications() {
        val intent = Intent(context, NotificationReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 3)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    private fun turnOffNotifications() {
        val intent = Intent(context, NotificationReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
}