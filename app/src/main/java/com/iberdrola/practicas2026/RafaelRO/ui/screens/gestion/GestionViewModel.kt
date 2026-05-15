package com.iberdrola.practicas2026.RafaelRO.ui.screens.gestion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.RafaelRO.data.remote.AnalyticsManager
import com.iberdrola.practicas2026.RafaelRO.domain.model.Contrato
import com.iberdrola.practicas2026.RafaelRO.domain.network.BaseResult
import com.iberdrola.practicas2026.RafaelRO.domain.usercase.GetContratosUseCase
import com.iberdrola.practicas2026.RafaelRO.domain.usercase.UpdateContratoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.text.toRegex

@HiltViewModel
class GestionViewModel @Inject constructor(
    private val getContratosUseCase: GetContratosUseCase,
    private val updateContratoUseCase: UpdateContratoUseCase,
    private val analyticsManager: AnalyticsManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    var state by mutableStateOf(GestionUiState())
        private set
    private val contratoId: Int? = savedStateHandle["contratoId"]
    private val emailPattern = "^[a-zA-Z0-9]+([._%+-][a-zA-Z0-9]+)*@[a-zA-Z0-9]+([-.][a-zA-Z0-9]+)*\\.[a-zA-Z]{2,}$".toRegex()
    private var isFirstLoad = true

    init {
        cargarDatos()
    }

    private fun cargarDatos() {
        val id = contratoId ?: return
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            getContratosUseCase().collect { result ->
                handleCargarDatosResult(result, id)
            }
        }
    }

    private fun handleCargarDatosResult(result: BaseResult<List<Contrato>>, id: Int) {
        when (result) {
            is BaseResult.Sucess -> {
                val encontrado = result.data.find { it.id == id }
                handleCargarDatosExito(encontrado)
            }
            is BaseResult.Error -> {
                state = state.copy(error = "Error al cargar contrato", isLoading = false)
            }
        }
    }

    private fun handleCargarDatosExito(encontrado: Contrato?) {
        val isActivacion = encontrado?.estado == false

        val nextEmailFormulario = if (isFirstLoad) "" else state.emailFormulario
        val nextEmailValido = if (isFirstLoad) {
            if (isActivacion) false else (encontrado?.email?.matches(emailPattern) ?: false)
        } else {
            state.isEmailValido
        }

        val emailOriginal = if (isFirstLoad) encontrado?.email ?: "" else state.emailOriginal

        state = state.copy(
            contrato = encontrado,
            esFlujoActivacion = isActivacion,
            emailFormulario = nextEmailFormulario,
            emailOriginal = emailOriginal,
            isEmailValido = nextEmailValido,
            isLoading = false
        )

        if (isFirstLoad) {
            generarNuevoCodigo()
            isFirstLoad = false
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
        logClick("confirmar_desactivacion", "detalle_factura_activa")
        viewModelScope.launch {
            state = state.copy(isVerifying = true)
            val email = state.emailFormulario.ifBlank { state.contrato?.email ?: "" }
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
        logClick("boton_verificar_codigo", "verificacion_codigo")
        ejecutarProcesoGuardado(validarCodigo = true, onSuccess = onSuccess)
    }

    fun guardarCambiosSinCodigo(onSuccess: () -> Unit) {
        logClick("boton_continuar_email", "modificar_email")
        if (state.emailFormulario == state.emailOriginal) {
            state = state.copy(mostrarDialogoEmailIdentico = true)
            return
        }
        ejecutarProcesoGuardado(validarCodigo = false, onSuccess = onSuccess)
    }

    private fun ejecutarProcesoGuardado(validarCodigo: Boolean, onSuccess: () -> Unit) {
        val id = contratoId ?: return

        viewModelScope.launch {
            state = state.copy(isVerifying = true, errorCodigo = false, mostrarBannerExito = false)
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
            }
        }
    }

    fun onEmailChanged(email: String) {
        state = state.copy(emailFormulario = email, isEmailValido = email.matches(emailPattern))
    }

    fun onCodigoChanged(codigo: String) {
        if (codigo.length <= 6) state = state.copy(codigoVerificacion = codigo)
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
        logClick("boton_reenviar_codigo", "verificacion_codigo")
        viewModelScope.launch {
            state = state.copy(
                isVerifying = true,
                mostrarBannerExito = false,
                errorCodigo = false,
                ultimoCodigoEnviado = null
            )
            delay(1500)
            val nuevosIntentos = state.intentosRestantes - 1
            val nuevoEstado = when{
                nuevosIntentos == 1 -> ReenvioEstado.ConIntento(nuevosIntentos)
                nuevosIntentos > 1 -> ReenvioEstado.ConIntentos(nuevosIntentos)
                else -> ReenvioEstado.Agotado
            }
            val nuevoCodigo = (100000..999999).random().toString()
            state = state.copy(
                isVerifying = false,
                mostrarBannerExito = true,
                intentosRestantes = nuevosIntentos,
                reenvioEstado = nuevoEstado,
                codigoGenerado = nuevoCodigo,
                ultimoCodigoEnviado = nuevoCodigo
            )

        }
    }

    fun logClick(nombreBoton: String, nombrePantalla: String, parametrosExtra: Map<String, String> = emptyMap()) {
        analyticsManager.registrarClic(nombreBoton, nombrePantalla, parametrosExtra)
    }

    fun dismissBanner() {
        state = state.copy(
            mostrarBannerExito = false,
            mostrarBannerError = false,
            errorCodigo = false
        )
    }

    fun dismissDialogoEmailIdentico() {
        state = state.copy(mostrarDialogoEmailIdentico = false)
    }

    fun verificarCodigo(codigo: String): Boolean = codigo.length == 6
}
