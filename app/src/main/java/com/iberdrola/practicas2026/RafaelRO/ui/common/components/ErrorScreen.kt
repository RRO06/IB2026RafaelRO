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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.GreenAplication
import com.iberdrola.practicas2026.RafaelRO.ui.screens.list_facturas.ListadoFacturasState

private data class ErrorVisuals(
    val icon: ImageVector,
    val color: Color,
    val title: String
)

@Composable
fun ErrorScreen(
    mensaje: String,
    type: ListadoFacturasState.ErrorType = ListadoFacturasState.ErrorType.GENERIC,
    onClearFilters: (() -> Unit)? = null,
    onModifierFilters: (() -> Unit)? = null,
    onRetry: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val visuals = getErrorVisuals(type)
    val scale = rememberIconPulseScale()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp).fillMaxWidth()
        ) {
            ErrorIconSection(visuals.icon, visuals.color, scale)
            Spacer(modifier = Modifier.height(40.dp))
            ErrorTextSection(visuals.title, mensaje)
            Spacer(modifier = Modifier.height(48.dp))
            ErrorActionsSection(
                type = type,
                onRetry = onRetry,
                onClearFilters = onClearFilters,
                onModifierFilters = onModifierFilters,
                onBack = onBack
            )
        }
    }
}

@Composable
private fun rememberIconPulseScale(): Float {
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
    return scale
}

@Composable
private fun ErrorIconSection(icon: ImageVector, color: Color, scale: Float) {
    Surface(
        modifier = Modifier.size(150.dp).scale(scale),
        shape = CircleShape,
        color = color.copy(alpha = 0.08f)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(75.dp),
                tint = color
            )
        }
    }
}

@Composable
private fun ErrorTextSection(title: String, message: String) {
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
        text = message,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        color = Color.Gray,
        lineHeight = 24.sp,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@Composable
private fun ErrorActionsSection(
    type: ListadoFacturasState.ErrorType,
    onRetry: (() -> Unit)?,
    onClearFilters: (() -> Unit)?,
    onModifierFilters: (() -> Unit)?,
    onBack: (() -> Unit)?
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (onRetry != null && type != ListadoFacturasState.ErrorType.EMPTY_RESULTS) {
            val buttonText = if(type == ListadoFacturasState.ErrorType.NO_SERVICES){
                "Actualizar servicios"
            }
            else{
                "Reintentar conexión"
            }
            ActionButton(onClick = onRetry, text = buttonText, icon = Icons.Default.Refresh)
        }

        if (type == ListadoFacturasState.ErrorType.EMPTY_RESULTS) {
            onClearFilters?.let { ActionButton(onClick = it, text = "Limpiar filtros") }
            onModifierFilters?.let { ActionButton(onClick = it, text = "Modificar filtros") }
        }

        onBack?.let {
            TextButton(onClick = it) {
                Text(
                    text = "Volver atrás",
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun ActionButton(onClick: () -> Unit, text: String, icon: ImageVector? = null) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = GreenAplication),
        shape = RoundedCornerShape(30.dp),
        modifier = Modifier.fillMaxWidth(0.8f).height(56.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            icon?.let {
                Icon(it, contentDescription = null)
                Spacer(Modifier.width(10.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            )
        }
    }
}

private fun getErrorVisuals(type: ListadoFacturasState.ErrorType): ErrorVisuals {
    return when (type) {
        ListadoFacturasState.ErrorType.NETWORK -> ErrorVisuals(
            Icons.Default.WifiOff, Color(0xFF4A90E2), "¡Vaya! No hay conexión"
        )
        ListadoFacturasState.ErrorType.NO_SERVICES -> ErrorVisuals(
            Icons.Default.Block, Color(0xFF7F8C8D),"Servicios no disponibles"
        )
        ListadoFacturasState.ErrorType.SERVER -> ErrorVisuals(
            Icons.Default.CloudOff, Color(0xFFE67E22), "El servidor está descansando"
        )
        ListadoFacturasState.ErrorType.DATABASE -> ErrorVisuals(
            Icons.Default.Storage, Color(0xFF95A5A6), "Problemas al leer los datos"
        )
        ListadoFacturasState.ErrorType.EMPTY_RESULTS -> ErrorVisuals(
            Icons.Default.SearchOff, GreenAplication, "No hemos encontrado nada"
        )
        ListadoFacturasState.ErrorType.GENERIC -> ErrorVisuals(
            Icons.Default.ErrorOutline, Color(0xFFE74C3C), "Algo no ha ido bien"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorScreenPreviewNetwork() {
    ErrorScreen(
        mensaje = "No se ha podido conectar con el servidor. Compruebe su conexión a internet.",
        type = ListadoFacturasState.ErrorType.EMPTY_RESULTS,
        onRetry = {},
        onClearFilters = {},
        onModifierFilters = {}
    )
}
