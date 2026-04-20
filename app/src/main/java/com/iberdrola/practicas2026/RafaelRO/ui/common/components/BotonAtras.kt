package com.iberdrola.practicas2026.RafaelRO.ui.common.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.iberdrola.practicas2026.RafaelRO.R
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.GreenAplication

/**
 * Componente reutilizable para el botón de navegación hacia atrás.
 * Mantiene la consistencia visual en toda la aplicación.
 */
@Composable
fun BotonAtras(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onBack)
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.chevron_left),
            contentDescription = stringResource(id = R.string.icon_back_listado_facturas),
            tint = GreenAplication,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = "Atrás",
            fontWeight = FontWeight.Bold,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.padding(start = 8.dp),
            color = GreenAplication
        )
    }
}
