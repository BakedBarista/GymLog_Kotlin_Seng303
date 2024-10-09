package com.example.seng303_groupb_assignment2.screens

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.seng303_groupb_assignment2.notifications.NotificationManager
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Home(
    navController: NavController
) {
    val context = LocalContext.current
    val permissionState = rememberMultiplePermissionsState(permissions = listOf(Manifest.permission.CAMERA, Manifest.permission.POST_NOTIFICATIONS))
    val notificationHandler = NotificationManager(context)

    LaunchedEffect(true) {
        permissionState.launchMultiplePermissionRequest()
        permissionState.permissions.forEach {
            when (it.permission) {
                Manifest.permission.POST_NOTIFICATIONS -> {
                notificationHandler.setupDailyNotifications()
                }
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Button(
            onClick = {
                navController.navigate("QRScanner")
                },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Scan QR Code")
        }
    }
}
