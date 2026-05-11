package com.iberdrola.practicas2026.RafaelRO.ui.screens.factura_electronica

import com.iberdrola.practicas2026.RafaelRO.domain.model.Contrato

data class FacturasElectronicasUiState(
    val contratos: List<Contrato> = emptyList(),
    val error : String = "",
    val isRefreshing: Boolean = false
)