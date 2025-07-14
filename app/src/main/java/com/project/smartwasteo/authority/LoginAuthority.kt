package com.project.smartwasteo.authority

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.runtime.livedata.observeAsState
import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.auth.AuthState
import com.project.smartwasteo.AuthViewModel
import com.project.smartwasteo.AuthViewModel.AuthState


@Composable
fun LoginAuthority(
    modifier: Modifier, navController: NavController, authViewModel: AuthViewModel
) {

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current
    // Text("Authority Login Page")

    LaunchedEffect(authState.value) {

        when (authState.value) {
            is AuthState.Authenticated -> navController.navigate("dashboardAuthority")
            is AuthState.Error -> Toast.makeText(
                context,
                (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT
            ).show()

            else -> Unit
        }
    }
    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    Box (
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Yellow)

            ){
        Column(
            modifier = modifier.fillMaxSize()
                .background(Color.Yellow),
                    //.padding(top = 350.dp),
                    verticalArrangement = Arrangement . Center,
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                },
                label = {
                    Text(text = "Email", fontSize = 16.sp)
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth(0.80f)
                    .height(60.dp)
                    .background(Color.White, shape = RoundedCornerShape(16.dp))


            )

            Spacer(modifier = Modifier.height(25.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                },
                label = {
                    Text(modifier = Modifier, text = "Password", fontSize = 16.sp)
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth(0.80f)
                    .height(60.dp)

                    .background(Color.White, shape = RoundedCornerShape(16.dp))


            )
            Spacer(modifier = Modifier.height(16.dp))


            Button(
                onClick = {
                    authViewModel.login(email, password)

                },
                enabled = authState.value != AuthState.Loading,
                        shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(contentColor = Color.Yellow)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(0.77f),
                    text = "Log In",
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

    }

}
