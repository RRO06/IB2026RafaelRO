package com.iberdrola.practicas2026.RafaelRO.ui.navigation

import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.iberdrola.practicas2026.RafaelRO.ui.screens.gestion.ActivarFacturaScreen
import com.iberdrola.practicas2026.RafaelRO.ui.screens.gestion.DetalleFacturaActivaScreen
import com.iberdrola.practicas2026.RafaelRO.ui.screens.gestion.GestionExitoScreen
import com.iberdrola.practicas2026.RafaelRO.ui.screens.gestion.GestionViewModel
import com.iberdrola.practicas2026.RafaelRO.ui.screens.gestion.ModificarEmailScreen
import com.iberdrola.practicas2026.RafaelRO.ui.screens.gestion.VerificacionCodigoScreen

fun NavGraphBuilder.gestionNavGraph(navController: NavHostController, modifier: Modifier) {
    composable(
        route = Screen.DetalleFacturaActiva.route,
        arguments = listOf(navArgument("contratoId") { type = NavType.IntType })
    ) {
        DetalleFacturaActivaScreen(
            viewModel = hiltViewModel(),
            onBack = { navController.safePopBackStack(Screen.DetalleFacturaActiva.route) },
            onModificarClick = { id -> 
                navController.safeNavigate(Screen.DetalleFacturaActiva.route, Screen.ModificarEmail.createRoute(id)) 
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
            onBack = { navController.safePopBackStack(Screen.ActivarFactura.route) },
            onNext = { id -> 
                navController.safeNavigate(Screen.ActivarFactura.route, Screen.VerificarCodigo.createRoute(id))
            },
            onClose = { 
                navController.safePopBackStackTo(Screen.ActivarFactura.route, Screen.FacturaElectronica.route) 
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
            onBack = { navController.safePopBackStack(Screen.ModificarEmail.route) },
            onNext = { id -> 
                navController.safeNavigate(Screen.ModificarEmail.route, Screen.VerificarCodigo.createRoute(id))
            },
            onClose = { 
                navController.safePopBackStackTo(Screen.ModificarEmail.route, Screen.FacturaElectronica.route) 
            },
            modifier = modifier
        )
    }

    composable(
        route = Screen.VerificarCodigo.route,
        arguments = listOf(navArgument("contratoId") { type = NavType.IntType })
    ) { backStackEntry ->
        val parentEntry = remember(backStackEntry) { navController.previousBackStackEntry }
        val viewModel: GestionViewModel = if (parentEntry != null) hiltViewModel(parentEntry) else hiltViewModel()

        VerificacionCodigoScreen(
            viewModel = viewModel,
            onBack = { navController.safePopBackStack(Screen.VerificarCodigo.route) },
            onNext = { email ->
                if (navController.currentBackStackEntry?.destination?.route == Screen.VerificarCodigo.route) {
                    val emailOfuscado = viewModel.obfuscateEmail(email)
                    val route = if (viewModel.esFlujoActivacion()) Screen.ExitoActivacion.createRoute(emailOfuscado)
                                else Screen.ExitoModificacion.createRoute(emailOfuscado)
                    navController.navigate(route) { popUpTo(Screen.FacturaElectronica.route) }
                }
            },
            onClose = { 
                navController.safePopBackStackTo(Screen.VerificarCodigo.route, Screen.FacturaElectronica.route) 
            },
            modifier = modifier
        )
    }

    composable(
        route = Screen.ExitoActivacion.route,
        arguments = listOf(navArgument("email") { type = NavType.StringType })
    ) { backStackEntry ->
        val email = backStackEntry.arguments?.getString("email") ?: ""
        GestionExitoScreen(
            titulo = "¡Has activado correctamente tu factura electrónica!",
            email = email,
            onContinuar = { 
                navController.safePopBackStackTo(Screen.ExitoActivacion.route, Screen.FacturaElectronica.route) 
            },
            modifier = modifier
        )
    }

    composable(
        route = Screen.ExitoModificacion.route,
        arguments = listOf(navArgument("email") { type = NavType.StringType })
    ) { backStackEntry ->
        val email = backStackEntry.arguments?.getString("email") ?: ""
        GestionExitoScreen(
            titulo = "¡Has modificado correctamente tu email!",
            email = email,
            onContinuar = { 
                navController.safePopBackStackTo(Screen.ExitoModificacion.route, Screen.FacturaElectronica.route) 
            },
            modifier = modifier
        )
    }
}
