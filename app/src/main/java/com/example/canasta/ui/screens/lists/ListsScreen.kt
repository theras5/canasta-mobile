package com.example.canasta.ui.screens.lists

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.canasta.ui.components.common.BottomNavBar
import com.example.canasta.ui.components.lists.CreateListModal
import com.example.canasta.ui.components.lists.EmptyStateListas
import com.example.canasta.ui.components.lists.TopBarListas
import com.example.canasta.ui.theme.Secondary

@Preview(showBackground = true)
@Composable
fun ListsScreen() {
    var showCreateModal by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = { TopBarListas() },
        bottomBar = { BottomNavBar() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateModal = true },
                containerColor = Secondary,
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Add, "Crear nueva lista")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            EmptyStateListas()
        }
    }
    
    // Modal para crear lista
    if (showCreateModal) {
        CreateListModal(
            onDismiss = { showCreateModal = false },
            onCreateList = { name, image ->
                // TODO: Aquí agregarías la lógica para guardar la nueva lista
                println("Nueva lista creada: $name con imagen: $image")
            }
        )
    }
}
