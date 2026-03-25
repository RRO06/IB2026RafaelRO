package com.iberdrola.practicas2026.RafaelRO.ui.screens.home_facturas

import com.iberdrola.practicas2026.RafaelRO.data.local.datastore.OpinionPreferences
import com.iberdrola.practicas2026.RafaelRO.data.local.datastore.SettingsDataStore
import com.iberdrola.practicas2026.RafaelRO.data.remote.AnalyticsManager
import com.iberdrola.practicas2026.RafaelRO.domain.model.UserProfile
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
class HomeViewModelTest {

    private val settingsDataStore = mockk<SettingsDataStore>(relaxed = true)
    private val analyticsManager = mockk<AnalyticsManager>(relaxed = true)

    private lateinit var viewModel: HomeViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Flow perfil
        every { settingsDataStore.userPerfilFlow } returns flowOf(
            UserProfile(
                nombre = "Rafa",
                ultimaConexion = 1711274400000L,
                foto = "",
                id = "",
                email = "",
                telefono = ""
            )
        )

        // Flow modo nube
        every { settingsDataStore.modoNubeFlow } returns flowOf(false)

        // Flow opinión
        every { settingsDataStore.opinionStateFlow } returns flowOf(
            OpinionPreferences(contador = 0, objetivo = 5, yaOpino = false)
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
    fun `cuando el perfil carga la fecha debe formatearse correctamente`() = runTest {
        // Eliminamos el runTest interno.
        // Usamos 'this' para referirnos al TestScope actual si es necesario.
        viewModel = createViewModel(this)

        advanceUntilIdle()

        assertEquals("24/03/2024", viewModel.stateUI.ultimaConexion)
        assertEquals("Rafa", viewModel.stateUI.nombreUsuario)
    }

    // ============================
    // BOTTOM SHEET
    // ============================
    @Test
    fun `cuando el contador supera el objetivo se muestra el BottomSheet`() = runTest {
        every { settingsDataStore.opinionStateFlow } returns flowOf(
            OpinionPreferences(contador = 5, objetivo = 5, yaOpino = false)
        )

        viewModel = createViewModel(this)

        assertTrue(viewModel.stateUI.showBottomSheet)
    }

    // ============================
    // RECORDAR MAS TARDE
    // ============================
    @Test
    fun `cuando pulsa recordar mas tarde se resetea con objetivo 3`() = runTest {
        viewModel = createViewModel(this)

        viewModel.onRecordarMasTarde()
        advanceUntilIdle()

        coVerify(exactly = 1) {
            settingsDataStore.resetEstadoOpinion(nuevoObjetivo = 3)
        }

        assertFalse(viewModel.stateUI.showBottomSheet)
    }

    // ============================
    // MODO NUBE
    // ============================
    @Test
    fun `cuando cambia modo nube se guarda en datastore y se loggea`() = runTest {
        viewModel = createViewModel(this)

        viewModel.onModoNubeChanged(true)
        advanceUntilIdle()

        coVerify {
            settingsDataStore.setModoNube(true)
        }

        verify {
            analyticsManager.logClick(
                "cambio_modo_datos",
                "home",
                match { it["modo"] == "nube" }
            )
        }
    }

    // ============================
    // ANALYTICS
    // ============================
    @Test
    fun `registrarClickFacturas llama a analytics`() {
        runTest { viewModel = createViewModel(this) }

        viewModel.registrarClickFacturas()

        verify {
            analyticsManager.logClick("ver_mis_facturas", "home")
        }
    }

    @Test
    fun `navegar a factura electronica llama a analytics`() {
        runTest {
            viewModel = createViewModel(this)
        }

        viewModel.onNavigateToFacturaElectronica()

        verify {
            analyticsManager.logClick("acceso_factura_electronica", "home")
        }
    }

    // ============================
    // HELPER
    // ============================
    private fun createViewModel(scope: TestScope): HomeViewModel {
        val vm = HomeViewModel(
            analyticsManager,
            settingsDataStore
        )
        scope.advanceUntilIdle()
        return vm
    }
}