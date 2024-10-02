package com.example.seng303_groupb_assignment2

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.seng303_groupb_assignment2.screens.AddWorkout
import com.example.seng303_groupb_assignment2.screens.Home
import com.example.seng303_groupb_assignment2.screens.RunWorkout
import com.example.seng303_groupb_assignment2.screens.ViewLeaderboard
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
                            .padding(padding)
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = "Home",
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        ) {
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
) {
    BottomAppBar {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            AppBarIconButton(navController, "Run", R.drawable.run, "Run")
            AppBarIconButton(navController, "Add", R.drawable.add, "Add")
            AppBarIconButton(navController, "Home", R.drawable.home, "Home")
            AppBarIconButton(navController, "Progress", R.drawable.progress, "Progress")
            AppBarIconButton(navController, "Leaderboard", R.drawable.leaderboard, "Leaderboard")
        }
    }
}

@Composable
fun CustomSideBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxHeight()
            .padding(8.dp)
    ) {
        AppBarIconButton(navController, "Run", R.drawable.run, "Run")
        AppBarIconButton(navController, "Add", R.drawable.add, "Add")
        AppBarIconButton(navController, "Home", R.drawable.home, "Home")
        AppBarIconButton(navController, "Progress", R.drawable.progress, "Progress")
        AppBarIconButton(navController, "Leaderboard", R.drawable.leaderboard, "Leaderboard")
    }
}

@Composable
fun AppBarIconButton(
    navController: NavController,
    destination: String,
    iconResId: Int,
    contentDescription: String
) {
    IconButton(onClick = { navController.navigate(destination) }) {
        Icon(
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