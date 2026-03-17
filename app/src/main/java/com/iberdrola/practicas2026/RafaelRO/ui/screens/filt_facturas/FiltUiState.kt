package com.iberdrola.practicas2026.RafaelRO.ui.screens.filt_facturas

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class FiltUiState(
    val dateFrom: LocalDate? = null, // Mejor que String para comparar
    val dateTo: LocalDate? = null,
    val showDatePickerFrom: Boolean = false,
    val showDatePickerTo: Boolean = false,
    val dateError: String? = null, // Para el control de errores
    val priceRangeStart: Float = 0f,
    val priceRangeEnd: Float = 500f,
    val selectedStates: Set<String> = setOf()
)  : Parcelable