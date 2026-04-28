package com.iberdrola.practicas2026.RafaelRO.ui.screens.gestion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.RafaelRO.R

@Composable
fun GestionExitoScreen(
    titulo: String,
    email: String,
    onContinuar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF006644)) // Verde Iberdrola exacto
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Botón cerrar superior
        Box(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), contentAlignment = Alignment.TopEnd) {
            IconButton(onClick = onContinuar) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Icono Thumbs Up (Si tienes el SVG con rayitas mejor, si no, lo rodeamos de estilo)
        Box(contentAlignment = Alignment.Center) {
            // Aquí puedes poner el Icon de Material o un Image con tu recurso R.drawable.thumb_up
            Icon(
                painter = painterResource(id = R.drawable.ic_success_thumbs_up),
                contentDescription = null,
                modifier = Modifier.size(180.dp),
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Título exacto de la imagen
        Text(
            text = titulo,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold
            ),
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Mensaje con el email ofuscado
        Text(
            text = "Pronto recibirás un correo electrónico de verificación para recibir tus facturas en la dirección $email",
            style = MaterialTheme.typography.labelMedium.copy(
                lineHeight = 22.sp,
            ),
            color = Color.White,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.weight(1.2f))

        Button(
            onClick = onContinuar,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color(0xFF006644)
            )
        ) {
            Text(
                text = "Aceptar",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}
@Preview(showBackground = true)
@Composable
fun GestionExitoScreenPreview(){
    GestionExitoScreen(
        titulo = "¡Has modificado correctamente tu email!",
        email = "holasoyrafa@gmail.com",
        onContinuar = {}
    )
}