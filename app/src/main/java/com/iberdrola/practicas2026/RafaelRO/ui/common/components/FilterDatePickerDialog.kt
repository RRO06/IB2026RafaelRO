package com.iberdrola.practicas2026.RafaelRO.ui.common.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDatePickerDialog(
    onDateSelected: (LocalDate?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    val greenIberdrola = Color(0xFF005944)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val date = datePickerState.selectedDateMillis?.let {
                    Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                }
                onDateSelected(date)
            }) { Text("Aceptar", color = greenIberdrola) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = Color.Gray) }
        },
        colors = DatePickerDefaults.colors(
            containerColor = Color.White
        )
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = Color.White,
                titleContentColor = Color.Black,
                headlineContentColor = Color.Black,
                weekdayContentColor = Color.Gray,
                subheadContentColor = Color.Black,
                yearContentColor = Color.Black,
                currentYearContentColor = greenIberdrola,
                selectedYearContentColor = Color.White,
                selectedYearContainerColor = greenIberdrola,
                dayContentColor = Color.Black,
                selectedDayContainerColor = greenIberdrola,
                selectedDayContentColor = Color.White,
                todayContentColor = greenIberdrola,
                todayDateBorderColor = greenIberdrola,
                navigationContentColor = greenIberdrola
            )
        )
    }
}
