package com.example.canasta.ui.screens.lists

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.canasta.ui.components.common.BottomNavBar
import com.example.canasta.ui.components.lists.EmptyStateListas
import com.example.canasta.ui.components.lists.FabCrearLista
import com.example.canasta.ui.components.lists.TopBarListas
import com.example.canasta.ui.theme.CanastaTheme
import com.example.canasta.ui.theme.Primary
import com.example.canasta.ui.theme.Secondary
import com.example.canasta.ui.theme.Titles

@Composable
fun ListsScreen() {
    androidx.wear.compose.material3.AppScaffold( // Usamos nuestra nueva plantilla reutilizable
        topBar = { TopBarListas() },
        bottomBar = { BottomNavBar() }, // El BottomNavBar ahora vive en `common`
        floatingActionButton = { FabCrearLista() }
    ) { innerPadding ->
        // Solo nos preocupamos por el contenido de ESTA pantalla
        Box(modifier = Modifier.padding(innerPadding)) {
            EmptyStateListas()
        }
    }
}


