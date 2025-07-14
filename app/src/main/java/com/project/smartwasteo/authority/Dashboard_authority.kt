package com.project.smartwasteo.authority

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.project.smartwasteo.AuthViewModel


    @Composable
    fun Dashboard_authority(
        modifier: Modifier = Modifier,
        navController: NavController,
        authViewModel: AuthViewModel
    ) {
        val complaintViewModel: ComplaintViewModel = viewModel()
        ComplaintScreen(viewModel = complaintViewModel)
    }