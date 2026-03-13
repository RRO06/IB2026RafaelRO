package com.iberdrola.practicas2026.RafaelRO.ui.common.components

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class UtilyClass {
    companion object {
        fun toSpanishMediumDate(fecha: LocalDate): String {
            val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("es", "ES"))
            return fecha.format(formatter).lowercase()
        }

        fun toLongSpanishDate(fecha: LocalDate): String {
            val formatter = DateTimeFormatter.ofPattern("d 'de' MMMM", Locale("es", "ES"))
            return fecha.format(formatter).lowercase()
        }

        fun toCurrencyFormat(cantidad: Double): String {
            return "%.2f €".format(cantidad)
        }
    }
}