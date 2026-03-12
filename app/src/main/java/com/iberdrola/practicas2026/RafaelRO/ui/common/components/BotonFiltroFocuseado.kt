package com.iberdrola.practicas2026.RafaelRO.ui.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iberdrola.practicas2026.RafaelRO.ui.common.theme.GreenButton

@Composable
fun BotonFiltroFocuseado(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier,
    isSelected: Boolean
) {

    Column(
        modifier = modifier.wrapContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextButton(
            modifier = modifier.height(35.dp),
            onClick = onClick,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = text,
                fontWeight = FontWeight.Bold
            )
        }
        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(5.dp)
                    .background(GreenButton)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BotonFiltroFocuseadoPreview() {
    BotonFiltroFocuseado("hola", {}, Modifier, true)
}
