package com.example.seng303_groupb_assignment2

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.seng303_groupb_assignment2.viewmodels.ManageWorkoutViewModel
import com.example.seng303_groupb_assignment2.screens.AddWorkout
import com.example.seng303_groupb_assignment2.screens.Home
import com.example.seng303_groupb_assignment2.screens.SelectWorkout
import com.example.seng303_groupb_assignment2.screens.ViewLeaderboard
import com.example.seng303_groupb_assignment2.screens.ViewProgress
import com.example.seng303_groupb_assignment2.ui.theme.SENG303_GroupB_Assignment2Theme
import com.example.seng303_groupb_assignment2.viewmodels.ExerciseViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.seng303_groupb_assignment2.viewmodels.WorkoutViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel as koinViewModel

class MainActivity : ComponentActivity() {
    private val exerciseViewModel: ExerciseViewModel by koinViewModel()
    private val workoutViewModel: WorkoutViewModel by koinViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SENG303_GroupB_Assignment2Theme {
                val navController = rememberNavController()
                val configuration = LocalConfiguration.current
                val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

                // TODO - make this use string resources instead of hard coded string literals
                var currentTitle by rememberSaveable { mutableStateOf("Home") }

                Scaffold(
                    topBar = {
                        if (isPortrait) {
                            CustomTopAppBar(title = currentTitle)
                        }
                    },
                    bottomBar = {
                        if (isPortrait) {
                            CustomBottomAppBar(navController)
                        }
                    },

                ) { padding ->
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = "Home",
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(padding)
                        ) {
                            composable("Home") {
                                currentTitle = "Home"
                                Home(navController = navController)
                            }
                            composable("SelectWorkout") {
                                currentTitle = "Select Workout"
                                SelectWorkout()
                            }
                            composable("Add") {
                                currentTitle = "Workout Builder"
                                val manageWorkoutViewModel: ManageWorkoutViewModel = viewModel()
                                AddWorkout(
                                    navController = navController,
                                    manageViewModel = manageWorkoutViewModel,
                                    exerciseViewModel = exerciseViewModel,
                                    workoutViewModel = workoutViewModel
                                )
                            }
                            composable("Progress") {
                                currentTitle = "View Progress"
                                ViewProgress(navController = navController)
                            }
                            composable("Leaderboard") {
                                currentTitle = "Leaderboard"
                                ViewLeaderboard(navController = navController)
                            }
                        }
                        if (!isPortrait) {
                            CustomSideBar(
                                navController,
                                Modifier
                                    .fillMaxHeight()
                                    .padding(8.dp))
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
) {val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry.value?.destination?.route
    BottomAppBar {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            AppBarIconButton(navController, "SelectWorkout", R.drawable.run, "Select Workout", currentDestination == "SelectWorkout")
            AppBarIconButton(navController, "Add", R.drawable.add, "Add", currentDestination == "Add")
            AppBarIconButton(navController, "Home", R.drawable.home, "Home", currentDestination == "Home")
            AppBarIconButton(navController, "Progress", R.drawable.progress, "Progress", currentDestination == "Progress")
            AppBarIconButton(navController, "Leaderboard", R.drawable.leaderboard, "Leaderboard", currentDestination == "Leaderboard")
        }
    }
}

@Composable
fun CustomSideBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry.value?.destination?.route

    NavigationRail(
        modifier = modifier
            .fillMaxHeight()
    ) {
        Box(
            modifier = Modifier.fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                AppBarNavigationRailItem(navController, "SelectWorkout", R.drawable.run, "Select Workout", currentDestination == "SelectWorkout")
                AppBarNavigationRailItem(navController, "Add", R.drawable.add, "Add", currentDestination == "Add")
                AppBarNavigationRailItem(navController, "Home", R.drawable.home, "Home", currentDestination == "Home")
                AppBarNavigationRailItem(navController, "Progress", R.drawable.progress, "Progress", currentDestination == "Progress")
                AppBarNavigationRailItem(navController, "Leaderboard", R.drawable.leaderboard, "Leaderboard", currentDestination == "Leaderboard")
            }
        }
    }
}

@Composable
fun AppBarNavigationRailItem(
    navController: NavController,
    destination: String,
    iconResId: Int,
    contentDescription: String,
    selected: Boolean
) {
    NavigationRailItem(
        selected = selected,
        onClick = { navController.navigate(destination) },
        icon = {
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = contentDescription
            )
        },
        label = {
            Text(text = contentDescription)
        },
        alwaysShowLabel = false
    )
}

@Composable
fun AppBarIconButton(
    navController: NavController,
    destination: String,
    iconResId: Int,
    contentDescription: String,
    selected: Boolean
) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .clip(MaterialTheme.shapes.small)
            .background(
                color = if (selected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
                shape = MaterialTheme.shapes.small
            )
            .clickable { navController.navigate(destination) }
            .padding(16.dp)
    ) {
        Icon (
            painter = painterResource(id = iconResId),
            contentDescription = contentDescription
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    title: String
) {
    // TODO possibly update this to center the text and maybe make the text larger.
    TopAppBar(
        title = {
            Text(
                text = title,
            )
        }
    )
}