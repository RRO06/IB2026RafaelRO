package com.iberdrola.practicas2026.RafaelRO.ui.screens.filt_facturas

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FilterViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(FilterUiState())
    val uiState: StateFlow<FilterUiState> = _uiState.asStateFlow()

    // Métodos para actualizar el estado (Lógica pendiente para ti)
    fun onDateFromChanged(date: String) {
        _uiState.update { it.copy(dateFrom = date) }
    }

    fun onDateToChanged(date: String) {
        _uiState.update { it.copy(dateTo = date) }
    }

    fun onPriceRangeChanged(range: ClosedFloatingPointRange<Float>) {
        _uiState.update { it.copy(priceRange = range) }
    }

    fun onStateCheckboxClicked(state: String) {
        _uiState.update { current ->
            val newSet = if (current.selectedStates.contains(state)) {
                current.selectedStates - state
            } else {
                current.selectedStates + state
            }
            current.copy(selectedStates = newSet)
        }
    }

    fun clearFilters() {
        _uiState.value = FilterUiState()
    }
}