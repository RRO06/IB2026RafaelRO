package com.iberdrola.practicas2026.RafaelRO.domain.usercase

import com.iberdrola.practicas2026.RafaelRO.data.repository.FacturaRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UpdateContratoUseCaseTest {

    // 1. Mock del repositorio
    private val repository: FacturaRepository = mockk()

    // 2. Clase bajo prueba
    private lateinit var updateContratoUseCase: UpdateContratoUseCase

    @Before
    fun setUp() {
        updateContratoUseCase = UpdateContratoUseCase(repository)
    }

    @Test
    fun `cuando el repositorio actualiza correctamente debe retornar true`() = runTest {
        // GIVEN: Datos de entrada y respuesta exitosa del repo
        val id = 123
        val email = "nuevo@email.com"
        val terminos = true

        coEvery {
            repository.updateContratoFields(id, email, terminos)
        } returns true

        // WHEN: Ejecutamos el UseCase
        val result = updateContratoUseCase(id, email, terminos)

        // THEN: El resultado es true y se llamó al repo con los parámetros exactos
        assertTrue(result)
        coVerify(exactly = 1) {
            repository.updateContratoFields(id, email, terminos)
        }
    }

    @Test
    fun `cuando el repositorio falla al actualizar debe retornar false`() = runTest {
        // GIVEN: El repositorio devuelve false por algún error interno
        coEvery {
            repository.updateContratoFields(any(), any(), any())
        } returns false

        // WHEN
        val result = updateContratoUseCase(1, "test@test.com", false)

        // THEN
        assertFalse(result)
        coVerify(exactly = 1) {
            repository.updateContratoFields(1, "test@test.com", false)
        }
    }
}