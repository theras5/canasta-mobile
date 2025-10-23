package com.example.canasta.ui.components.lists

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.canasta.ui.theme.Titles

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarListas() {
    TopAppBar(
        title = {
            Text(
                "Listas",
                style = MaterialTheme.typography.headlineLarge,
                color = Titles
            )
        },
        actions = {
            IconButton(onClick = { /* TODO: Lógica para ver el historial */ }) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "Historial de listas",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}