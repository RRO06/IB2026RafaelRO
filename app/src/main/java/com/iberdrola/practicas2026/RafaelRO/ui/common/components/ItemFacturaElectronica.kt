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
import androidx.compose.material.icons.filled.ChevronRight
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
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Parte 1: El Icono del servicio
            ServiceIcon(tipo = tipo)

            Spacer(modifier = Modifier.width(16.dp))

            // Parte 2: El texto y los Badges (Cuerpo)
            ServiceInfo(
                tipo = tipo,
                estaActiva = estaActiva,
                modifier = Modifier.weight(1f)
            )

            // Parte 3: La flecha (Acción)
            ServiceActionIcon()
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            thickness = 1.dp,
            color = Color.LightGray.copy(alpha = 0.3f)
        )
    }
}

@Composable
private fun ServiceIcon(tipo: Tipo) {
    // Usamos un Box para asegurar el centrado absoluto del dibujo
    Box(
        modifier = Modifier.size(36.dp),
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
    Column(modifier = modifier) {
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
private fun ServiceActionIcon() {
    Icon(
        imageVector = Icons.Default.ChevronRight,
        contentDescription = null,
        tint = Color.Gray,
        modifier = Modifier.size(28.dp)
    )
}

@Composable
private fun BadgeEstadoElectronico(activa: Boolean) {
    val color = if (activa) GreenAplication else Color.LightGray
    val texto = if (activa) "Activa" else "Sin Activar"

    Box(
        modifier = Modifier
            .padding(top = 4.dp)
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 2.dp)
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