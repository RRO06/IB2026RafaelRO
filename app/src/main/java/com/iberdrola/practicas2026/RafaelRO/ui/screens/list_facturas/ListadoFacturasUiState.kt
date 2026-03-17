package com.iberdrola.practicas2026.RafaelRO.ui.screens.list_facturas

import com.iberdrola.practicas2026.RafaelRO.domain.model.Estado
import com.iberdrola.practicas2026.RafaelRO.domain.model.Factura
import com.iberdrola.practicas2026.RafaelRO.domain.model.Tipo
import java.time.LocalDate
data class ListadoFacturasUiState(
    val facturasBase: List<Factura> = emptyList(),
    val facturasAMostrar: List<Factura> = emptyList(), // La lista final tras aplicar
    val filtroTipoActual: Tipo = Tipo.Luz,           // En lugar de dos booleanos (luz/gas)
    val facturasPorAnio: Map<Int, List<Factura>> = emptyMap(),
    val ultimaFactura: Factura = Factura(
        fechaInicio = LocalDate.now(),
        fechaFinal = LocalDate.now(),
        tipo = Tipo.Luz,
        estado = Estado.Pagado,
        valor = 20.00
    ),
    val showDialog: Boolean = false
)
