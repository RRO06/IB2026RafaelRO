package com.iberdrola.practicas2026.RafaelRO.ui.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// Color gris suave para el skeleton
val SkeletonGray = Color(0xFFE0E0E0)

@Preview(showBackground = true)
@Composable
fun LoadingScreen() {
    Column(
        modifier = Modifier.padding(12.dp)
    ) {
        // Card Skeleton
        SkeletonUltimaFacturaCard()

        Spacer(modifier = Modifier.height(24.dp))

        // Fila de "Historico" y Botón Filtrar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SkeletonPlaceholder(width = 150.dp, height = 24.dp) // Texto Histórico
            SkeletonPlaceholder(width = 80.dp, height = 32.dp, shape = RoundedCornerShape(20.dp)) // Botón
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de facturas cargando
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(5) { // Mostramos 5 elementos de ejemplo
                SkeletonItemList()
                HorizontalDivider(color = SkeletonGray.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
fun SkeletonUltimaFacturaCard() {
    Card(
        modifier = Modifier.height(200.dp).fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, SkeletonGray)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    SkeletonPlaceholder(width = 100.dp, height = 18.dp)
                    Spacer(modifier = Modifier.height(4.dp))
                    SkeletonPlaceholder(width = 80.dp, height = 14.dp)
                }
                SkeletonPlaceholder(width = 34.dp, height = 34.dp, shape = RoundedCornerShape(17.dp))
            }
            Spacer(modifier = Modifier.weight(1f))
            SkeletonPlaceholder(width = 70.dp, height = 24.dp)
            Spacer(modifier = Modifier.height(4.dp))
            SkeletonPlaceholder(width = 180.dp, height = 14.dp)
            Spacer(modifier = Modifier.weight(1f))
            SkeletonPlaceholder(width = 120.dp, height = 24.dp, shape = RoundedCornerShape(24.dp))
        }
    }
}

@Composable
fun SkeletonItemList() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            SkeletonPlaceholder(width = 120.dp, height = 16.dp)
            Spacer(modifier = Modifier.height(8.dp))
            SkeletonPlaceholder(width = 80.dp, height = 12.dp)
        }
        SkeletonPlaceholder(width = 40.dp, height = 20.dp)
    }
}

// Componente base para las cajas grises
@Composable
fun SkeletonPlaceholder(
    width: androidx.compose.ui.unit.Dp,
    height: androidx.compose.ui.unit.Dp,
    shape: RoundedCornerShape = RoundedCornerShape(4.dp)
) {
    Box(
        modifier = Modifier
            .size(width, height)
            .background(SkeletonGray, shape)
    )
}