package com.iberdrola.practicas2026.RafaelRO.ui.screens.gestion

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class VerificacionActions(
    val onCodigoChanged: (String) -> Unit = {},
    val onVolverAEnviar: () -> Unit = {},
    val onBack: () -> Unit = {},
    val onNext: (String) -> Unit = {},
    val onDismissBanner: () -> Unit = {},
    val onGuardarCambios: (() -> Unit) -> Unit = {},
    val verificarCodigo: (String) -> Boolean = {true},
    val obfuscatePhone : (String) -> String = { "" },
    val onClose : () -> Unit = {}
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

    VerificacionCodigoContent(
        state = state,
        actions = actions,
        modifier = modifier
    )
}

@Composable
fun VerificacionCodigoContent(
    state: GestionUiState,
    actions: VerificacionActions,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // Header
            IconButton(onClick = actions.onClose, modifier = Modifier.align(Alignment.End)) {
                Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFF006644))
            }

            Text(
                text = "Activa tu factura electrónica",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold)
            )

            LinearProgressIndicator(
                progress = { 0.75f }, // Avanzamos el progreso
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
                    .height(4.dp),
                color = Color(0xFF006644),
                trackColor = Color.LightGray.copy(alpha = 0.3f)
            )

            Text(
                text = "Introduce tu código de verificación",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Para verificar tu identidad, hemos enviado un código al teléfono ${actions.obfuscatePhone(state.contrato?.telefono ?: "")}. Por favor, introdúcelo a continuación:",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            // Input de Código
            TextField(
                value = state.codigoVerificacion,
                onValueChange = actions.onCodigoChanged,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("* Código de verificación") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color(0xFF006644)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE3F2FD), RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Row {
                    Icon(Icons.Outlined.Info, contentDescription = null, tint = Color.Gray)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            "¿No has recibido el código?",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "Si no lo encuentras, podemos volver a enviar el SMS. Recuerda que hoy te quedan 2 intentos.",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Volver a enviar",
                            color = Color(0xFF006644),
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier
                                .clickable { actions.onVolverAEnviar() }
                                .padding(top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botones Inferiores
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = actions.onBack,
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp),
                    border = BorderStroke(1.5.dp, Color(0xFF006644)),
                    shape = RoundedCornerShape(27.dp)
                ) {
                    Text("Anterior", color = Color(0xFF006644), fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        actions.onGuardarCambios(
                            { actions.onNext(state.contrato!!.email) }
                        )
                    },
                    enabled = actions.verificarCodigo(state.codigoVerificacion), // Ejemplo: habilitar con 6 dígitos
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp),
                    shape = RoundedCornerShape(27.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006644))
                ) {
                    Text("Siguiente", fontWeight = FontWeight.Bold)
                }
            }
        }
        if (state.errorCodigo) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 100.dp)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .background(Color(0xFFF8D7DA), RoundedCornerShape(8.dp)) // Rojo
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Código incorrecto",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Red
                    )
                }
            }
        }

        // CAPA 1: Banner de Éxito (Imagen 3)
        if (state.mostrarBannerExito) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 100.dp) // Ajustar según altura de botones
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .background(Color(0xFFC8E6C9), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Hemos vuelto a enviar un SMS con tu código de verificación.",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodySmall
                    )
                    IconButton(onClick = actions.onDismissBanner, modifier = Modifier.size(24.dp)) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // CAPA 2: Overlay de Carga (Imagen 2)
        if (state.isVerifying) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color(0xFF006644),
                    strokeWidth = 6.dp,
                    modifier = Modifier.size(80.dp)
                )
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun VerificacionCodigoContentPreview(){
    VerificacionCodigoContent(
        state = GestionUiState(),
        actions = VerificacionActions()
    )
}
