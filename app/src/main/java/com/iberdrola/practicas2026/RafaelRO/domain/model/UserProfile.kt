package com.iberdrola.practicas2026.RafaelRO.domain.model

data class UserProfile(
    val foto : String = "",
    val nombre: String = "",
    val id: String = "",
    val email: String = "",
    val telefono: String ="",
    val ultimaConexion: Long = 0
)