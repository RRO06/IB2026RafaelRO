package com.iberdrola.practicas2026.RafaelRO.ui.screens.list_facturas

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.RafaelRO.domain.model.Factura
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

    init {
        cargarDatosIniciales()

        // Observamos cambios en los filtros para actualizar el estado consolidado de la UI
        viewModelScope.launch {
            savedStateHandle.getStateFlow("filter_data", FiltUiState()).collect { nuevosFiltros ->
                // Actualizamos los filtros dentro del objeto de estado de la UI
                stateUI = stateUI.copy(filtros = nuevosFiltros)
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
                    val mensaje = when (result.exception) {
                        InvokeException.DatabaseError -> "La base de datos no responde"
                        InvokeException.FileError -> "Error al cargar el archivo de facturas."
                        InvokeException.NetworkError -> "No tienes conexión a internet."
                        InvokeException.ServerError -> "El servidor de Iberdrola no responde."
                        is InvokeException.UnknownError -> "Error desconocido"
                    }
                    stateData = ListadoFacturasState.Error(mensaje)
                }
            }
        }
    }

    fun limpiarFiltros() {
        Log.d("ListadoFacturasVM", "DEBUG: Limpiando filtros en SavedStateHandle")
        val resetFiltros = calcularRangoInicial(stateUI.facturasBase)
        savedStateHandle["filter_data"] = resetFiltros
    }

    private fun calcularRangoInicial(facturas: List<Factura>): FiltUiState {
        if (facturas.isEmpty()) return FiltUiState()

        val maxVal = facturas.maxOf { it.valor }.toFloat()
        val minVal = if (facturas.size == 1) 0f else facturas.minOf { it.valor }.toFloat()

        return FiltUiState(
            priceRangeStart = minVal,
            priceRangeEnd = maxVal,
            minPrice = minVal,
            maxPrice = maxVal
        )
    }

    fun onFilterLuz() = actualizarInterfaz(Tipo.Luz)
    fun onFilterGas() = actualizarInterfaz(Tipo.Gas)

    /**
     * Función principal que orquestal la actualización de la interfaz.
     */
    fun actualizarInterfaz(
        tipo: Tipo = stateUI.filtroTipoActual,
        filtrosExtra: FiltUiState = stateUI.filtros
    ) {
        if (estaCargando()) return

        // 1. Sincronización de filtros externos (si vienen del NavHost por ejemplo)
        if (filtrosExtra != stateUI.filtros) {
            savedStateHandle["filter_data"] = filtrosExtra
            return
        }

        // 2. Inicialización de rango si es la primera vez o se han reseteado
        if (necesitaRangoInicial()) {
            savedStateHandle["filter_data"] = calcularRangoInicial(stateUI.facturasBase)
            return
        }

        // 3. Proceso de filtrado
        val facturasFiltradas = filtrarFacturas(tipo, stateUI.filtros)

        // 4. Actualización del estado según el resultado
        if (facturasFiltradas.isEmpty()) {
            gestionarErrorSinResultados(tipo)
        } else {
            actualizarEstadoExito(tipo, facturasFiltradas)
        }
    }

    private fun estaCargando() = stateUI.facturasBase.isEmpty() && stateData is ListadoFacturasState.Loading

    private fun necesitaRangoInicial() = stateUI.filtros == FiltUiState() && stateUI.facturasBase.isNotEmpty()

    private fun filtrarFacturas(tipo: Tipo, filtros: FiltUiState): List<Factura> {
        return stateUI.facturasBase.filter { factura ->
            val cumpleTipo = factura.tipo == tipo
            val cumpleFecha = (filtros.dateFrom == null || !factura.fechaInicio.isBefore(filtros.dateFrom)) &&
                    (filtros.dateTo == null || !factura.fechaFinal.isAfter(filtros.dateTo))
            val cumpleImporte = factura.valor >= filtros.priceRangeStart &&
                    factura.valor <= filtros.priceRangeEnd
            val cumpleEstado = filtros.selectedStates.isEmpty() ||
                    filtros.selectedStates.contains(factura.estado.name)

            cumpleTipo && cumpleFecha && cumpleImporte && cumpleEstado
        }
    }

    private fun gestionarErrorSinResultados(tipo: Tipo) {
        stateUI = stateUI.copy(filtroTipoActual = tipo)
        stateData = if (stateUI.facturasBase.isNotEmpty()) {
            ListadoFacturasState.Error("No existen facturas con estos filtros")
        } else {
            ListadoFacturasState.Error("No se han encontrado facturas en su cuenta")
        }
    }

    private fun actualizarEstadoExito(tipo: Tipo, facturas: List<Factura>) {
        val agrupada = facturas.groupBy { it.fechaFinal.year }
        stateUI = stateUI.copy(
            filtroTipoActual = tipo,
            facturasAMostrar = facturas,
            facturasPorAnio = agrupada,
            ultimaFactura = facturas.firstOrNull()
        )
        stateData = ListadoFacturasState.Success(stateUI.facturasBase)
    }

    fun tieneFiltrosActivos(): Boolean {
        val base = calcularRangoInicial(stateUI.facturasBase)
        // Consideramos activos si son diferentes al rango base (excluyendo flags de UI como dateError)
        return stateUI.filtros.copy(showDatePickerFrom = false, showDatePickerTo = false, dateError = null) !=
                base.copy(showDatePickerFrom = false, showDatePickerTo = false, dateError = null)
    }

    fun onFacturaClick() {
        if (stateData is ListadoFacturasState.Success) {
            stateUI = stateUI.copy(showDialog = true)
        }
    }

    fun dismissDialog() {
        stateUI = stateUI.copy(showDialog = false)
    }

}
