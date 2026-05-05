package com.iberdrola.practicas2026.RafaelRO.ui.common.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iberdrola.practicas2026.RafaelRO.R
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.GreenAplication

val SkeletonGray = Color(0xFFE0E4E3)

@Preview(showBackground = true)
@Composable
fun LoadingScreen() {
    // Animación de pulso para el efecto shimmer
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp), // Coincide con el padding del listado real
        verticalArrangement = Arrangement.Top
    ) {
        // 1. Skeleton de la Tarjeta de última factura (Calco exacto)
        item {
            Box(Modifier.alpha(alpha)) {
                SkeletonFacturaCard()
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 2. Cabecera del histórico y botón filtrar
        item {
            Row(
                modifier = Modifier.fillMaxWidth().alpha(alpha),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SkeletonPlaceholder(width = 160.dp, height = 24.dp)
                SkeletonPlaceholder(width = 100.dp, height = 36.dp, shape = RoundedCornerShape(20.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. Skeleton de grupo por año
        item {
            Box(modifier = Modifier.padding(vertical = 8.dp).alpha(alpha)) {
                SkeletonPlaceholder(width = 50.dp, height = 20.dp)
            }
        }

        // 4. Lista de facturas (simulamos 5 entradas)
        items(5) {
            Box(Modifier.alpha(alpha)) {
                SkeletonListItem()
            }
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
        border = BorderStroke(1.5.dp, GreenAplication) // Calco del borde verde original
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header de la card
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
            // Cuerpo de la card
            Column {
                SkeletonPlaceholder(width = 100.dp, height = 32.dp)
                Spacer(modifier = Modifier.height(6.dp))
                SkeletonPlaceholder(width = 200.dp, height = 16.dp)
            }
            Spacer(modifier = Modifier.weight(1f))
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth().height(1.dp),
                color = Color.LightGray.copy(alpha = 0.3f)
            )
            Spacer(modifier = Modifier.weight(1f))
            // Badge de estado
            SkeletonPlaceholder(width = 110.dp, height = 24.dp, shape = RoundedCornerShape(11.dp))
        }
    }
}

@Composable
fun SkeletonListItem() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp), // Calco exacto de ItemList
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Izquierda: InfoFactura
            Column {
                SkeletonPlaceholder(width = 145.dp, height = 18.dp)
                Spacer(modifier = Modifier.height(6.dp))
                SkeletonPlaceholder(width = 100.dp, height = 16.dp)
                Spacer(modifier = Modifier.height(10.dp))
                SkeletonPlaceholder(width = 90.dp, height = 22.dp, shape = RoundedCornerShape(11.dp))
            }

            // Derecha: Importe + Flecha
            Row(verticalAlignment = Alignment.CenterVertically) {
                SkeletonPlaceholder(width = 65.dp, height = 20.dp)
                // Usamos el icono real con color de skeleton para el calco perfecto
                Icon(
                    painter = painterResource(R.drawable.chevron_right),
                    contentDescription = null,
                    tint = SkeletonGray,
                    modifier = Modifier
                        .size(34.dp)
                        .offset(x = 12.dp) // Calco exacto del ajuste de offset en ItemList
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp), // Calco exacto del divisor en ItemList
            color = Color.LightGray.copy(alpha = 0.3f)
        )
    }
}

@Composable
fun SkeletonPlaceholder(
    width: androidx.compose.ui.unit.Dp,
    height: androidx.compose.ui.unit.Dp,
    shape: androidx.compose.foundation.shape.CornerBasedShape = RoundedCornerShape(4.dp)
) {
    Box(
        modifier = Modifier
            .size(width, height)
            .background(SkeletonGray, shape)
    )
}
