package com.example.canasta.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

<<<<<<< Updated upstream
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
=======
object RoutePatterns {
    const val LISTS = "Lists"
    const val PRODUCTS = "Products"
    const val PROFILE = "Profile"
    const val LIST_DETAIL = "list_detail/{listId}/{listName}" // pattern
    fun listDetail(listId: String, listName: String): String =
        "list_detail/$listId/${java.net.URLEncoder.encode(listName, Charsets.UTF_8.name())}"
}
>>>>>>> Stashed changes

/**
 * Enum que define las rutas principales de navegación de la aplicación
 */
enum class AppDestinations(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val label: String,
<<<<<<< Updated upstream
    val route: Any
=======
    val route: String
>>>>>>> Stashed changes
) {
    LISTS(
        selectedIcon = Icons.Outlined.ListAlt,
        unselectedIcon = Icons.Outlined.ListAlt,
        label = "Listas",
        route = RoutePatterns.LISTS
    ),
    PRODUCTS(
        selectedIcon = Icons.Default.ShoppingCart,
        unselectedIcon = Icons.Outlined.ShoppingCart,
        label = "Productos",
        route = RoutePatterns.PRODUCTS
    ),
    PROFILE(
        selectedIcon = Icons.Default.Person,
        unselectedIcon = Icons.Outlined.Person,
        label = "Perfil",
<<<<<<< Updated upstream
        route = Profile
    )
=======
        route = RoutePatterns.PROFILE
    );
>>>>>>> Stashed changes
}

