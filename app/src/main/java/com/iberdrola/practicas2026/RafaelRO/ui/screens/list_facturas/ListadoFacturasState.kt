package com.iberdrola.practicas2026.RafaelRO.ui.screens.list_facturas

import com.iberdrola.practicas2026.RafaelRO.domain.model.Factura


sealed interface ListadoFacturasState{
    data class Error(val message : String) : ListadoFacturasState
    data object Loading : ListadoFacturasState
    data class Success(val facturas : List<Factura>) : ListadoFacturasState
}
