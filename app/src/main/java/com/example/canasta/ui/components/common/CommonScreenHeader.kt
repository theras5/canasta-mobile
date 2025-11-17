package com.example.canasta.ui.components.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.canasta.ui.theme.Titles

/**
 * Encabezado simple reutilizable para pantallas de listado.
 * Muestra un título con estilo consistente y opcionalmente un fondo hueso.
 */
@Composable
fun CommonScreenHeader(
    title: String,
    modifier: Modifier = Modifier,
    useBackground: Boolean = false
) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        color = Titles,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 16.dp)
    )
}
