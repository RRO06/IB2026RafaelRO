package com.iberdrola.practicas2026.RafaelRO.ui.screens.gestion

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.RafaelRO.ui.common.components.IberdrolaTextField
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.IB2026RafaelROTheme

data class ActivarFacturaActions(
    val onEmailChanged: (String) -> Unit = {},
    val onTermsAccepted: (Boolean) -> Unit = {},
    val obfuscateEmail: (String?) -> String = { "" },
    val guardarCambios: (() -> Unit) -> Unit = {},
    val onBack: () -> Unit = {},
    val onNext: (Int) -> Unit = {},
    val onClose: () -> Unit = {},
    val onMoreInfo: (String) -> Unit = {}
)

@Composable
fun ActivarFacturaScreen(
    viewModel: GestionViewModel,
    onBack: () -> Unit,
    onClose: () -> Unit,
    onNext: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val state = viewModel.state
    var infoDialogTitle by remember { mutableStateOf<String?>(null) }

    val actions =
        ActivarFacturaActions(
            onEmailChanged = viewModel::onEmailChanged,
            onTermsAccepted = viewModel::onTermsAccepted,
            obfuscateEmail = viewModel::obfuscateEmail,
            guardarCambios = viewModel::guardarCambiosSinCodigo,
            onBack = onBack,
            onNext = onNext,
            onClose = onClose,
            onMoreInfo = { label ->
                infoDialogTitle = label
            }
        )

    Box(modifier = modifier.fillMaxSize()) {
        ActivarFacturaContent(
            state = state,
            actions = actions
        )

        if (infoDialogTitle != null) {
            AlertDialog(
                onDismissRequest = { infoDialogTitle = null },
                title = { Text(text = infoDialogTitle!!) },
                text = { Text("Aquí se mostraría la información detallada sobre $infoDialogTitle.") },
                confirmButton = {
                    TextButton(onClick = { infoDialogTitle = null }) {
                        Text("Cerrar", color = Color(0xFF006644))
                    }
                }
            )
        }
    }
}

@Composable
fun ActivarFacturaContent(
    state: GestionUiState,
    actions: ActivarFacturaActions,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)) {
            IconButton(
                onClick = actions.onClose,
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color(0xFF006644))
            }

            Text(
                text = "Activa tu factura electrónica",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(Color(0xFFE0E0E0))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .fillMaxHeight()
                    .background(Color(0xFF006644))
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 12.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Email vinculado a tu cuenta:",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = actions.obfuscateEmail(state.contrato?.email),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "¿En qué email deseas recibir tus facturas?",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 18.sp
                ),
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            IberdrolaTextField(
                value = state.emailFormulario,
                onValueChange = actions.onEmailChanged,
                label = { Text("* Email") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                isError = state.emailFormulario.isNotEmpty() && !state.isEmailValido
            )

            if (state.emailFormulario.isNotEmpty() && !state.isEmailValido) {
                Text(
                    "El formato del email no es válido",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.padding(vertical = 10.dp)) {
                Text(
                    text = "Información básica sobre protección de datos",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 18.sp
                    ),
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 5.dp)
                )

                InfoRow(
                    label = "Responsable: Iberdrola Clientes S.A.U.",
                    onMoreInfoClick = { actions.onMoreInfo("Responsable") }
                )

                InfoRow(
                    label = "Finalidad: Gestión de la factura electrónica.",
                    onMoreInfoClick = { actions.onMoreInfo("Finalidad") }
                )

                InfoRow(
                    label = "Derechos: Acceso, rectificación, supresión, limitación del tratamiento, portabilidad de datos u oposición, incluida la oposición a decisiones individuales automatizadas.",
                    onMoreInfoClick = { actions.onMoreInfo("Derechos") }
                )
            }

            Row(verticalAlignment = Alignment.Top) {
                Checkbox(
                    checked = state.terminosAceptados,
                    onCheckedChange = actions.onTermsAccepted,
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF006644),
                        uncheckedColor = Color(0xFF006644)
                    )
                )

                val annotatedTerms = buildAnnotatedString {
                    append("He leído y acepto la Política de privacidad, acepto las ")
                    withLink(
                        LinkAnnotation.Clickable(
                            tag = "condiciones",
                            styles = TextLinkStyles(
                                style = SpanStyle(
                                    color = Color(0xFF006644),
                                    fontWeight = FontWeight.Bold,
                                    textDecoration = TextDecoration.Underline
                                )
                            ),
                            linkInteractionListener = { actions.onMoreInfo("Condiciones Generales") }
                        )
                    ) {
                        append("Condiciones Generales")
                    }
                    append(" y Particulares de la oferta y la suscripción a Factura Electrónica.")
                }

                Text(
                    text = annotatedTerms,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.Black,
                        fontSize = 14.sp
                    ),
                    modifier = Modifier.padding(top = 10.dp, start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 0.5.dp,
                color = Color.LightGray.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = actions.onBack,
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp),
                    border = BorderStroke(1.5.dp, Color(0xFF006644)),
                    shape = RoundedCornerShape(27.dp)
                ) {
                    Text("Anterior", color = Color(0xFF006644), fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { actions.onNext(state.contrato!!.id) },
                    enabled = state.terminosAceptados && state.isEmailValido,
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp),
                    shape = RoundedCornerShape(27.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF006644),
                        disabledContainerColor = Color(0xFFE8F3EF)
                    )
                ) {
                    Text(
                        "Siguiente",
                        fontWeight = FontWeight.Bold,
                        color = if (state.terminosAceptados && state.isEmailValido) Color.White else Color.Gray
                    )
                }
            }
            Spacer(modifier = Modifier.height(36.dp))

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 0.5.dp,
                color = Color.LightGray.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun InfoRow(
    label: String,
    onMoreInfoClick: () -> Unit = {}
) {
    val annotatedString = buildAnnotatedString {
        append(label)
        append(" ")
        withLink(
            LinkAnnotation.Clickable(
                tag = "more_info",
                styles = TextLinkStyles(
                    style = SpanStyle(
                        color = Color(0xFF006644),
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline
                    )
                ),
                linkInteractionListener = { _ -> onMoreInfoClick() }
            )
        ) {
            append("Más info")
        }
    }

    Text(
        text = annotatedString,
        style = MaterialTheme.typography.bodyLarge.copy(
            color = Color.Black,
            fontSize = 14.sp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun ActivarFacturaPreview() {
    IB2026RafaelROTheme {
        ActivarFacturaContent(
            state = GestionUiState(),
            actions = ActivarFacturaActions()
        )
    }
}
