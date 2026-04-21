package com.iberdrola.practicas2026.RafaelRO.ui.screens.list_facturas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.RafaelRO.domain.model.Tipo
import com.iberdrola.practicas2026.RafaelRO.domain.network.BaseResult
import com.iberdrola.practicas2026.RafaelRO.domain.network.InvokeException
import com.iberdrola.practicas2026.RafaelRO.domain.usercase.GetFacturasUseCase
import com.iberdrola.practicas2026.RafaelRO.ui.screens.filt_facturas.FiltUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListadoFacturasViewModel @Inject constructor(
    private val getFacturasUseCase: GetFacturasUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    var stateData by mutableStateOf<ListadoFacturasState>(ListadoFacturasState.Loading)
        private set
    var stateUI by mutableStateOf(ListadoFacturasUiState())
        private set

    private var filtrosAvanzadosActuales: FiltUiState
        get() = savedStateHandle.get<FiltUiState>("filter_data") ?: FiltUiState()
        set(value) {
            savedStateHandle["filter_data"] = value
        }

    init {
        cargarDatosIniciales()
        // Observamos cambios en los filtros para actualizar la interfaz automáticamente
        viewModelScope.launch {
            savedStateHandle.getStateFlow("filter_data", FiltUiState()).collect {
                actualizarInterfaz()
            }
        }
    }

    private fun cargarDatosIniciales() {
        viewModelScope.launch {
            stateUI = stateUI.copy(showDialog = false)
            stateData = ListadoFacturasState.Loading
            
            val result = getFacturasUseCase()
            
            delay(1000)
            
            when (result) {
                is BaseResult.Sucess -> {
                    stateUI = stateUI.copy(
                        facturasBase = result.data
                    )
                    actualizarInterfaz(Tipo.Luz)
                }

                is BaseResult.Error -> {
                    val memsaje = when (result.exception) {
                        InvokeException.DatabaseError -> "La base de datos no responde"
                        InvokeException.FileError -> "Error al cargar el archivo de facturas."
                        InvokeException.NetworkError -> "No tienes conexión a internet."
                        InvokeException.ServerError -> "El servidor de Iberdrola no responde."
                        is InvokeException.UnknownError -> "Error desconocido"
                    }
                    stateData = ListadoFacturasState.Error(memsaje)
                }
            }
        }
    }

    fun limpiarFiltros() {
        filtrosAvanzadosActuales = FiltUiState()
    }

    fun onFilterLuz() = actualizarInterfaz(Tipo.Luz)
    fun onFilterGas() = actualizarInterfaz(Tipo.Gas)

    fun actualizarInterfaz(
        tipo: Tipo = stateUI.filtroTipoActual,
        filtrosExtra: FiltUiState = filtrosAvanzadosActuales
    ) {
        if (stateUI.facturasBase.isEmpty() && stateData is ListadoFacturasState.Loading) {
            return
        }

        // Si se pasan filtrosExtra explícitamente (como desde limpiarFiltros), actualizamos el handle
        if (filtrosExtra != filtrosAvanzadosActuales) {
            filtrosAvanzadosActuales = filtrosExtra
        }

        val filtros = filtrosAvanzadosActuales

        val resultado = stateUI.facturasBase.filter { factura ->
            val cumpleTipo = factura.tipo == tipo
            val cumpleFecha =
                (filtros.dateFrom == null || !factura.fechaInicio.isBefore(filtros.dateFrom)) &&
                        (filtros.dateTo == null || !factura.fechaFinal.isAfter(filtros.dateTo))
            val cumpleImporte = factura.valor >= filtros.priceRangeStart &&
                    factura.valor <= filtros.priceRangeEnd
            val cumpleEstado = filtros.selectedStates.isEmpty() ||
                    filtros.selectedStates.contains(factura.estado.name)

            cumpleTipo && cumpleFecha && cumpleImporte && cumpleEstado
        }

        if (resultado.isEmpty()) {
            stateUI = stateUI.copy(filtroTipoActual = tipo)
            stateData = if (stateUI.facturasBase.isNotEmpty()) {
                ListadoFacturasState.Error("No existen facturas con estos filtros")
            } else {
                ListadoFacturasState.Error("No se han encontrado facturas en su cuenta")
            }
            return
        }

        val ultima = resultado.first()
        val agrupada = resultado.groupBy { it.fechaFinal.year }

        stateUI = stateUI.copy(
            filtroTipoActual = tipo,
            facturasAMostrar = resultado,
            facturasPorAnio = agrupada,
            ultimaFactura = ultima
        )
        stateData = ListadoFacturasState.Success(stateUI.facturasBase)
    }

    fun tieneFiltrosActivos(): Boolean = filtrosAvanzadosActuales != FiltUiState()
    
    fun onFacturaClick() {
        if (stateData is ListadoFacturasState.Success) {
            stateUI = stateUI.copy(showDialog = true)
        }
    }

    fun dismissDialog() {
        stateUI = stateUI.copy(showDialog = false)
    }

}