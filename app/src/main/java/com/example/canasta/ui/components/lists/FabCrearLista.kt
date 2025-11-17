package com.example.canasta.ui.components.lists

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import com.example.canasta.ui.components.common.CommonFab

@Composable
fun FabCrearLista(onClick: () -> Unit = {}) {
    CommonFab(
        icon = Icons.Filled.Add,
        contentDescription = "Crear nueva lista",
        onClick = onClick
    )
}