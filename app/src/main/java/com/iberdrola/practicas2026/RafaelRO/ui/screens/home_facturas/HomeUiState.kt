package com.iberdrola.practicas2026.RafaelRO.ui.screens.home_facturas

data class HomeUiState(
    val nombreUsuario: String = "Rafael R.O.",
    val idUsuario: String = "ID-2026-IB",
    val ultimaConexion: String = "13/03/2026",
    val esModoNube: Boolean = false, // false = Local (Assets), true = Nube (Mockoon)
    val esModoAvanzado: Boolean = false,
    val showBottomSheet : Boolean = false
)
