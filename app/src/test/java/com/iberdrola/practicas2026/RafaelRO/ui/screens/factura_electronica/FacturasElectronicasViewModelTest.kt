package com.iberdrola.practicas2026.RafaelRO.ui.screens.factura_electronica

import com.iberdrola.practicas2026.RafaelRO.data.remote.RemoteConfigManager
import com.iberdrola.practicas2026.RafaelRO.domain.model.Tipo
import com.iberdrola.practicas2026.RafaelRO.domain.network.BaseResult
import com.iberdrola.practicas2026.RafaelRO.domain.usercase.GetContratosUseCase
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FacturasElectronicasViewModelTest {

    private val getContratosUseCase = mockk<GetContratosUseCase>()
    private val remoteConfig = mockk<RemoteConfigManager>(relaxed = true)

    private lateinit var viewModel: FacturasElectronicasViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // 1. IMPORTANTE: Definimos qué hace el UseCase por defecto
        // para que el init del ViewModel no explote
        every { getContratosUseCase() } returns flowOf(BaseResult.Sucess(emptyList()))

        // 2. Configuramos el RemoteConfig para que ejecute la lambda que recibe
        every { remoteConfig.fetchConfig(any()) } answers {
            val callback = it.invocation.args[0] as () -> Unit
            callback()
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cuando refreshData se activa el estado isRefreshing debe pasar a true y luego a false`() = runTest {
        // 1. Simulamos un UseCase que tarda un poco (500ms) en responder
        every { getContratosUseCase() } returns flow {
            delay(500)
            emit(BaseResult.Sucess(emptyList()))
        }

        viewModel = FacturasElectronicasViewModel(getContratosUseCase, remoteConfig)

        // 2. Ejecutamos el refresh
        viewModel.refreshData()

        // 3. Movemos el reloj del test 100ms.
        // En este punto, el estado DEBE ser true porque el delay de 500ms no ha terminado.
        advanceTimeBy(100)
        assertEquals(true, viewModel.state.isRefreshing)

        // 4. Avanzamos hasta que todas las corrutinas terminen
        advanceUntilIdle()

        // Ahora el estado debe haber vuelto a false
        assertEquals(false, viewModel.state.isRefreshing)
    }

    @Test
    fun `cuando el tipo es Luz y RemoteConfig dice que esta desactivado, isContratoBloqueado devuelve true`() {
        // GIVEN
        every { remoteConfig.isContratosLuzEnabled() } returns false
        viewModel = FacturasElectronicasViewModel(getContratosUseCase, remoteConfig)

        // WHEN
        val estaBloqueado = viewModel.isContratoBloqueado(Tipo.Luz)

        // THEN
        assert(estaBloqueado)
    }
}