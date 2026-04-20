package com.iberdrola.practicas2026.RafaelRO.ui.screens.list_facturas

import com.iberdrola.practicas2026.RafaelRO.domain.model.Factura
import com.iberdrola.practicas2026.RafaelRO.domain.model.Tipo

data class ListadoFacturasUiState(
    val facturasBase: List<Factura> = emptyList(),
    val facturasAMostrar: List<Factura> = emptyList(), 
    val filtroTipoActual: Tipo = Tipo.Luz,           
    val facturasPorAnio: Map<Int, List<Factura>> = emptyMap(),
    val ultimaFactura: Factura? = null,
    val showDialog: Boolean = false
)
