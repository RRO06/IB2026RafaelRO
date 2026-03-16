package com.iberdrola.practicas2026.RafaelRO.ui.common.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentNeutral
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.GreenAplication

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpinionBottomSheet(
    onOpinionSelected: () -> Unit,
    onRecordarMasTarde: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        // El DragHandle es la rayita gris horizontal de arriba
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFFE0E0E0)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Tu opinión nos importa",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "¿Cómo de probable es que recomiendes esta app a amigos o familiares para que realicen sus gestiones?",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray,
                lineHeight = 20.sp
            )

            // Divisor sutil como el de la imagen
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 32.dp),
                thickness = 0.5.dp,
                color = Color.LightGray.copy(alpha = 0.5f)
            )

            // Fila de Emoticonos con sus colores exactos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OpinionIcon(Icons.Default.SentimentVeryDissatisfied, Color(0xFFD32F2F), onOpinionSelected)
                OpinionIcon(Icons.Default.SentimentDissatisfied, Color(0xFFF57C00), onOpinionSelected)
                OpinionIcon(Icons.Default.SentimentNeutral, Color(0xFF9E9E9E), onOpinionSelected)
                OpinionIcon(Icons.Default.SentimentSatisfied, Color(0xFF1976D2), onOpinionSelected)
                OpinionIcon(Icons.Default.SentimentVerySatisfied, Color(0xFF388E3C), onOpinionSelected)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón "Responder más tarde"
            Text(
                text = "Responder más tarde",
                modifier = Modifier
                    .clickable { onRecordarMasTarde() }
                    .padding(8.dp),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline
                ),
                color = GreenAplication// El verde oscuro de tu marca
            )
        }
    }
}

@Composable
private fun OpinionIcon(
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(36.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OpinionButtonSheetPreview(){
    OpinionBottomSheet(
        onOpinionSelected = {},
        onDismiss = {},
        onRecordarMasTarde = {}
    )
}