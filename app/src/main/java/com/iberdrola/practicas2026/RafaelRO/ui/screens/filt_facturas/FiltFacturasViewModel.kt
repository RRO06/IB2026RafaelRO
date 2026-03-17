package com.iberdrola.practicas2026.RafaelRO.ui.screens.filt_facturas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.iberdrola.practicas2026.RafaelRO.domain.model.Estado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import javax.inject.Inject

class FilterViewModel @Inject constructor() : ViewModel() {

    var uiState by mutableStateOf(FiltUiState())
        private set

    fun onDateFromClick() {
        uiState = uiState.copy(showDatePickerFrom = true)
    }

    fun onDateToClick() {
        uiState = uiState.copy(showDatePickerTo = true)
    }

    fun onDateFromSelected(date: LocalDate?) {
        uiState = uiState.copy(
            dateFrom = date,
            showDatePickerFrom = false,
            dateError = if (date != null && uiState.dateTo != null && date.isAfter(uiState.dateTo)) {
                "La fecha de inicio no puede ser posterior"
            } else null
        )
    }

    fun onDateToSelected(date: LocalDate?) {
        uiState = uiState.copy(
            dateTo = date,
            showDatePickerTo = false,
            // Si la fecha fin es menor que la inicio, marcamos error
            dateError = if (date != null && uiState.dateFrom != null && date.isBefore(uiState.dateFrom)) {
                "La fecha de fin no puede ser anterior"
            } else null
        )
    }

    fun dismissDatePickers() {
        uiState = uiState.copy(showDatePickerFrom = false, showDatePickerTo = false)
    }

    fun onPriceRangeChanged(range: ClosedFloatingPointRange<Float>) {
        uiState = uiState.copy(
            priceRangeStart = range.start,
            priceRangeEnd = range.endInclusive
        )
    }

    fun onStateToggle(estado: String) {
        val currentStates = uiState.selectedStates
        val newStates = if (currentStates.contains(estado)) {
            currentStates - estado
        } else {
            currentStates + estado
        }

        uiState = uiState.copy(selectedStates = newStates)
    }

    fun onClear() {
        uiState = FiltUiState()
    }
}