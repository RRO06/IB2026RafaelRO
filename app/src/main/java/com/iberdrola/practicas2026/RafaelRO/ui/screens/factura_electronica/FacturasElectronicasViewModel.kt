package com.iberdrola.practicas2026.RafaelRO.ui.screens.factura_electronica

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.RafaelRO.domain.usercase.GetContratosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.iberdrola.practicas2026.RafaelRO.data.remote.AnalyticsManager
import com.iberdrola.practicas2026.RafaelRO.data.remote.RemoteConfigManager
import com.iberdrola.practicas2026.RafaelRO.domain.model.Tipo
import com.iberdrola.practicas2026.RafaelRO.domain.network.BaseResult
import com.iberdrola.practicas2026.RafaelRO.domain.network.InvokeException
import javax.inject.Inject

@HiltViewModel
class FacturasElectronicasViewModel @Inject constructor(
    private val getContratosUseCase: GetContratosUseCase,
    private val remoteConfig: RemoteConfigManager,
    private val analyticsManager: AnalyticsManager
) : ViewModel() {
    var state by mutableStateOf(FacturasElectronicasUiState())
        private set

    init {
        remoteConfig.fetchConfig {
            cargardatos()
        }
    }

    private fun cargardatos() {
        viewModelScope.launch {
            getContratosUseCase().collect { result ->
                when (result) {
                    is BaseResult.Sucess -> {
                        state = state.copy(contratos = result.data, isRefreshing = false)
                    }

                    is BaseResult.Error -> {
                        val mensajeError = when (result.exception) {
                            InvokeException.FileError -> "Error al cargar los contratos locales"
                            else -> "Error inesperado"
                        }
                        state = state.copy(error = mensajeError, isRefreshing = false)
                    }
                }
            }
        }
    }

    fun refreshData() {
        logClick("pull_to_refresh", "factura_electronica")
        viewModelScope.launch {
            state = state.copy(isRefreshing = true)

            remoteConfig.fetchConfig {
                cargardatos()
            }
        }
    }

    fun isContratoBloqueado(tipo: Tipo): Boolean {
        return when (tipo) {
            Tipo.Luz -> !remoteConfig.isContratosLuzEnabled()
            Tipo.Gas -> !remoteConfig.isContratosGasEnabled()
        }
    }

    fun logClick(nombreBoton: String, nombrePantalla: String, parametrosExtra: Map<String, String> = emptyMap()) {
        analyticsManager.registrarClic(nombreBoton, nombrePantalla, parametrosExtra)
    }
}