package com.iberdrola.practicas2026.RafaelRO.ui.screens.filt_facturas

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

data class FilterUiState(
    val dateFrom: String = "",
    val dateTo: String = "",
    val priceRange: ClosedFloatingPointRange<Float> = 15f..151f,
    val selectedStates: Set<String> = emptySet()
)
@Composable
fun FiltFacturasScreen(){
    FiltFacturasContent()
}

@Composable
fun FiltFacturasContent(){

}

@Preview(showBackground = true)
@Composable
fun FiltFacturasContentPreview(){
    FiltFacturasContent()
}