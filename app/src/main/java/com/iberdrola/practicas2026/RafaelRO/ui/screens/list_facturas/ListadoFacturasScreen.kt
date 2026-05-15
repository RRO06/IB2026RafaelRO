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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.iberdrola.practicas2026.RafaelRO.R
import com.iberdrola.practicas2026.RafaelRO.domain.model.Factura
import com.iberdrola.practicas2026.RafaelRO.domain.model.Tipo
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.BotonAtras
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.BotonFiltroFocuseado
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.ErrorScreen
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.FacturaStatusBadge
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.FallbackBanner
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.ItemList
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.LoadingScreen
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.UtilyClass
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.Divider
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.GreenAplication
import com.iberdrola.practicas2026.RafaelRO.ui.screens.filt_facturas.FiltUiState

data class ListadoFacturasActions(
    val onFilter: () -> Unit,
    val onFacturaClick: (Int) -> Unit,
    val onRefresh: () -> Unit
)

@Composable
fun ListadoFacturasScreen(
    viewModel: ListadoFacturasViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onFilter: (FiltUiState) -> Unit,
    onFacturaClick: (Int) -> Unit,
    filtState: FiltUiState
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    BackHandler {
        if (lifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
            onBack()
        }
    }

    LaunchedEffect(filtState) {
        viewModel.actualizarInterfaz(filtrosExtra = filtState)
    }

    ListadoFacturasContent(
        stateData = viewModel.stateData,
        stateUI = viewModel.stateUI,
        onBack = {
            if (lifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
                onBack()
            }
        },
        onFilter = {
            if (lifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
                onFilter(viewModel.stateUI.filtros)
            }
        },
        onFilterLuz = { viewModel.onFilterLuz() },
        onFilterGas = { viewModel.onFilterGas() },
        onFacturaClick = { id ->
            if (lifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
                onFacturaClick(id)
            }
        },
        onRefresh = { viewModel.refreshData() },
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
    onFacturaClick: (Int) -> Unit,
    onRefresh: () -> Unit,
    onClearFilters: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberFacturaPagerState(
        isLuzEnabled = stateUI.isLuzEnabled,
        isGasEnabled = stateUI.isGasEnabled,
        filtroTipoActual = stateUI.filtroTipoActual
    )

    FacturaPagerSincronizador(
        pagerState = pagerState,
        isLuzEnabled = stateUI.isLuzEnabled,
        isGasEnabled = stateUI.isGasEnabled,
        filtroTipoActual = stateUI.filtroTipoActual,
        onFilterLuz = onFilterLuz,
        onFilterGas = onFilterGas
    )

    Column(modifier = modifier
        .systemBarsPadding()
        .fillMaxSize()
        .background(Color.White)) {
        FacturasHeader(
            onBack = onBack,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))

        FacturasTabs(
            isLuzEnabled = stateUI.isLuzEnabled,
            isGasEnabled = stateUI.isGasEnabled,
            filtroTipoActual = stateUI.filtroTipoActual,
            onFilterLuz = onFilterLuz,
            onFilterGas = onFilterGas
        )

        if (stateUI.isFallback && stateData is ListadoFacturasState.Success) {
            FallbackBanner(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
        } else {
            Spacer(modifier = Modifier.height(16.dp))
        }

        ListadoFacturasPager(
            pagerState = pagerState,
            stateUI = stateUI,
            stateData = stateData,
            onRefresh = onRefresh,
            onFilter = onFilter,
            onFacturaClick = onFacturaClick,
            onClearFilters = onClearFilters,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun rememberFacturaPagerState(
    isLuzEnabled: Boolean,
    isGasEnabled: Boolean,
    filtroTipoActual: Tipo
): PagerState {
    val pageCount = if (isLuzEnabled && isGasEnabled) 2 else 1
    val initialPage = when {
        isLuzEnabled && isGasEnabled -> if (filtroTipoActual == Tipo.Luz) 0 else 1
        else -> 0
    }
    return rememberPagerState(initialPage = initialPage, pageCount = { pageCount })
}

@Composable
private fun FacturaPagerSincronizador(
    pagerState: PagerState,
    isLuzEnabled: Boolean,
    isGasEnabled: Boolean,
    filtroTipoActual: Tipo,
    onFilterLuz: () -> Unit,
    onFilterGas: () -> Unit
) {
    LaunchedEffect(pagerState.currentPage) {
        if (isLuzEnabled && isGasEnabled) {
            val targetTipo = if (pagerState.currentPage == 0) Tipo.Luz else Tipo.Gas
            if (filtroTipoActual != targetTipo) {
                if (targetTipo == Tipo.Luz) onFilterLuz() else onFilterGas()
            }
        }
    }

    LaunchedEffect(filtroTipoActual) {
        if (isLuzEnabled && isGasEnabled) {
            val targetPage = if (filtroTipoActual == Tipo.Luz) 0 else 1
            if (pagerState.currentPage != targetPage) {
                pagerState.animateScrollToPage(targetPage)
            }
        }
    }
}

@Composable
private fun ListadoFacturasPager(
    pagerState: PagerState,
    stateUI: ListadoFacturasUiState,
    stateData: ListadoFacturasState,
    onRefresh: () -> Unit,
    onFilter: () -> Unit,
    onFacturaClick: (Int) -> Unit,
    onClearFilters: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxWidth(),
        userScrollEnabled = stateUI.isLuzEnabled && stateUI.isGasEnabled
    ) { page ->
        FacturaPagerItem(
            page = page,
            stateUI = stateUI,
            stateData = stateData,
            onRefresh = onRefresh,
            onFilter = onFilter,
            onFacturaClick = onFacturaClick,
            onClearFilters = onClearFilters
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FacturaPagerItem(
    page: Int,
    stateUI: ListadoFacturasUiState,
    stateData: ListadoFacturasState,
    onRefresh: () -> Unit,
    onFilter: () -> Unit,
    onFacturaClick: (Int) -> Unit,
    onClearFilters: (() -> Unit)?
) {
    val typeForPage = when {
        stateUI.isLuzEnabled && stateUI.isGasEnabled -> if (page == 0) Tipo.Luz else Tipo.Gas
        stateUI.isLuzEnabled -> Tipo.Luz
        else -> Tipo.Gas
    }

    val isRefreshing = stateUI.isRefreshing && stateUI.filtroTipoActual == typeForPage
    val state = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        state = state,
        indicator = {
            Indicator(
                isRefreshing = isRefreshing,
                containerColor = Color.White,
                color = GreenAplication,
                modifier = Modifier.align(Alignment.TopCenter),
                state = state
            )
        },

        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        FacturaDataStateWrapper(
            stateData = stateData,
            stateUI = stateUI,
            onFilter = onFilter,
            onFacturaClick = onFacturaClick,
            onRefresh = onRefresh,
            onClearFilters = onClearFilters
        )
    }
}

@Composable
fun FacturasTabs(
    isLuzEnabled: Boolean,
    isGasEnabled: Boolean,
    filtroTipoActual: Tipo,
    onFilterLuz: () -> Unit,
    onFilterGas: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomStart
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(Divider)
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            if (isLuzEnabled) {
                BotonFiltroFocuseado(
                    text = "Luz",
                    onClick = onFilterLuz,
                    modifier = Modifier.padding(start = 4.dp),
                    isSelected = filtroTipoActual == Tipo.Luz
                )
            }
            if (isGasEnabled) {
                BotonFiltroFocuseado(
                    text = "Gas",
                    onClick = onFilterGas,
                    modifier = Modifier.padding(start = 4.dp),
                    isSelected = filtroTipoActual == Tipo.Gas
                )
            }
        }
    }
}

@Composable
fun FacturaDataStateWrapper(
    stateData: ListadoFacturasState,
    stateUI: ListadoFacturasUiState,
    onFilter: () -> Unit,
    onFacturaClick: (Int) -> Unit,
    onRefresh: () -> Unit,
    onClearFilters: (() -> Unit)?
) {
    when (stateData) {
        is ListadoFacturasState.Loading -> LoadingScreen()
        is ListadoFacturasState.Error -> {
            ErrorScreen(
                mensaje = stateData.message,
                type = stateData.type,
                onClearFilters = onClearFilters,
                onModifierFilters = onFilter,
                onRetry = onRefresh
            )
        }

        is ListadoFacturasState.Success -> {
            ListadoFacturasSuccessContent(
                actions = ListadoFacturasActions(
                    onFilter = onFilter,
                    onFacturaClick = onFacturaClick,
                    onRefresh = onRefresh
                ),
                stateUI = stateUI,
                filtrosActivos = onClearFilters != null
            )
        }
    }
}

@Composable
fun ListadoFacturasSuccessContent(
    actions: ListadoFacturasActions,
    stateUI: ListadoFacturasUiState,
    filtrosActivos: Boolean
) {
    val listState = rememberLazyListState()
    var showExtraSpacer by remember { mutableStateOf(false) }

    LaunchedEffect(filtrosActivos, stateUI.filtros, stateUI.filtroTipoActual) {
        if (filtrosActivos && stateUI.ultimaFactura != null) {
            showExtraSpacer = true
            listState.animateScrollToItem(1)
        } else {
            showExtraSpacer = false
        }
    }

    LaunchedEffect(
        remember { derivedStateOf { listState.firstVisibleItemIndex } },
        listState.isScrollInProgress
    ) {
        if (listState.firstVisibleItemIndex == 0 && !listState.isScrollInProgress) {
            showExtraSpacer = false
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        stateUI.ultimaFactura?.let { factura ->
            item(key = "ultima_factura") {
                UltimaFacturaCard(
                    factura = factura,
                    onClick = { actions.onFacturaClick(factura.id) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        item(key = "header_historico") {
            HistoricoSectionHeader(
                onFilter = actions.onFilter,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        stateUI.facturasPorAnio.forEach { (anio, facturas) ->
            item(key = "header_$anio") {
                AnioHeader(
                    anio = anio.toString(),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            items(
                items = facturas,
                key = { it.id }
            ) { factura ->
                FacturaListEntry(
                    factura = factura,
                    onClick = { actions.onFacturaClick(factura.id) }
                )
            }
        }

        if (showExtraSpacer) {
            item {
                Spacer(modifier = Modifier.fillParentMaxHeight())
            }
        }
    }
}

@Composable
private fun HistoricoSectionHeader(onFilter: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Historico de facturas",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Button(
            onClick = onFilter,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = GreenAplication
            ),
            border = BorderStroke(2.dp, GreenAplication)
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
}

@Composable
private fun AnioHeader(anio: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = anio,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun FacturaListEntry(factura: Factura, onClick: () -> Unit) {
    Column {
        ItemList(
            factura = factura,
            onClick = onClick
        )
    }
}

@Composable
fun FacturasHeader(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        BotonAtras(
            onBack = onBack,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = stringResource(R.string.title_listado_facturas),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "C/ PALMA - ARTA KM 49,5, 4ºA -PINTO- MADRID",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun UltimaFacturaCard(factura: Factura, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .height(200.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.5.dp, GreenAplication)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            UltimaFacturaCardHeader(tipo = factura.tipo)
            Spacer(modifier = Modifier.weight(1f))
            UltimaFacturaCardBody(factura = factura)
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
private fun UltimaFacturaCardHeader(tipo: Tipo) {
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
            Spacer(Modifier.padding(vertical = 2.dp))
            Text(
                text = "Factura $tipo",
                style = MaterialTheme.typography.titleSmall
            )
        }
        if (tipo == Tipo.Luz) {
            Icon(
                imageVector = Icons.Outlined.Lightbulb,
                contentDescription = "",
                modifier = Modifier.size(34.dp),
                tint = GreenAplication
            )
        } else {
            Icon(
                painter = painterResource(id = R.drawable.ic_gas_iberdrola),
                contentDescription = "",
                modifier = Modifier.size(34.dp),
                tint = GreenAplication
            )
        }
    }
}

@Composable
private fun UltimaFacturaCardBody(factura: Factura) {
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
        Spacer(Modifier.height(4.dp))
        Text(
            text = UtilyClass.toSpanishMediumDate(fecha = factura.fechaInicio) +
                    " - " +
                    UtilyClass.toSpanishMediumDate(fecha = factura.fechaFinal),
            style = MaterialTheme.typography.titleSmall,
            color = Color.Gray
        )
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
        onRefresh = {},
        onClearFilters = null
    )
}
