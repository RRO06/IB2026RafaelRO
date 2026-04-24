package com.iberdrola.practicas2026.RafaelRO.ui.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iberdrola.practicas2026.RafaelRO.R
import com.iberdrola.practicas2026.RafaelRO.domain.model.Tipo
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.GreenAplication
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.LightGreen

@Composable
fun ItemFacturaElectronica(
    tipo: Tipo,
    estaActiva: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(vertical = 16.dp, horizontal = 8.dp), // Alineado con el BotonAtras (16+8=24dp desde el borde)
            verticalAlignment = Alignment.Top
        ) {
            // Parte 1: El Icono del servicio (38dp)
            ServiceIcon(tipo = tipo)

            Spacer(modifier = Modifier.width(16.dp))

            // Parte 2: El texto y los Badges (Cuerpo)
            ServiceInfo(
                tipo = tipo,
                estaActiva = estaActiva,
                modifier = Modifier.weight(1f)
            )

            ServiceActionIcon(modifier = Modifier.align(Alignment.CenterVertically))
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 8.dp),
            thickness = 1.dp,
            color = Color.LightGray.copy(alpha = 0.3f)
        )
    }
}

@Composable
private fun ServiceIcon(
    tipo: Tipo,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(38.dp),
        contentAlignment = Alignment.Center
    ) {
        when (tipo) {
            Tipo.Luz -> {
                Icon(
                    imageVector = Icons.Outlined.Lightbulb,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    tint = GreenAplication
                )
            }
            Tipo.Gas -> {
                Icon(
                    painter = painterResource(id = R.drawable.ic_gas_iberdrola),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    tint = GreenAplication
                )
            }
        }
    }
}

@Composable
private fun ServiceInfo(
    tipo: Tipo,
    estaActiva: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(top = 2.dp)) {
        Text(
            text = if (tipo == Tipo.Luz) "Contrato de Luz" else "Contrato de Gas",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        BadgeEstadoElectronico(estaActiva)
    }
}

@Composable
private fun ServiceActionIcon(modifier: Modifier = Modifier) {
    Icon(
        painter = painterResource(R.drawable.chevron_right),
        contentDescription = null,
        tint = Color.Gray,
        modifier = modifier.size(28.dp)
    )
}

@Composable
private fun BadgeEstadoElectronico(activa: Boolean) {
    val color = if (activa) GreenAplication else Color.DarkGray
    val texto = if (activa) "Activa" else "Sin Activar"
    val backgroundColor = if (activa) LightGreen else Color.DarkGray.copy(alpha = 0.1f)

    Box(
        modifier = Modifier
            .padding(top = 8.dp) // Alineado con la base del icono de 38dp
            .background(backgroundColor, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = texto,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ItemFacturaElectronicaGasPreview(){
    ItemFacturaElectronica(
        tipo = Tipo.Gas,
        estaActiva = true,
        onClick = {}
    )
}
@Preview(showBackground = true)
@Composable
fun ItemFacturaElectronicaLuzPreview(){
    ItemFacturaElectronica(
        tipo = Tipo.Luz,
        estaActiva = false,
        onClick = {}
    )
}
