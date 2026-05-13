package com.iberdrola.practicas2026.RafaelRO.ui.screens.list_facturas

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.iberdrola.practicas2026.RafaelRO.R
import com.iberdrola.practicas2026.RafaelRO.domain.model.Estado
import com.iberdrola.practicas2026.RafaelRO.domain.model.Factura
import com.iberdrola.practicas2026.RafaelRO.domain.model.Tipo
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.BotonAtras
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.ErrorScreen
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.FacturaStatusDetails
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.UtilyClass
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.GreenAplication
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.IB2026RafaelROTheme
import java.time.LocalDate

@Composable
fun DetalleFacturaScreen(
    viewModel: DetalleFacturaViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state = viewModel.state
    var showDownloadDialog by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current

    DetalleFacturaStatelessContent(
        state = state,
        onBack = onBack,
        showDownloadDialog = showDownloadDialog,
        onDismissDialog = { showDownloadDialog = false },
        onDownloadClick = {
            if (lifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
                showDownloadDialog = true
            }
        },
        onRetry = { viewModel.loadFactura() },
        modifier = modifier
    )
}

@Composable
fun DetalleFacturaStatelessContent(
    state: DetalleFacturaState,
    onBack: () -> Unit,
    showDownloadDialog: Boolean,
    onDismissDialog: () -> Unit,
    onDownloadClick: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    if (showDownloadDialog) {
        DownloadSuccessDialog(onDismiss = onDismissDialog)
    }

    Column(
        modifier = modifier
            .systemBarsPadding()
            .fillMaxSize()
            .background(Color.White)
    ) {
        BotonAtras(
            onBack = onBack,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        )

        AnimatedContent(
            targetState = state,
            label = "StateTransition",
            modifier = Modifier.weight(1f),
            transitionSpec = {
                fadeIn().togetherWith(fadeOut())
            }
        ) { targetState ->
            when (targetState) {
                is DetalleFacturaState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = GreenAplication)
                    }
                }
                is DetalleFacturaState.Success -> {
                    DetalleFacturaContentSuccess(
                        factura = targetState.factura,
                        isFallback = targetState.isFallback,
                        onDownloadClick = onDownloadClick,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                is DetalleFacturaState.Error -> {
                    ErrorScreen(
                        mensaje = targetState.message,
                        type = targetState.type,
                        onRetry = onRetry,
                        onBack = null
                    )
                }
            }
        }
    }
}

@Composable
fun DetalleFacturaContentSuccess(
    factura: Factura,
    isFallback: Boolean,
    onDownloadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            if (isFallback) {
                FallbackBanner(modifier = Modifier.padding(bottom = 24.dp))
            }

            Text(
                text = "Detalle de tu factura",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Factura de ${factura.tipo}",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.5.dp, GreenAplication.copy(alpha = 0.3f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp, horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "IMPORTE TOTAL",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "%.2f".format(factura.valor),
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontSize = 52.sp,
                                fontWeight = FontWeight.Black
                            ),
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "€",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = GreenAplication
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    FacturaStatusDetails(
                        estado = factura.estado,
                        modifier = Modifier.fillMaxWidth(0.85f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Información de facturación",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            InfoRow(
                icon = Icons.Default.Receipt,
                label = "Número de factura",
                value = "FX-${factura.id}9283"
            )
            InfoRow(
                icon = Icons.Default.CalendarMonth,
                label = "Fecha de emisión",
                value = UtilyClass.toLongSpanishDate(factura.fechaExpedicion)
            )
            InfoRow(
                icon = Icons.Default.History,
                label = "Periodo de consumo",
                value = "${UtilyClass.toSpanishMediumDate(factura.fechaInicio)} - ${UtilyClass.toSpanishMediumDate(factura.fechaFinal)}"
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4).copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFFFBC02D),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Si tienes alguna duda con el importe, puedes contactar con nuestro servicio de atención al cliente gratuito.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                }
            }
        }

        Button(
            onClick = onDownloadClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenAplication),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(text = "Descargar factura en PDF", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
fun FallbackBanner(modifier: Modifier = Modifier) {
    Surface(
        color = Color(0xFFFFF3E0),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CloudOff,
                contentDescription = null,
                tint = Color(0xFFE65100),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Estás viendo una copia local. Comprueba tu conexión para actualizar.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFE65100),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun DownloadSuccessDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Vale", color = GreenAplication, fontWeight = FontWeight.Bold)
            }
        },
        title = {
            Text(
                text = "Estamos trabajando en esta funcionalidad",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.anim_engranajes))
                
                LottieAnimation(
                    composition = composition,
                    iterations = LottieConstants.IterateForever,
                    modifier = Modifier.size(150.dp)
                )
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = GreenAplication,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
}

@Preview(showBackground = true)
@Composable
fun DetalleFacturaStatelessPreview() {
    val mockFactura = Factura(
        id = 123,
        fechaExpedicion = LocalDate.of(2024, 2, 1),
        fechaInicio = LocalDate.of(2024, 1, 1),
        fechaFinal = LocalDate.of(2024, 1, 31),
        tipo = Tipo.Luz,
        estado = Estado.PendientePago,
        valor = 20.00
    )

    IB2026RafaelROTheme {
        DetalleFacturaStatelessContent(
            state = DetalleFacturaState.Success(mockFactura, isFallback = false),
            onBack = {},
            showDownloadDialog = false,
            onDismissDialog = {},
            onDownloadClick = {},
            onRetry = {}
        )
    }
}
