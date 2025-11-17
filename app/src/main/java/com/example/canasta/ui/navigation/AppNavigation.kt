// file: app/src/main/java/com/example/canasta/ui/navigation/AppNavigation.kt

package com.example.canasta.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.example.canasta.ui.components.common.BottomBar
import com.example.canasta.ui.components.common.NetworkStatusBanner
import com.example.canasta.ui.components.common.SideNavBar
import com.example.canasta.ui.theme.Background
import com.example.canasta.utils.DeviceUtils
import com.example.canasta.utils.NetworkConnectivityManager

/**
 * Punto de entrada principal de la navegación de la aplicación
 * Gestiona el NavController y el estado de la navegación
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable // <-- Add this annotation to make it a Composable function
fun AppNavigation() { // <-- Wrap the logic in a function
    val context = LocalContext.current
    val navController = rememberNavController() // <-- Call it inside the Composable
    var currentRoute by rememberSaveable { mutableStateOf(AppDestination.LISTS) }
    val isTablet = DeviceUtils.isTablet()

    // Observar el estado de conectividad
    val isConnected by NetworkConnectivityManager.observeConnectivity(context).collectAsState(initial = true)

    // Observar la ruta actual para saber si mostrar el BottomBar
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    // Actualizar currentRoute cuando cambia el back stack (incluyendo al presionar back)
    LaunchedEffect(currentDestination?.route) {
        currentRoute = when {
            currentDestination?.route?.contains("Lists") == true -> AppDestination.LISTS
            currentDestination?.route?.contains("Products") == true -> AppDestination.PRODUCTS
            currentDestination?.route?.contains("Profile") == true -> AppDestination.PROFILE
            currentDestination?.route?.contains("Settings") == true -> AppDestination.MORE
            currentDestination?.route?.contains("Categories") == true -> AppDestination.MORE
            else -> currentRoute // Mantener el valor actual si no coincide
        }
    }


    // Determinar si debemos mostrar el BottomBar
    val showBottomBar = currentDestination?.route?.let { route ->
        // Mostrar BottomBar solo en las pantallas principales (no en Splash ni ListDetail)
        route.contains("Lists") || route.contains("Products") || route.contains("Profile") ||
                route.contains("Settings") || route.contains("Categories")
    } ?: false

    val navigationHandler: (Any) -> Unit = { route ->
        // Actualizar la ruta actual basándonos en el destino
        currentRoute = when(route) {
            is Lists -> AppDestination.LISTS
            is Products -> AppDestination.PRODUCTS
            is Profile -> AppDestination.PROFILE
            is Settings -> AppDestination.MORE
            is Categories -> AppDestination.MORE
            else -> AppDestination.LISTS
        }

        // Opciones de navegación para limpiar el back stack hasta la primera aparición
        // de la pantalla seleccionada. Esto evita acumular duplicados en el stack.
        val navOptions = navOptions {
            when(route) {
                is Lists -> popUpTo<Lists> { inclusive = true }
                is Products -> popUpTo<Products> { inclusive = true }
                is Profile -> popUpTo<Profile> { inclusive = true }
                is Settings -> popUpTo<Settings> { inclusive = true }
                is Categories -> popUpTo<Categories> { inclusive = true }
                else -> {}
            }
        }

        // Navegar a la ruta seleccionada
        navController.navigate(
            route = route,
            navOptions = navOptions
        )
    }

    if (isTablet) {
        // Modo Tablet: Barra lateral a la izquierda
        Column(modifier = Modifier.fillMaxSize()) {
            NetworkStatusBanner(isConnected = isConnected)
            Row(modifier = Modifier.fillMaxSize()) {
                if (showBottomBar) {
                    SideNavBar(
                        currentRoute = currentRoute,
                        onNavigate = navigationHandler
                    )
                }
                AppNavGraph(navController = navController, contentPadding = PaddingValues(0.dp))
            }
        }
    } else {
        // Modo Móvil: Barra inferior
        Scaffold(
            containerColor = Background,
            topBar = {
                NetworkStatusBanner(isConnected = isConnected)
            },
            bottomBar = {
                if (showBottomBar) {
                    BottomBar(
                        currentRoute = currentRoute,
                        onNavigate = navigationHandler
                    )
                }
            }
        ) { innerPadding ->
            AppNavGraph(navController = navController, contentPadding = innerPadding)
        }
    }
}