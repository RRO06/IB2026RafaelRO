package com.iberdrola.practicas2026.RafaelRO.ui.screens.list_facturas

import com.iberdrola.practicas2026.RafaelRO.domain.model.Factura
import com.iberdrola.practicas2026.RafaelRO.domain.model.Tipo
import com.iberdrola.practicas2026.RafaelRO.ui.screens.filt_facturas.FiltUiState

data class ListadoFacturasUiState(
    val facturasBase: List<Factura> = emptyList(),
    val facturasAMostrar: List<Factura> = emptyList(), 
    val filtroTipoActual: Tipo = Tipo.Luz,           
    val facturasPorAnio: Map<Int, List<Factura>> = emptyMap(),
    val ultimaFactura: Factura? = null,
    val showDialog: Boolean = false,
    val isRefreshing: Boolean = false,
    val filtros: FiltUiState = FiltUiState() // Los filtros ahora son parte del estado oficial
)
