package com.example.seng303_groupb_assignment2.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Help(
    navController: NavController
) {
    Column {
        Text(text = "Push-Up", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(text = "A basic bodyweight exercise targeting chest, shoulders, and triceps. Start in a plank position, lower your body until your chest nearly touches the floor, and then push back up.", fontSize = 16.sp)

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Bench Press", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(text = "An upper body exercise that targets the chest, shoulders, and triceps. Lie back on a bench, hold a barbell with hands shoulder-width apart, lower it to your chest, then press it back up.", fontSize = 16.sp)

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Squat", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(text = "A compound movement focusing on the legs and glutes. Stand with feet shoulder-width apart, lower your hips back and down as if sitting in a chair, and return to standing.", fontSize = 16.sp)
    }
}

