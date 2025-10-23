package com.example.canasta.ui.components.lists

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.canasta.ui.theme.Secondary

@Composable
fun FabCrearLista() {
    FloatingActionButton(
        onClick = { /* TODO: Lógica para crear una nueva lista */ },
        containerColor = Secondary,
        contentColor = Color.White
    ) {
        Icon(Icons.Filled.Add, "Crear nueva lista")
    }
}