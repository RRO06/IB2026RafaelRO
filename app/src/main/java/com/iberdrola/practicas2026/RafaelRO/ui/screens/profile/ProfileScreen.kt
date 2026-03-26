package com.iberdrola.practicas2026.RafaelRO.ui.screens.Perfil

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.CustomOutlinedTextField
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.Divider
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.GreenAplication
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.GreenButton
import com.iberdrola.practicas2026.RafaelRO.ui.screens.profile.PerfilUiState

data class PerfilActions(
    val onNombreChanged: (String) -> Unit = {},
    val onEmailChanged: (String) -> Unit = {},
    val onTelefonoChanged: (String) -> Unit = {},
    val onSavePerfil: () -> Unit = {},
    val onNavigateBack: () -> Unit = {},
    val onFotoChanged: (Uri?) -> Unit = {}
)

@Composable
fun PerfilScreen(
    viewModel: PerfilViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState = viewModel.stateUI

    // Vinculamos las acciones del ViewModel con la data class
    val actions = PerfilActions(
        onNombreChanged = viewModel::onNombreChanged,
        onEmailChanged = viewModel::onEmailChanged,
        onTelefonoChanged = viewModel::onTelefonoChanged,
        onFotoChanged = viewModel::onFotoChanged,
        onSavePerfil = {
            viewModel.saveChanges(onNavigateBack)
        },
        onNavigateBack = onNavigateBack
    )

    PerfilContent(
        uiState = uiState,
        actions = actions
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilContent(
    uiState: PerfilUiState,
    actions: PerfilActions
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> actions.onFotoChanged(uri) }

    Scaffold(
        containerColor = Color(0xFFF7F9F8), // Un fondo grisáceo muy claro casi blanco
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", fontWeight = FontWeight.Bold, color = GreenButton) },
                navigationIcon = {
                    IconButton(onClick = actions.onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = GreenButton
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cabecera con degradado suave GreenAplication
            ProfileHeader(
                fotoUri = uiState.fotoUri,
                onFotoClick = {
                    launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
            )

            Text(
                text = "INFORMACIÓN PERSONAL",
                style = MaterialTheme.typography.labelLarge,
                color = GreenAplication,
                letterSpacing = 1.sp
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = BorderStroke(1.dp, Divider),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CustomOutlinedTextField(
                        value = uiState.nombreUsuario,
                        onValueChange = actions.onNombreChanged,
                        label = "Nombre Completo",
                        icon = Icons.Default.Person
                    )
                    CustomOutlinedTextField(
                        value = uiState.email,
                        onValueChange = actions.onEmailChanged,
                        label = "Correo electrónico",
                        icon = Icons.Default.Email,
                        error = uiState.emailError,
                        keyboardType = KeyboardType.Email
                    )

                    // Campo Teléfono con validación de UtilyClass
                    CustomOutlinedTextField(
                        value = uiState.telefono,
                        onValueChange = actions.onTelefonoChanged,
                        label = "Teléfono",
                        icon = Icons.Default.Phone,
                        error = uiState.telefonoError,
                        keyboardType = KeyboardType.Phone
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Botón estilo Iberdrola (GreenButton)
                Button(
                    onClick = actions.onSavePerfil,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp), // Más redondeado, estilo moderno
                    enabled = !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenButton,
                        disabledContainerColor = Color.LightGray
                    )
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text(
                            "GUARDAR CAMBIOS",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun ProfileHeader(
    fotoUri: String?,
    onFotoClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(GreenAplication.copy(alpha = 0.1f), Color.Transparent)
                )
            )
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            if (!fotoUri.isNullOrEmpty()) {
                AsyncImage(
                    model = fotoUri,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(
                            3.dp,
                            GreenButton,
                            CircleShape
                        ) // Usamos GreenButton de Iberdrola
                        .clickable { onFotoClick() },
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(3.dp, Divider, CircleShape)
                        .clickable { onFotoClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = GreenAplication
                    )
                }
            }

            // Botón de edición flotante
            Surface(
                shape = CircleShape,
                color = GreenButton, // Color corporativo
                modifier = Modifier
                    .size(36.dp)
                    .offset(x = (-4).dp, y = (-4).dp),
                shadowElevation = 4.dp
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Toca para cambiar la foto",
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PerfilContentPreview() {
    PerfilContent(
        uiState = PerfilUiState(),
        actions = PerfilActions()
    )
}