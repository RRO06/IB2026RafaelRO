package com.iberdrola.practicas2026.RafaelRO.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
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

        composable(
            route = Screen.DetalleFactura.route,
            arguments = listOf(navArgument("facturaId") { type = NavType.IntType })
        ) {
            DetalleFacturaScreen(
                viewModel = hiltViewModel(),
                onBack = { navController.safePopBackStack(Screen.DetalleFactura.route) },
                modifier = modifier
            )
        }

        gestionNavGraph(navController, modifier)
    }
}

// --- Extensiones de Navegación Segura (Centralizadas) ---

fun NavHostController.safePopBackStack(expectedRoute: String) {
    val currentEntry = currentBackStackEntry
    if (currentEntry?.destination?.route == expectedRoute &&
        currentEntry.lifecycle.currentState == Lifecycle.State.RESUMED) {
        popBackStack()
    }
}

fun NavHostController.safePopBackStackTo(expectedRoute: String, targetRoute: String, inclusive: Boolean = false) {
    val currentEntry = currentBackStackEntry
    if (currentEntry?.destination?.route == expectedRoute &&
        currentEntry.lifecycle.currentState == Lifecycle.State.RESUMED) {
        popBackStack(targetRoute, inclusive)
    }
}

/**
 * Navegación segura que verifica el estado del ciclo de vida y la ruta actual.
 * El parámetro 'builder' es el último para permitir el uso de trailing lambdas para NavOptions (como popUpTo).
 */
fun NavHostController.safeNavigate(
    expectedRoute: String, 
    targetRoute: String, 
    action: (() -> Unit)? = null,
    builder: (NavOptionsBuilder.() -> Unit)? = null
) {
    val currentEntry = currentBackStackEntry
    if (currentEntry?.destination?.route == expectedRoute &&
        currentEntry.lifecycle.currentState == Lifecycle.State.RESUMED) {
        if (builder != null) navigate(targetRoute, builder) else navigate(targetRoute)
        action?.invoke()
    }
}

// --- Implementación en las Rutas ---

@Composable
private fun HomeRoute(navController: NavHostController, modifier: Modifier) {
    val homeViewModel: HomeViewModel = hiltViewModel()
    HomeScreen(
        viewModel = homeViewModel,
        onNavigateToFacturas = { navController.safeNavigate(Screen.Home.route, Screen.ListadoFacturas.route) },
        onNavigateToFacturaElectronica = { navController.safeNavigate(Screen.Home.route, Screen.FacturaElectronica.route) },
        onNavigateToPerfil = { navController.safeNavigate(Screen.Home.route, Screen.Perfil.route) },
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
            // Usamos el parámetro nombrado 'action' porque queremos que se ejecute DESPUÉS de navegar
            navController.safeNavigate(
                expectedRoute = Screen.ListadoFacturas.route, 
                targetRoute = Screen.Filtro.route,
                action = {
                    navController.getBackStackEntry(Screen.Filtro.route).savedStateHandle["filter_data"] = currentFilt
                }
            )
        },
        onFacturaClick = { id -> 
            navController.safeNavigate(Screen.ListadoFacturas.route, Screen.DetalleFactura.createRoute(id))
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
            val currentEntry = navController.currentBackStackEntry
            if (currentEntry?.destination?.route == Screen.Filtro.route &&
                currentEntry.lifecycle.currentState == Lifecycle.State.RESUMED) {
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
