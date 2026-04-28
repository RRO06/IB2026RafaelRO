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
    private var isFirstLoad = true

    init {
        cargarDatos()
    }

    private fun cargarDatos() {
        val id = contratoId ?: return
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            getContratosUseCase().collect { result ->
                when (result) {
                    is BaseResult.Sucess -> {
                        val encontrado = result.data.find { it.id == id }
                        val isActivacion = encontrado?.estado == false
                        state = state.copy(
                            contrato = encontrado,
                            esFlujoActivacion = isActivacion,
                            emailFormulario = if (isFirstLoad) {
                                ""
                            } else state.emailFormulario,
                            isEmailValido = if (isFirstLoad) {
                                if (isActivacion) false else (encontrado?.email?.matches(emailPattern) ?: false)
                            } else state.isEmailValido,
                            isLoading = false
                        )
                        if (isFirstLoad) {
                            generarNuevoCodigo()
                            isFirstLoad = false
                        }
                    }
                    is BaseResult.Error -> {
                        state = state.copy(error = "Error al cargar contrato", isLoading = false)
                    }
                }
            }
        }
    }

    private fun generarNuevoCodigo() {
        val nuevoCodigo = (100000..999999).random().toString()
        state = state.copy(codigoGenerado = nuevoCodigo, ultimoCodigoEnviado = nuevoCodigo)
    }

    fun toastMostrado() {
        state = state.copy(ultimoCodigoEnviado = null)
    }

    fun obfuscatePhone(phone: String?): String {
        if (phone.isNullOrBlank()) return "desconocido"
        val cleanPhone = phone.replace(" ", "")
        return if (cleanPhone.length > 3) "******${cleanPhone.takeLast(3)}" else "***$cleanPhone"
    }

    fun esFlujoActivacion(): Boolean = state.esFlujoActivacion

    fun desactivarFacturaElectronica(onSuccess: () -> Unit) {
        val id = contratoId ?: return
        viewModelScope.launch {
            state = state.copy(isVerifying = true)
            val email = if (state.emailFormulario.isBlank()) state.contrato?.email ?: "" else state.emailFormulario
            val success = updateContratoUseCase(id, email, false)
            if (success) {
                state = state.copy(isVerifying = false)
                onSuccess()
            } else {
                state = state.copy(isVerifying = false)
            }
        }
    }

    fun guardarCambiosConCodigo(onSuccess: () -> Unit) {
        ejecutarProcesoGuardado(validarCodigo = true, onSuccess = onSuccess)
    }

    fun guardarCambiosSinCodigo(onSuccess: () -> Unit) {
        onSuccess()
    }

    private fun ejecutarProcesoGuardado(validarCodigo: Boolean, onSuccess: () -> Unit) {
        val id = contratoId ?: return
        viewModelScope.launch {
            state = state.copy(isVerifying = true, errorCodigo = false)
            val codigoCorrecto = state.codigoVerificacion == state.codigoGenerado
            val puedeProceder = if (validarCodigo) codigoCorrecto else true
            if (puedeProceder) {
                delay(1500)
                val emailFinal = state.emailFormulario
                val esActivacion = state.esFlujoActivacion
                val estadoFinal = if (esActivacion) true else (state.contrato?.estado ?: true)
                val success = updateContratoUseCase(id, emailFinal, estadoFinal)
                if (success) {
                    state = state.copy(
                        contrato = state.contrato?.copy(email = emailFinal, estado = estadoFinal),
                        isVerifying = false
                    )
                    onSuccess()
                } else {
                    state = state.copy(isVerifying = false)
                }
            } else {
                state = state.copy(isVerifying = false, errorCodigo = true)
                delay(4000)
                state = state.copy(errorCodigo = false)
            }
        }
    }

    fun onEmailChanged(email: String) {
        state = state.copy(emailFormulario = email, isEmailValido = email.matches(emailPattern))
    }

    fun onCodigoChanged(codigo: String) {
        if (codigo.length <= 6) state = state.copy(codigoVerificacion = codigo, errorCodigo = false)
    }

    fun onTermsAccepted(aceptado: Boolean) {
        state = state.copy(terminosAceptados = aceptado)
    }

    fun obfuscateEmail(email: String?): String {
        if (email.isNullOrBlank() || !email.contains("@")) return ""
        val parts = email.split("@")
        val name = parts[0]
        return if (name.length > 1) "${name.first()}*****${name.last()}@${parts[1]}" else "*@${parts[1]}"
    }

    fun reenviarCodigo() {
        if (state.isVerifying || state.reenvioEstado is ReenvioEstado.Agotado) return
        viewModelScope.launch {
            state = state.copy(isVerifying = true, mostrarBannerExito = false)
            delay(1500)
            val nuevosIntentos = state.intentosRestantes - 1
            val nuevoEstado = if (nuevosIntentos <= 0) ReenvioEstado.Agotado else ReenvioEstado.ConIntentos(nuevosIntentos)
            val nuevoCodigo = (100000..999999).random().toString()
            state = state.copy(
                isVerifying = false,
                mostrarBannerExito = true,
                intentosRestantes = nuevosIntentos,
                reenvioEstado = nuevoEstado,
                codigoGenerado = nuevoCodigo,
                ultimoCodigoEnviado = nuevoCodigo
            )
            delay(4000)
            state = state.copy(mostrarBannerExito = false)
        }
    }

    fun dismissBanner() {
        state = state.copy(mostrarBannerExito = false, mostrarBannerError = false)
    }

    fun verificarCodigo(codigo: String): Boolean = codigo.length == 6
}
