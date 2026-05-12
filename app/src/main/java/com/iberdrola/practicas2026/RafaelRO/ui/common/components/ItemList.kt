package com.iberdrola.practicas2026.RafaelRO.ui.common.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iberdrola.practicas2026.RafaelRO.R
import com.iberdrola.practicas2026.RafaelRO.domain.model.Estado
import com.iberdrola.practicas2026.RafaelRO.domain.model.Factura
import com.iberdrola.practicas2026.RafaelRO.domain.model.Tipo
import java.time.LocalDate

private val example = Factura(
    fechaExpedicion = LocalDate.now(),
    fechaInicio = LocalDate.now(),
    fechaFinal = LocalDate.now(),
    tipo = Tipo.Luz,
    estado = Estado.PendientePago,
    valor = 20.00
)

@Preview(showBackground = true)
@Composable
fun ItemList(
    factura: Factura = example,
    onClick: () -> Unit = {}
) {
    val fechaFormateada = UtilyClass.toLongSpanishDate(factura.fechaExpedicion)
    val importe = UtilyClass.toCurrencyFormat(factura.valor)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            InfoFactura(fechaFormateada, factura.tipo.toString(), factura.estado)
            ImporteFactura(importe)
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Color.LightGray.copy(0.3f)
        )
    }
}

@Composable
private fun InfoFactura(fecha: String, tipo: String, estado: Estado) {
    Column {
        Text(
            text = fecha,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Factura $tipo",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        FacturaStatusBadge(estado = estado)
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun ImporteFactura(importe: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = importe,
            color = Color.Gray,
            style = MaterialTheme.typography.bodyLarge
        )
        Icon(
            painter = painterResource(R.drawable.chevron_right),
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier
                .size(24.dp)
                .offset(x = 8.dp)
        )
    }
}
