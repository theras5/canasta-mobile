package com.example.canasta.ui.components.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.canasta.ui.theme.Primary
import com.example.canasta.ui.theme.Secondary

@Composable
fun BottomNavBar() {
    NavigationBar(
        containerColor = Primary,
        contentColor = Color.White
    ) {
        NavigationBarItem(
            selected = true, // La pantalla de listas está seleccionada
            onClick = { /* No hace nada por ahora */ },
            icon = { Icon(Icons.Outlined.ListAlt, contentDescription = "Listas") },
            label = { Text("Listas") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Primary, // El color del icono dentro del indicador
                selectedTextColor = Color.White,
                unselectedIconColor = Color.White.copy(alpha = 0.7f),
                unselectedTextColor = Color.White.copy(alpha = 0.7f),
                indicatorColor = Secondary // El color del fondo del item seleccionado
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* TODO: Navegar a Productos */ },
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Productos") },
            label = { Text("Productos") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* TODO: Navegar a Perfil */ },
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
            label = { Text("Perfil") }
        )
    }
}