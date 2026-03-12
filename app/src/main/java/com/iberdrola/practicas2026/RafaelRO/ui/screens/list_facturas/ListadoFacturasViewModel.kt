package com.iberdrola.practicas2026.RafaelRO.ui.screens.list_facturas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.iberdrola.practicas2026.RafaelRO.domain.model.Tipo
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ListadoFacturasViewModel @Inject constructor(
) : ViewModel() {
    var stateData by mutableStateOf(ListadoFacturasState.Loading)
        private set
    var stateUI by mutableStateOf(ListadoFacturasUiState())
        private set

    val dateFormatter = DateTimeFormatter.ofPattern("d 'de' MMMM", Locale("es", "ES"))
    fun onFilterLuz() {
        filtrarYAgrupar(Tipo.Luz)
    }

    fun onFilterGas() {
        filtrarYAgrupar(Tipo.Gas)
    }

    private fun filtrarYAgrupar(tipo: Tipo) {
        val successState = stateData as? ListadoFacturasState.Success ?: return

        // 1. Filtrar y ordenar la lista base
        val filtrada = successState.facturas
            .filter { it.tipo == tipo }
            .sortedByDescending { it.fechaFinal }

        if (filtrada.isEmpty()) return

        // 2. Agrupar por año (Usando Year de java.time)
        val agrupada = filtrada.groupBy { it.fechaFinal.year }

        // 3. Actualizar el estado de una sola vez
        stateUI = stateUI.copy(
            luz = tipo == Tipo.Luz,
            gas = tipo == Tipo.Gas,
            listaFiltrada = filtrada,
            facturasPorAnio = agrupada, // Map<Int, List<Factura>>
            ultimaFactura = filtrada.first()
        )
    }

}