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
    @ApplicationContext private val context: Context
) : ViewModel() {

    var stateUI by mutableStateOf(PerfilUiState())
        private set

    val isFormValid: Boolean
        get() = stateUI.nombreUsuario.isNotBlank() &&
                stateUI.email.isNotEmpty() &&
                stateUI.telefono.isNotEmpty() &&
                stateUI.emailError == null &&
                stateUI.telefonoError == null

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
        stateUI = stateUI.copy(nombreUsuario = v)
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
        val error = when {
            v.isEmpty() -> "El teléfono es obligatorio"
            !UtilyClass.isValidSpanishPhone(v) -> "Número inválido (9 dígitos)"
            else -> null
        }
        stateUI = stateUI.copy(telefono = v, telefonoError = error)
    }

    fun onFotoChanged(uri: Uri?) {
        if (uri != null) {
            // Guardamos en la carpeta temporal de caché del sistema
            val cachePath = UtilyClass.saveImageToCache(context, uri)

            if (cachePath != null) {
                stateUI = stateUI.copy(fotoUri = cachePath)
            }
        }
    }

    fun saveChanges(onSuccess: () -> Unit) {
        if (!isFormValid) return

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
}