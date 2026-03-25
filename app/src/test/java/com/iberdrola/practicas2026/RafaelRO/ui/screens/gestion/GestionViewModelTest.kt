package com.iberdrola.practicas2026.RafaelRO.ui.screens.gestion

import androidx.lifecycle.SavedStateHandle
import com.iberdrola.practicas2026.RafaelRO.data.remote.AnalyticsManager
import com.iberdrola.practicas2026.RafaelRO.domain.model.Contrato
import com.iberdrola.practicas2026.RafaelRO.domain.model.Tipo
import com.iberdrola.practicas2026.RafaelRO.domain.network.BaseResult
import com.iberdrola.practicas2026.RafaelRO.domain.usercase.GetContratosUseCase
import com.iberdrola.practicas2026.RafaelRO.domain.usercase.UpdateContratoUseCase

import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*

import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GestionViewModelTest {

    private val getContratosUseCase = mockk<GetContratosUseCase>()
    private val updateContratoUseCase = mockk<UpdateContratoUseCase>()
    private val analyticsManager = mockk<AnalyticsManager>(relaxed = true)
    private val savedStateHandle = SavedStateHandle(mapOf("contratoId" to 1))

    private lateinit var viewModel: GestionViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        every { getContratosUseCase.invoke() } returns flowOf(
            BaseResult.Sucess(
                listOf(
                    Contrato(
                        id = 1,
                        email = "test@test.com",
                        estado = true,
                        telefono = "600123456",
                        tipo = Tipo.Luz,
                        direccion = "Dirección de prueba"
                    )
                )
            )
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ============================
    // INIT
    // ============================
    @Test
    fun `init carga contrato correctamente`() = runTest {
        viewModel = createViewModel()

        assertEquals(1, viewModel.state.contrato?.id)
        assertEquals("test@test.com", viewModel.state.emailFormulario)
        assertFalse(viewModel.state.isLoading)
    }

    // ============================
    // EMAIL
    // ============================
    @Test
    fun `email valido actualiza estado correctamente`() = runTest {
        viewModel = createViewModel()

        viewModel.onEmailChanged("correo@test.com")

        assertTrue(viewModel.state.isEmailValido)
    }

    @Test
    fun `email invalido marca error`() = runTest {
        viewModel = createViewModel()

        viewModel.onEmailChanged("mal-email")

        assertFalse(viewModel.state.isEmailValido)
    }

    // ============================
    // CODIGO
    // ============================
    @Test
    fun `verificarCodigo devuelve true si tiene 6 digitos`() {
        viewModel = createViewModel()

        assertTrue(viewModel.verificarCodigo("123456"))
        assertFalse(viewModel.verificarCodigo("123"))
    }

    // ============================
    // GUARDAR SIN CODIGO
    // ============================
    @Test
    fun `guardar sin codigo ejecuta update correctamente`() = runTest {
        coEvery { updateContratoUseCase(any(), any(), any()) } returns true

        viewModel = createViewModel()

        var success = false

        viewModel.guardarCambiosSinCodigo {
            success = true
        }

        runCurrent()
        assertTrue(viewModel.state.isVerifying)

        advanceTimeBy(1501)
        runCurrent()

        coVerify(exactly = 1) {
            updateContratoUseCase(1, any(), true)
        }

        assertTrue(success)
    }

    // ============================
    // GUARDAR CON CODIGO CORRECTO
    // ============================
    @Test
    fun `guardar con codigo correcto funciona`() = runTest {
        coEvery { updateContratoUseCase(any(), any(), any()) } returns true

        viewModel = createViewModel()
        viewModel.onCodigoChanged("123456")

        var success = false

        viewModel.guardarCambiosConCodigo {
            success = true
        }

        runCurrent()
        advanceTimeBy(1501)
        runCurrent()

        assertTrue(success)
    }

    // ============================
    // CODIGO INCORRECTO
    // ============================
    @Test
    fun `codigo incorrecto muestra error`() = runTest {
        viewModel = createViewModel()
        viewModel.onCodigoChanged("000000")

        viewModel.guardarCambiosConCodigo {}

        runCurrent()

        assertTrue(viewModel.state.errorCodigo)

        advanceTimeBy(4001)
        runCurrent()

        assertFalse(viewModel.state.errorCodigo)
    }

    // ============================
    // DESACTIVAR FACTURA
    // ============================
    @Test
    fun `desactivar factura llama usecase`() = runTest {
        coEvery { updateContratoUseCase(any(), any(), any()) } returns true

        viewModel = createViewModel()

        var success = false

        viewModel.desactivarFacturaElectronica {
            success = true
        }

        runCurrent()

        coVerify(exactly = 1) {
            updateContratoUseCase(1, any(), false)
        }

        assertTrue(success)
    }

    // ============================
    // REENVIAR CODIGO
    // ============================
    @Test
    fun `reenviar codigo muestra banner correctamente`() = runTest {
        viewModel = createViewModel()

        viewModel.reenviarCodigo()

        runCurrent()
        assertTrue(viewModel.state.isVerifying)

        advanceTimeBy(1501)
        runCurrent()

        assertTrue(viewModel.state.mostrarBannerExito)

        advanceTimeBy(4001)
        runCurrent()

        assertFalse(viewModel.state.mostrarBannerExito)
    }

    // ============================
    // HELPER
    // ============================
    private fun createViewModel(): GestionViewModel {
        val vm = GestionViewModel(
            getContratosUseCase,
            updateContratoUseCase,
            analyticsManager,
            savedStateHandle
        )
        return vm
    }
}