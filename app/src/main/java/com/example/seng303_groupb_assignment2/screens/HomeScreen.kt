package com.example.seng303_groupb_assignment2.screens

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.seng303_groupb_assignment2.notifications.NotificationManager
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Home(
    navController: NavController
) {
    Text(text = "home")

    val context = LocalContext.current
    val postNotificationPermission = rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    val notificationHandler = NotificationManager(context)

    LaunchedEffect(true) {
        val notificationPermission = postNotificationPermission.status.isGranted
        if (!notificationPermission) {
            postNotificationPermission.launchPermissionRequest()
        }
        notificationHandler.setupDailyNotifications()
    }

}
