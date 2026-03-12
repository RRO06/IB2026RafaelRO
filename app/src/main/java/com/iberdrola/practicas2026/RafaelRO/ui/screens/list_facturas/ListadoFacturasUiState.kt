package com.iberdrola.practicas2026.RafaelRO.ui.screens.list_facturas

import com.iberdrola.practicas2026.RafaelRO.domain.model.Factura
import com.iberdrola.practicas2026.RafaelRO.domain.model.Tipo
import java.time.LocalDate

data class ListadoFacturasUiState(
    val listaFiltrada : List<Factura> = emptyList<Factura>(),
    val luz : Boolean = true,
    val gas : Boolean = false,
    val facturasPorAnio: Map<Int, List<Factura>> = emptyMap(),
    val ultimaFactura : Factura = Factura(
        fechaInicio = LocalDate.now(),
        fechaFinal = LocalDate.now(),
        tipo = Tipo.Luz,
        estado = true,
        valor = 20.00
    )
)
