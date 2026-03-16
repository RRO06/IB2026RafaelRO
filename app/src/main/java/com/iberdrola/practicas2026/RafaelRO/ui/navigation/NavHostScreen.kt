package com.iberdrola.practicas2026.RafaelRO.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.iberdrola.practicas2026.RafaelRO.ui.screens.home_facturas.HomeScreen
import com.iberdrola.practicas2026.RafaelRO.ui.screens.home_facturas.HomeViewModel
import com.iberdrola.practicas2026.RafaelRO.ui.screens.list_facturas.ListadoFacturasScreen

@Composable
fun NavHostScreen(navController: NavHostController, modifier : Modifier){
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ){
        composable(Screen.Home.route){
            val homeViewModel : HomeViewModel = hiltViewModel()
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToFacturas = {
                    navController.navigate(Screen.ListadoFacturas.route)
                },
                modifier = modifier
            )
        }
        composable(Screen.ListadoFacturas.route){
            val parentEntry = remember(it) {
                navController.getBackStackEntry(Screen.Home.route)
            }
            val homeViewModel: HomeViewModel = hiltViewModel(parentEntry)
            ListadoFacturasScreen(
                viewModel = hiltViewModel(),
                onBack = {
                    homeViewModel.onBackFromFacturas()
                    navController.popBackStack()
                },
                modifier = modifier
            )
        }
    }
}