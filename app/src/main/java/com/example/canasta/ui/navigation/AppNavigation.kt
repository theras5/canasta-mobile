package com.example.canasta.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.canasta.ui.screens.lists.ListsScreen
// import com.example.canasta.ui.screens.products.ProductsScreen <-- Las crearás luego
// import com.example.canasta.ui.screens.profile.ProfileScreen <-- Las crearás luego

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "lists") {
        composable("lists") {
            ListsScreen(/* pasaremos el navController aquí luego */)
        }
        composable("products") {
            // ProductsScreen(navController)
        }
        composable("profile") {
            // ProfileScreen(navController)
        }
    }
}
