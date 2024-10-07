package com.example.seng303_groupb_assignment2.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ViewLeaderboard(
    navController: NavController
) {
    Scaffold(
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                LeaderboardTable()
            }
        }
    )
}

@Composable
fun LeaderboardTable() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        LeaderboardHeader()
    }
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(30) { index ->
            LeaderboardRow(
                rank = index + 1,
                name = when (index) {
                    0 -> "Ryan"
                    1 -> "Liam"
                    2 -> "Ben"
                    29 -> "You"
                    else -> "User $index"
                },
                weight = when (index) {
                    0 -> "100kg"
                    1 -> "98kg"
                    2 -> "96kg"
                    29 -> "5kg"
                    else -> "${80 + index}kg"
                }
            )
        }
    }
}

@Composable
fun LeaderboardHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "Ranking", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text(text = "Name", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text(text = "Weight", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
    }
}

@Composable
fun LeaderboardRow(rank: Int, name: String, weight: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = rank.toString(), modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text(text = name, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text(text = weight, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
    }
}

