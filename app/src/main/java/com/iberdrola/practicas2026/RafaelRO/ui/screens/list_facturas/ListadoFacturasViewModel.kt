package com.iberdrola.practicas2026.RafaelRO.ui.screens.list_facturas

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.RafaelRO.data.local.datastore.SettingsDataStore
import com.iberdrola.practicas2026.RafaelRO.domain.model.Factura
import com.iberdrola.practicas2026.RafaelRO.data.remote.AnalyticsManager
import com.iberdrola.practicas2026.RafaelRO.data.remote.RemoteConfigManager
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
    private val analyticsManager: AnalyticsManager,
    private val remoteConfig: RemoteConfigManager,
    private val settingsDataStore: SettingsDataStore,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    var stateData by mutableStateOf<ListadoFacturasState>(ListadoFacturasState.Loading)
        private set
    var stateUI by mutableStateOf(ListadoFacturasUiState())
        private set

    init {
        actualizarFlagsConfig()
        observarFiltros()
        determinarTipoInicialYLoguear()
    }

    private fun actualizarFlagsConfig() {
        stateUI = stateUI.copy(
            isLuzEnabled = remoteConfig.isContratosLuzEnabled(),
            isGasEnabled = remoteConfig.isContratosGasEnabled()
        )
    }

    private fun observarFiltros() {
        viewModelScope.launch {
            savedStateHandle.getStateFlow(
                key = "filter_data",
                initialValue = FiltUiState()
            ).collect { nuevosFiltros ->
                stateUI = stateUI.copy(filtros = nuevosFiltros)
                if (stateUI.facturasBase.isNotEmpty()) {
                    actualizarInterfaz()
                }
            }
        }
    }

    private fun determinarTipoInicialYLoguear() {
        val tipoInicial = when {
            stateUI.isLuzEnabled -> Tipo.Luz
            stateUI.isGasEnabled -> Tipo.Gas
            else -> null
        }

        if (tipoInicial != null) {
            cargarDatosIniciales(tipoInicial)
        } else {
            stateData = ListadoFacturasState.Error(
                message = "No hay servicios disponibles actualmente",
                type = ListadoFacturasState.ErrorType.NO_SERVICES
            )
        }
    }

    private fun cargarDatosIniciales(tipoInicial: Tipo) {
        viewModelScope.launch {
            stateUI = stateUI.copy(filtroTipoActual = tipoInicial)
            cargarDatosInternal()
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            stateUI = stateUI.copy(isRefreshing = true)
            stateData = ListadoFacturasState.Loading
            try {
                remoteConfig.fetchAndActivate()
                actualizarFlagsConfig()

                if (!stateUI.isLuzEnabled && !stateUI.isGasEnabled) {
                    stateData = ListadoFacturasState.Error(
                        message = "No hay servicios disponibles actualmente",
                        type = ListadoFacturasState.ErrorType.NO_SERVICES
                    )
                } else {
                    cargarDatosInternal()
                }
            } catch (e: Exception) {
                Log.e("ListadoFacturaVM", "Excepción en refreshData", e)
            } finally {
                stateUI = stateUI.copy(isRefreshing = false)
            }
        }
    }

    private suspend fun cargarDatosInternal() {
        stateData = ListadoFacturasState.Loading

        val result = getFacturasUseCase()
        delay((1000L..3000L).random())

        when (result) {
            is BaseResult.Sucess -> {
                val modoNubeRequested = settingsDataStore.modoNubeFlow.firstOrNull() ?: false
                val isFallback = modoNubeRequested && result.isLocal
                stateUI = stateUI.copy(
                    facturasBase = result.data,
                    isFallback = isFallback
                )
                stateData = ListadoFacturasState.Success(result.data, isFallback)
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
                } else {
                    actualizarInterfaz()
                }
            }
        }
    }

    fun limpiarFiltros() {
        val resetFiltros = calcularRangoInicial(stateUI.facturasBase)
        Log.d("ListadoFacturaVM", "limpiarFiltros: $resetFiltros")
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

    fun onFilterLuz() {
        if (!stateUI.isLuzEnabled) return
        analyticsManager.logClick("filtro_rapido_luz", "listado_facturas")
        actualizarInterfaz(Tipo.Luz)
    }

    fun onFilterGas() {
        if (!stateUI.isGasEnabled) return
        analyticsManager.logClick("filtro_rapido_gas", "listado_facturas")
        actualizarInterfaz(Tipo.Gas)
    }

    fun actualizarInterfaz(
        tipo: Tipo = stateUI.filtroTipoActual,
        filtrosExtra: FiltUiState? = null
    ) {
        if (ignorarPorCargaInicial(filtrosExtra)) return
        if (!asegurarServiciosDisponibles()) return

        val tipoValidado = validarYObtenerTipo(tipo)
         if (procesarCambioFiltros(filtrosExtra)) return

        if (asegurarRangoInicial()) return

        val facturasFiltradas = filtrarFacturas(tipoValidado, stateUI.filtros)


        finalizarActualizacion(tipoValidado, facturasFiltradas)
    }

    private fun ignorarPorCargaInicial(filtrosExtra: FiltUiState?): Boolean {
        if (stateUI.facturasBase.isEmpty() && stateData is ListadoFacturasState.Loading) {
            if (stateUI.isLuzEnabled || stateUI.isGasEnabled) {
                filtrosExtra?.let {
                    if (it != stateUI.filtros) savedStateHandle["filter_data"] = it
                }
            }
            if (stateUI.isLuzEnabled || stateUI.isGasEnabled) {
                filtrosExtra?.let {
                    if (it != stateUI.filtros) savedStateHandle["filter_data"] = it
                }
            }
            return true
        }
        return false
    }

    private fun asegurarServiciosDisponibles(): Boolean {
        if (!stateUI.isLuzEnabled && !stateUI.isGasEnabled) {
            stateData = ListadoFacturasState.Error(
                message = "No hay servicios disponibles actualmente",
                type = ListadoFacturasState.ErrorType.NO_SERVICES
            )
            return false
        }
        return true
    }

    private fun validarYObtenerTipo(tipo: Tipo): Tipo {
        return when {
            tipo == Tipo.Luz && !stateUI.isLuzEnabled -> if (stateUI.isGasEnabled) Tipo.Gas else tipo
            tipo == Tipo.Gas && !stateUI.isGasEnabled -> if (stateUI.isLuzEnabled) Tipo.Luz else tipo
            else -> tipo
        }
    }
    private fun procesarCambioFiltros(filtrosExtra: FiltUiState?): Boolean {
        if (filtrosExtra != null && filtrosExtra != stateUI.filtros) {
            savedStateHandle["filter_data"] = filtrosExtra
            return true
        }
        return false
    }

    private fun asegurarRangoInicial(): Boolean {
        if (necesitaRangoInicial()) {
            savedStateHandle["filter_data"] = calcularRangoInicial(stateUI.facturasBase)
            return true
        }
        return false
    }

    private fun finalizarActualizacion(tipo: Tipo, facturasFiltradas: List<Factura>) {
        if (facturasFiltradas.isEmpty()) {
            val estadoActual = stateData
            if (estadoActual is ListadoFacturasState.Error &&
                estadoActual.type != ListadoFacturasState.ErrorType.EMPTY_RESULTS
            ) {
                stateUI = stateUI.copy(filtroTipoActual = tipo)
                return
            }
            gestionarErrorSinResultados(tipo)
        } else {
            actualizarEstadoExito(tipo, facturasFiltradas)
        }
    }

    private fun necesitaRangoInicial() =
        stateUI.filtros == FiltUiState() && stateUI.facturasBase.isNotEmpty()

    private fun filtrarFacturas(tipo: Tipo, filtros: FiltUiState): List<Factura> {
        return stateUI.facturasBase.filter { factura ->
            val cumpleTipo = factura.tipo == tipo
            val cumpleFecha =
                (filtros.dateFrom == null || !factura.fechaExpedicion.isBefore(filtros.dateFrom)) &&
                        (filtros.dateTo == null || !factura.fechaExpedicion.isAfter(filtros.dateTo))
            val cumpleImporte =
                factura.valor >= filtros.priceRangeStart && factura.valor <= filtros.priceRangeEnd
            val cumpleEstado =
                filtros.selectedStates.isEmpty() || filtros.selectedStates.contains(factura.estado.name)

            cumpleTipo && cumpleFecha && cumpleImporte && cumpleEstado
        }
    }

    private fun gestionarErrorSinResultados(tipo: Tipo) {
        val tieneFacturasDeEsteTipo = stateUI.facturasBase.any { it.tipo == tipo }

        stateUI = stateUI.copy(
            filtroTipoActual = tipo,
            facturasAMostrar = emptyList(),
            facturasPorAnio = emptyMap(),
            ultimaFactura = null
        )

        val mensaje = if (tieneFacturasDeEsteTipo) {
            "No existen facturas con estos filtros"
        } else {
            "No se han encontrado facturas de tipo ${tipo.name} en su cuenta"
        }

        stateData = ListadoFacturasState.Error(
            message = mensaje,
            type = ListadoFacturasState.ErrorType.EMPTY_RESULTS
        )
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
        if (stateUI.facturasBase.isEmpty()) return false
        val base = calcularRangoInicial(stateUI.facturasBase)
        return stateUI.filtros.copy(
            showDatePickerFrom = false,
            showDatePickerTo = false,
            dateError = null
        ) != base.copy(showDatePickerFrom = false, showDatePickerTo = false, dateError = null)
    }
}