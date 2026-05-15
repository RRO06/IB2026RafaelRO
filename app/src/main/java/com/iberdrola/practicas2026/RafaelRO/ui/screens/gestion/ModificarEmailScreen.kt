package com.iberdrola.practicas2026.RafaelRO.ui.screens.gestion

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.IberdrolaTextField
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.CustomTypography

data class ModificarEmailActions(
    val onEmailChanged: (String) -> Unit = {},
    val onBack: () -> Unit = {},
    val onClose : () -> Unit = {},
    val onSaveAndNext: () -> Unit = {},
    val onDismissDialog: () -> Unit = {}
)
@Composable
fun ModificarEmailScreen(
    viewModel: GestionViewModel,
    onBack: () -> Unit,
    onClose : () -> Unit,
    onNext: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val state = viewModel.state

    BackHandler {
        if (lifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
            viewModel.logClick("boton_atras_fisico", "modificar_email")
            onBack()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.logClick("visualizacion_pantalla", "modificar_email")
    }

    LaunchedEffect(state.mostrarDialogoEmailIdentico) {
        if (state.mostrarDialogoEmailIdentico) {
            viewModel.logClick("dialogo_email_identico_mostrado", "modificar_email")
        }
    }

    val actions = ModificarEmailActions(
        onEmailChanged = { viewModel.onEmailChanged(it) },
        onBack = {
            if (lifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
                viewModel.logClick("boton_anterior", "modificar_email")
                onBack()
            }
        },
        onClose = {
            if (lifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
                viewModel.logClick("boton_cerrar", "modificar_email")
                onClose()
            }
        },
        onSaveAndNext = {
            if (lifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
                viewModel.guardarCambiosSinCodigo {
                    state.contrato?.id?.let { id -> onNext(id) }
                }
            }
        },
        onDismissDialog = {
            viewModel.logClick("boton_aceptar_email_identico", "modificar_email")
            viewModel.dismissDialogoEmailIdentico()
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
    if (state.mostrarDialogoEmailIdentico) {
        EmailIdenticoDialog(onDismiss = actions.onDismissDialog)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding()
    ) {
        ModificarEmailHeader(onClose = actions.onClose)

        Spacer(modifier = Modifier.height(20.dp))

        SolidProgressBar()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 10.dp)
        ) {
            ModificarEmailForm(
                emailFormulario = state.emailFormulario,
                isEmailValido = state.isEmailValido,
                onEmailChanged = actions.onEmailChanged
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        ModificarEmailFooter(
            isEmailValido = state.isEmailValido,
            emailNotEmpty = state.emailFormulario.isNotEmpty(),
            isVerifying = state.isVerifying,
            onBack = actions.onBack,
            onNext = actions.onSaveAndNext
        )
    }
}

@Composable
private fun EmailIdenticoDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Email idéntico",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "El nuevo email no puede ser igual al actual. Por favor, introduce una dirección de correo electrónico diferente.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Aceptar", color = Color(0xFF006644), fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun ModificarEmailHeader(onClose: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp)) {
        IconButton(
            onClick = onClose,
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color(0xFF006644))
        }

        Text(
            text = "Modificar email",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = Color.Black,
            fontFamily = CustomTypography
        )
    }
}

@Composable
private fun SolidProgressBar(progress: Float = 0.5f ) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .background(Color(0xFFE0E0E0))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .fillMaxHeight()
                .background(Color(0xFF006644))
        )
    }
}

@Composable
private fun ModificarEmailForm(
    emailFormulario: String,
    isEmailValido: Boolean,
    onEmailChanged: (String) -> Unit
) {
    Text(
        text = "¿En qué email deseas recibir tus facturas?",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp)
    )

    IberdrolaTextField(
        value = emailFormulario,
        onValueChange = onEmailChanged,
        label = { Text("* Nuevo email") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        isError = emailFormulario.isNotEmpty() && !isEmailValido
    )

    if (emailFormulario.isNotEmpty() && !isEmailValido) {
        Text(
            text = "El formato del email no es válido",
            color = Color.Red,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun ModificarEmailFooter(
    isEmailValido: Boolean,
    emailNotEmpty: Boolean,
    isVerifying: Boolean,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ){
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 0.5.dp,
            color = Color.LightGray.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                border = BorderStroke(1.5.dp, Color(0xFF006644)),
                shape = RoundedCornerShape(27.dp)
            ) {
                Text("Anterior", color = Color(0xFF006644), fontWeight = FontWeight.Bold)
            }

            val nextEnabled = isEmailValido && emailNotEmpty && !isVerifying
            Button(
                onClick = onNext,
                enabled = nextEnabled,
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
                    color = if (nextEnabled) Color.White else Color.Gray
                )
            }
        }
        Spacer(modifier = Modifier.height(36.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun ModificaremailPreview(){
    ModificarEmailContent(
        state = GestionUiState(),
        actions = ModificarEmailActions()
    )
}
