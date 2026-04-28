package com.iberdrola.practicas2026.RafaelRO.ui.screens.gestion

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

data class VerificacionActions(
    val onCodigoChanged: (String) -> Unit = {},
    val onVolverAEnviar: () -> Unit = {},
    val onBack: () -> Unit = {},
    val onNext: (String) -> Unit = {},
    val onDismissBanner: () -> Unit = {},
    val onGuardarCambios: (() -> Unit) -> Unit = {},
    val verificarCodigo: (String) -> Boolean = { true },
    val obfuscatePhone: (String) -> String = { "" },
    val onClose: () -> Unit = {}
)

@Composable
fun VerificacionCodigoScreen(
    viewModel: GestionViewModel,
    onBack: () -> Unit,
    onClose: () -> Unit,
    onNext: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state = viewModel.state
    var showNotification by remember { mutableStateOf(false) }
    var codeToDisplay by remember { mutableStateOf("") }

    LaunchedEffect(state.ultimoCodigoEnviado) {
        state.ultimoCodigoEnviado?.let { codigo ->
            codeToDisplay = codigo
            showNotification = true
            delay(5000)
            showNotification = false
            viewModel.toastMostrado()
        }
    }

    val actions = VerificacionActions(
        onCodigoChanged = viewModel::onCodigoChanged,
        onVolverAEnviar = viewModel::reenviarCodigo,
        onBack = onBack,
        onNext = onNext,
        onDismissBanner = viewModel::dismissBanner,
        onGuardarCambios = viewModel::guardarCambiosConCodigo,
        verificarCodigo = viewModel::verificarCodigo,
        obfuscatePhone = viewModel::obfuscatePhone,
        onClose = onClose
    )

    Box(modifier = modifier.fillMaxSize()) {
        VerificacionCodigoContent(state = state, actions = actions)
        
        SMSNotification(
            visible = showNotification,
            code = codeToDisplay,
            onClose = { showNotification = false }
        )
    }
}

@Composable
fun VerificacionCodigoContent(
    state: GestionUiState,
    actions: VerificacionActions
) {
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(modifier = Modifier.fillMaxSize()) {
            VerificacionHeader(onClose = actions.onClose)

            SolidProgressBar(progressValue = 0.75f)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                VerificacionForm(
                    codigoValue = state.codigoVerificacion,
                    phone = actions.obfuscatePhone(state.contrato?.telefono ?: ""),
                    onCodigoChanged = actions.onCodigoChanged
                )

                Spacer(modifier = Modifier.height(32.dp))

                ReenvioInfoCard(
                    intentos = state.intentosRestantes,
                    onReenviar = actions.onVolverAEnviar
                )
            }

            VerificacionBottomSection(
                codigoValid = state.codigoVerificacion.length == 6,
                mostrarBanner = state.mostrarBannerExito,
                onDismissBanner = actions.onDismissBanner,
                onBack = actions.onBack,
                onNext = { actions.onGuardarCambios { actions.onNext(state.contrato!!.email) } }
            )
        }

        ErrorBanner(visible = state.errorCodigo)

        LoadingOverlay(visible = state.isVerifying)
    }
}

@Composable
private fun VerificacionHeader(onClose: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 16.dp)) {
        IconButton(onClick = onClose, modifier = Modifier.align(Alignment.End)) {
            Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFF006644))
        }
        Text(
            text = "Activa tu factura electrónica",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold)
        )
    }
}

@Composable
private fun SolidProgressBar(progressValue: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .background(Color(0xFFE0E0E0))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progressValue)
                .fillMaxHeight()
                .background(Color(0xFF006644))
        )
    }
}

@Composable
private fun VerificacionForm(
    codigoValue: String,
    phone: String,
    onCodigoChanged: (String) -> Unit
) {
    Text(
        text = "Introduce tu código de verificación",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = "Para verificar tu identidad, hemos enviado un código al teléfono $phone. Por favor, introdúcelo a continuación:",
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(vertical = 12.dp),
        color = Color.Gray
    )
    TextField(
        value = codigoValue,
        onValueChange = onCodigoChanged,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        label = { Text("* Código de verificación", style = MaterialTheme.typography.bodySmall) },
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color(0xFF006644)
        ),
        singleLine = true
    )
}

@Composable
private fun ReenvioInfoCard(intentos: Int, onReenviar: () -> Unit) {
    val agotado = intentos == 0
    val bocadilloShape = RoundedCornerShape(topStart = 0.dp, topEnd = 12.dp, bottomEnd = 12.dp, bottomStart = 12.dp)
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (agotado) Color(0xFFFFE0B2) else Color(0xFFE3F2FD),
                shape = bocadilloShape
            )
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Icon(
                imageVector = if (agotado) Icons.Outlined.Warning else Icons.Outlined.Info,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = if (agotado) "Intentos agotados" else "¿No has recibido el código?",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black
                )
                Text(
                    text = if (agotado) {
                        "Has superado el límite de reenvíos para hoy. Por favor, inténtalo de nuevo más tarde."
                    } else {
                        "Si no lo encuentras, podemos volver a enviar el SMS. Recuerda que hoy te quedan $intentos intentos."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black
                )
                if (!agotado) {
                    Text(
                        text = "Volver a enviar",
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .clickable { onReenviar() },
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
private fun VerificacionBottomSection(
    codigoValid: Boolean,
    mostrarBanner: Boolean,
    onDismissBanner: () -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    Column {
        AnimatedVisibility(
            visible = mostrarBanner,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            SuccessBanner(onDismiss = onDismissBanner)
        }

        AnimatedVisibility(
            visible = !mostrarBanner,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 0.5.dp,
                color = Color.LightGray.copy(alpha = 0.5f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f).height(54.dp),
                border = BorderStroke(1.5.dp, Color(0xFF006644)),
                shape = RoundedCornerShape(27.dp)
            ) {
                Text("Anterior", color = Color(0xFF006644), fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = onNext,
                enabled = codigoValid,
                modifier = Modifier.weight(1f).height(54.dp),
                shape = RoundedCornerShape(27.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF006644),
                    disabledContainerColor = Color(0xFFE8F3EF)
                )
            ) {
                Text("Siguiente", fontWeight = FontWeight.Bold, color = if (codigoValid) Color.White else Color.Gray)
            }
        }
    }
}

@Composable
private fun SuccessBanner(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFC8E6C9))
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF006644),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Hemos vuelto a enviar un SMS con tu código de verificación.",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black
            )
            IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
            }
        }
    }
}

@Composable
private fun SMSNotification(visible: Boolean, code: String, onClose: () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = Modifier.padding(top = 40.dp, start = 16.dp, end = 16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF006644)),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Sms, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Nuevo código de seguridad", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.labelSmall)
                    Text("Tu código es: $code", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                }
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
                }
            }
        }
    }
}

@Composable
private fun ErrorBanner(visible: Boolean) {
    if (visible) {
        Box(
            modifier = Modifier.fillMaxSize().padding(bottom = 100.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8D7DA))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("El código introducido no es correcto", style = MaterialTheme.typography.bodySmall, color = Color.Red)
                }
            }
        }
    }
}

@Composable
private fun LoadingOverlay(visible: Boolean) {
    if (visible) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFF006644), strokeWidth = 6.dp, modifier = Modifier.size(80.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VerificacionCodigoContentPreview() {
    VerificacionCodigoContent(state = GestionUiState(), actions = VerificacionActions())
}
