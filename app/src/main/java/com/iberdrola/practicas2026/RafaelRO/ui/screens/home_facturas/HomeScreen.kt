package com.iberdrola.practicas2026.RafaelRO.ui.screens.home_facturas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.BotonFiltroFocuseado
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.GreenAplication

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToFacturas: () -> Unit,
    modifier : Modifier = Modifier,
) {
    val uiState = viewModel.stateUI

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Cabecera de Bienvenida
        Text(
            text = "¡Bienvenido de nuevo!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.weight(1f))

        // 2. Tarjeta de Usuario
        UserCard(uiState)

        Spacer(modifier = Modifier.weight(4f))

        // 3. Selección de Origen de Datos (Local vs Nube)
        SelectorDeOrigen(
            esNube = uiState.esModoNube,
            onOptionSelected = { viewModel.onModoNubeChanged(it) }
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { onNavigateToFacturas() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenAplication),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Ver mis facturas", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}
@Composable
fun UserCard(state: HomeUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.5.dp, GreenAplication)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(48.dp), tint = GreenAplication)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(text = state.nombreUsuario, fontWeight = FontWeight.Bold)
                    Text(text = "Usuario: ${state.idUsuario}", style = MaterialTheme.typography.bodySmall)
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(0.5f))
            Text(text = "Último acceso: ${state.ultimaConexion}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}
@Composable
fun SelectorDeOrigen(esNube: Boolean, onOptionSelected: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        BotonFiltroFocuseado(
            text = "Local",
            isSelected = !esNube,
            onClick = { onOptionSelected(false) },
            modifier = Modifier.height(40.dp)
        )
        BotonFiltroFocuseado(
            text = "Nube",
            isSelected = esNube,
            onClick = { onOptionSelected(true) },
            modifier = Modifier.height(40.dp)
        )
    }
}
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview(){
    HomeScreen(
        viewModel = hiltViewModel(),
        onNavigateToFacturas = {}
    )
}