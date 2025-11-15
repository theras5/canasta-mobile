package com.example.canasta.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.canasta.ui.screens.listdetail.ListDetailScreen
import com.example.canasta.ui.screens.listdetail.ListDetailViewModel
import com.example.canasta.ui.screens.lists.ListsScreen
import com.example.canasta.ui.screens.products.ProductsScreen
import com.example.canasta.ui.screens.profile.ProfileScreen

/**
 * Grafo de navegación de la aplicación
 * Define las rutas y las pantallas correspondientes
 */
@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = RoutePatterns.LISTS,
        modifier = modifier
    ) {
        composable(RoutePatterns.LISTS) {
            ListsScreen(
                onNavigateToListDetail = { list ->
                    navController.navigate(
                        RoutePatterns.listDetail(list.id, list.name)
                    )
                }
            )
        }
        composable(RoutePatterns.PRODUCTS) {
            ProductsScreen()
        }
        composable(RoutePatterns.PROFILE) {
            ProfileScreen()
        }
        composable(
            route = RoutePatterns.LIST_DETAIL,
            arguments = listOf(
                navArgument("listId") { type = NavType.StringType },
                navArgument("listName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val listId = backStackEntry.arguments?.getString("listId") ?: ""
            val rawListName = backStackEntry.arguments?.getString("listName") ?: ""
            val listName = java.net.URLDecoder.decode(rawListName, Charsets.UTF_8.name())

            val detailViewModel: ListDetailViewModel = viewModel()
            LaunchedEffect(listId) {
                detailViewModel.loadList(listId, listName)
            }

            ListDetailScreen(
                viewModel = detailViewModel,
                onBackClick = { navController.popBackStack() },
                onShareClick = { /* TODO compartir lista */ }
            )
        }
    }
}

