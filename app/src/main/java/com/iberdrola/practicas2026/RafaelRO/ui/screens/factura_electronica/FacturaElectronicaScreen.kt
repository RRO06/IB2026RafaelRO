package com.iberdrola.practicas2026.RafaelRO.ui.screens.factura_electronica

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.iberdrola.practicas2026.RafaelRO.domain.model.Contrato
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.ItemFacturaElectronica
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.GreenAplication

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacturaElectronicaScreen(
    viewModel: FacturasElectronicasViewModel, // Datos que vendrán del ViewModel
    onBack: () -> Unit,
    onContratoClick: (Contrato) -> Unit,
    modifier: Modifier = Modifier
) {
    PullToRefreshBox(
        isRefreshing = viewModel.state.isRefreshing,
        onRefresh = { viewModel.refreshData() },
        modifier = modifier.fillMaxSize().background(Color.White)
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()) // IMPORTANTE: Debe ser scrollable para que el gesto funcione
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onBack() }
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = null,
                    tint = GreenAplication
                )
                Text(
                    text = "Atrás",
                    color = GreenAplication,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Factura electrónica",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Invocación del contenido separado (State Hoisting)
            FacturaElectronicaContent(
                state = viewModel.state,
                onContratoClick = onContratoClick
            )
        }
    }
}
@Composable
fun FacturaElectronicaContent(
    state: FacturasElectronicasUiState,
    onContratoClick: (Contrato) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        state.contratos.forEach { contrato ->
            ItemFacturaElectronica(
                tipo = contrato.tipo,
                estaActiva = contrato.estado,
                onClick = { onContratoClick(contrato) }
            )
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = Color.LightGray.copy(alpha = 0.5f)
            )

        }
        // Divisor de cierre al final de la lista
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Color.LightGray.copy(alpha = 0.5f)
        )
    }
}