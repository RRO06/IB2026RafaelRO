package com.iberdrola.practicas2026.RafaelRO.ui.screens.home_facturas

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.OpinionBottomSheet
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.GreenAplication
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.PistachioGreen

data class HomeActions(
    val onNavigateToFacturas: () -> Unit = {},
    val onModoNubeChanged: (Boolean) -> Unit = {},
    val onOpinionDada: () -> Unit = {},
    val onRecordarMasTarde: () -> Unit = {},
    val onDismissSheet: () -> Unit = {},
    val onNavigateToFacturaElectronica: () -> Unit = {},
    val onNavigateToPerfil: () -> Unit = {},
    val onForceCrash: () -> Unit = {},
    val registrarClickFacturas: () -> Unit = {}
)

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToFacturas: () -> Unit,
    onNavigateToFacturaElectronica: () -> Unit,
    onNavigateToPerfil: () -> Unit,
    modifier: Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState = viewModel.stateUI

    LaunchedEffect(Unit) {
        viewModel.logClick("visualizacion_pantalla", "home")
    }

    LaunchedEffect(uiState.showBottomSheet) {
        if (uiState.showBottomSheet) {
            viewModel.logClick("visualizacion_bottom_sheet_opinion", "home")
        }
    }

    val actions = HomeActions(
        onNavigateToFacturas = {
            if (lifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
                viewModel.registrarClickFacturas()
                onNavigateToFacturas()
            }
        },
        onModoNubeChanged = viewModel::onModoNubeChanged,
        onOpinionDada = viewModel::onOpinionDada,
        onRecordarMasTarde = viewModel::onRecordarMasTarde,
        onDismissSheet = viewModel::onDismissSheet,
        onNavigateToFacturaElectronica = {
            if (lifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
                viewModel.onNavigateToFacturaElectronica()
                onNavigateToFacturaElectronica()
            }
        },
        onNavigateToPerfil = {
            if (lifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
                viewModel.logClick("editar_perfil", "home")
                onNavigateToPerfil()
            }
        },
        onForceCrash = {
            viewModel.logClick("boton_forzar_error", "home")
            throw RuntimeException("Test Crash desde el botón de la Home")
        },
        registrarClickFacturas = viewModel::registrarClickFacturas
    )

    HomeContent(
        uiState = uiState,
        actions = actions,
        modifier = modifier
    )
}

@Composable
fun HomeContent(
    uiState: HomeUiState,
    actions: HomeActions,
    modifier: Modifier = Modifier
) {
    if (uiState.showBottomSheet) {
        OpinionBottomSheet(
            onOpinionSelected = actions.onOpinionDada,
            onRecordarMasTarde = actions.onRecordarMasTarde,
            onDismiss = actions.onDismissSheet
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "¡Bienvenido de nuevo!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(0.5f))

            UserCard(
                state = uiState,
                onEditClick = actions.onNavigateToPerfil
            )

            Spacer(modifier = Modifier.weight(1f))

            FacturaElectronicaCard(
                onClick = actions.onNavigateToFacturaElectronica
            )

            Spacer(modifier = Modifier.weight(0.8f))

            SelectorDeOrigen(
                esNube = uiState.esModoNube,
                onOptionSelected = actions.onModoNubeChanged
            )

            AnimatedContent(
                targetState = uiState.esModoNube,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(400)) + slideInVertically { it / 3 })
                        .togetherWith(fadeOut(animationSpec = tween(400)) + slideOutVertically { -it / 3 })
                }
            ) { targetEsNube ->
                Row(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (targetEsNube) Icons.Default.Cloud else Icons.Default.Dns,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = GreenAplication.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (targetEsNube) "Conectado a servicios vía Mockoon" else "Usando datos locales del dispositivo",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                this@Column.AnimatedVisibility(
                    visible = uiState.showThankYouMessage,
                    enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut()
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        color = Color.White,
                        border = BorderStroke(1.5.dp, GreenAplication),
                        shadowElevation = 8.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = GreenAplication,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "¡Gracias por tu respuesta!",
                                color = GreenAplication,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {
                    actions.onNavigateToFacturas()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenAplication),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Ver mis facturas", color = Color.White, fontWeight = FontWeight.Bold)
            }

            DebugCrashCard(
                onClick = actions.onForceCrash
            )
        }
    }
}

@Composable
fun DebugCrashCard(
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.padding(top = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.BugReport,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color.Gray
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Probar reporte de errores",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun FacturaElectronicaCard(onClick: () -> Unit) {
    val cardShape = RoundedCornerShape(16.dp)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clip(cardShape)
            .clickable { onClick() },
        shape = cardShape,
        colors = CardDefaults.cardColors(
            containerColor = PistachioGreen
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(GreenAplication.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = GreenAplication,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Factura electrónica",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E5148)
                )
                Spacer(modifier = Modifier.padding(vertical = 2.dp))
                Text(
                    text = "Gestiona tus contratos y cuida el medio ambiente.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color.LightGray
            )
        }
    }
}

@Composable
fun UserCard(
    state: HomeUiState,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.5.dp, GreenAplication)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.foto.isNotEmpty()) {
                    AsyncImage(
                        model = state.foto,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .border(1.dp, GreenAplication.copy(alpha = 0.3f), CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(GreenAplication.copy(alpha = 0.1f), CircleShape)
                            .border(1.dp, GreenAplication.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = GreenAplication
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = state.nombreUsuario, fontWeight = FontWeight.Bold)
                    Text(
                        text = "Usuario: ${state.idUsuario}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar perfil",
                        tint = GreenAplication,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color.LightGray.copy(0.5f)
            )
            Text(
                text = "Último acceso: ${state.ultimaConexion}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun SelectorDeOrigen(esNube: Boolean, onOptionSelected: (Boolean) -> Unit) {
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(28.dp),
        color = PistachioGreen,
        border = BorderStroke(1.dp, GreenAplication.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SelectorOption(
                text = "Local",
                isSelected = !esNube,
                onClick = { onOptionSelected(false) },
                modifier = Modifier.weight(1f)
            )
            SelectorOption(
                text = "Nube",
                isSelected = esNube,
                onClick = { onOptionSelected(true) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SelectorOption(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animBgColor by animateColorAsState(
        targetValue = if (isSelected) GreenAplication else Color.Transparent,
        animationSpec = tween(durationMillis = 300),
        label = "bgColor"
    )
    val animTextColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else GreenAplication,
        animationSpec = tween(durationMillis = 300),
        label = "textColor"
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .padding(4.dp)
            .clip(CircleShape)
            .background(animBgColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = animTextColor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeContent(
        uiState = HomeUiState(
            showThankYouMessage = true
        ),
        actions = HomeActions()
    )
}