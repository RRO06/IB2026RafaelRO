package com.iberdrola.practicas2026.RafaelRO.ui.screens.filt_facturas

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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.RafaelRO.domain.model.Estado
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.BotonAtras
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.FilterDatePickerDialog
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class FiltUiActions(
    val onDateFromClick: () -> Unit = {},
    val onDateToClick: () -> Unit = {},
    val onDateFromSelected: (LocalDate?) -> Unit = {}, // Nueva
    val onDateToSelected: (LocalDate?) -> Unit = {},   // Nueva
    val onDismissDate: () -> Unit = {},                // Nueva
    val onPriceChange: (ClosedFloatingPointRange<Float>) -> Unit = {},
    val onStateToggle: (String) -> Unit = {},
    val onApply: (FiltUiState) -> Unit = {},
    val onClear: () -> Unit = {},
    val onBack: () -> Unit = {}
)

@Composable
fun FilterScreen(
    viewModel: FilterViewModel,
    onBack: () -> Unit,
    onApply: (FiltUiState?) -> Unit,
    modifier: Modifier = Modifier
) {
    val state = viewModel.uiState

    val actions = FiltUiActions(
        onDateFromClick = viewModel::onDateFromClick,
        onDateToClick = viewModel::onDateToClick,
        onDateFromSelected = viewModel::onDateFromSelected,
        onDateToSelected = viewModel::onDateToSelected,
        onDismissDate = viewModel::dismissDatePickers,
        onPriceChange = viewModel::onPriceRangeChanged,
        onStateToggle = viewModel::onStateToggle,
        onApply = onApply,
        onClear = viewModel::onClear,
        onBack = onBack
    )

    FilterContent(
        state = state,
        actions = actions,
        modifier = modifier
    )
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
        // Cabecera: Botón Atrás (Centralizado)
        BotonAtras(
            onBack = actions.onBack,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        Text(
            text = "Filtrar",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // Gestión de Errores de Fecha
        state.dateError?.let { error ->
            Text(
                text = error,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // SECCIÓN: FECHA
        Text(text = "Por fecha", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
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

        // SECCIÓN: IMPORTE
        Text(text = "Por un importe", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        PriceRangeSelector(priceRangeStart = state.priceRangeStart, priceRangeEnd = state.priceRangeEnd, actions.onPriceChange)

        Spacer(modifier = Modifier.height(24.dp))

        // SECCIÓN: ESTADO
        Text(text = "Por estado", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Estado.entries.forEach { estado ->
            StateRow(
                label = estado.name,
                isSelected = state.selectedStates.contains(estado.name),
                onToggle = { actions.onStateToggle(estado.name) }
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(32.dp))

        // BOTONES DE ACCIÓN
        Button(
            onClick = { actions.onApply(state) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D5D4E)),
            shape = RoundedCornerShape(28.dp),
            // Opcional: deshabilitar si hay error de fecha
            enabled = state.dateError == null
        ) {
            Text("Aplicar filtros", color = Color.White, fontSize = 16.sp)
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
    priceRangeEnd : Float,
    onRangeChange: (ClosedFloatingPointRange<Float>) -> Unit
) {
    val activeColor = Color(0xFF005944) // Verde oscuro de la imagen
    val inactiveColor = Color.LightGray.copy(alpha = 0.3f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Indicador de rango (Burbuja superior)
        Surface(
            color = Color(0xFFE0ECE9),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Text(
                text = "${priceRangeStart.toInt()} € - ${priceRangeEnd.toInt()} €",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF002E1C)
            )
        }

        // Slider de rango personalizado
        RangeSlider(
            value = priceRangeStart..priceRangeEnd,
            onValueChange = onRangeChange,
            valueRange = 0f..500f,
            startThumb = {
                Box(
                    modifier = Modifier
                        .size(24.dp) // Círculos un poco más grandes para mayor presencia
                        .background(activeColor, CircleShape)
                )
            },
            endThumb = {
                Box(
                    modifier = Modifier
                        .size(24.dp)
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
                        .height(5.dp) // Línea un poco más gruesa como en la imagen
                ) {
                    val width = size.width
                    val height = size.height
                    val centerY = height / 2

                    // 1. Línea inactiva (el fondo gris continuo)
                    drawLine(
                        color = inactiveColor,
                        start = Offset(0f, centerY),
                        end = Offset(width, centerY),
                        strokeWidth = height,
                        cap = StrokeCap.Round
                    )

                    // 2. Línea activa (el tramo verde)
                    // Usamos StrokeCap.Butt para que los bordes sean rectos y
                    // se "claven" perfectamente en el centro de los círculos
                    drawLine(
                        color = activeColor,
                        start = Offset(width * fractionStart, centerY),
                        end = Offset(width * fractionEnd, centerY),
                        strokeWidth = height,
                        cap = StrokeCap.Butt
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        // Etiquetas de los extremos
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${priceRangeStart.toInt()} €",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${priceRangeEnd.toInt()} €",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


@Composable
fun StateRow(label: String, isSelected: Boolean, onToggle: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF005944))
        )
        Text(label)
    }
}

@Composable
fun ReadOnlyTextField(value: String, label: String, modifier: Modifier, onClick: () -> Unit) {
    // Usamos Box para capturar el clic de forma global sobre el componente
    Box(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            enabled = false, // <--- Importante: evita que el teclado intente subir
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            // Aplicamos colores para que no parezca "deshabilitado" (gris)
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null
                )
            }
        )
        // Capa invisible encima que detecta el clic
        Box(
            modifier = Modifier
                .matchParentSize()
                .alpha(0f)
                .clickable { onClick() }
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
