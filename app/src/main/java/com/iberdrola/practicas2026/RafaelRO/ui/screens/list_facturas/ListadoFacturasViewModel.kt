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
import kotlinx.coroutines.delay
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
            // Limpiamos cualquier estado de diálogo previo al iniciar
            stateUI = stateUI.copy(showDialog = false)
            stateData = ListadoFacturasState.Loading
            
            val result = getFacturasUseCase()
            
            // Forzamos un retraso de 1 segundo para asegurar que se vea el skeleton
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
        if (stateUI.facturasBase.isEmpty() && stateData is ListadoFacturasState.Loading) {
            return
        }

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
        // Solo permitimos abrir el diálogo si los datos están listos
        if (stateData is ListadoFacturasState.Success) {
            stateUI = stateUI.copy(showDialog = true)
        }
    }

    fun dismissDialog() {
        stateUI = stateUI.copy(showDialog = false)
    }

}