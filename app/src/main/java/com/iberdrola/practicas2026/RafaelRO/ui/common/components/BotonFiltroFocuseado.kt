package com.iberdrola.practicas2026.RafaelRO.ui.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.CustomTypography
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.GreenAplication
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.GreenButton
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.LightGreen

@Composable
fun BotonFiltroFocuseado(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier,
    isSelected: Boolean
) {
    // Definimos el color del texto basado en si está seleccionado o no
    val contentColor = if (isSelected) Color.Black else Color(0xFF595959)

    Column(
        modifier = modifier.wrapContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextButton(
            modifier = Modifier.height(35.dp),
            onClick = onClick,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.textButtonColors(
                contentColor = contentColor
            )
        ) {
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontFamily = CustomTypography
            )
        }
        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(5.dp) // Grosor coincidente con la línea divisoria
                    .background(GreenAplication)
            )
        } else {
            // Espaciador para mantener la estructura pero sin color
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(3.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BotonFiltroFocuseadoPreview() {
    Column {
        BotonFiltroFocuseado("Luz", {}, Modifier, true)
        BotonFiltroFocuseado("Gas", {}, Modifier, false)
    }
}
