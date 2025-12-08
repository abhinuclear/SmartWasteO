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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.semantics.SemanticsActions.OnClick
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun FirstScreen(modifier: Modifier,navController: NavController) {

    val gradientColors = listOf(
        Color(0xFFE6E6FA),
        Color(0xFF87CEEB)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush =Brush.verticalGradient(
                    colors=gradientColors
                )
            )

    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text="WASTRO",
                fontSize=48.sp,
                fontWeight=FontWeight.Bold,
                color=Color(0xFF9870DB),
                letterSpacing=2.sp
            )
            Text(
                text="Smart Waste Management",
                fontSize=16.sp,
                color=Color(0xFF4682B4),
               // modifier=Modifier.padding(bottom=48.sp)
            )
        }
            Button(
                onClick = {
                    navController.navigate("loginauthority")
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(contentColor = Color.Yellow),
                modifier = modifier
                    .padding(top = 125.dp, start = 114.dp)
                    .size(180.dp)


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
                    .padding(top = 520.dp, start = 114.dp)

                    .size(180.dp)


            ) {
                Text(fontSize = 24.sp, text = "Worker")
            }
        }

    }
























