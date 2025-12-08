package com.project.smartwasteo

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.unit.dp
//import androidx.compose.ui.tooling.preview.Preview
import com.project.smartwasteo.ui.theme.SmartWasteOTheme
import kotlin.getValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val authViewModel: AuthViewModel by viewModels()
        setContent {
            SmartWasteOTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {

                        // 1. Your Main Content
                        AppNavigation(authViewModel = authViewModel)

                        // 2. The Test Crash Button (Overlay)
                        Button(
                            onClick = { throw RuntimeException("Test Crash") },
                            modifier = Modifier
                                .align(Alignment.TopCenter) // Align to top
                                .padding(top = 50.dp)       // Add 50dp top padding
                        ) {
                            Text("Test Crash")
                        }
                    }
                }
            }
        }
    }
}
