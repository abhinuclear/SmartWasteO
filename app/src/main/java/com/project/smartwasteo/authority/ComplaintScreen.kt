package com.project.smartwasteo.authority

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ComplaintScreen(
    navController: NavController,
    viewModel: ComplaintViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val list = viewModel.complaints.value
    BackHandler {
        navController.navigate("firstScreen") {
            popUpTo("firstScreen") { inclusive = false }
            launchSingleTop = true
        }
    }
    Column(modifier = Modifier.padding(35.dp)) {
        Text("Complaints", fontSize = 26.sp, fontWeight = FontWeight.Bold)

        list.forEach { complaint ->
            androidx.compose.material3.Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = androidx.compose.material3.CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Name: ${complaint.name}", fontWeight = FontWeight.Bold)
                    Text("Mobile: ${complaint.mobno}")
                    Text("Address: ${complaint.address}")
                    Text("Location: ${complaint.latitude}, ${complaint.longitude}")
                    Text("Time: ${complaint.time}", fontSize = 16.sp, color = Color.Gray)
                }
            }
        }
    }
}
