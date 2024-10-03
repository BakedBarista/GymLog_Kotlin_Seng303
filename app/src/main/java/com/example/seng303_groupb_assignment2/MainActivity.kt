package com.example.seng303_groupb_assignment2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.seng303_groupb_assignment2.screens.AddWorkout
import com.example.seng303_groupb_assignment2.screens.Home
import com.example.seng303_groupb_assignment2.screens.RunWorkout
import com.example.seng303_groupb_assignment2.screens.ViewLeaderboard
import com.example.seng303_groupb_assignment2.screens.ViewPreferences
import com.example.seng303_groupb_assignment2.screens.ViewProgress
import com.example.seng303_groupb_assignment2.ui.theme.SENG303_GroupB_Assignment2Theme
import com.example.seng303_groupb_assignment2.viewmodels.ExerciseViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel as koinViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: ExerciseViewModel by koinViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SENG303_GroupB_Assignment2Theme {

                val navController = rememberNavController()

                // TODO - make this use string resources instead of hard coded string literals
                var currentTitle by rememberSaveable { mutableStateOf("Home") }

                Scaffold(
                    topBar = { CustomTopAppBar(title = currentTitle, navController) },
                    bottomBar = { CustomBottomAppBar(navController) }
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        NavHost(navController = navController, startDestination = "Home") {
                            composable("Home") {
                                currentTitle = "Home"
                                Home(navController = navController)
                            }
                            composable("Run") {
                                currentTitle = "Run Workout"
                                RunWorkout(navController = navController)
                            }
                            composable("Add") {
                                currentTitle = "Workout Builder"
                                AddWorkout(navController = navController)
                            }
                            composable("Progress") {
                                currentTitle = "View Progress"
                                ViewProgress(navController = navController)
                            }
                            composable("Leaderboard") {
                                currentTitle = "Leaderboard"
                                ViewLeaderboard(navController = navController)
                            }
                            composable("Preferences") {
                                currentTitle = "Preferences"
                                ViewPreferences(navController = navController)
                            }
                        }
                    }
                }
            }
        }
    }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    title: String,
    navController: NavController
) {
    TopAppBar(
        title = {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 30.sp
                    ),
                    modifier = Modifier.align(Alignment.Center)
                )

                IconButton(
                    onClick = { navController.navigate("Preferences") },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 12.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_gear),
                        contentDescription = "Settings"
                    )
                }
            }
        }
    )
}


