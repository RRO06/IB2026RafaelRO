package com.iberdrola.practicas2026.RafaelRO.domain.usercase

import com.iberdrola.practicas2026.RafaelRO.data.repository.FacturaRepository
import com.iberdrola.practicas2026.RafaelRO.domain.model.Factura
import com.iberdrola.practicas2026.RafaelRO.domain.network.BaseResult
import javax.inject.Inject

class GetFacturaByIdUseCase @Inject constructor(
    private val repository: FacturaRepository
) {
    suspend operator fun invoke(id: Int): Factura? {
        val result = repository.getFacturas()
        return if (result is BaseResult.Sucess) {
            result.data.find { it.id == id }
        } else null
    }
}