package com.example.seng303_groupb_assignment2.screens

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.seng303_groupb_assignment2.notifications.NotificationManager
import com.example.seng303_groupb_assignment2.viewmodels.PreferenceViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Home(
    navController: NavController,
    preferenceViewModel: PreferenceViewModel
) {
    val context = LocalContext.current
    val permissionState = rememberMultiplePermissionsState(permissions = listOf(Manifest.permission.CAMERA, Manifest.permission.POST_NOTIFICATIONS))
    val notificationHandler = NotificationManager(context, preferenceViewModel.preferenceStorage)

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
        val buttonColors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )

        Button(
            onClick = {
                navController.navigate("QRScanner")
                },
            colors = buttonColors,
            shape = RectangleShape,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Scan QR Code")
        }
    }
}
