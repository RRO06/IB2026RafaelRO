package com.iberdrola.practicas2026.RafaelRO.ui.screens.list_facturas

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.iberdrola.practicas2026.RafaelRO.data.remote.AnalyticsManager
import com.iberdrola.practicas2026.RafaelRO.domain.network.BaseResult
import com.iberdrola.practicas2026.RafaelRO.domain.usercase.GetFacturasUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ListadoFacturasViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    // --- MOCKS ---
    private val getFacturasUseCase: GetFacturasUseCase = mockk()
    private val analyticsManager: AnalyticsManager = mockk(relaxed = true) // Usamos relaxed = true para no tener que definir cada logClick

    private lateinit var viewModel: ListadoFacturasViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { getFacturasUseCase() } returns BaseResult.Sucess(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cuando se pulsa en una factura, el estado showDialog cambia a true`() {
        // ACTUALIZADO: Pasamos el mock de analytics
        viewModel = ListadoFacturasViewModel(getFacturasUseCase, analyticsManager)

        viewModel.onFacturaClick()

        assert(viewModel.stateUI.showDialog)
    }

    @Test
    fun `cuando se cierra el dialogo, showDialog vuelve a ser false`() {
        // ACTUALIZADO: Pasamos el mock de analytics
        viewModel = ListadoFacturasViewModel(getFacturasUseCase, analyticsManager)

        viewModel.onFacturaClick()
        viewModel.dismissDialog()

        assert(!viewModel.stateUI.showDialog)
    }
}