package com.iberdrola.practicas2026.RafaelRO.ui.screens.gestion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.RafaelRO.domain.network.BaseResult
import com.iberdrola.practicas2026.RafaelRO.domain.usercase.GetContratosUseCase
import com.iberdrola.practicas2026.RafaelRO.domain.usercase.UpdateContratoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GestionViewModel @Inject constructor(
    private val getContratosUseCase: GetContratosUseCase,
    private val updateContratoUseCase: UpdateContratoUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    var state by mutableStateOf(GestionUiState())
        private set
    private val contratoId: Int? = savedStateHandle["contratoId"]
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
    private val CODIGO_CORRECTO = "123456"

    init {
        cargarDatos()
    }

    private fun cargarDatos() {
        val id = contratoId ?: return
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            getContratosUseCase().collect { result ->
                when (result ) {
                    is BaseResult.Sucess -> {
                        val encontrado = result.data.find { it.id == id }
                        state = state.copy(
                            contrato = encontrado,
                            emailFormulario = encontrado?.email ?: "",
                            isEmailValido = isEmailValid(encontrado?.email ?: ""),
                            isLoading = false
                        )
                    }

                    is BaseResult.Error -> {
                        state = state.copy(
                            error = "Error al cargar contrato",
                            isLoading = false
                        )
                    }
                }
            }
        }
    }
    fun obfuscatePhone(phone: String?): String {
        if (phone.isNullOrBlank()) return "desconocido"
        // Limpiamos espacios por si el string viene con formato
        val cleanPhone = phone.replace(" ", "")

        return if (cleanPhone.length > 3) {
            "******${cleanPhone.takeLast(3)}"
        } else {
            "***$cleanPhone"
        }
    }
    fun esFlujoActivacion(): Boolean {
        return state.contrato?.estado == false
    }
    fun desactivarFacturaElectronica(onSuccess: () -> Unit) {
        val id = contratoId ?: return
        viewModelScope.launch {
            state = state.copy(isVerifying = true)

            val success = updateContratoUseCase(id, state.emailFormulario, false)

            if (success) {
                state = state.copy(isVerifying = false)
                onSuccess() // Esto debería navegar de vuelta al Home o a una pantalla de éxito
            } else {
                state = state.copy(isVerifying = false, error = "No se pudo desactivar")
            }
        }
    }

    fun verificarCodigo(codigo: String): Boolean {
        return codigo.length == 6
    }

    fun guardarCambiosSinCodigo(onSuccess: () -> Unit) {
        ejecutarProcesoGuardado(validarCodigo = false, onSuccess = onSuccess)
    }

    fun guardarCambiosConCodigo(onSuccess: () -> Unit) {
        ejecutarProcesoGuardado(validarCodigo = true, onSuccess = onSuccess)
    }

    private fun ejecutarProcesoGuardado(validarCodigo: Boolean, onSuccess: () -> Unit) {
        val id = contratoId ?: return

        viewModelScope.launch {
            state = state.copy(isVerifying = true, errorCodigo = false)

            val codigoCorrecto = state.codigoVerificacion == CODIGO_CORRECTO

            // CORRECCIÓN: Solo validamos el código si 'validarCodigo' es true.
            // Quitamos el chequeo del estado del contrato aquí porque genera ambigüedad.
            val puedeProceder = if (validarCodigo) codigoCorrecto else true

            if (puedeProceder) {
                delay(1500)
                // Aquí enviamos 'true' para asegurar que la factura se activa al guardar
                val success = updateContratoUseCase(id, state.emailFormulario, true)

                if (success) {
                    state = state.copy(isVerifying = false)
                    onSuccess()
                } else {
                    state = state.copy(isVerifying = false, error = "Error de base de datos")
                }
            } else {
                // Ahora sí entrará aquí si validarCodigo es true y el código es erróneo
                state = state.copy(isVerifying = false, errorCodigo = true)
                delay(4000)
                state = state.copy(errorCodigo = false)

            }
        }
    }

    fun onEmailChanged(email: String) {
        state = state.copy(
            emailFormulario = email,
            isEmailValido = isEmailValid(email)
        )
    }

    private fun isEmailValid(email: String): Boolean {
        return email.matches(emailPattern)
    }

    fun onCodigoChanged(codigo: String) {
        if (codigo.length <= 6) {
            state = state.copy(codigoVerificacion = codigo, errorCodigo = false)
        }
    }

    fun onTermsAccepted(aceptado: Boolean) {
        state = state.copy(terminosAceptados = aceptado)
    }

    fun obfuscateEmail(email: String?): String {
        if (email.isNullOrBlank() || !email.contains("@")) return ""
        val parts = email.split("@")
        val name = parts[0]
        val domain = parts[1]

        return if (name.length > 1) {
            "${name.first()}*****${name.last()}@$domain"
        } else {
            "*@$domain"
        }
    }

    fun reenviarCodigo() {
        viewModelScope.launch {
            // 1. Mostramos el overlay de carga
            state = state.copy(isVerifying = true, mostrarBannerExito = false)

            // 2. Retardo para simular el envío
            delay(1500)

            // 3. Quitamos carga y mostramos banner verde
            state = state.copy(isVerifying = false, mostrarBannerExito = true)

            // 4. (Opcional) El banner desaparece solo tras 4 segundos
            delay(4000)
            state = state.copy(mostrarBannerExito = false)
        }
    }
    fun dismissBanner() {
        state = state.copy(mostrarBannerExito = false, mostrarBannerError = false)
    }
}


