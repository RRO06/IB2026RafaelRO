package com.iberdrola.practicas2026.RafaelRO

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.IB2026RafaelROTheme
import com.iberdrola.practicas2026.RafaelRO.ui.screens.list_facturas.ListadoFacturasScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IB2026RafaelROTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ListadoFacturasScreen(
                        viewModel = hiltViewModel(),
                        onBack = {  },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}