package com.iberdrola.practicas2026.RafaelRO.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.iberdrola.practicas2026.RafaelRO.ui.screens.home_facturas.HomeScreen
import com.iberdrola.practicas2026.RafaelRO.ui.screens.list_facturas.ListadoFacturasScreen

@Composable
fun NavHostScreen(navController: NavHostController, modifier : Modifier){
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ){
        composable(Screen.Home.route){
            HomeScreen(
                viewModel = hiltViewModel(),
                onNavigateToFacturas = {
                    navController.navigate(Screen.ListadoFacturas.route)
                },
                modifier = modifier
            )
        }
        composable(Screen.ListadoFacturas.route){
            ListadoFacturasScreen(
                viewModel = hiltViewModel(),
                onBack = {
                    navController.popBackStack()
                },
                modifier = modifier
            )
        }
    }
}