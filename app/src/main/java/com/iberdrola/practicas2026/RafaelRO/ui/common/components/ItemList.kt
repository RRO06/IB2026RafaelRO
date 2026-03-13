package com.iberdrola.practicas2026.RafaelRO.ui.common.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iberdrola.practicas2026.RafaelRO.domain.model.Factura
import com.iberdrola.practicas2026.RafaelRO.domain.model.Tipo
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.GreenAplication
import java.time.LocalDate

private val example = Factura(
    fechaInicio = LocalDate.now(),
    fechaFinal = LocalDate.now(),
    tipo = Tipo.Luz,
    estado = true,
    valor = 20.00
)

@Preview(showBackground = true)
@Composable
fun ItemList(factura: Factura = example, modifier: Modifier = Modifier) {
    val (statusText, statusColor) = if (factura.estado) {
        "Pendiente de pago" to Color.Red
    } else {
        "Pagada" to GreenAplication
    }

    val fechaFormateada = UtilyClass.toLongSpanishDate(factura.fechaFinal)
    val importe = "${UtilyClass.toCurrencyFormat(factura.valor)} €"

    // 2. La UI ahora es mucho más legible
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            InfoFactura(fechaFormateada, factura.tipo.toString(), statusText, statusColor)

            ImporteFactura(importe)
        }

        HorizontalDivider(color = Color.LightGray.copy(0.5f))
    }
}
@Composable
private fun InfoFactura(fecha: String, tipo: String, statusText: String, color: Color) {
    Column {
        Text(text = fecha, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        Text(text = "Factura $tipo")
        Spacer(modifier = Modifier.height(12.dp))
        StatusBadge(text = statusText, color = color)
    }
}

@Composable
private fun StatusBadge(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun ImporteFactura(importe: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = importe, color = Color.Gray, style = MaterialTheme.typography.bodyLarge)
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(35.dp)
        )
    }
}