package com.example.canasta.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavOptions
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.example.canasta.ui.components.common.BottomBar
import com.example.canasta.ui.theme.CanastaTheme

/**
 * Punto de entrada principal de la navegación de la aplicación
 * Gestiona el NavController y el estado de la navegación
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var currentRoute by rememberSaveable { mutableStateOf(AppDestinations.LISTS) }

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
                    currentRoute = currentRoute
                ) { route ->
                    // Actualizar la ruta actual basándonos en el destino
                    currentRoute = when(route) {
                        is Lists -> AppDestinations.LISTS
                        is Products -> AppDestinations.PRODUCTS
                        is Profile -> AppDestinations.PROFILE
                        else -> AppDestinations.LISTS
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
    ) {
        AppNavGraph(navController = navController)
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true, device = "spec:width=411dp,height=891dp")
fun AppNavigationPreview() {
    CanastaTheme {
        AppNavigation()
    }
}
