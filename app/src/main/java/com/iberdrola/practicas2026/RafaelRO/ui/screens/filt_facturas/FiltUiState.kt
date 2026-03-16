package com.iberdrola.practicas2026.RafaelRO.ui.screens.filt_facturas

import com.iberdrola.practicas2026.RafaelRO.domain.model.Estado
import com.iberdrola.practicas2026.RafaelRO.domain.model.Tipo

data class FiltUiState(
    val dateFrom: String = "",
    val dateTo: String = "",
    val priceRange: ClosedFloatingPointRange<Float> = 15f..151f,
    val selectedStates: Set<String> = emptySet(),
    val availableStates: Estado = Estado.Pagado
)