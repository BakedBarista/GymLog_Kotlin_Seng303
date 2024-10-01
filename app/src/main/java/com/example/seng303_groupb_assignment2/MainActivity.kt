package com.example.seng303_groupb_assignment2

import android.app.Application
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.seng303_groupb_assignment2.screens.AddExerciseScreen
import com.example.seng303_groupb_assignment2.screens.AddWorkout
import com.example.seng303_groupb_assignment2.screens.ExerciseListScreen
import com.example.seng303_groupb_assignment2.screens.RunWorkout
import com.example.seng303_groupb_assignment2.screens.ViewLeaderboard
import com.example.seng303_groupb_assignment2.screens.ViewProgress
import com.example.seng303_groupb_assignment2.ui.theme.SENG303_GroupB_Assignment2Theme
import com.example.seng303_groupb_assignment2.viewmodels.ExerciseViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SENG303_GroupB_Assignment2Theme {
                val viewModel: ExerciseViewModel = viewModel(factory = ExerciseViewModelFactory(application))

                val navController = rememberNavController()
                Scaffold(
                    bottomBar = { CustomBottomAppBar(navController) }
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        NavHost(navController = navController, startDestination = "Run") {
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
                            composable("AddExercise") {
                                AddExerciseScreen(viewModel = viewModel)
                            }
                            composable("ExerciseList") {
                                ExerciseListScreen(viewModel = viewModel)
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
            // These two are just temporary and are here to test the database. will remove these when I know the saving is working
            IconButton(onClick = { navController.navigate("AddExercise") }) {
                Icon(
                    painter = painterResource(id = R.drawable.add),
                    contentDescription = "Add Exercise"
                )
            }
            IconButton(onClick = { navController.navigate("ExerciseList") }) {
                Icon(
                    painter = painterResource(id = R.drawable.add),
                    contentDescription = "Exercise List"
                )
            }
        }
    }
}

class ExerciseViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExerciseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExerciseViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}