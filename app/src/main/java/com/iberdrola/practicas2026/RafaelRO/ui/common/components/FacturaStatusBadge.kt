package com.iberdrola.practicas2026.RafaelRO.ui.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun FacturaStatusBadge(estado: Estado) {
    val (text, color) = when (estado) {
        Estado.Pagado -> "Pagada" to GreenAplication
        Estado.PendientePago -> "Pendiente de pago" to RedAplication
        Estado.Tramite -> "En trámite" to Color.Gray
        Estado.Anulado -> "Anulada" to Color.DarkGray
        Estado.CuotaFija -> "Cuota fija" to Color.Blue
    }
    val backgroundColor = when (estado) {
        Estado.Pagado -> LightGreen
        Estado.PendientePago -> LightRed
        Estado.Tramite -> Color.Gray.copy(0.1f)
        Estado.Anulado -> Color.DarkGray.copy(0.1f)
        Estado.CuotaFija -> Color.Blue.copy(0.1f)
    }
    Box(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(11.dp))
            .padding(horizontal = 11.dp, vertical = 3.dp),
    ) {
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 11.5.sp,
                fontWeight = FontWeight.ExtraBold
            )
        )
    }
}