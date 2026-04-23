package com.iberdrola.practicas2026.RafaelRO.ui.screens.filt_facturas

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.RafaelRO.domain.model.Estado
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.BotonAtras
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.FilterDatePickerDialog
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.LightRed
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.RedAplication
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class FiltUiActions(
    val onDateFromClick: () -> Unit = {},
    val onDateToClick: () -> Unit = {},
    val onDateFromSelected: (LocalDate?) -> Unit = {},
    val onDateToSelected: (LocalDate?) -> Unit = {},
    val onDismissDate: () -> Unit = {},
    val onPriceChange: (ClosedFloatingPointRange<Float>) -> Unit = {},
    val onStateToggle: (String) -> Unit = {},
    val onApply: (FiltUiState) -> Unit = {},
    val onClear: () -> Unit = {},
    val onClearError: () -> Unit = {},
    val onBack: () -> Unit = {}
)

@Composable
fun FilterScreen(
    viewModel: FilterViewModel,
    initialFilters: FiltUiState? = null,
    onBack: () -> Unit,
    onApply: (FiltUiState?) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(initialFilters) {
        initialFilters?.let { viewModel.initFilters(it) }
    }

    val state by viewModel.uiState.collectAsState()

    val actions = FiltUiActions(
        onDateFromClick = viewModel::onDateFromClick,
        onDateToClick = viewModel::onDateToClick,
        onDateFromSelected = viewModel::onDateFromSelected,
        onDateToSelected = viewModel::onDateToSelected,
        onDismissDate = viewModel::dismissDatePickers,
        onPriceChange = viewModel::onPriceRangeChanged,
        onStateToggle = viewModel::onStateToggle,
        onApply = onApply,
        onClear = {
            viewModel.onClear()
            onClear()
        },
        onClearError = viewModel::clearError,
        onBack = onBack
    )

    FilterContent(
        state = state,
        actions = actions,
        modifier = modifier
    )
}

@Composable
fun ErrorToast(message: String, modifier: Modifier = Modifier) {
    Surface(
        color = LightRed,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = RedAplication,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = message,
                color = RedAplication,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
fun FilterContent(
    state: FiltUiState,
    actions: FiltUiActions,
    modifier: Modifier = Modifier
) {
    if (state.showDatePickerFrom) {
        FilterDatePickerDialog(
            onDateSelected = actions.onDateFromSelected,
            onDismiss = actions.onDismissDate
        )
    }

    if (state.showDatePickerTo) {
        FilterDatePickerDialog(
            onDateSelected = actions.onDateToSelected,
            onDismiss = actions.onDismissDate
        )
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        BotonAtras(
            onBack = actions.onBack,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        Text(
            text = "Filtrar",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        AnimatedVisibility(
            visible = state.dateError != null,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            ErrorToast(
                message = state.dateError ?: "",
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Por fecha", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val dateFromStr =
                state.dateFrom?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: ""
            val dateToStr = state.dateTo?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: ""

            ReadOnlyTextField(
                value = dateFromStr,
                label = "Desde",
                modifier = Modifier.weight(1f),
                onClick = actions.onDateFromClick
            )
            ReadOnlyTextField(
                value = dateToStr,
                label = "Hasta",
                modifier = Modifier.weight(1f),
                onClick = actions.onDateToClick
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Por un importe", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        PriceRangeSelector(
            priceRangeStart = state.priceRangeStart,
            priceRangeEnd = state.priceRangeEnd,
            minPrice = state.minPrice,
            maxPrice = state.maxPrice,
            onRangeChange = actions.onPriceChange
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Por estado", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Estado.entries.forEach { estado ->
            StateRow(
                label = estado,
                isSelected = state.selectedStates.contains(estado.name),
                onToggle = { actions.onStateToggle(estado.name) }
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { actions.onApply(state) },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .padding(horizontal = 30.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D5D4E)),
            shape = RoundedCornerShape(28.dp),
            enabled = state.dateError == null
        ) {
            Text(
                "Aplicar filtros",
                color = Color.White,
                fontSize = 14.sp
            )
        }

        TextButton(
            onClick = actions.onClear,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Borrar filtros",
                color = Color(0xFF005944),
                textDecoration = TextDecoration.Underline
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceRangeSelector(
    priceRangeStart: Float,
    priceRangeEnd: Float,
    minPrice: Float,
    maxPrice: Float,
    onRangeChange: (ClosedFloatingPointRange<Float>) -> Unit
) {
    val activeColor = Color(0xFF005944)
    val inactiveColor = Color.LightGray.copy(alpha = 0.3f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Surface(
            color = Color(0xFFE0ECE9),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Text(
                text = "${priceRangeStart.toInt()} € - ${priceRangeEnd.toInt()} €",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF002E1C)
            )
        }

        RangeSlider(
            value = priceRangeStart..priceRangeEnd,
            onValueChange = onRangeChange,
            valueRange = minPrice..maxPrice,
            startThumb = {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(activeColor, CircleShape)
                )
            },
            endThumb = {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(activeColor, CircleShape)
                )
            },
            track = { rangeSliderState ->
                val start = rangeSliderState.valueRange.start
                val end = rangeSliderState.valueRange.endInclusive
                val fractionStart = (rangeSliderState.activeRangeStart - start) / (end - start)
                val fractionEnd = (rangeSliderState.activeRangeEnd - start) / (end - start)

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                ) {
                    val width = size.width
                    val height = size.height
                    val centerY = height / 2

                    drawLine(
                        color = inactiveColor,
                        start = Offset(0f, centerY),
                        end = Offset(width, centerY),
                        strokeWidth = height,
                        cap = StrokeCap.Round
                    )

                    drawLine(
                        color = activeColor,
                        start = Offset(width * fractionStart, centerY),
                        end = Offset(width * fractionEnd, centerY),
                        strokeWidth = height,
                        cap = StrokeCap.Butt
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${minPrice.toInt()} €",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${maxPrice.toInt()} €",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


@Composable
fun StateRow(label: Estado, isSelected: Boolean, onToggle: () -> Unit) {
    val text = when (label) {
        Estado.Pagado -> "Pagadas"
        Estado.PendientePago -> "Pendiente de Pago"
        Estado.Anulado -> "Anuladas"
        Estado.CuotaFija -> "Cuota Fija"
        Estado.Tramite -> "En trámite de cobro"
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onToggle() }
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFF005944),
                uncheckedColor = Color(0xFF005944), // Color del borde cuando NO está marcado
                checkmarkColor = Color.White
            )
        )
        Text(text)
    }
}

@Composable
fun ReadOnlyTextField(value: String, label: String, modifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.Gray)) {
                        append("* ")
                    }
                    if (value.isEmpty()) {
                        append(label)
                    } else {
                        append(value)
                    }
                },
                color = if (value.isEmpty()) Color.Gray else Color.Black,
                style = MaterialTheme.typography.bodyMedium
            )
            Icon(
                imageVector = Icons.Outlined.CalendarToday,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Color.Gray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FiltFacturasContentPreview() {
    FilterContent(
        state = FiltUiState(),
        actions = FiltUiActions()
    )
}
