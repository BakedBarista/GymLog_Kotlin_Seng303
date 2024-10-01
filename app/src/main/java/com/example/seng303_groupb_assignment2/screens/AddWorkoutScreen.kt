package com.example.seng303_groupb_assignment2.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.seng303_groupb_assignment2.R
import com.example.seng303_groupb_assignment2.models.ManageWorkoutViewModel

@Composable
fun AddWorkout(
    navController: NavController,
    viewModel: ManageWorkoutViewModel,
) {
    // TODO - replace ui text with string resources
    // TODO - user rememberBy to handle orientation changes

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val textStyle = TextStyle(
                fontSize = 20.sp
            )

            TextField(
                value = viewModel.name,
                onValueChange = { viewModel.updateName(it) },
                textStyle = textStyle,
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.Gray),
                placeholder = {
                    Text("Workout name...", style = textStyle)
                },
            )
        }
        Spacer(modifier = Modifier.padding(20.dp))
        Row {
            // TODO - fix how this looks
            Text(text = "Exercises: ")

            IconButton(onClick = { /*TODO - add exercise to workout (this opens the modal) */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.plus),
                    contentDescription = "Plus"
                )
            }
        }
        Spacer(modifier = Modifier.padding(10.dp))
        // TODO give this a fixed height
        LazyColumn() {
            // TODO for each exercise added
        }
        Spacer(modifier = Modifier.padding(10.dp))
        Row {
            Button(onClick = { /*TODO cancel */ }) {
                Text(text = "Cancel")
            }
            Button(onClick = { /*TODO save workout */ }) {
                Text(text = "Save")
            }
        }
    }
}