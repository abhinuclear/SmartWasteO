package com.project.smartwasteo

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.project.smartwasteo.authority.ComplaintScreen
import com.project.smartwasteo.authority.ComplaintViewModel
import com.project.smartwasteo.authority.Dashboard_authority
import com.project.smartwasteo.authority.LoginAuthority
import com.project.smartwasteo.worker.Dashboard_worker
import com.project.smartwasteo.worker.WorkerLogin
import com.project.smartwasteo.worker.WorkerViewModel


@Composable
fun AppNavigation(modifier: Modifier= Modifier,authViewModel: AuthViewModel){
    val navController= rememberNavController()
    val complaintViewModel: ComplaintViewModel = viewModel()
    val phoneAuthViewModel: PhoneAuthViewModel = viewModel()
    val workerViewModel: WorkerViewModel = viewModel()


    NavHost(navController=navController, startDestination = "firstScreen", builder = {
        composable ("firstScreen"){
            FirstScreen(modifier,navController)
        }
        composable("loginauthority"){
            LoginAuthority(modifier,navController,authViewModel)
        }
        composable("dashboardAuthority"){
            Dashboard_authority(modifier,navController,complaintViewModel)
        }
        composable ("workerlogin"){
            WorkerLogin(modifier,navController,phoneAuthViewModel)
        }
        composable ("dashboardWorker"){
            Dashboard_worker(modifier,navController,workerViewModel)
        }
        composable("complaintScreen") {
            ComplaintScreen(navController)
        }

    })

}

