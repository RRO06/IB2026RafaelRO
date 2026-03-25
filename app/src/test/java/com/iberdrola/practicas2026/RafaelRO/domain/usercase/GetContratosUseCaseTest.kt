package com.iberdrola.practicas2026.RafaelRO.domain.usercase

import com.iberdrola.practicas2026.RafaelRO.data.repository.FacturaRepository
import com.iberdrola.practicas2026.RafaelRO.domain.model.Contrato
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import com.iberdrola.practicas2026.RafaelRO.domain.model.Tipo
import com.iberdrola.practicas2026.RafaelRO.domain.network.BaseResult
import org.junit.Before
import org.junit.Test

class GetContratosUseCaseTest {

    private val repository: FacturaRepository = mockk()
    private lateinit var getContratosUseCase: GetContratosUseCase

    @Before
    fun setUp() {
        getContratosUseCase = GetContratosUseCase(repository)
    }

    @Test
    fun `cuando se invoca el use case debe retornar el flujo del repositorio con datos reales`() = runTest {
        // GIVEN: Creamos objetos que respeten tu @Entity de Contrato
        val listaContratosFake = listOf(
            Contrato(
                id = 1,
                tipo = Tipo.Luz,
                telefono = "600123456",
                direccion = "Calle Falsa 123",
                estado = true,
                email = "rafa@test.com"
            ),
            Contrato(
                id = 2,
                tipo = Tipo.Gas,
                telefono = "600987654",
                direccion = "Avenida Siempre Viva 742",
                estado = false,
                email = "iberdrola@test.com"
            )
        )

        val expectedResult = BaseResult.Sucess(listaContratosFake)

        // Configuramos el comportamiento del repositorio
        every { repository.getContratosFlow() } returns flowOf(expectedResult)

        // WHEN: Ejecutamos el Use Case
        val resultFlow = getContratosUseCase()
        val results = resultFlow.toList()

        // THEN: Validaciones rigurosas
        assertEquals("Debería haber emitido 1 resultado en el Flow", 1, results.size)

        val actualResult = results[0] as BaseResult.Sucess
        assertEquals("El primer contrato debería ser de tipo Luz", Tipo.Luz, actualResult.data[0].tipo)
        assertEquals("El ID del segundo contrato debería ser 2", 2, actualResult.data[1].id)

        // Verificamos que no hubo llamadas extrañas
        verify(exactly = 1) { repository.getContratosFlow() }
    }
}