package com.iberdrola.practicas2026.RafaelRO.ui.screens.profile

data class PerfilUiState(
    val fotoUri: String? = null,
    val nombreUsuario: String = "",
    val idUsuario: String = "",
    val email: String = "",
    val telefono: String = "",
    val isLoading: Boolean = false,
    // Nuevos campos de validación
    val emailError: String? = null,
    val telefonoError: String? = null
)