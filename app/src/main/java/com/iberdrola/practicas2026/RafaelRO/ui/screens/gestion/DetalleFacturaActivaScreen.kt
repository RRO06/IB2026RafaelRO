package com.iberdrola.practicas2026.RafaelRO.ui.screens.gestion

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import com.iberdrola.practicas2026.RafaelRO.domain.model.Contrato
import com.iberdrola.practicas2026.RafaelRO.domain.model.Tipo
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.GreenAplication
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
import com.iberdrola.practicas2026.RafaelRO.R
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.BotonAtras
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.IB2026RafaelROTheme

@Composable
fun DetalleFacturaActivaScreen(
    viewModel: GestionViewModel,
    onBack: () -> Unit,
    onModificarClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState = viewModel.state
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp)
            .background(Color.White)
    ) {
        FacturasElectronicasHeader(onBack = onBack)

        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GreenAplication)
                }
            }

            uiState.contrato != null -> {
                DetalleFacturaContent(
                    contrato = uiState.contrato,
                    onModificarClick = { onModificarClick(uiState.contrato.id) },
                    onDesactivarClick = { showDialog = true }
                )
                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("¿Desactivar factura electrónica?") },
                        text = { Text("Volverás a recibir tus facturas en formato papel por correo postal.") },
                        confirmButton = {
                            TextButton(onClick = {
                                showDialog = false
                                viewModel.desactivarFacturaElectronica { onBack() }
                            }) {
                                Text("DESACTIVAR", color = Color.Red, fontWeight = FontWeight.Bold)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text("CANCELAR", color = Color.Gray)
                            }
                        },
                        containerColor = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun FacturasElectronicasHeader(
    onBack: () -> Unit
) {
    BotonAtras(onBack = onBack)
}

@Composable
fun DetalleFacturaContent(
    contrato: Contrato,
    onModificarClick: () -> Unit,
    onDesactivarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Contrato de ${
                when (contrato.tipo) {
                    Tipo.Luz -> "Luz"
                    Tipo.Gas -> "Gas"
                }
            }",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Dirección del contrato
        Text(
            text = contrato.direccion,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 4.dp),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Texto informativo
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
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = contrato.email,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        // Línea divisoria sutil
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            thickness = 1.dp,
            color = Color.LightGray.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(8.dp))


        // Nota con icono informativo
        Row(verticalAlignment = Alignment.Top) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Recuerda que la factura electrónica es un requisito de este Plan, por lo que no es recomendable desactivarla.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)), // Gris muy claro
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
                        text = "Factura electrónica activa",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF006644) // Verde éxito
                    )
                    Text(
                        text = "Si lo prefieres, puedes volver a recibir tus facturas en papel.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                // Switch o Botón de texto que destaque
                Text(
                    text = "DESACTIVAR",
                    color = Color(0xFFD32F2F), // Rojo suave
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier
                        .clickable { onDesactivarClick() }
                        .padding(8.dp)
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 0.5.dp,
            color = Color.LightGray.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onModificarClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2E5148)
            ),
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

@Preview(showBackground = true)
@Composable
fun DetalleFacturaActivaScreenPreview() {
    IB2026RafaelROTheme {
        DetalleFacturaContent(
            contrato = Contrato(
                tipo = Tipo.Luz,
                telefono = "+34 600 000 000",
                direccion = "Calle Falsa 123",
                estado = true,
                email = "contacto@ejemplo.com"
            ),
            onModificarClick = {},
            onDesactivarClick = {}
        )
    }
}