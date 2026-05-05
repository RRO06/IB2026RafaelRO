package com.iberdrola.practicas2026.RafaelRO.ui.common.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.GreenAplication
import com.iberdrola.practicas2026.RafaelRO.ui.screens.list_facturas.ListadoFacturasState

@Composable
fun ErrorScreen(
    mensaje: String,
    type: ListadoFacturasState.ErrorType = ListadoFacturasState.ErrorType.GENERIC,
    onClearFilters: (() -> Unit)? = null,
    onRetry: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val (icon, colorBase, title) = when (type) {
        ListadoFacturasState.ErrorType.NETWORK -> Triple(
            Icons.Default.WifiOff,
            Color(0xFF4A90E2), 
            "¡Vaya! No hay conexión"
        )
        ListadoFacturasState.ErrorType.SERVER -> Triple(
            Icons.Default.CloudOff,
            Color(0xFFE67E22), 
            "El servidor está descansando"
        )
        ListadoFacturasState.ErrorType.DATABASE -> Triple(
            Icons.Default.Storage,
            Color(0xFF95A5A6),
            "Problemas al leer los datos"
        )
        ListadoFacturasState.ErrorType.EMPTY_RESULTS -> Triple(
            Icons.Default.SearchOff,
            GreenAplication,
            "No hemos encontrado nada"
        )
        ListadoFacturasState.ErrorType.GENERIC -> Triple(
            Icons.Default.ErrorOutline,
            Color(0xFFE74C3C),
            "Algo no ha ido bien"
        )
    }

    // Animación suave de "latido" para el círculo del icono
    val infiniteTransition = rememberInfiniteTransition(label = "iconScale")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth()
        ) {
            // Icono con fondo circular animado
            Surface(
                modifier = Modifier
                    .size(150.dp)
                    .scale(scale),
                shape = CircleShape,
                color = colorBase.copy(alpha = 0.08f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(75.dp),
                        tint = colorBase
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                ),
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = mensaje,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = Color.Gray,
                lineHeight = 24.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Botones de acción
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (onRetry != null && type != ListadoFacturasState.ErrorType.EMPTY_RESULTS) {
                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GreenAplication
                        ),
                        shape = RoundedCornerShape(30.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(56.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(Modifier.width(10.dp))
                            Text(
                                "Reintentar conexión",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            )
                        }
                    }
                }

                if (onClearFilters != null && type == ListadoFacturasState.ErrorType.EMPTY_RESULTS) {
                    Button(
                        onClick = onClearFilters,
                        colors = ButtonDefaults.buttonColors(containerColor = GreenAplication),
                        shape = RoundedCornerShape(30.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(56.dp)
                    ) {
                        Text(
                            text = "Limpiar filtros",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                    }
                }

                if (onBack != null) {
                    TextButton(onClick = onBack) {
                        Text(
                            "Volver atrás",
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorScreenPreviewNetwork() {
    ErrorScreen(
        mensaje = "No se ha podido conectar con el servidor. Compruebe su conexión a internet.",
        type = ListadoFacturasState.ErrorType.NETWORK,
        onRetry = {},
        onBack = {}
    )
}
