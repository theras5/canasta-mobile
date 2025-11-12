package com.example.canasta.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

/**
 * Objetos serializables para navegación type-safe
 */
@Serializable
object Lists

@Serializable
object Products

@Serializable
object Profile

@Serializable
data class ListDetail(
    val listId: String,
    val listName: String
)

/**
 * Enum que define las rutas principales de navegación de la aplicación
 */
enum class AppDestinations(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val label: String,
    val route: Any
) {
    LISTS(
        selectedIcon = Icons.Outlined.ListAlt,
        unselectedIcon = Icons.Outlined.ListAlt,
        label = "Listas",
        route = Lists
    ),
    PRODUCTS(
        selectedIcon = Icons.Default.ShoppingCart,
        unselectedIcon = Icons.Outlined.ShoppingCart,
        label = "Productos",
        route = Products
    ),
    PROFILE(
        selectedIcon = Icons.Default.Person,
        unselectedIcon = Icons.Outlined.Person,
        label = "Perfil",
        route = Profile
    )
}

