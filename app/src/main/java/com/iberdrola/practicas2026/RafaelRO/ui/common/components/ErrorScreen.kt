package com.iberdrola.practicas2026.RafaelRO.ui.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.GreenAplication

@Preview(showBackground = true)
@Composable
fun ErrorScreen(
    mensaje: String = "Error",
    onClearFilters: (() -> Unit)? = null, // Callback opcional
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Icono de Error",
                modifier = Modifier.size(64.dp),
                tint = Color.Red
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = mensaje,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            // Solo mostramos el botón si hay una acción de limpieza
            if (onClearFilters != null) {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onClearFilters,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenAplication
                    )
                ) {
                    Text(text = "Eliminar filtros", color = Color.White)
                }
            }
        }
    }
}