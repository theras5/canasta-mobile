package com.example.canasta.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.canasta.ui.screens.auth.LoginScreen
import com.example.canasta.ui.screens.listdetail.ListDetailScreen
import com.example.canasta.ui.screens.lists.ListsScreen
import com.example.canasta.ui.screens.products.ProductsScreen
import com.example.canasta.ui.screens.profile.ProfileScreen

/**
 * Grafo de navegación de la aplicación
 * Define las rutas y las pantallas correspondientes
 */
@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Login
    ) {
        composable<Login> {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(Lists) {
                        popUpTo(Login) { inclusive = true }
                    }
                }
            )
        }
        composable<Lists> {
            ListsScreen(
                onNavigateToListDetail = { list ->
                    navController.navigate(
                        ListDetail(
                            listId = list.id,
                            listName = list.name
                        )
                    )
                }
            )
        }
        composable<Products> {
            ProductsScreen()
        }
        composable<Profile> {
            ProfileScreen()
        }
        composable<ListDetail> { backStackEntry ->
            val listDetail = backStackEntry.toRoute<ListDetail>()
            ListDetailScreen(
                listName = listDetail.listName,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
