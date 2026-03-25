package com.iberdrola.practicas2026.RafaelRO.ui.screens.profile

import android.content.Context
import android.net.Uri

import com.iberdrola.practicas2026.RafaelRO.data.local.datastore.SettingsDataStore
import com.iberdrola.practicas2026.RafaelRO.domain.model.UserProfile
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.UtilyClass
import com.iberdrola.practicas2026.RafaelRO.ui.screens.Perfil.PerfilViewModel

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
class PerfilViewModelTest {

    private val settingsDataStore = mockk<SettingsDataStore>(relaxed = true)
    private val context = mockk<Context>(relaxed = true)

    private lateinit var viewModel: PerfilViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Mock del companion object
        mockkObject(UtilyClass.Companion)

        // Flow inicial COMPLETO
        every { settingsDataStore.userPerfilFlow } returns flowOf(
            UserProfile(
                nombre = "Rafa",
                id = "ID-123",
                email = "rafa@test.com",
                telefono = "600123456",
                foto = ""
            )
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkObject(UtilyClass.Companion)
    }

    // ===============================
    // INIT
    // ===============================
    @Test
    fun `init carga correctamente los datos del DataStore`() = runTest {
        viewModel = PerfilViewModel(settingsDataStore, context)

        advanceUntilIdle()

        assertEquals("Rafa", viewModel.stateUI.nombreUsuario)
        assertEquals("ID-123", viewModel.stateUI.idUsuario)
        assertEquals("rafa@test.com", viewModel.stateUI.email)
        assertEquals("600123456", viewModel.stateUI.telefono)
    }

    // ===============================
    // EMAIL
    // ===============================
    @Test
    fun `cuando el email es invalido el formulario no es valido`() = runTest {
        every { UtilyClass.isValidEmail(any()) } returns false

        viewModel = PerfilViewModel(settingsDataStore, context)
        advanceUntilIdle()

        viewModel.onEmailChanged("correo-mal")

        assertNotNull(viewModel.stateUI.emailError)
        assertFalse(viewModel.isFormValid)
    }

    @Test
    fun `cuando el email es valido no hay error`() = runTest {
        every { UtilyClass.isValidEmail(any()) } returns true

        viewModel = PerfilViewModel(settingsDataStore, context)
        advanceUntilIdle()

        viewModel.onEmailChanged("test@test.com")

        assertNull(viewModel.stateUI.emailError)
    }

    // ===============================
    // TELEFONO
    // ===============================
    @Test
    fun `telefono invalido muestra error`() = runTest {
        every { UtilyClass.isValidSpanishPhone(any()) } returns false

        viewModel = PerfilViewModel(settingsDataStore, context)
        advanceUntilIdle()

        viewModel.onTelefonoChanged("123")

        assertNotNull(viewModel.stateUI.telefonoError)
        assertFalse(viewModel.isFormValid)
    }

    @Test
    fun `telefono valido no muestra error`() = runTest {
        every { UtilyClass.isValidSpanishPhone(any()) } returns true

        viewModel = PerfilViewModel(settingsDataStore, context)
        advanceUntilIdle()

        viewModel.onTelefonoChanged("600123456")

        assertNull(viewModel.stateUI.telefonoError)
    }

    // ===============================
    // FOTO
    // ===============================
    @Test
    fun `cuando seleccionamos una foto se guarda en cache`() = runTest {
        viewModel = PerfilViewModel(settingsDataStore, context)
        val uriFake = mockk<Uri>()

        every { UtilyClass.saveImageToCache(context, uriFake) } returns "cache/image.jpg"

        viewModel.onFotoChanged(uriFake)

        assertEquals("cache/image.jpg", viewModel.stateUI.fotoUri)

        verify(exactly = 1) {
            UtilyClass.saveImageToCache(context, uriFake)
        }
    }

    @Test
    fun `si la uri es null no hace nada`() = runTest {
        viewModel = PerfilViewModel(settingsDataStore, context)

        viewModel.onFotoChanged(null)

        verify(exactly = 0) {
            UtilyClass.saveImageToCache(any(), any())
        }
    }

    // ===============================
    // SAVE CHANGES
    // ===============================
    @Test
    fun `saveChanges con formulario valido guarda datos y llama success`() = runTest {
        every { UtilyClass.isValidEmail(any()) } returns true
        every { UtilyClass.isValidSpanishPhone(any()) } returns true

        viewModel = PerfilViewModel(settingsDataStore, context)
        advanceUntilIdle()

        viewModel.onNombreChanged("Rafael")
        viewModel.onEmailChanged("rafa@test.com")
        viewModel.onTelefonoChanged("600123456")

        runCurrent()

        var successCalled = false

        viewModel.saveChanges {
            successCalled = true
        }

        // Ejecuta la coroutine
        runCurrent()

        // Comprueba loading
        assertTrue(viewModel.stateUI.isLoading)

        // Avanza el delay
        advanceTimeBy(2001)
        runCurrent()

        coVerify(exactly = 1) {
            settingsDataStore.guardarDatosPerfil(
                nombre = "Rafael",
                id = "ID-123",
                email = "rafa@test.com",
                telefono = "600123456",
                foto = ""
            )
        }

        assertTrue(successCalled)
    }

    @Test
    fun `saveChanges con formulario invalido no hace nada`() = runTest {
        every { UtilyClass.isValidEmail(any()) } returns false

        viewModel = PerfilViewModel(settingsDataStore, context)
        advanceUntilIdle()

        viewModel.onEmailChanged("mal")

        viewModel.saveChanges {}

        runCurrent()

        coVerify(exactly = 0) {
            settingsDataStore.guardarDatosPerfil(any(), any(), any(), any(), any())
        }
    }
}