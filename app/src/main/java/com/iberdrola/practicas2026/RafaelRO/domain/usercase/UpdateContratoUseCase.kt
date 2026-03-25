package com.iberdrola.practicas2026.RafaelRO.domain.usercase

import com.iberdrola.practicas2026.RafaelRO.data.repository.FacturaRepository
import javax.inject.Inject

class UpdateContratoUseCase @Inject constructor(
    private val repository: FacturaRepository
) {
    suspend operator fun invoke(id: Int, nuevoEmail: String, terminosAceptados: Boolean): Boolean {
        return repository.updateContratoFields(id, nuevoEmail, terminosAceptados)
    }
}