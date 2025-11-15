package com.example.canasta.ui.components.common

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.canasta.ui.navigation.AppDestinations
import com.example.canasta.ui.theme.Primary
import com.example.canasta.ui.theme.Secondary

/**
 * Barra de navegación inferior
 * @param currentRoute La ruta actual seleccionada
 * @param onNavigate Callback para navegar a una ruta (String de la ruta)
 */
@Composable
fun BottomBar(
<<<<<<< Updated upstream
    currentRoute: AppDestinations,
    onNavigate: (Any) -> Unit
=======
    currentRoute: AppDestination,
    onNavigate: (String) -> Unit
>>>>>>> Stashed changes
) {
    NavigationBar(
        containerColor = Primary,
        contentColor = Color.White
    ) {
        AppDestinations.entries.forEach { destination ->
            NavigationBarItem(
                selected = currentRoute == destination,
                onClick = { onNavigate(destination.route) },
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
}
