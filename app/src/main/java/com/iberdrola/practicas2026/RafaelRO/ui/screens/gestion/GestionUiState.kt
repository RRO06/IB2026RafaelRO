package com.iberdrola.practicas2026.RafaelRO.ui.screens.gestion

import com.iberdrola.practicas2026.RafaelRO.domain.model.Contrato

data class GestionUiState(
    val contrato: Contrato? = Contrato(
        email = "hola@gmail.com"
    ),
    val isLoading: Boolean = false,
    val isVerifying: Boolean = false,
    val mostrarBannerExito: Boolean = false,
    val mostrarBannerError: Boolean = false,
    val error: String? = null,
    val isEmailValido: Boolean = false,
    val emailFormulario: String = "",
    val terminosAceptados: Boolean = false,
    val codigoVerificacion: String = "",
    val errorCodigo: Boolean = false,
    val codigoGenerado: String = "",
    val intentosRestantes: Int = 3,
    val ultimoCodigoEnviado: String? = null
)
