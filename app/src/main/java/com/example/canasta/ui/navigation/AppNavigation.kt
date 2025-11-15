// file: app/src/main/java/com/example/canasta/ui/navigation/AppNavigation.kt

package com.example.canasta.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavOptions
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController // <-- Add this import
import androidx.navigation.navOptions
import com.example.canasta.ui.components.common.BottomBar
import com.example.canasta.ui.screens.products.ProductsScreen
import com.example.canasta.ui.screens.profile.ProfileScreen

/**
 * Punto de entrada principal de la navegación de la aplicación
 * Gestiona el NavController y el estado de la navegación
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable // <-- Add this annotation to make it a Composable function
fun AppNavigation() { // <-- Wrap the logic in a function
    val navController = rememberNavController() // <-- Call it inside the Composable
    var currentRoute by rememberSaveable { mutableStateOf(AppDestination.LISTS) }

    // Observar la ruta actual para saber si mostrar el BottomBar
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Determinar si debemos mostrar el BottomBar
    val showBottomBar = currentDestination?.route?.let { route ->
        // Mostrar BottomBar solo en las pantallas principales (no en Splash ni ListDetail)
        route.contains("Lists") || route.contains("Products") || route.contains("Profile") ||
                route.contains("Settings") || route.contains("Categories")
    } ?: false

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomBar(
                    currentRoute = currentRoute
                ) { route ->
                    // Actualizar la ruta actual basándonos en el destino
                    currentRoute = when(route) {
                        is Lists -> AppDestination.LISTS
                        is Products -> AppDestination.PRODUCTS
                        is Profile -> AppDestination.PROFILE
                        is Settings, is Categories -> AppDestination.MORE
                        else -> AppDestination.LISTS
                    }

                    // Opciones de navegación para limpiar el back stack cuando volvemos a Lists
                    var navOptions: NavOptions? = null
                    if (route == Lists) {
                        navOptions = navOptions {
                            popUpTo<Lists> { inclusive = true }
                        }
                    }

                    // Navegar a la ruta seleccionada
                    navController.navigate(
                        route = route,
                        navOptions = navOptions
                    )
                }
            }
        }
    ) { innerPadding ->
        AppNavGraph(navController = navController, contentPadding = innerPadding)
    }
}