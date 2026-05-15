package com.iberdrola.practicas2026.RafaelRO.ui.screens.Perfil

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.RafaelRO.data.local.datastore.SettingsDataStore
import com.iberdrola.practicas2026.RafaelRO.data.remote.AnalyticsManager
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.UtilyClass
import com.iberdrola.practicas2026.RafaelRO.ui.screens.profile.PerfilUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PerfilViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore,
    private val analyticsManager: AnalyticsManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    var stateUI by mutableStateOf(PerfilUiState())
        private set

    val isFormValid: Boolean
        get() = stateUI.nombreUsuario.isNotBlank() &&
                UtilyClass.isValidEmail(stateUI.email) &&
                UtilyClass.isValidSpanishPhone(stateUI.telefono) &&
                stateUI.emailError == null &&
                stateUI.telefonoError == null &&
                stateUI.nombreError == null

    init {
        viewModelScope.launch {
            settingsDataStore.userPerfilFlow.collect { perfil ->
                stateUI = stateUI.copy(
                    nombreUsuario = perfil.nombre,
                    idUsuario = perfil.id,
                    email = perfil.email,
                    telefono = perfil.telefono,
                    fotoUri = perfil.foto
                )
            }
        }
    }

    fun onNombreChanged(v: String) {
        stateUI = stateUI.copy(
            nombreUsuario = v,
            nombreError = if (v.isBlank()) "El nombre es obligatorio" else null
        )
    }

    fun onEmailChanged(v: String) {
        val error = when {
            v.isEmpty() -> "El correo es obligatorio"
            !UtilyClass.isValidEmail(v) -> "Formato de correo inválido"
            else -> null
        }
        stateUI = stateUI.copy(email = v, emailError = error)
    }

    fun onTelefonoChanged(v: String) {
        val filtered = v.filter { it.isDigit() }.take(9)
        
        val error = when {
            filtered.isEmpty() -> "El teléfono es obligatorio"
            filtered.isNotEmpty() && filtered[0] !in listOf('6', '7', '9') ->
                "Debe empezar por 6, 7 o 9 (España)"
            filtered.length < 9 -> "Faltan dígitos (9 requeridos)"
            else -> null
        }
        
        stateUI = stateUI.copy(telefono = filtered, telefonoError = error)
    }

    fun onFotoChanged(uri: Uri?) {
        if (uri != null) {
            logClick("cambiar_foto_perfil", "perfil")
            val cachePath = UtilyClass.saveImageToCache(context, uri)

            if (cachePath != null) {
                stateUI = stateUI.copy(fotoUri = cachePath)
            }
        }
    }

    fun saveChanges(onSuccess: () -> Unit) {
        val nombreErr = if (stateUI.nombreUsuario.isBlank()) "El nombre es obligatorio" else null
        
        val emailErr = when {
            stateUI.email.isEmpty() -> "El correo es obligatorio"
            !UtilyClass.isValidEmail(stateUI.email) -> "Formato de correo inválido"
            else -> null
        }
        
        val telefonoErr = when {
            stateUI.telefono.isEmpty() -> "El teléfono es obligatorio"
            stateUI.telefono.isNotEmpty() && stateUI.telefono[0] !in listOf('6', '7', '9') ->
                "Debe empezar por 6, 7 o 9 (España)"
            stateUI.telefono.length < 9 -> "Faltan dígitos (9 requeridos)"
            else -> null
        }

        if (nombreErr != null || emailErr != null || telefonoErr != null) {
            stateUI = stateUI.copy(
                nombreError = nombreErr,
                emailError = emailErr,
                telefonoError = telefonoErr
            )
            return
        }

        logClick("boton_guardar_perfil", "perfil")

        viewModelScope.launch {
            stateUI = stateUI.copy(isLoading = true)
            delay(2000)
            try {
                settingsDataStore.guardarDatosPerfil(
                    nombre = stateUI.nombreUsuario,
                    id = stateUI.idUsuario,
                    email = stateUI.email,
                    telefono = stateUI.telefono,
                    foto = stateUI.fotoUri ?: ""
                )
                onSuccess()
                stateUI = stateUI.copy(isLoading = false)
            } catch (e: Exception) {
                stateUI = stateUI.copy(isLoading = false)
                Log.e("PerfilViewModel", "Error al guardar: ${e.message}")
            }
        }
    }

    fun logClick(nombreBoton: String, nombrePantalla: String, parametrosExtra: Map<String, String> = emptyMap()) {
        analyticsManager.registrarClic(nombreBoton, nombrePantalla, parametrosExtra)
    }
}
