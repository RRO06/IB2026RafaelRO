package com.iberdrola.practicas2026.RafaelRO.ui.screens.gestion

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.IB2026RafaelROTheme

data class ActivarFacturaActions(
    val onEmailChanged: (String) -> Unit = {},
    val onTermsAccepted: (Boolean) -> Unit = {},
    val obfuscateEmail: (String?) -> String = {""},
    val guardarCambios: (() -> Unit) -> Unit = {},
    val onBack: () -> Unit = {},
    val onNext: (Int) -> Unit = {},
    val onClose: () -> Unit = {}
)

@Composable
fun ActivarFacturaScreen(
    viewModel: GestionViewModel,
    onBack: () -> Unit,
    onClose : () -> Unit,
    onNext: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val state = viewModel.state

    // Agrupamos las acciones
    val actions =
        ActivarFacturaActions(
            onEmailChanged = viewModel::onEmailChanged,
            onTermsAccepted = viewModel::onTermsAccepted,
            obfuscateEmail = viewModel::obfuscateEmail,
            guardarCambios = viewModel::guardarCambiosSinCodigo,
            onBack = onBack,
            onNext = onNext,
            onClose = onClose
        )


    ActivarFacturaContent(
        state = state,
        actions = actions,
        modifier = modifier
    )
}

@Composable
fun ActivarFacturaContent(
    state: GestionUiState,
    actions: ActivarFacturaActions,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // Botón cerrar X
        IconButton(
            onClick = actions.onClose,
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color(0xFF006644))
        }

        Text(
            text = "Activa tu factura electrónica",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
            color = Color.Black
        )

        LinearProgressIndicator(
            progress = { 0.5f },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
                .height(4.dp),
            color = Color(0xFF006644),
            trackColor = Color.LightGray.copy(alpha = 0.3f)
        )

        // Email vinculado (Ofuscado)
        Text(
            text = "Email vinculado a tu cuenta:",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        Text(
            text = actions.obfuscateEmail(state.contrato?.email), // USAMOS LA FUNCIÓN AQUÍ
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "¿En qué email deseas recibir tus facturas?",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        // El TextField ya viene con el email del contrato gracias al ViewModel
        TextField(
            value = state.emailFormulario,
            onValueChange = actions.onEmailChanged,
            label = { Text("* Email") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color(0xFF006644),
                focusedLabelColor = Color.Gray
            ),
            isError = state.emailFormulario.isNotEmpty() && !state.isEmailValido,
            singleLine = true
        )

        if (state.emailFormulario.isNotEmpty() && !state.isEmailValido) {
            Text(
                "El formato del email no es válido",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Text(
                text = "Información básica sobre protección de datos",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            InfoRow(label = "Responsable: Iberdrola Clientes S.A.U.")

            InfoRow(label = "Finalidad: Gestión de la factura electrónica.")

            Text(
                text = "Derechos: Acceso, rectificación, supresión, limitación del tratamiento, portabilidad de datos u oposición, incluida la oposición a decisiones individuales automatizadas. Más info",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.Top) {
            Checkbox(
                checked = state.terminosAceptados,
                onCheckedChange = actions.onTermsAccepted,
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF006644))
            )
            Text(
                text = "He leído y acepto la Política de privacidad, acepto las Condiciones Generales y Particulares...",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Botones inferiores
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
                     actions.onNext(state.contrato!!.id)
                },
                enabled = state.terminosAceptados && state.isEmailValido,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                shape = RoundedCornerShape(27.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF006644),
                    disabledContainerColor = Color(0xFFE8F3EF)
                )
            ) {
                Text(
                    "Siguiente",
                    fontWeight = FontWeight.Bold,
                    color = if (state.terminosAceptados && state.isEmailValido) Color.White else Color.Gray
                )
            }
        }
    }
}

@Composable
fun InfoRow(label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        TextButton(
            onClick = { /* Sin funcionalidad */ },
            contentPadding = PaddingValues(start = 4.dp, end = 4.dp, top = 0.dp, bottom = 0.dp),
            modifier = Modifier.height(24.dp) // Altura reducida para que no separe las líneas
        ) {
            Text(
                text = "Más info",
                style = MaterialTheme.typography.bodySmall.copy(
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFF006644) // El verde de tu app
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun ActivarFacturaPreview(){
    IB2026RafaelROTheme {
        ActivarFacturaContent(
            state = GestionUiState(),
            actions = ActivarFacturaActions()
        )
    }
}