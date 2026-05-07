package com.iberdrola.practicas2026.RafaelRO.ui.screens.gestion

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.iberdrola.practicas2026.RafaelRO.R
import com.iberdrola.practicas2026.RafaelRO.domain.model.Contrato
import com.iberdrola.practicas2026.RafaelRO.domain.model.Tipo
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.BotonAtras
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.GreenAplication
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.IB2026RafaelROTheme

@Composable
fun DetalleFacturaActivaScreen(
    viewModel: GestionViewModel,
    onBack: () -> Unit,
    onModificarClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState = viewModel.state
    var showDialog by remember { mutableStateOf(false) }

    BackHandler { onBack() }

    DetalleFacturaActivaContent(
        uiState = uiState,
        showDialog = showDialog,
        onBack = onBack,
        onModificarClick = {
            if (lifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
                uiState.contrato?.id?.let { onModificarClick(it) }
            }
        },
        onDesactivarClick = { showDialog = true },
        onDismissDialog = { showDialog = false },
        onConfirmDesactivar = {
            if (lifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
                showDialog = false
                viewModel.desactivarFacturaElectronica { onBack() }
            }
        },
        modifier = modifier
    )
}

@Composable
fun DetalleFacturaActivaContent(
    uiState: GestionUiState,
    showDialog: Boolean,
    onBack: () -> Unit,
    onModificarClick: () -> Unit,
    onDesactivarClick: () -> Unit,
    onDismissDialog: () -> Unit,
    onConfirmDesactivar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .systemBarsPadding()
            .fillMaxSize()
            .background(Color.White)
    ) {
        BotonAtras(
            onBack = onBack,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        )

        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GreenAplication)
                }
            }
            uiState.contrato != null -> {
                DetalleFacturaBody(
                    contrato = uiState.contrato,
                    onModificarClick = onModificarClick,
                    onDesactivarClick = onDesactivarClick
                )
            }
        }
    }

    if (showDialog) {
        DesactivarFacturaDialog(
            onDismiss = onDismissDialog,
            onConfirm = onConfirmDesactivar
        )
    }
}

@Composable
private fun DetalleFacturaBody(
    contrato: Contrato,
    onModificarClick: () -> Unit,
    onDesactivarClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        HeaderDetalle(tipo = contrato.tipo, direccion = contrato.direccion)

        Spacer(modifier = Modifier.height(32.dp))

        EmailSeccion(email = contrato.email)

        Spacer(modifier = Modifier.height(24.dp))

        FacturaActivaCard(onDesactivarClick = onDesactivarClick)

        Spacer(modifier = Modifier.weight(1f))

        AccionModificarEmail(onClick = onModificarClick)
    }
}

@Composable
private fun HeaderDetalle(tipo: Tipo, direccion: String) {
    Column {
        Text(
            text = "Contrato de ${if (tipo == Tipo.Luz) "Luz" else "Gas"}",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = direccion,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun EmailSeccion(email: String) {
    Column {
        Text(
            text = "Actualmente recibes las facturas electrónicas de este contrato al:",
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Recibes tus facturas en este email",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = email,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            thickness = 1.dp,
            color = Color.LightGray.copy(alpha = 0.5f)
        )
        Row(verticalAlignment = Alignment.Top) {
            Icon(Icons.Outlined.Info, null, Modifier.size(20.dp), Color.Gray)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Recuerda que la factura electrónica es un requisito de este Plan, por lo que no es recomendable desactivarla.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun FacturaActivaCard(onDesactivarClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Factura electrónica activa",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF006644)
                )
                Text(
                    "Si lo prefieres, puedes volver a recibir tus facturas en papel.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Text(
                text = "DESACTIVAR",
                color = Color(0xFFD32F2F),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .clickable { onDesactivarClick() }
                    .padding(8.dp)
            )
        }
    }
}

@Composable
private fun AccionModificarEmail(onClick: () -> Unit) {
    Column {
        HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
                .padding(horizontal = 8.dp)
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E5148)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_edit_iberdrola),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Modificar email", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun DesactivarFacturaDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("¿Desactivar factura electrónica?") },
        text = { Text("Volverás a recibir tus facturas en formato papel por correo postal.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("DESACTIVAR", color = Color.Red, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCELAR", color = Color.Gray)
            }
        },
        containerColor = Color.White
    )
}

// --- PREVIEWS ---

@Preview(showBackground = true, name = "Estado Cargado")
@Composable
fun DetalleFacturaActivaPreview() {
    IB2026RafaelROTheme {
        DetalleFacturaActivaContent(
            uiState = GestionUiState(
                contrato = Contrato(
                    tipo = Tipo.Luz,
                    direccion = "Calle Falsa 123",
                    email = "usuario@ejemplo.com",
                    estado = true
                ),
                isLoading = false
            ),
            showDialog = false,
            onBack = {},
            onModificarClick = {},
            onDesactivarClick = {},
            onDismissDialog = {},
            onConfirmDesactivar = {}
        )
    }
}

@Preview(showBackground = true, name = "Con Diálogo Abierto")
@Composable
fun DetalleFacturaConDialogPreview() {
    IB2026RafaelROTheme {
        DetalleFacturaActivaContent(
            uiState = GestionUiState(
                contrato = Contrato(
                    tipo = Tipo.Gas,
                    direccion = "Av. Principal 45",
                    email = "test@iberdrola.es",
                    estado = true
                )
            ),
            showDialog = true,
            onBack = {},
            onModificarClick = {},
            onDesactivarClick = {},
            onDismissDialog = {},
            onConfirmDesactivar = {}
        )
    }
}

@Preview(showBackground = true, name = "Cargando")
@Composable
fun DetalleFacturaLoadingPreview() {
    IB2026RafaelROTheme {
        DetalleFacturaActivaContent(
            uiState = GestionUiState(isLoading = true, contrato = null),
            showDialog = false,
            onBack = {},
            onModificarClick = {},
            onDesactivarClick = {},
            onDismissDialog = {},
            onConfirmDesactivar = {}
        )
    }
}
