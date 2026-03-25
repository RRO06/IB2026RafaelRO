package com.iberdrola.practicas2026.RafaelRO.ui.screens.filt_facturas

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class FilterViewModelTest {

    private lateinit var viewModel: FilterViewModel

    @Before
    fun setup() {
        // Inicializamos el ViewModel antes de cada test
        viewModel = FilterViewModel()
    }

    @Test
    fun `cuando seleccionamos fecha de inicio posterior a fin, debe mostrar error`() {
        // GIVEN: Una fecha de fin establecida
        val fechaFin = LocalDate.of(2024, 1, 1)
        viewModel.onDateToSelected(fechaFin)

        // WHEN: Seleccionamos una fecha de inicio posterior
        val fechaInicioErronea = LocalDate.of(2024, 2, 1)
        viewModel.onDateFromSelected(fechaInicioErronea)

        // THEN: El estado debe contener un error
        assertNotNull(viewModel.uiState.dateError)
        assertEquals("La fecha de inicio no puede ser posterior", viewModel.uiState.dateError)
    }

    @Test
    fun `cuando seleccionamos fechas correctas, el error debe ser null`() {
        // GIVEN: Fechas coherentes
        val fechaInicio = LocalDate.of(2024, 1, 1)
        val fechaFin = LocalDate.of(2024, 2, 1)

        // WHEN
        viewModel.onDateFromSelected(fechaInicio)
        viewModel.onDateToSelected(fechaFin)

        // THEN
        assertNull(viewModel.uiState.dateError)
        assertEquals(fechaInicio, viewModel.uiState.dateFrom)
        assertEquals(fechaFin, viewModel.uiState.dateTo)
    }

    @Test
    fun `onStateToggle debe añadir un estado si no existe y quitarlo si ya existe`() {
        val estadoTest = "Pagada"

        // WHEN: Añadimos por primera vez
        viewModel.onStateToggle(estadoTest)
        // THEN: Debe estar en la lista
        assertTrue(viewModel.uiState.selectedStates.contains(estadoTest))

        // WHEN: Pulsamos otra vez (toggle)
        viewModel.onStateToggle(estadoTest)
        // THEN: Debe haber desaparecido
        assertTrue(!viewModel.uiState.selectedStates.contains(estadoTest))
    }

    @Test
    fun `onClear debe resetear el estado a los valores por defecto`() {
        // GIVEN: Modificamos el estado original
        viewModel.onDateFromSelected(LocalDate.now())
        viewModel.onStateToggle("Anulada")

        // WHEN: Limpiamos
        viewModel.onClear()

        // THEN: Debe volver a ser nulo o vacío
        assertNull(viewModel.uiState.dateFrom)
        assertTrue(viewModel.uiState.selectedStates.isEmpty())
    }
}