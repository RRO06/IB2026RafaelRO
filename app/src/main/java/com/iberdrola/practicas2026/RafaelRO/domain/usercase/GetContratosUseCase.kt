package com.iberdrola.practicas2026.RafaelRO.domain.usercase

import com.iberdrola.practicas2026.RafaelRO.data.repository.FacturaRepository
import com.iberdrola.practicas2026.RafaelRO.domain.model.Contrato
import com.iberdrola.practicas2026.RafaelRO.domain.network.BaseResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetContratosUseCase @Inject constructor(
    private val repository: FacturaRepository
) {
    operator fun invoke(): Flow<BaseResult<List<Contrato>>> {
        return repository.getContratosFlow()
    }
}