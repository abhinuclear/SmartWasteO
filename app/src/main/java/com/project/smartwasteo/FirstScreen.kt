package com.project.smartwasteo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.semantics.SemanticsActions.OnClick
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun FirstScreen(modifier: Modifier,navController: NavController) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Yellow)

    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    navController.navigate("loginauthority")
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(contentColor = Color.Yellow),
                modifier = modifier
                    .size(150.dp)

            ) {
                Text(
                    fontSize = 24.sp, text = "Authority"
                )
            }
            Spacer(modifier=modifier.height(16.dp))

            Button(
                onClick = {
                    navController.navigate("workerlogin")
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(contentColor = Color.Yellow),
                modifier = modifier

                    .size(150.dp)


            ) {
                Text(fontSize = 24.sp, text = "Worker")
            }
        }

    }


}





















