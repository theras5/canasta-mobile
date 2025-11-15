package com.example.canasta.ui.components.common

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.canasta.ui.navigation.AppDestination
import com.example.canasta.ui.navigation.Categories
import com.example.canasta.ui.navigation.Settings
import com.example.canasta.ui.theme.Primary
import com.example.canasta.ui.theme.Secondary

/**
 * Barra de navegación inferior
 * @param currentRoute La ruta actual seleccionada
 * @param onNavigate Callback para navegar a una ruta
 */
@Composable
fun BottomBar(
    currentRoute: AppDestination,
    onNavigate: (Any) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Box {
        NavigationBar(
            containerColor = Primary,
            contentColor = Color.White
        ) {
            AppDestination.entries.forEach { destination ->
                NavigationBarItem(
                    selected = currentRoute == destination,
                    onClick = {
                        if (destination == AppDestination.MORE) {
                            // Mostrar el menú desplegable en lugar de navegar
                            showMenu = true
                        } else {
                            onNavigate(destination.route)
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (currentRoute == destination)
                                destination.selectedIcon
                            else
                                destination.unselectedIcon,
                            contentDescription = destination.label
                        )
                    },
                    label = { Text(destination.label) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedIconColor = Color.White.copy(alpha = 0.7f),
                        unselectedTextColor = Color.White.copy(alpha = 0.7f),
                        indicatorColor = Secondary
                    )
                )
            }
        }

        // Menú desplegable para la opción "Más" - Aparece arriba del botón
        Box(
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                offset = DpOffset(x = (-16).dp, y = (-80).dp)
            ) {
                DropdownMenuItem(
                    text = { Text("Configuración") },
                    onClick = {
                        showMenu = false
                        onNavigate(Settings)
                    }
                )
                DropdownMenuItem(
                    text = { Text("Categorías") },
                    onClick = {
                        showMenu = false
                        onNavigate(Categories)
                    }
                )
            }
        }
    }
}

