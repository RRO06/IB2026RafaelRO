package com.iberdrola.practicas2026.RafaelRO.ui.screens.list_facturas

import com.iberdrola.practicas2026.RafaelRO.domain.model.Estado
import com.iberdrola.practicas2026.RafaelRO.domain.model.Factura
import com.iberdrola.practicas2026.RafaelRO.domain.model.Tipo
import com.iberdrola.practicas2026.RafaelRO.ui.screens.filt_facturas.FiltUiState
import java.time.LocalDate

data class ListadoFacturasUiState(
    val facturasBase: List<Factura> = emptyList(),
    val facturasAMostrar: List<Factura> = emptyList(),
    val filtroTipoActual: Tipo = Tipo.Luz,
    val facturasPorAnio: Map<Int, List<Factura>> = emptyMap(),
    val ultimaFactura: Factura? = Factura(
        fechaExpedicion = LocalDate.of(2026, 5, 4),
        fechaInicio = LocalDate.of(2026, 4, 1),
        fechaFinal = LocalDate.of(2026, 4, 30),
        tipo = Tipo.Luz,
        estado = Estado.PendientePago,
        valor = 45.99
    ),
    val isRefreshing: Boolean = false,
    val filtros: FiltUiState = FiltUiState()
)
