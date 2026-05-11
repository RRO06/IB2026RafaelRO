package com.iberdrola.practicas2026.RafaelRO.ui.screens.factura_electronica

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.RafaelRO.domain.network.BaseResult
import com.iberdrola.practicas2026.RafaelRO.domain.network.InvokeException
import com.iberdrola.practicas2026.RafaelRO.domain.usercase.GetContratosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FacturasElectronicasViewModel @Inject constructor(
    private val getContratosUseCase: GetContratosUseCase
) : ViewModel() {
    var state by mutableStateOf(FacturasElectronicasUiState())
        private set

    init {
        observarContratos()
    }

    private fun observarContratos() {
        viewModelScope.launch {
            // Un único collect para toda la vida del ViewModel
            getContratosUseCase().collect { result ->
                when (result) {
                    is BaseResult.Sucess -> {
                        state = state.copy(
                            contratos = result.data,
                            isRefreshing = false // Paramos el refresco al recibir datos
                        )
                    }

                    is BaseResult.Error -> {
                        val mensajeError = when (result.exception) {
                            InvokeException.FileError -> "Error al cargar los contratos locales"
                            else -> "Error inesperado"
                        }
                        state = state.copy(
                            error = mensajeError, 
                            isRefreshing = false
                        )
                    }
                }
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            state = state.copy(isRefreshing = true)
            // Añadimos un pequeño delay para que la animación de la flecha sea fluida
            // y no se quede trabada por una actualización demasiado rápida
            delay(1000)
            // Al ser un Flow reactivo, si hay cambios en Room el observer de arriba 
            // ya actualizará los datos. Si no hay cambios, forzamos el fin de la animación:
            state = state.copy(isRefreshing = false)
        }
    }
}
