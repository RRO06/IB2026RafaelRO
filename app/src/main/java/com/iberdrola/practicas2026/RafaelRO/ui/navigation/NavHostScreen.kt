package com.iberdrola.practicas2026.RafaelRO.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
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
        composable(Screen.ListadoFacturas.route) {
            val filtrosRecibidos = it.savedStateHandle
                .getStateFlow("fr_data", FiltUiState())
                .collectAsState()
            val parentEntry = remember(it) {
                navController.getBackStackEntry(Screen.Home.route)
            }
            val homeViewModel: HomeViewModel = hiltViewModel(parentEntry)
            ListadoFacturasScreen(
                viewModel = hiltViewModel(),
                onBack = {
                    if (navController.currentBackStackEntry?.destination?.route == Screen.ListadoFacturas.route) {
                        homeViewModel.onBackFromFacturas()
                        navController.popBackStack()
                    }
                },
                onFilter = {
                    if (navController.currentBackStackEntry?.destination?.route == Screen.ListadoFacturas.route) {
                        navController.navigate(Screen.Filtro.route)
                    }
                },
                filtState = filtrosRecibidos.value,
                modifier = modifier
            )
        }
        composable(Screen.Filtro.route) {
            FilterScreen(
                viewModel = hiltViewModel(),
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
                modifier = modifier
            )
        }
        composable(Screen.FacturaElectronica.route) {
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
                            navController.navigate(
                                route = Screen.DetalleFacturaActiva.createRoute(contrato.id)
                            )
                        } else {
                            navController.navigate(
                                route = Screen.ActivarFactura.createRoute(contrato.id)
                            )
                        }
                    }
                }
            )
        }
        composable(
            route = Screen.DetalleFacturaActiva.route,
            arguments = listOf(navArgument("contratoId") { type = NavType.IntType })
        ) {
            DetalleFacturaActivaScreen(
                viewModel = hiltViewModel(),
                onBack = {
                    if (navController.currentBackStackEntry?.destination?.route == Screen.DetalleFacturaActiva.route) {
                        navController.popBackStack()
                    }
                },
                onModificarClick = { id ->
                    if (navController.currentBackStackEntry?.destination?.route == Screen.DetalleFacturaActiva.route) {
                        navController.navigate(
                            route = Screen.ModificarEmail.createRoute(id)
                        )
                    }
                },
                modifier = modifier
            )
        }
        composable(
            route = Screen.ActivarFactura.route,
            arguments = listOf(navArgument("contratoId") { type = NavType.IntType })
        ) {
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
        composable(
            route = Screen.ModificarEmail.route,
            arguments = listOf(navArgument("contratoId") { type = NavType.IntType })
        ) {
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
        composable(
            route = Screen.VerificarCodigo.route,
            arguments = listOf(navArgument("contratoId") { type = NavType.IntType })
        ) {
            val viewModel: GestionViewModel = hiltViewModel()
            VerificacionCodigoScreen(
                viewModel = viewModel,
                onBack = {
                    if (navController.currentBackStackEntry?.destination?.route == Screen.VerificarCodigo.route) {
                        navController.popBackStack()
                    }
                },
                onNext = { email ->
                    if (navController.currentBackStackEntry?.destination?.route == Screen.VerificarCodigo.route) {
                        val emailOfuscado = viewModel.obfuscateEmail(email)
                        if (viewModel.esFlujoActivacion()) {
                            navController.navigate(Screen.ExitoActivacion.createRoute(emailOfuscado))
                        } else {
                            navController.navigate(Screen.ExitoModificacion.createRoute(emailOfuscado))
                        }
                    }
                },
                onClose = {
                    if (navController.currentBackStackEntry?.destination?.route == Screen.VerificarCodigo.route) {
                        navController.popBackStack(Screen.FacturaElectronica.route, inclusive = false)
                    }
                },
                modifier = modifier
            )
        }
        composable(
            route = "exito_activacion/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val route = Screen.ExitoActivacion.createRoute(email)
            GestionExitoScreen(
                titulo = "¡Has activado correctamente tu factura electrónica!",
                email = email,
                onContinuar = {
                    if (navController.currentBackStackEntry?.destination?.route == route) {
                        navController.popBackStack(Screen.Home.route, inclusive = false)
                    }
                },
                modifier = modifier
            )
        }

        composable(
            route = "exito_modificacion/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val route = Screen.ExitoModificacion.createRoute(email)
            GestionExitoScreen(
                titulo = "¡Has modificado correctamente tu email!",
                email = email,
                onContinuar = {
                    if (navController.currentBackStackEntry?.destination?.route == route) {
                        navController.popBackStack(Screen.Home.route, inclusive = false)
                    }
                },
                modifier = modifier
            )
        }
    }
}
