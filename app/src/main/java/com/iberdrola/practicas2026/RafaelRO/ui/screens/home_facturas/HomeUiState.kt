package com.iberdrola.practicas2026.RafaelRO.ui.screens.home_facturas

data class HomeUiState(
    val foto : String = "",
    val nombreUsuario: String = "Rafael R.O.",
    val idUsuario: String = "ID-2026-IB",
    val email : String = "",
    val telefono : String = "",
    val ultimaConexion: String = "13/03/2026",
    val esModoNube: Boolean = false,
    val esModoAvanzado: Boolean = false,
    val showBottomSheet : Boolean = false
)
