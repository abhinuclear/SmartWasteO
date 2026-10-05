package com.project.smartwasteo.authority

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun Dashboard_authority(
    modifier: Modifier= Modifier,
    navController: NavController,
    complaintViewModel: ComplaintViewModel,
) {
    
    ComplaintScreen(
        navController = navController,  // Pass navController here
        viewModel = complaintViewModel
    )
}