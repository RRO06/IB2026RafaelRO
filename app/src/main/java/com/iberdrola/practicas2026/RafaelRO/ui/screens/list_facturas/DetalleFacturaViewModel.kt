package com.iberdrola.practicas2026.RafaelRO.ui.screens.list_facturas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.RafaelRO.domain.model.Factura
import com.iberdrola.practicas2026.RafaelRO.domain.usercase.GetFacturaByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DetalleFacturaState {
    object Loading : DetalleFacturaState()
    data class Success(val factura: Factura) : DetalleFacturaState()
    data class Error(val message: String) : DetalleFacturaState()
}

@HiltViewModel
class DetalleFacturaViewModel @Inject constructor(
    private val getFacturaByIdUseCase: GetFacturaByIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var state by mutableStateOf<DetalleFacturaState>(DetalleFacturaState.Loading)
        private set

    private val facturaId: Int = checkNotNull(savedStateHandle["facturaId"])

    init {
        loadFactura()
    }

    private fun loadFactura() {
        viewModelScope.launch {
            state = DetalleFacturaState.Loading
            val factura = getFacturaByIdUseCase(facturaId)
            state = if (factura != null) {
                DetalleFacturaState.Success(factura)
            } else {
                DetalleFacturaState.Error("No se ha podido encontrar la factura.")
            }
        }
    }
}