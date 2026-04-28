package com.iberdrola.practicas2026.RafaelRO.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.iberdrola.practicas2026.RafaelRO.ui.screens.Perfil.PerfilScreen
import com.iberdrola.practicas2026.RafaelRO.ui.screens.factura_electronica.FacturaElectronicaScreen
import com.iberdrola.practicas2026.RafaelRO.ui.screens.filt_facturas.FiltUiState
import com.iberdrola.practicas2026.RafaelRO.ui.screens.filt_facturas.FilterScreen
import com.iberdrola.practicas2026.RafaelRO.ui.screens.gestion.ActivarFacturaScreen
import com.iberdrola.practicas2026.RafaelRO.ui.screens.gestion.DetalleFacturaActivaScreen
import com.iberdrola.practicas2026.RafaelRO.ui.screens.gestion.GestionExitoScreen
import com.iberdrola.practicas2026.RafaelRO.ui.screens.gestion.GestionViewModel
import com.iberdrola.practicas2026.RafaelRO.ui.screens.gestion.ModificarEmailScreen
import com.iberdrola.practicas2026.RafaelRO.ui.screens.gestion.VerificacionCodigoScreen
import com.iberdrola.practicas2026.RafaelRO.ui.screens.home_facturas.HomeScreen
import com.iberdrola.practicas2026.RafaelRO.ui.screens.home_facturas.HomeViewModel
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
                onNavigateBack = {
                    if (navController.currentBackStackEntry?.destination?.route == Screen.Perfil.route) {
                        navController.popBackStack()
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeRoute(navController, modifier)
        }
        composable(Screen.ListadoFacturas.route) {
            ListadoFacturasRoute(it, navController, modifier)
        }
        composable(Screen.Filtro.route) {
            FiltroRoute(it, navController, modifier)
        }
        composable(Screen.FacturaElectronica.route) {
            FacturaElectronicaRoute(navController, modifier)
        }
        composable(
            route = Screen.DetalleFacturaActiva.route,
            arguments = listOf(navArgument("contratoId") { type = NavType.IntType })
        ) {
            DetalleFacturaActivaRoute(navController, modifier)
        }
        composable(
            route = Screen.ActivarFactura.route,
            arguments = listOf(navArgument("contratoId") { type = NavType.IntType })
        ) {
            ActivarFacturaRoute(navController, modifier)
        }
        composable(
            route = Screen.ModificarEmail.route,
            arguments = listOf(navArgument("contratoId") { type = NavType.IntType })
        ) {
            ModificarEmailRoute(navController, modifier)
        }
        composable(
            route = Screen.VerificarCodigo.route,
            arguments = listOf(navArgument("contratoId") { type = NavType.IntType })
        ) {
            VerificarCodigoRoute(navController, modifier)
        }
        composable(
            route = "exito_activacion/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            GestionExitoScreen(
                titulo = "¡Has activado correctamente tu factura electrónica!",
                email = email,
                onContinuar = {
                    navController.popBackStack(Screen.FacturaElectronica.route, inclusive = false)
                },
                modifier = modifier
            )
        }
        composable(
            route = "exito_modificacion/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            GestionExitoScreen(
                titulo = "¡Has modificado correctamente tu email!",
                email = email,
                onContinuar = {
                    navController.popBackStack(Screen.FacturaElectronica.route, inclusive = false)
                },
                modifier = modifier
            )
        }
    }
}

@Composable
private fun HomeRoute(navController: NavHostController, modifier: Modifier) {
    val homeViewModel: HomeViewModel = hiltViewModel()
    HomeScreen(
        viewModel = homeViewModel,
        onNavigateToFacturas = {
            if (navController.currentBackStackEntry?.destination?.route == Screen.Home.route) {
                navController.navigate(Screen.ListadoFacturas.route)
            }
        },
        onNavigateToFacturaElectronica = {
            if (navController.currentBackStackEntry?.destination?.route == Screen.Home.route) {
                navController.navigate(Screen.FacturaElectronica.route)
            }
        },
        onNavigateToPerfil = {
            if (navController.currentBackStackEntry?.destination?.route == Screen.Home.route) {
                navController.navigate(Screen.Perfil.route)
            }
        },
        modifier = modifier
    )
}

@Composable
private fun ListadoFacturasRoute(it: NavBackStackEntry, navController: NavHostController, modifier: Modifier) {
    val viewModel: ListadoFacturasViewModel = hiltViewModel(it)
    
    val filtrosRecibidos by it.savedStateHandle
        .getStateFlow("filter_data", FiltUiState())
        .collectAsState()

    val parentEntry = remember(it) {
        navController.getBackStackEntry(Screen.Home.route)
    }
    val homeViewModel: HomeViewModel = hiltViewModel(parentEntry)

    ListadoFacturasScreen(
        viewModel = viewModel,
        onBack = {
            if (navController.currentBackStackEntry?.destination?.route == Screen.ListadoFacturas.route) {
                homeViewModel.onBackFromFacturas()
                navController.popBackStack()
            }
        },
        onFilter = { currentFilt ->
            if (navController.currentBackStackEntry?.destination?.route == Screen.ListadoFacturas.route) {
                navController.navigate(Screen.Filtro.route)
                navController.getBackStackEntry(Screen.Filtro.route)
                    .savedStateHandle["filter_data"] = currentFilt
                Log.d("FilterViewModel", "DEBUG: Filtros enviados al destino: $currentFilt")
            }
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
        onBack = {
            if (navController.currentBackStackEntry?.destination?.route == Screen.Filtro.route) {
                navController.popBackStack()
            }
        },
        onApply = { filtState ->
            if (navController.currentBackStackEntry?.destination?.route == Screen.Filtro.route) {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("filter_data", filtState)
                navController.popBackStack()
            }
        },
        modifier = modifier,
        onClear = {
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set("filter_data", FiltUiState())
        }
    )
}

@Composable
private fun FacturaElectronicaRoute(navController: NavHostController, modifier: Modifier) {
    FacturaElectronicaScreen(
        viewModel = hiltViewModel(),
        onBack = {
            if (navController.currentBackStackEntry?.destination?.route == Screen.FacturaElectronica.route) {
                navController.popBackStack()
            }
        },
        modifier = modifier,
        onContratoClick = { contrato ->
            if (navController.currentBackStackEntry?.destination?.route == Screen.FacturaElectronica.route) {
                if (contrato.estado) {
                    navController.navigate(Screen.DetalleFacturaActiva.createRoute(contrato.id))
                } else {
                    navController.navigate(Screen.ActivarFactura.createRoute(contrato.id))
                }
            }
        }
    )
}

@Composable
private fun DetalleFacturaActivaRoute(navController: NavHostController, modifier: Modifier) {
    DetalleFacturaActivaScreen(
        viewModel = hiltViewModel(),
        onBack = {
            if (navController.currentBackStackEntry?.destination?.route == Screen.DetalleFacturaActiva.route) {
                navController.popBackStack()
            }
        },
        onModificarClick = { id ->
            if (navController.currentBackStackEntry?.destination?.route == Screen.DetalleFacturaActiva.route) {
                navController.navigate(Screen.ModificarEmail.createRoute(id))
            }
        },
        modifier = modifier
    )
}

@Composable
private fun ActivarFacturaRoute(navController: NavHostController, modifier: Modifier) {
    ActivarFacturaScreen(
        viewModel = hiltViewModel(),
        onBack = {
            if (navController.currentBackStackEntry?.destination?.route == Screen.ActivarFactura.route) {
                navController.popBackStack()
            }
        },
        onNext = { id ->
            if (navController.currentBackStackEntry?.destination?.route == Screen.ActivarFactura.route) {
                navController.navigate(Screen.VerificarCodigo.createRoute(id))
            }
        },
        onClose = {
            if (navController.currentBackStackEntry?.destination?.route == Screen.ActivarFactura.route) {
                navController.popBackStack(Screen.FacturaElectronica.route, inclusive = false)
            }
        },
        modifier = modifier
    )
}

@Composable
private fun ModificarEmailRoute(navController: NavHostController, modifier: Modifier) {
    ModificarEmailScreen(
        viewModel = hiltViewModel(),
        onBack = {
            if (navController.currentBackStackEntry?.destination?.route == Screen.ModificarEmail.route) {
                navController.popBackStack()
            }
        },
        onNext = { id ->
            if (navController.currentBackStackEntry?.destination?.route == Screen.ModificarEmail.route) {
                navController.navigate(Screen.VerificarCodigo.createRoute(id))
            }
        },
        onClose = {
            if (navController.currentBackStackEntry?.destination?.route == Screen.ModificarEmail.route) {
                navController.popBackStack(Screen.FacturaElectronica.route, inclusive = false)
            }
        },
        modifier = modifier
    )
}

@Composable
private fun VerificarCodigoRoute(navController: NavHostController, modifier: Modifier) {
    val viewModel: GestionViewModel = hiltViewModel()
    val currentRoute = navController.currentBackStackEntry?.destination?.route
    VerificacionCodigoScreen(
        viewModel = viewModel,
        onBack = {
            if (navController.currentBackStackEntry?.destination?.route == currentRoute) {
                navController.popBackStack()
            }
        },
        onNext = { email ->
            if (navController.currentBackStackEntry?.destination?.route == currentRoute) {
                val emailOfuscado = viewModel.obfuscateEmail(email)
                if (viewModel.esFlujoActivacion()) {
                    navController.navigate(Screen.ExitoActivacion.createRoute(emailOfuscado)) {
                        popUpTo(Screen.FacturaElectronica.route)
                    }
                } else {
                    navController.navigate(Screen.ExitoModificacion.createRoute(emailOfuscado)) {
                        popUpTo(Screen.FacturaElectronica.route)
                    }
                }
            }
        },
        onClose = {
            if (navController.currentBackStackEntry?.destination?.route == currentRoute) {
                navController.popBackStack(Screen.FacturaElectronica.route, inclusive = false)
            }
        },
        modifier = modifier
    )
}
