package com.iberdrola.practicas2026.RafaelRO.ui.screens.list_facturas

import com.iberdrola.practicas2026.RafaelRO.domain.model.Estado
import com.iberdrola.practicas2026.RafaelRO.domain.model.Factura
import com.iberdrola.practicas2026.RafaelRO.domain.model.Tipo
import java.time.LocalDate
data class ListadoFacturasUiState(
    val facturasBase: List<Factura> = emptyList(),
    val facturasAMostrar: List<Factura> = emptyList(), // La lista final tras aplicar
    val filtroTipoActual: Tipo = Tipo.Luz,           // En lugar de dos booleanos (luz/gas)
    val facturasPorAnio: Map<Int, List<Factura>> = facturasAño,
    val ultimaFactura: Factura = Factura(
        fechaInicio = LocalDate.now(),
        fechaFinal = LocalDate.now(),
        tipo = Tipo.Luz,
        estado = Estado.PendientePago,
        valor = 20.00
    ),
    val showDialog: Boolean = false
)

val facturasAño: Map<Int, List<Factura>> = mapOf(
    2023 to listOf(
        Factura(
            id = 1,
            fechaInicio = LocalDate.of(2023, 11, 1),
            fechaFinal = LocalDate.of(2023, 11, 30),
            tipo = Tipo.Luz,
            estado = Estado.Pagado,
            valor = 42.10
        ),
        Factura(
            id = 2,
            fechaInicio = LocalDate.of(2023, 12, 1),
            fechaFinal = LocalDate.of(2023, 12, 31),
            tipo = Tipo.Luz,
            estado = Estado.Pagado,
            valor = 55.00
        )
    ),
    2024 to listOf(
        Factura(
            id = 3,
            fechaInicio = LocalDate.of(2024, 1, 1),
            fechaFinal = LocalDate.of(2024, 1, 31),
            tipo = Tipo.Luz,
            estado = Estado.PendientePago,
            valor = 60.25
        ),
        Factura(
            id = 4,
            fechaInicio = LocalDate.of(2024, 2, 1),
            fechaFinal = LocalDate.of(2024, 2, 28),
            tipo = Tipo.Luz,
            estado = Estado.CuotaFija,
            valor = 50.00
        )
    )
)