package com.iberdrola.practicas2026.RafaelRO.ui.navigation

sealed class Screen(val route : String) {
    object Home : Screen("home")
    object ListadoFacturas : Screen("listado_facturas")
    object Filtro : Screen("filtro")
}