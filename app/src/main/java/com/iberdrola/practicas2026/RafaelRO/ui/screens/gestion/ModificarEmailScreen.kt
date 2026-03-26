package com.iberdrola.practicas2026.RafaelRO.ui.screens.gestion

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp

data class ModificarEmailActions(
    val onEmailChanged: (String) -> Unit = {},
    val onBack: () -> Unit = {},
    val onClose: () -> Unit = {},
    val onSaveAndNext: () -> Unit = {}
)
@Composable
fun ModificarEmailScreen(
    viewModel: GestionViewModel,
    onBack: () -> Unit,
    onClose : () -> Unit,
    onNext: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val state = viewModel.state

    val actions = ModificarEmailActions(
        onEmailChanged = { viewModel.onEmailChanged(it) },
        onBack = onBack,
        onClose = onClose,
        onSaveAndNext = {
            viewModel.guardarCambiosSinCodigo {
                state.contrato?.id?.let { id -> onNext(id) }
            }
        }
    )

    ModificarEmailContent(
        state = state,
        actions = actions,
        modifier = modifier
    )
}

@Composable
fun ModificarEmailContent(
    state: GestionUiState,
    actions: ModificarEmailActions,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // Botón Cerrar
        IconButton(
            onClick = actions.onClose,
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color(0xFF006644))
        }

        Text(
            text = "Modificar email",
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

        Text(
            text = "¿En qué email deseas recibir tus facturas?",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp)
        )

        // Input de Email
        TextField(
            value = state.emailFormulario,
            onValueChange = actions.onEmailChanged,
            label = { Text("* Nuevo email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color(0xFF006644),
                cursorColor = Color(0xFF006644)
            ),
            isError = state.emailFormulario.isNotEmpty() && !state.isEmailValido,
            singleLine = true
        )

        if (state.emailFormulario.isNotEmpty() && !state.isEmailValido) {
            Text(
                text = "El formato del email no es válido",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Botones de navegación inferior
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
                    actions.onSaveAndNext()
                          },
                enabled = state.isEmailValido && state.emailFormulario.isNotEmpty(),
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
                    text = "Siguiente",
                    fontWeight = FontWeight.Bold,
                    color = if(state.isEmailValido) Color.White else Color.Gray
                )
            }
        }
    }
}