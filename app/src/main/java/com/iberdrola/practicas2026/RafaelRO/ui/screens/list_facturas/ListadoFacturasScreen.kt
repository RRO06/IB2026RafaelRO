package com.iberdrola.practicas2026.RafaelRO.ui.screens.list_facturas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.Lightbulb
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
import androidx.compose.runtime.Composable
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
import com.iberdrola.practicas2026.RafaelRO.domain.model.Factura
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.BotonFiltroFocuseado
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.ErrorScreen
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.ItemList
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.LoadingScreen
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.UtilyClass
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.Divider
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.GreenAplication

data class ListadoFacturasActions(
    val onBack: () -> Unit,
    val onFilterLuz: () -> Unit,
    val onFilterGas: () -> Unit,
    val onFilter: () -> Unit
)

@Composable
fun ListadoFacturasScreen(viewModel: ListadoFacturasViewModel) {
    when (viewModel.stateData) {
        is ListadoFacturasState.Error -> ErrorScreen()
        is ListadoFacturasState.Loading -> LoadingScreen()
        is ListadoFacturasState.Success -> ListadoFacturasContent(
            actions = ListadoFacturasActions(
                onBack = { },
                onFilterLuz = { },
                onFilterGas = { },
                onFilter = { }
            ),
            stateUI = viewModel.stateUI
        )
    }
}

@Composable
fun ListadoFacturasContent(
    actions: ListadoFacturasActions,
    stateUI: ListadoFacturasUiState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        FacturasHeader(onBack = actions.onBack)
        Spacer(modifier = Modifier.height(24.dp))

        Row{
            BotonFiltroFocuseado(
                text = "Luz",
                onClick = { actions.onFilterLuz() },
                Modifier,
                isSelected = stateUI.luz
            )
            BotonFiltroFocuseado(
                text = "Gas",
                onClick = { actions.onFilterGas() },
                Modifier,
                isSelected = stateUI.gas
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(Divider)
        )
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
                    onClick = { actions.onFilter },
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
            LazyColumFacturas(stateUI.facturasPorAnio)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyColumFacturas( facturasPorAnio: Map<Int, List<Factura>>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ){
        facturasPorAnio.forEach { (anio, facturas) ->
            stickyHeader {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(vertical = 8.dp)
                ) {
                    Text(text = "Año $anio", color = Color.Gray, fontWeight = FontWeight.Bold)
                }
            }
            items(facturas){ factura ->
                ItemList(factura = factura)
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
                    imageVector = Icons.Outlined.Lightbulb,
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
fun FacturaStatusBadge(isPending: Boolean) {
    val color = if (isPending) Color.Red else GreenAplication
    Box(
        modifier = Modifier
            .background(color.copy(0.1f), RoundedCornerShape(24.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp),
    ) {
        Text(
            text = if (isPending) "Pendiente de pago" else "Pagada",
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
            onBack = { },
            onFilterLuz = { },
            onFilterGas = { },
            onFilter = { }
        ),
        stateUI = ListadoFacturasUiState()
    )
}

