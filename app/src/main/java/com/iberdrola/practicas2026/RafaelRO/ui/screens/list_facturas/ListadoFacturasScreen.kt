package com.iberdrola.practicas2026.RafaelRO.ui.screens.list_facturas

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.RafaelRO.R
import com.iberdrola.practicas2026.RafaelRO.domain.model.Factura
import com.iberdrola.practicas2026.RafaelRO.domain.model.Tipo
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.BotonAtras
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.BotonFiltroFocuseado
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.ErrorScreen
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.FacturaStatusBadge
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
    BackHandler {
        onBack()
    }

    LaunchedEffect(filtState) {
        viewModel.actualizarInterfaz(filtrosExtra = filtState)
    }

    ListadoFacturasContent(
        stateData = viewModel.stateData,
        stateUI = viewModel.stateUI,
        onBack = onBack,
        onFilter = onFilter,
        onFilterLuz = { viewModel.onFilterLuz() },
        onFilterGas = { viewModel.onFilterGas() },
        onFacturaClick = { viewModel.onFacturaClick() },
        onDismissDialog = { viewModel.dismissDialog() },
        onClearFilters = if (viewModel.tieneFiltrosActivos()) {
            { viewModel.limpiarFiltros() }
        } else null,
        modifier = modifier
    )
}

@Composable
fun ListadoFacturasContent(
    stateData: ListadoFacturasState,
    stateUI: ListadoFacturasUiState,
    onBack: () -> Unit,
    onFilter: () -> Unit,
    onFilterLuz: () -> Unit,
    onFilterGas: () -> Unit,
    onFacturaClick: () -> Unit,
    onDismissDialog: () -> Unit,
    onClearFilters: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        FacturasHeader(onBack = onBack)
        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomStart
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(Divider)
            )

            Row {
                BotonFiltroFocuseado(
                    text = "Luz",
                    onClick = onFilterLuz,
                    modifier = Modifier,
                    isSelected = stateUI.filtroTipoActual == Tipo.Luz
                )
                BotonFiltroFocuseado(
                    text = "Gas",
                    onClick = onFilterGas,
                    modifier = Modifier,
                    isSelected = stateUI.filtroTipoActual == Tipo.Gas
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (stateData) {
            is ListadoFacturasState.Error -> ErrorScreen(
                mensaje = stateData.message,
                onClearFilters = onClearFilters
            )

            is ListadoFacturasState.Loading -> LoadingScreen()
            is ListadoFacturasState.Success -> ListadoFacturasSuccessContent(
                actions = ListadoFacturasActions(
                    onFilter = onFilter,
                    onFacturaClick = onFacturaClick,
                    dismissDialog = onDismissDialog
                ),
                stateUI = stateUI
            )
        }
    }
}

@Composable
fun ListadoFacturasSuccessContent(
    actions: ListadoFacturasActions,
    stateUI: ListadoFacturasUiState,
) {
    Column(
        modifier = Modifier.padding(12.dp)
    ) {
        stateUI.ultimaFactura?.let {
            UltimaFacturaCard(
                factura = it,
                onClick = actions.onFacturaClick
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

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
                    modifier = Modifier.padding(start = 4.dp),
                    fontWeight = FontWeight.Bold
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
            item(key = "header_$anio") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "$anio",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            items(
                items = facturas,
                key = { it.fechaInicio.toString() + it.valor.toString() } // Clave única para evitar saltos
            ) { factura ->
                ItemList(
                    factura = factura,
                    modifier = Modifier.clickable(
                        onClick = onFacturaClick
                    )
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
        BotonAtras(
            onBack = onBack,
            modifier = Modifier.padding(bottom = 18.dp)
        )

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
fun UltimaFacturaCard(factura: Factura, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "%.2f".format(factura.valor),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = " €",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
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

@Preview(showBackground = true)
@Composable
fun ListadoFacturasContentPreview() {
    ListadoFacturasContent(
        stateData = ListadoFacturasState.Success(emptyList()),
        stateUI = ListadoFacturasUiState(),
        onBack = {},
        onFilter = {},
        onFilterLuz = {},
        onFilterGas = {},
        onFacturaClick = {},
        onDismissDialog = {},
        onClearFilters = {}
    )
}
