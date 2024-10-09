package com.example.seng303_groupb_assignment2.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.example.seng303_groupb_assignment2.R

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Help(navController: NavController) {
    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                ExerciseCard(
                    title = "Push-Up",
                    onClick = {
                        navController.navigate("push_up_help")
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                ExerciseCard(
                    title = "Bench Press",
                    onClick = {
                        navController.navigate("bench_press_help")
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                ExerciseCard(
                    title = "Squat",
                    onClick = {
                        navController.navigate("squat_help")
                    }
                )
            }
        }
    }
}

@Composable
fun ExerciseCard(title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
                android.util.Log.d("HelpScreen", "$title card clicked")
            }
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondary
            )
        }
    }
}

@Composable
fun VideoGuide(context: Context, link: String) {
    Button(onClick = {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        context.startActivity(intent)
    }) {
        Text("Watch Video Guide")
    }
}

fun openVideoGuide(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}

@Composable
fun PushUpHelpScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Push-Up",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "A basic bodyweight exercise targeting chest, shoulders, and triceps. Start in a plank position, lower your body until your chest nearly touches the floor, and then push back up.",
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )
            val context = LocalContext.current
            Text(
                text = "Watch Video Guide",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    openVideoGuide(context, getString(context, R.string.push_up))
                }
            )
        }
    }
}

@Composable
fun BenchPressHelpScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Bench Press",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "An upper body exercise that targets the chest, shoulders, and triceps. Lie back on a bench, hold a barbell with hands shoulder-width apart, lower it to your chest, then press it back up.",
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )
            val context = LocalContext.current
            Text(
                text = "Watch Video Guide",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    openVideoGuide(context, getString(context, R.string.bench_press))
                }
            )
        }
    }
}

@Composable
fun SquatHelpScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Squat",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "A compound movement focusing on the legs and glutes. Stand with feet shoulder-width apart, lower your hips back and down as if sitting in a chair, and return to standing.",
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )
            val context = LocalContext.current
            Text(
                text = "Watch Video Guide",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    openVideoGuide(context, getString(context, R.string.squat))
                }
            )
        }
    }
}
