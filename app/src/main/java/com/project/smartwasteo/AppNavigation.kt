package com.project.smartwasteo

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.project.smartwasteo.authority.Dashboard_authority
import com.project.smartwasteo.authority.LoginAuthority
import com.project.smartwasteo.worker.Dashboard_worker
import com.project.smartwasteo.worker.WorkerLogin


@Composable
fun AppNavigation(modifier: Modifier= Modifier,authViewModel: AuthViewModel){
    val navController= rememberNavController()
    NavHost(navController=navController, startDestination = "firstScreen", builder = {
        composable ("firstScreen"){
            FirstScreen(modifier,navController)
        }
        composable("loginauthority"){
            LoginAuthority(modifier,navController,authViewModel)
        }
        composable ("workerauthority"){
            WorkerLogin(modifier,navController,authViewModel)
        }
        composable ("dashboardAuthority"){
            Dashboard_authority(modifier,navController,authViewModel)
        }
        composable ("dashboardWorker"){
            Dashboard_worker(modifier,navController,authViewModel)
        }
    })

}

