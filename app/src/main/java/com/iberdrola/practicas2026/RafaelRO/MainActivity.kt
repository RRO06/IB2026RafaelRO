package com.iberdrola.practicas2026.RafaelRO

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.IB2026RafaelROTheme
import com.iberdrola.practicas2026.RafaelRO.ui.navigation.NavHostScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IB2026RafaelROTheme {
                // Surface asegura que el fondo base de la app sea el definido en el tema (Blanco puro)
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    val navController = androidx.navigation.compose.rememberNavController()
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        NavHostScreen(navController, Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}