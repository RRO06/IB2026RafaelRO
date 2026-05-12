package com.iberdrola.practicas2026.RafaelRO.ui.screens.list_facturas

import com.iberdrola.practicas2026.RafaelRO.domain.model.Factura

sealed interface ListadoFacturasState {
    data class Error(
        val message: String,
        val type: ErrorType = ErrorType.GENERIC
    ) : ListadoFacturasState

    data object Loading : ListadoFacturasState
    data class Success(
        val facturas: List<Factura>,
        val isFallback: Boolean = false
    ) : ListadoFacturasState

    enum class ErrorType {
        NETWORK,
        SERVER,
        DATABASE,
        EMPTY_RESULTS,
        NO_SERVICES,
        GENERIC
    }
}
