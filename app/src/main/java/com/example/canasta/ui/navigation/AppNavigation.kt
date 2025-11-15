package com.example.canasta.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
<<<<<<< Updated upstream
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavOptions
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.example.canasta.ui.components.common.BottomBar
import com.example.canasta.ui.theme.CanastaTheme
=======
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.canasta.ui.components.common.BottomBar
>>>>>>> Stashed changes

/**
 * Punto de entrada principal de la navegación de la aplicación
 * Gestiona el NavController y el estado de la navegación
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
<<<<<<< Updated upstream
    var currentRoute by rememberSaveable { mutableStateOf(AppDestinations.LISTS) }
=======
    // Usamos un estado explícito sin delegación para evitar problemas de setValue
    val currentRouteState = rememberSaveable { mutableStateOf(AppDestination.LISTS) }
>>>>>>> Stashed changes

    // Observar la ruta actual para saber si mostrar el BottomBar
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Determinar si debemos mostrar el BottomBar
    val showBottomBar = currentDestination?.route?.let { route ->
        // Mostrar BottomBar solo en las pantallas principales
        route.contains("Lists") || route.contains("Products") || route.contains("Profile")
    } ?: true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomBar(
<<<<<<< Updated upstream
                    currentRoute = currentRoute
                ) { route ->
                    // Actualizar la ruta actual basándonos en el destino
                    currentRoute = when(route) {
                        is Lists -> AppDestinations.LISTS
                        is Products -> AppDestinations.PRODUCTS
                        is Profile -> AppDestinations.PROFILE
                        else -> AppDestinations.LISTS
                    }
=======
                    currentRoute = currentRouteState.value
                ) { routeString ->
                    // Evitar navegar si ya estamos en esa ruta
                    val currentRoute = currentRouteState.value.route
                    if (routeString == currentRoute) return@BottomBar
>>>>>>> Stashed changes

                    // Buscar destino válido por su route
                    val destination = AppDestination.entries.firstOrNull { it.route == routeString }
                        ?: return@BottomBar // ruta desconocida, no navegamos

                    currentRouteState.value = destination

                    navController.navigate(routeString) {
                        // Volver al destino raíz cuando navegamos a LISTS
                        if (routeString == RoutePatterns.LISTS) {
                            popUpTo(RoutePatterns.LISTS) { inclusive = true }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }
    ) { innerPadding ->
        AppNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true, device = "spec:width=411dp,height=891dp")
fun AppNavigationPreview() {
    CanastaTheme {
        AppNavigation()
    }
}
