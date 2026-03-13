package com.iberdrola.practicas2026.RafaelRO.domain.usercase

import com.iberdrola.practicas2026.RafaelRO.data.repository.FacturaRepository
import com.iberdrola.practicas2026.RafaelRO.domain.model.Factura
import com.iberdrola.practicas2026.RafaelRO.domain.model.Tipo
import com.iberdrola.practicas2026.RafaelRO.domain.network.BaseResult
import javax.inject.Inject

class GetFacturasUseCase @Inject constructor(
    private val repository: FacturaRepository
) {
    suspend operator fun invoke(tipo: Tipo? = null): BaseResult<List<Factura>> {
        return when (val result = repository.getFacturasAPI()) {
            is BaseResult.Sucess -> {
                // Si tuvo éxito, aplicamos la lógica de negocio (ordenar y filtrar)
                val facturasOriginales = result.data

                val facturasProcesadas = facturasOriginales
                    .sortedByDescending { it.fechaFinal }
                    .let { lista ->
                        if (tipo != null) lista.filter { it.tipo == tipo }
                        else lista
                    }

                BaseResult.Sucess(facturasProcesadas)
            }

            is BaseResult.Error -> {
                result
            }
        }
    }
}