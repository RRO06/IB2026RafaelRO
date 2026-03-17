package com.iberdrola.practicas2026.RafaelRO.ui.screens.list_facturas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.RafaelRO.domain.model.Tipo
import com.iberdrola.practicas2026.RafaelRO.domain.network.BaseResult
import com.iberdrola.practicas2026.RafaelRO.domain.network.InvokeException
import com.iberdrola.practicas2026.RafaelRO.domain.usercase.GetFacturasUseCase
import com.iberdrola.practicas2026.RafaelRO.ui.screens.filt_facturas.FiltUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListadoFacturasViewModel @Inject constructor(
    private val getFacturasUseCase: GetFacturasUseCase
) : ViewModel() {
    var stateData by mutableStateOf<ListadoFacturasState>(ListadoFacturasState.Loading)
        private set
    var stateUI by mutableStateOf(ListadoFacturasUiState())
        private set
    private var filtrosAvanzadosActuales = FiltUiState()

    init {
        cargarDatosIniciales()
    }

    private fun cargarDatosIniciales() {
        viewModelScope.launch {
            when (val result = getFacturasUseCase()) {
                is BaseResult.Sucess -> {
                    stateData = ListadoFacturasState.Success(result.data)
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
        actualizarInterfaz(
            tipo = stateUI.filtroTipoActual,
            filtrosExtra = FiltUiState()
        )
    }

    fun onFilterLuz() = actualizarInterfaz(Tipo.Luz)
    fun onFilterGas() = actualizarInterfaz(Tipo.Gas)
    fun actualizarInterfaz(
        tipo: Tipo = stateUI.filtroTipoActual,
        filtrosExtra: FiltUiState = filtrosAvanzadosActuales
    ) {
        filtrosAvanzadosActuales = filtrosExtra

        val resultado = stateUI.facturasBase.filter { factura ->
            val cumpleTipo = factura.tipo == tipo
            val cumpleFecha =
                (filtrosExtra.dateFrom == null || !factura.fechaInicio.isBefore(filtrosExtra.dateFrom)) &&
                        (filtrosExtra.dateTo == null || !factura.fechaFinal.isAfter(filtrosExtra.dateTo))
            val cumpleImporte = factura.valor >= filtrosExtra.priceRangeStart &&
                    factura.valor <= filtrosExtra.priceRangeEnd
            val cumpleEstado = filtrosExtra.selectedStates.isEmpty() ||
                    filtrosExtra.selectedStates.contains(factura.estado.name)

            cumpleTipo && cumpleFecha && cumpleImporte && cumpleEstado
        }

        if (resultado.isEmpty()) {
            stateUI = stateUI.copy(filtroTipoActual = tipo)
            // Si 'todasLasFacturas' NO está vacía, pero 'resultado' SÍ, es culpa de los filtros
            if (stateUI.facturasBase.isNotEmpty()) {
                stateData = ListadoFacturasState.Error("No existen facturas con estos filtros")
            } else {
                // Error real: la base de datos no trajo nada
                stateData = ListadoFacturasState.Error("No se han encontrado facturas en su cuenta")
            }
            return
        }

        // 2. Identificamos la última factura (la primera de la lista filtrada)
        val ultima = resultado.first()

        // 3. Creamos una lista para el histórico excluyendo la última
        val historicoRestante = resultado.drop(1)

        // 4. Agrupamos solo el histórico restante por año
        val agrupada = historicoRestante.groupBy { it.fechaFinal.year }

        stateUI = stateUI.copy(
            filtroTipoActual = tipo,
            facturasAMostrar = resultado, // Mantenemos la filtrada completa por si la necesitas
            facturasPorAnio = agrupada, // Esta ahora no tiene la "última"
            ultimaFactura = ultima
        )
        stateData = ListadoFacturasState.Success(stateUI.facturasBase)
    }
    fun tieneFiltrosActivos(): Boolean = filtrosAvanzadosActuales != FiltUiState()
    fun onFacturaClick() {
        stateUI = stateUI.copy(showDialog = true)
    }

    fun dismissDialog() {
        stateUI = stateUI.copy(showDialog = false)
    }

}