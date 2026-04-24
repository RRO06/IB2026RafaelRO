package com.iberdrola.practicas2026.RafaelRO.ui.screens.factura_electronica

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iberdrola.practicas2026.RafaelRO.domain.model.Contrato
import com.iberdrola.practicas2026.RafaelRO.domain.model.Tipo
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.BotonAtras
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.ItemFacturaElectronica
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.IB2026RafaelROTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacturaElectronicaScreen(
    viewModel: FacturasElectronicasViewModel,
    onBack: () -> Unit,
    onContratoClick: (Contrato) -> Unit,
    modifier: Modifier = Modifier
) {
    val state = viewModel.state

    FacturaElectronicaStatelessContent(
        isRefreshing = state.isRefreshing,
        contratos = state.contratos,
        onRefresh = { viewModel.refreshData() },
        onBack = onBack,
        onContratoClick = onContratoClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacturaElectronicaStatelessContent(
    isRefreshing: Boolean,
    contratos: List<Contrato>,
    onRefresh: () -> Unit,
    onBack: () -> Unit,
    onContratoClick: (Contrato) -> Unit,
    modifier: Modifier = Modifier
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier.fillMaxSize().background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            BotonAtras(onBack = onBack)

            Spacer(modifier = Modifier.height(24.dp))

            HeaderSeccion(
                titulo = "Factura electrónica",
                modifier = Modifier.padding(start = 24.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            ListaContratos(
                contratos = contratos,
                onContratoClick = onContratoClick
            )
        }
    }
}
@Composable
fun HeaderSeccion(
    titulo: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = titulo,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

@Composable
fun ListaContratos(
    contratos: List<Contrato>,
    onContratoClick: (Contrato) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        contratos.forEach { contrato ->
            ItemFacturaElectronica(
                tipo = contrato.tipo,
                estaActiva = contrato.estado,
                onClick = { onContratoClick(contrato) }
            )
            FacturaDivider()
        }
    }
}

@Composable
fun FacturaDivider() {
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        thickness = 1.dp,
        color = Color.LightGray.copy(alpha = 0.5f)
    )
}

@Preview(showBackground = true)
@Composable
fun FacturaElectronicaPreview() {
    val mockContratos = listOf(
        Contrato(tipo = Tipo.Luz, estado = true),
        Contrato(tipo = Tipo.Gas, estado = false)
    )

    IB2026RafaelROTheme {
        FacturaElectronicaStatelessContent(
            isRefreshing = false,
            contratos = mockContratos,
            onRefresh = {},
            onBack = {},
            onContratoClick = {}
        )
    }
}
