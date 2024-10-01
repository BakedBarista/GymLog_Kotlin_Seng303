package com.example.seng303_groupb_assignment2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.seng303_groupb_assignment2.screens.AddWorkout
import com.example.seng303_groupb_assignment2.screens.RunWorkout
import com.example.seng303_groupb_assignment2.screens.ViewLeaderboard
import com.example.seng303_groupb_assignment2.screens.ViewProgress
import com.example.seng303_groupb_assignment2.ui.theme.SENG303_GroupB_Assignment2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SENG303_GroupB_Assignment2Theme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = { CustomBottomAppBar(navController) }
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        NavHost(navController = navController, startDestination = "Home") {
                            composable("Home") {
                                Home(navController = navController)
                            }
                            composable("Run") {
                                RunWorkout(navController = navController)
                            }
                            composable("Add") {
                                AddWorkout(navController = navController)
                            }
                            composable("Progress") {
                                ViewProgress(navController = navController)
                            }
                            composable("Leaderboard") {
                                ViewLeaderboard(navController = navController)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Home(
    navController: NavController
) {
    Text(text = "home")
}


@Composable
fun CustomBottomAppBar(
    navController: NavController
) {
    BottomAppBar {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = { navController.navigate("Run") }) {
                Icon(
                    painter = painterResource(id = R.drawable.run),
                    contentDescription = "Run"
                )
            }
            IconButton(onClick = { navController.navigate("Add") }) {
                Icon(
                    painter = painterResource(id = R.drawable.add),
                    contentDescription = "Add"
                )
            }
            IconButton(onClick = { navController.navigate("Home") }) {
                Icon(
                    painter = painterResource(id = R.drawable.home),
                    contentDescription = "Home"
                )
            }
            IconButton(onClick = { navController.navigate("Progress") }) {
                Icon(
                    painter = painterResource(id = R.drawable.progress),
                    contentDescription = "Progress"
                )
            }
            IconButton(onClick = { navController.navigate("Leaderboard") }) {
                Icon(
                    painter = painterResource(id = R.drawable.leaderboard),
                    contentDescription = "Leaderboard"
                )
            }
        }
    }
}