package com.iberdrola.practicas2026.RafaelRO.ui.screens.list_facturas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.RafaelRO.data.local.datastore.SettingsDataStore
import com.iberdrola.practicas2026.RafaelRO.data.remote.AnalyticsManager
import com.iberdrola.practicas2026.RafaelRO.domain.model.Factura
import com.iberdrola.practicas2026.RafaelRO.domain.usercase.GetFacturaByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DetalleFacturaState {
    data object Loading : DetalleFacturaState()
    data class Success(
        val factura: Factura,
        val isFallback: Boolean // Solo true si se esperaba nube y vino local
    ) : DetalleFacturaState()
    data class Error(
        val message: String,
        val type: ListadoFacturasState.ErrorType = ListadoFacturasState.ErrorType.GENERIC
    ) : DetalleFacturaState()
}

@HiltViewModel
class DetalleFacturaViewModel @Inject constructor(
    private val getFacturaByIdUseCase: GetFacturaByIdUseCase,
    private val settingsDataStore: SettingsDataStore,
    private val analyticsManager: AnalyticsManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var state by mutableStateOf<DetalleFacturaState>(DetalleFacturaState.Loading)
        private set

    private val facturaId: Int = checkNotNull(savedStateHandle["facturaId"])

    init {
        loadFactura()
    }

    fun loadFactura() {
        viewModelScope.launch {
            state = DetalleFacturaState.Loading
            delay(1500)
            
            val modoNubeRequested = settingsDataStore.modoNubeFlow.firstOrNull() ?: false
            val result = getFacturaByIdUseCase(facturaId)
            
            state = if (result != null) {
                // Es fallback si queríamos nube pero el resultado es local
                val isFallback = modoNubeRequested && result.isLocal
                DetalleFacturaState.Success(result.factura, isFallback)
            } else {
                DetalleFacturaState.Error(
                    message = "No se ha podido encontrar la factura. Compruebe su conexión.",
                    type = ListadoFacturasState.ErrorType.NETWORK
                )
            }
        }
    }

    fun logClick(nombreBoton: String, nombrePantalla: String, parametrosExtra: Map<String, String> = emptyMap()) {
        analyticsManager.registrarClic(nombreBoton, nombrePantalla, parametrosExtra)
    }
}