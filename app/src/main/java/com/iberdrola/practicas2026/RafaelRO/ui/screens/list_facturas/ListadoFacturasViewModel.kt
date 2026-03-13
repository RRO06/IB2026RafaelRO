package com.iberdrola.practicas2026.RafaelRO.ui.screens.list_facturas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.RafaelRO.domain.model.Factura
import com.iberdrola.practicas2026.RafaelRO.domain.model.Tipo
import com.iberdrola.practicas2026.RafaelRO.domain.network.BaseResult
import com.iberdrola.practicas2026.RafaelRO.domain.network.InvokeException
import com.iberdrola.practicas2026.RafaelRO.domain.usercase.GetFacturasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ListadoFacturasViewModel @Inject constructor(
    private val getFacturasUseCase: GetFacturasUseCase
) : ViewModel() {
    var stateData by mutableStateOf<ListadoFacturasState>(ListadoFacturasState.Loading)
        private set
    var stateUI by mutableStateOf(ListadoFacturasUiState())
        private set

    init {
        cargarDatosIniciales()
    }

    private fun cargarDatosIniciales() {
        viewModelScope.launch {
            when (val result = getFacturasUseCase()) {
                is BaseResult.Sucess -> {
                    stateData = ListadoFacturasState.Success(result.data)
                    aplicarFiltroYAgrupacion(Tipo.Luz)
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


    fun onFilterLuz() = aplicarFiltroYAgrupacion(Tipo.Luz)
    fun onFilterGas() = aplicarFiltroYAgrupacion(Tipo.Gas)

    private fun aplicarFiltroYAgrupacion(tipo: Tipo) {
        stateUI = stateUI.copy(
            luz = tipo == Tipo.Luz,
            gas = tipo == Tipo.Gas
        )

        val successState = stateData as? ListadoFacturasState.Success ?: return
        val facturasBase = successState.facturas

        // 1. Filtramos por tipo (Luz o Gas)
        val filtrada = facturasBase.filter { it.tipo == tipo }

        if (filtrada.isEmpty()) {
            stateData = ListadoFacturasState.Error("No existen facturas de este tipo")
            return
        }

        // 2. Identificamos la última factura (la primera de la lista filtrada)
        val ultima = filtrada.first()

        // 3. Creamos una lista para el histórico excluyendo la última
        val historicoRestante = filtrada.drop(1)

        // 4. Agrupamos solo el histórico restante por año
        val agrupada = historicoRestante.groupBy { it.fechaFinal.year }

        stateUI = stateUI.copy(
            listaFiltrada = filtrada, // Mantenemos la filtrada completa por si la necesitas
            facturasPorAnio = agrupada, // Esta ahora no tiene la "última"
            ultimaFactura = ultima
        )
    }

    fun onFacturaClick() {
        stateUI = stateUI.copy(showDialog = true)
    }

    fun dismissDialog() {
        stateUI = stateUI.copy(showDialog = false)
    }

}