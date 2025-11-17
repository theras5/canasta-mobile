package com.example.canasta.ui.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.canasta.R
import com.example.canasta.ui.navigation.AppDestination
import com.example.canasta.ui.navigation.Categories
import com.example.canasta.ui.navigation.Settings
import com.example.canasta.ui.theme.Primary
import com.example.canasta.ui.theme.Secondary

/**
 * Barra de navegación lateral para tablets
 * @param currentRoute La ruta actual seleccionada
 * @param onNavigate Callback para navegar a una ruta
 */
@Composable
fun SideNavBar(
    currentRoute: AppDestination,
    onNavigate: (Any) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var moreButtonYPosition by remember { mutableStateOf(0f) }
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(80.dp)
    ) {
        NavigationRail(
            modifier = Modifier.fillMaxHeight(),
            containerColor = Primary,
            contentColor = Color.White
        ) {
            // Spacer superior para dar margen a los iconos
            Spacer(modifier = Modifier.height(16.dp))

            // Column para apilar los iconos arriba con espaciado
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AppDestination.entries.forEach { destination ->
                    val itemModifier = if (destination == AppDestination.MORE) {
                        Modifier.onGloballyPositioned { coordinates ->
                            moreButtonYPosition = coordinates.positionInRoot().y
                        }
                    } else {
                        Modifier
                    }

                    NavigationRailItem(
                        modifier = itemModifier,
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
                                contentDescription = stringResource(destination.labelRes)
                            )
                        },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            unselectedIconColor = Color.White.copy(alpha = 0.7f),
                            unselectedTextColor = Color.White.copy(alpha = 0.7f),
                            indicatorColor = Secondary
                        )
                    )

                    // Espaciado entre iconos
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        // Menú desplegable para la opción "Más" - Aparece a la derecha del rail
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
            offset = with(density) {
                DpOffset(
                    x = 80.dp,
                    y = moreButtonYPosition.toDp() + 80.dp
                )
            }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.settings)) },
                onClick = {
                    showMenu = false
                    onNavigate(Settings)
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.categories)) },
                onClick = {
                    showMenu = false
                    onNavigate(Categories)
                }
            )
        }
    }
}

