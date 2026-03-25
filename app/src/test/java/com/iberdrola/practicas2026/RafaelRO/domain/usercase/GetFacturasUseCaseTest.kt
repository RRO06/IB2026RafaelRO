package com.iberdrola.practicas2026.RafaelRO.domain.usercase

import com.iberdrola.practicas2026.RafaelRO.data.repository.FacturaRepository
import com.iberdrola.practicas2026.RafaelRO.domain.model.Estado
import com.iberdrola.practicas2026.RafaelRO.domain.model.Factura
import com.iberdrola.practicas2026.RafaelRO.domain.model.Tipo
import com.iberdrola.practicas2026.RafaelRO.domain.network.BaseResult
import com.iberdrola.practicas2026.RafaelRO.domain.network.InvokeException
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class GetFacturasUseCaseTest {

    private val repository: FacturaRepository = mockk()
    private lateinit var getFacturasUseCase: GetFacturasUseCase

    @Before
    fun setUp() {
        getFacturasUseCase = GetFacturasUseCase(repository)
    }

    @Test
    fun `cuando el repositorio devuelve facturas, deben retornar ordenadas por fecha descendente`() = runTest {
        // GIVEN: Facturas desordenadas
        val facturaAntigua = Factura(1, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 31), Tipo.Luz, Estado.Pagado, 50.0)
        val facturaNueva = Factura(2, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31), Tipo.Luz, Estado.Pagado, 60.0)

        coEvery { repository.getFacturas() } returns BaseResult.Sucess(listOf(facturaAntigua, facturaNueva))

        // WHEN: Invocamos el UseCase sin filtro de tipo
        val result = getFacturasUseCase()

        // THEN: La primera factura debe ser la más reciente (ID 2)
        assertTrue(result is BaseResult.Sucess)
        val data = (result as BaseResult.Sucess).data
        assertEquals(2, data[0].id)
        assertEquals(1, data[1].id)
    }

    @Test
    fun `cuando se filtra por tipo Luz, solo deben retornar facturas de luz`() = runTest {
        // GIVEN: Mix de facturas
        val facturaLuz = Factura(1, LocalDate.now(), LocalDate.now(), Tipo.Luz, Estado.Pagado, 50.0)
        val facturaGas = Factura(2, LocalDate.now(), LocalDate.now(), Tipo.Gas, Estado.Pagado, 80.0)

        coEvery { repository.getFacturas() } returns BaseResult.Sucess(listOf(facturaLuz, facturaGas))

        // WHEN: Filtramos por Luz
        val result = getFacturasUseCase(tipo = Tipo.Luz)

        // THEN: Solo hay una factura y es de Luz
        val data = (result as BaseResult.Sucess).data
        assertEquals(1, data.size)
        assertEquals(Tipo.Luz, data[0].tipo)
    }

    @Test
    fun `cuando el repositorio da error, el UseCase debe retornar ese mismo error`() = runTest {
        // GIVEN: Un error en el repo
        val excepcion = InvokeException.NetworkError
        coEvery { repository.getFacturas() } returns BaseResult.Error(excepcion)

        // WHEN
        val result = getFacturasUseCase()

        // THEN
        assertTrue(result is BaseResult.Error)
        assertEquals(excepcion, (result as BaseResult.Error).exception)
    }
}