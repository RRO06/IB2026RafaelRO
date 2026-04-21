package com.iberdrola.practicas2026.RafaelRO.ui.screens.filt_facturas

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class FilterViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Fuente de verdad reactiva conectada al SavedStateHandle
    val uiState: StateFlow<FiltUiState> = savedStateHandle.getStateFlow("filter_data", FiltUiState())

    private val currentState: FiltUiState get() = uiState.value

    init {
        val initial = savedStateHandle.get<FiltUiState>("filter_data")
        Log.d("FilterViewModel", "DEBUG: Initial state from SavedStateHandle: $initial")
    }

    /**
     * Inicializa los filtros con los valores recibidos de la pantalla anterior.
     * Solo se aplica si el estado actual es el por defecto para evitar sobreescrituras accidentales.
     */
    fun initFilters(filters: FiltUiState) {
        if (currentState == FiltUiState() && filters != FiltUiState()) {
            Log.d("FilterViewModel", "DEBUG: Initializing filters from external source: $filters")
            updateState(filters)
        }
    }

    fun onDateFromClick() {
        updateState(currentState.copy(showDatePickerFrom = true))
    }

    fun onDateToClick() {
        updateState(currentState.copy(showDatePickerTo = true))
    }

    fun onDateFromSelected(date: LocalDate?) {
        val error = if (date != null && currentState.dateTo != null && date.isAfter(currentState.dateTo)) {
            "La fecha de inicio no puede ser posterior"
        } else null
        
        updateState(currentState.copy(
            dateFrom = date,
            showDatePickerFrom = false,
            dateError = error
        ))
    }

    fun onDateToSelected(date: LocalDate?) {
        val error = if (date != null && currentState.dateFrom != null && date.isBefore(currentState.dateFrom)) {
            "La fecha de fin no puede ser anterior"
        } else null

        updateState(currentState.copy(
            dateTo = date,
            showDatePickerTo = false,
            dateError = error
        ))
    }

    fun clearError() {
        updateState(currentState.copy(dateError = null))
    }

    fun dismissDatePickers() {
        updateState(currentState.copy(showDatePickerFrom = false, showDatePickerTo = false))
    }

    fun onPriceRangeChanged(range: ClosedFloatingPointRange<Float>) {
        updateState(currentState.copy(
            priceRangeStart = range.start,
            priceRangeEnd = range.endInclusive
        ))
    }

    fun onStateToggle(estado: String) {
        val currentStates = currentState.selectedStates
        val newStates = if (currentStates.contains(estado)) {
            currentStates - estado
        } else {
            currentStates + estado
        }
        updateState(currentState.copy(selectedStates = newStates))
    }

    fun onClear() {
        updateState(FiltUiState())
    }

    private fun updateState(newState: FiltUiState) {
        Log.d("FilterViewModel", "DEBUG: Updating state in SavedStateHandle: $newState")
        savedStateHandle["filter_data"] = newState
    }
}
