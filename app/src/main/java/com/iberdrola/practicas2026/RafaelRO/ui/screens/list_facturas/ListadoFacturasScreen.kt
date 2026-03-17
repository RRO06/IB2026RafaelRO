package com.iberdrola.practicas2026.RafaelRO.ui.screens.list_facturas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iberdrola.practicas2026.RafaelRO.R
import com.iberdrola.practicas2026.RafaelRO.domain.model.Estado
import com.iberdrola.practicas2026.RafaelRO.domain.model.Factura
import com.iberdrola.practicas2026.RafaelRO.domain.model.Tipo
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.BotonFiltroFocuseado
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.ErrorScreen
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.ItemList
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.LoadingScreen
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.UtilyClass
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.Divider
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.GreenAplication
import com.iberdrola.practicas2026.RafaelRO.ui.screens.filt_facturas.FiltUiState

data class ListadoFacturasActions(
    val onFilter: () -> Unit,
    val onFacturaClick: () -> Unit,
    val dismissDialog: () -> Unit
)

@Composable
fun ListadoFacturasScreen(
    viewModel: ListadoFacturasViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onFilter: () -> Unit,
    filtState: FiltUiState
) {
    LaunchedEffect(filtState) {
        viewModel.actualizarInterfaz(filtrosExtra = filtState)
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        FacturasHeader(onBack = onBack)
        Spacer(modifier = Modifier.height(24.dp))

        Row {
            BotonFiltroFocuseado(
                text = "Luz",
                onClick = { viewModel.onFilterLuz() },
                modifier = Modifier,
                isSelected = viewModel.stateUI.filtroTipoActual == Tipo.Luz
            )
            BotonFiltroFocuseado(
                text = "Gas",
                onClick = { viewModel.onFilterGas() },
                modifier = Modifier,
                isSelected = viewModel.stateUI.filtroTipoActual == Tipo.Gas
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(Divider)
        )
        when (val state = viewModel.stateData) {
            is ListadoFacturasState.Error -> ErrorScreen(
                mensaje = state.message,
                onClearFilters = if (viewModel.tieneFiltrosActivos()) {
                    { viewModel.limpiarFiltros() }
                } else null
            )
            is ListadoFacturasState.Loading -> LoadingScreen()
            is ListadoFacturasState.Success -> ListadoFacturasContent(
                actions = ListadoFacturasActions(
                    onFilter = onFilter,
                    onFacturaClick = viewModel::onFacturaClick,
                    dismissDialog = viewModel::dismissDialog
                ),
                stateUI = viewModel.stateUI
            )
        }
    }
}

@Composable
fun ListadoFacturasContent(
    actions: ListadoFacturasActions,
    stateUI: ListadoFacturasUiState,
) {
    Column(
        modifier = Modifier.padding(12.dp)
    ) {
        UltimaFacturaCard(stateUI.ultimaFactura)
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Historico de facturas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = actions.onFilter,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = GreenAplication
                ),
                border = BorderStroke(
                    width = 2.dp,
                    color = GreenAplication
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(end = 4.dp)
                )
                Text(
                    text = "Filtrar",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
        LazyColumFacturas(stateUI.facturasPorAnio, actions.onFacturaClick)
    }
    if (stateUI.showDialog) {
        AlertDialog(
            onDismissRequest = { actions.dismissDialog() },
            confirmButton = {
                TextButton(onClick = { actions.dismissDialog() }) {
                    Text("Aceptar", color = GreenAplication)
                }
            },
            title = { Text(text = "Aviso") },
            text = { Text(text = "Esta factura no está disponible para su visualización.") }
        )
    }

}

@Composable
fun LazyColumFacturas(
    facturasPorAnio: Map<Int, List<Factura>>,
    onFacturaClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        facturasPorAnio.forEach { (anio, facturas) ->
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(text = "$anio", fontWeight = FontWeight.Bold)
                }
            }
            items(facturas) { factura ->
                ItemList(
                    factura = factura,
                    modifier = Modifier.clickable(
                        indication = LocalIndication.current,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onFacturaClick() }
                )
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp),
                    thickness = DividerDefaults.Thickness,
                    color = DividerDefaults.color
                )
            }
        }
    }
}

@Composable
fun FacturasHeader(onBack: () -> Unit) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.padding(bottom = 18.dp)
        ) {
            IconButton(
                onClick = { onBack() },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.chevron_icon),
                    contentDescription = stringResource(R.string.icon_back_listado_facturas)
                )
            }
            Text(
                text = "Atrás",
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.padding(start = 8.dp),
                color = GreenAplication
            )
        }

        Text(
            text = stringResource(R.string.title_listado_facturas),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "C/ PALMA - ARTA KM 49,5, 4ºA -PINTO- MADRID",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun UltimaFacturaCard(factura: Factura) {
    Card(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(
            width = 1.5.dp,
            color = GreenAplication
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Última factura",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Factura ${factura.tipo}",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                Icon(
                    imageVector = when (factura.tipo) {
                        Tipo.Luz -> Icons.Outlined.Lightbulb
                        Tipo.Gas -> Icons.Default.Whatshot
                    },
                    contentDescription = "",
                    modifier = Modifier
                        .size(34.dp),
                    tint = GreenAplication
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Column {
                Text(
                    text = "${"%.2f".format(factura.valor)} €",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = UtilyClass.toSpanishMediumDate(fecha = factura.fechaInicio) +
                            " - " +
                            UtilyClass.toSpanishMediumDate(fecha = factura.fechaFinal),
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp),
                thickness = DividerDefaults.Thickness,
                color = DividerDefaults.color
            )
            Spacer(modifier = Modifier.weight(1f))
            FacturaStatusBadge(factura.estado)
        }
    }
}

@Composable
fun FacturaStatusBadge(estado: Estado) {
    val (text, color) = when (estado) {
        Estado.Pagado -> "Pagada" to GreenAplication
        Estado.PendientePago -> "Pendiente de pago" to Color.Red
        Estado.Tramite -> "En trámite" to Color.Gray
        Estado.Anulado -> "Anulada" to Color.DarkGray
        Estado.CuotaFija -> "Cuota fija" to Color.Blue
    }
    Box(
        modifier = Modifier
            .background(color.copy(0.1f), RoundedCornerShape(24.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp),
    ) {
        Text(
            text = text,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ListadoFacturasContentPreview() {
    ListadoFacturasContent(
        ListadoFacturasActions(
            onFilter = { },
            onFacturaClick = {},
            dismissDialog = {}
        ),
        stateUI = ListadoFacturasUiState()
    )
}
