package com.iberdrola.practicas2026.RafaelRO.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.iberdrola.practicas2026.RafaelRO.ui.screens.Perfil.PerfilScreen
import com.iberdrola.practicas2026.RafaelRO.ui.screens.factura_electronica.FacturaElectronicaScreen
import com.iberdrola.practicas2026.RafaelRO.ui.screens.filt_facturas.FiltUiState
import com.iberdrola.practicas2026.RafaelRO.ui.screens.filt_facturas.FilterScreen
import com.iberdrola.practicas2026.RafaelRO.ui.screens.home_facturas.HomeScreen
import com.iberdrola.practicas2026.RafaelRO.ui.screens.home_facturas.HomeViewModel
import com.iberdrola.practicas2026.RafaelRO.ui.screens.list_facturas.DetalleFacturaScreen
import com.iberdrola.practicas2026.RafaelRO.ui.screens.list_facturas.ListadoFacturasScreen
import com.iberdrola.practicas2026.RafaelRO.ui.screens.list_facturas.ListadoFacturasViewModel

@Composable
fun NavHostScreen(navController: NavHostController, modifier: Modifier) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Perfil.route) {
            PerfilScreen(
                viewModel = hiltViewModel(),
                onNavigateBack = { navController.safePopBackStack(Screen.Perfil.route) }
            )
        }
        
        composable(Screen.Home.route) { HomeRoute(navController, modifier) }
        
        composable(Screen.ListadoFacturas.route) { ListadoFacturasRoute(it, navController, modifier) }
        
        composable(Screen.Filtro.route) { FiltroRoute(it, navController, modifier) }
        
        composable(Screen.FacturaElectronica.route) { FacturaElectronicaRoute(navController, modifier) }

        composable(Screen.DetalleFactura.route) {
            DetalleFacturaScreen(
                viewModel = hiltViewModel(),
                onBack = { navController.popBackStack() }
            )
        }

        gestionNavGraph(navController, modifier)
    }
}

fun NavHostController.safePopBackStack(expectedRoute: String) {
    if (this.currentBackStackEntry?.destination?.route == expectedRoute) {
        this.popBackStack()
    }
}

fun NavHostController.safePopBackStackTo(expectedRoute: String, targetRoute: String, inclusive: Boolean = false) {
    if (this.currentBackStackEntry?.destination?.route == expectedRoute) {
        this.popBackStack(targetRoute, inclusive)
    }
}

fun NavHostController.safeNavigate(expectedRoute: String, targetRoute: String) {
    if (this.currentBackStackEntry?.destination?.route == expectedRoute) {
        this.navigate(targetRoute)
    }
}

@Composable
private fun HomeRoute(navController: NavHostController, modifier: Modifier) {
    val homeViewModel: HomeViewModel = hiltViewModel()
    HomeScreen(
        viewModel = homeViewModel,
        onNavigateToFacturas = {
            navController.safeNavigate(Screen.Home.route, Screen.ListadoFacturas.route)
        },
        onNavigateToFacturaElectronica = {
            navController.safeNavigate(Screen.Home.route, Screen.FacturaElectronica.route)
        },
        onNavigateToPerfil = {
            navController.safeNavigate(Screen.Home.route, Screen.Perfil.route)
        },
        modifier = modifier
    )
}

@Composable
private fun ListadoFacturasRoute(it: NavBackStackEntry, navController: NavHostController, modifier: Modifier) {
    val viewModel: ListadoFacturasViewModel = hiltViewModel(it)
    val filtrosRecibidos by it.savedStateHandle.getStateFlow("filter_data", FiltUiState()).collectAsState()
    ListadoFacturasScreen(
        viewModel = viewModel,
        onBack = { navController.safePopBackStack(Screen.ListadoFacturas.route) },
        onFilter = { currentFilt ->
            if (navController.currentBackStackEntry?.destination?.route == Screen.ListadoFacturas.route) {
                navController.navigate(Screen.Filtro.route)
                navController.getBackStackEntry(Screen.Filtro.route).savedStateHandle["filter_data"] = currentFilt
            }
        },
        onFacturaClick = { facturaId ->
            navController.navigate(Screen.DetalleFactura.createRoute(facturaId))
        },
        filtState = filtrosRecibidos,
        modifier = modifier
    )
}

@Composable
private fun FiltroRoute(it: NavBackStackEntry, navController: NavHostController, modifier: Modifier) {
    val initialFilters = it.savedStateHandle.get<FiltUiState>("filter_data")
    FilterScreen(
        viewModel = hiltViewModel(),
        initialFilters = initialFilters,
        onBack = { navController.safePopBackStack(Screen.Filtro.route) },
        onApply = { filtState ->
            if (navController.currentBackStackEntry?.destination?.route == Screen.Filtro.route) {
                navController.previousBackStackEntry?.savedStateHandle?.set("filter_data", filtState)
                navController.popBackStack()
            }
        },
        modifier = modifier,
        onClear = {
            navController.previousBackStackEntry?.savedStateHandle?.set("filter_data", FiltUiState())
        }
    )
}

@Composable
private fun FacturaElectronicaRoute(navController: NavHostController, modifier: Modifier) {
    FacturaElectronicaScreen(
        viewModel = hiltViewModel(),
        onBack = { navController.safePopBackStack(Screen.FacturaElectronica.route) },
        modifier = modifier,
        onContratoClick = { contrato ->
            val targetRoute = if (contrato.estado) Screen.DetalleFacturaActiva.createRoute(contrato.id)
                        else Screen.ActivarFactura.createRoute(contrato.id)
            navController.safeNavigate(Screen.FacturaElectronica.route, targetRoute)
        }
    )
}
