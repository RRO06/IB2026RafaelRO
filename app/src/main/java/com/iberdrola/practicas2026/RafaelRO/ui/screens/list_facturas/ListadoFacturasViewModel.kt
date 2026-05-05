package com.iberdrola.practicas2026.RafaelRO.ui.screens.list_facturas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.RafaelRO.data.local.datastore.SettingsDataStore
import com.iberdrola.practicas2026.RafaelRO.domain.model.Factura
import com.iberdrola.practicas2026.RafaelRO.domain.model.Tipo
import com.iberdrola.practicas2026.RafaelRO.domain.network.BaseResult
import com.iberdrola.practicas2026.RafaelRO.domain.network.InvokeException
import com.iberdrola.practicas2026.RafaelRO.domain.usercase.GetFacturasUseCase
import com.iberdrola.practicas2026.RafaelRO.ui.screens.filt_facturas.FiltUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.floor

@HiltViewModel
class ListadoFacturasViewModel @Inject constructor(
    private val getFacturasUseCase: GetFacturasUseCase,
    private val settingsDataStore: SettingsDataStore,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    var stateData by mutableStateOf<ListadoFacturasState>(ListadoFacturasState.Loading)
        private set
    var stateUI by mutableStateOf(ListadoFacturasUiState())
        private set

    init {
        cargarDatos()

        viewModelScope.launch {
            savedStateHandle.getStateFlow("filter_data", FiltUiState()).collect { nuevosFiltros ->
                stateUI = stateUI.copy(filtros = nuevosFiltros)
                actualizarInterfaz()
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            stateUI = stateUI.copy(isRefreshing = true)
            cargarDatosInternal()
            stateUI = stateUI.copy(isRefreshing = false)
        }
    }

    private fun cargarDatos() {
        viewModelScope.launch {
            cargarDatosInternal()
        }
    }

    private suspend fun cargarDatosInternal() {
        if (!stateUI.isRefreshing) {
            stateData = ListadoFacturasState.Loading
        }

        val modoNubeRequested = settingsDataStore.modoNubeFlow.firstOrNull() ?: false
        val result = getFacturasUseCase()
        delay((1000L..2000L).random())

        when (result) {
            is BaseResult.Sucess -> {
                val isFallback = modoNubeRequested && result.isLocal
                stateUI = stateUI.copy(
                    facturasBase = result.data,
                    isFallback = isFallback
                )
                actualizarInterfaz(stateUI.filtroTipoActual)
            }

            is BaseResult.Error -> {
                if (stateUI.facturasBase.isEmpty()) {
                    val (mensaje, type) = when (result.exception) {
                        InvokeException.DatabaseError -> "La base de datos no responde" to ListadoFacturasState.ErrorType.DATABASE
                        InvokeException.FileError -> "Error al cargar el archivo de facturas" to ListadoFacturasState.ErrorType.GENERIC
                        InvokeException.NetworkError, InvokeException.ServerError -> "No se ha podido conectar con el servidor. Compruebe su conexión a internet." to ListadoFacturasState.ErrorType.NETWORK
                        is InvokeException.UnknownError -> "Se ha producido un error inesperado" to ListadoFacturasState.ErrorType.GENERIC
                    }
                    stateData = ListadoFacturasState.Error(mensaje, type)
                }
            }
        }
    }

    fun limpiarFiltros() {
        val resetFiltros = calcularRangoInicial(stateUI.facturasBase)
        savedStateHandle["filter_data"] = resetFiltros
    }

    private fun calcularRangoInicial(facturas: List<Factura>): FiltUiState {
        if (facturas.isEmpty()) return FiltUiState()
        val maxVal = ceil(facturas.maxOf { it.valor }).toFloat()
        val minVal = floor(facturas.minOf { it.valor }).toFloat()
        return FiltUiState(
            priceRangeStart = minVal,
            priceRangeEnd = maxVal,
            minPrice = minVal,
            maxPrice = maxVal
        )
    }

    fun onFilterLuz() = actualizarInterfaz(Tipo.Luz)
    fun onFilterGas() = actualizarInterfaz(Tipo.Gas)

    fun actualizarInterfaz(
        tipo: Tipo = stateUI.filtroTipoActual,
        filtrosExtra: FiltUiState = stateUI.filtros
    ) {
        if (estaCargando()) return

        val currentState = stateData
        if (stateUI.facturasBase.isEmpty() && 
            currentState is ListadoFacturasState.Error && 
            currentState.type != ListadoFacturasState.ErrorType.EMPTY_RESULTS) {
            stateUI = stateUI.copy(filtroTipoActual = tipo)
            return
        }

        if (filtrosExtra != stateUI.filtros) {
            savedStateHandle["filter_data"] = filtrosExtra
            return
        }

        if (necesitaRangoInicial()) {
            savedStateHandle["filter_data"] = calcularRangoInicial(stateUI.facturasBase)
            return
        }

        val facturasFiltradas = filtrarFacturas(tipo, stateUI.filtros)

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
            val cumpleFecha = (filtros.dateFrom == null || !factura.fechaExpedicion.isBefore(filtros.dateFrom)) &&
                    (filtros.dateTo == null || !factura.fechaExpedicion.isAfter(filtros.dateTo))

            val cumpleImporte = factura.valor >= filtros.priceRangeStart.toInt().toDouble() &&
                                factura.valor <= filtros.priceRangeEnd.toInt().toDouble()

            val cumpleEstado = filtros.selectedStates.isEmpty() ||
                    filtros.selectedStates.contains(factura.estado.name)

            cumpleTipo && cumpleFecha && cumpleImporte && cumpleEstado
        }
    }

    private fun gestionarErrorSinResultados(tipo: Tipo) {
        stateUI = stateUI.copy(filtroTipoActual = tipo)
        val mensaje = if (stateUI.facturasBase.isNotEmpty()) "No existen facturas con estos filtros" 
                     else "No se han encontrado facturas en su cuenta"
        stateData = ListadoFacturasState.Error(mensaje, ListadoFacturasState.ErrorType.EMPTY_RESULTS)
    }

    private fun actualizarEstadoExito(tipo: Tipo, facturas: List<Factura>) {
        val agrupada = facturas.groupBy { it.fechaExpedicion.year }
        stateUI = stateUI.copy(
            filtroTipoActual = tipo,
            facturasAMostrar = facturas,
            facturasPorAnio = agrupada,
            ultimaFactura = facturas.firstOrNull()
        )
        stateData = ListadoFacturasState.Success(facturas, isFallback = stateUI.isFallback)
    }

    fun tieneFiltrosActivos(): Boolean {
        val base = calcularRangoInicial(stateUI.facturasBase)
        return stateUI.filtros.copy(showDatePickerFrom = false, showDatePickerTo = false, dateError = null) !=
                base.copy(showDatePickerFrom = false, showDatePickerTo = false, dateError = null)
    }
}