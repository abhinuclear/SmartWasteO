package com.project.smartwasteo.worker

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.AlertDialogDefaults.shape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.project.smartwasteo.AuthViewModel
import com.project.smartwasteo.PhoneAuthViewModel

@Composable
fun WorkerLogin(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    viewModel: PhoneAuthViewModel = viewModel()
) {
    val context = LocalContext.current

    val phoneNumber = remember { mutableStateOf("") }
    val otpCode = remember { mutableStateOf("") }

    val otpSent by viewModel.otpSent
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Yellow)

    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Login via Phone",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (!otpSent) {
                OutlinedTextField(
                    value = phoneNumber.value,
                    onValueChange = { phoneNumber.value = it },
                    label = { Text("Phone Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(Color.White, shape = RoundedCornerShape(16.dp))
                   // modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (phoneNumber.value.length == 10) {
                            viewModel.sendOtp(phoneNumber.value, context)
                        } else {
                            Toast.makeText(
                                context,
                                "Enter valid 10-digit number",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Send OTP")
                }
            } else {
                OutlinedTextField(
                    value = otpCode.value,
                    onValueChange = { otpCode.value = it },
                    label = { Text("Enter OTP") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.40f)
                        .align ( Alignment.CenterHorizontally)
                        .height(60.dp)
                        .background(Color.White, shape = RoundedCornerShape(16.dp))
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (otpCode.value.length >= 6) {
                            viewModel.verifyOtp(otpCode.value) {
                                navController.navigate("dashboardWorker") {
                                    popUpTo("workerLogin") { inclusive = true }
                                    launchSingleTop= true
                                }
                            }
                        } else {
                            Toast.makeText(context, "Enter 6-digit OTP", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Verify OTP")
                }
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = it, color = Color.Red)
            }
        }
    }
}