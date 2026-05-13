package com.iberdrola.practicas2026.RafaelRO.ui.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.RafaelRO.domain.model.Estado
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.GreenAplication
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.LightGreen
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.LightRed
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.RedAplication

@Composable
fun FacturaStatusDetails(estado: Estado, modifier: Modifier = Modifier) {
    val (text, color, icon) = when (estado) {
        Estado.Pagado -> Triple("Pagada", GreenAplication, Icons.Default.CheckCircle)
        Estado.PendientePago -> Triple("Pendiente de Pago", RedAplication, Icons.Default.Error)
        Estado.Tramite -> Triple("En trámite", Color(0xFF757575), Icons.Default.History)
        Estado.Anulado -> Triple("Anulada", Color(0xFF616161), Icons.Default.Info)
        Estado.CuotaFija -> Triple("Cuota fija", Color(0xFF1976D2), Icons.Default.CheckCircle)
    }
    
    val backgroundColor = when (estado) {
        Estado.Pagado -> LightGreen
        Estado.PendientePago -> LightRed
        Estado.Tramite -> Color(0xFFEEEEEE)
        Estado.Anulado -> Color(0xFFF5F5F5)
        Estado.CuotaFija -> Color(0xFFE3F2FD)
    }

    Row(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.2.sp
            )
        )
    }
}
