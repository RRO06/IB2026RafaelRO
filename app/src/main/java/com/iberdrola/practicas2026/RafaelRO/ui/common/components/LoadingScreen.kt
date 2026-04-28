package com.iberdrola.practicas2026.RafaelRO.ui.common.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.GreenAplication

val SkeletonGray = Color(0xFFE0E4E3)

@Preview(showBackground = true)
@Composable
fun LoadingScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        // 1. Skeleton de la Tarjeta de última factura
        item {
            SkeletonFacturaCard()
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 2. Cabecera del histórico y botón filtrar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SkeletonPlaceholder(width = 160.dp, height = 24.dp)
                SkeletonPlaceholder(width = 90.dp, height = 36.dp, shape = RoundedCornerShape(20.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. Skeleton de grupos por año y lista
        item {
            Box(modifier = Modifier.padding(vertical = 8.dp)) {
                SkeletonPlaceholder(width = 45.dp, height = 20.dp)
            }
        }

        items(5) {
            SkeletonListItem()
        }
    }
}

@Composable
fun SkeletonFacturaCard() {
    Card(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.5.dp, GreenAplication)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    SkeletonPlaceholder(width = 110.dp, height = 20.dp)
                    Spacer(modifier = Modifier.height(4.dp))
                    SkeletonPlaceholder(width = 80.dp, height = 16.dp)
                }
                SkeletonPlaceholder(width = 34.dp, height = 34.dp)
            }
            Spacer(modifier = Modifier.weight(1f))
            Column {
                SkeletonPlaceholder(width = 100.dp, height = 30.dp)
                Spacer(modifier = Modifier.height(4.dp))
                SkeletonPlaceholder(width = 200.dp, height = 16.dp)
            }
            Spacer(modifier = Modifier.weight(1f))
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth().height(1.dp),
                color = DividerDefaults.color
            )
            Spacer(modifier = Modifier.weight(1f))
            SkeletonPlaceholder(width = 95.dp, height = 24.dp, shape = RoundedCornerShape(11.dp))
        }
    }
}

@Composable
fun SkeletonListItem() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Lado izquierdo: InfoFactura
            Column {
                SkeletonPlaceholder(width = 150.dp, height = 18.dp)
                Spacer(modifier = Modifier.height(4.dp))
                SkeletonPlaceholder(width = 100.dp, height = 16.dp)
                Spacer(modifier = Modifier.height(8.dp))
                SkeletonPlaceholder(width = 90.dp, height = 22.dp, shape = RoundedCornerShape(11.dp))
            }

            // Lado derecho: ImporteFactura
            Row(verticalAlignment = Alignment.CenterVertically) {
                SkeletonPlaceholder(width = 65.dp, height = 20.dp)
                Spacer(modifier = Modifier.width(8.dp))
                SkeletonPlaceholder(width = 34.dp, height = 34.dp)
            }
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth().height(1.dp),
            color = DividerDefaults.color
        )
    }
}

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
