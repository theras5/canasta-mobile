package com.example.canasta.ui.screens.lists

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.canasta.ui.components.common.BottomNavBar
import com.example.canasta.ui.components.lists.EmptyStateListas
import com.example.canasta.ui.components.lists.FabCrearLista
import com.example.canasta.ui.components.lists.TopBarListas

@Composable
fun ListsScreen() {
    Scaffold(
        topBar = { TopBarListas() },
        bottomBar = { BottomNavBar() },
        floatingActionButton = { FabCrearLista() }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            EmptyStateListas()
        }
    }
}
