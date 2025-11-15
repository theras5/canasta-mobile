package com.example.canasta.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

/**
 * Destinos serializables para navegación type-safe.
 * Se usan objetos @Serializable como "route" para poder codificarlos en argumentos.
 */
@Serializable
object Lists

@Serializable
object Products

@Serializable
object Profile

@Serializable
object Settings

@Serializable
object Categories

@Serializable
data class ListDetail(
    val listId: String,
    val listName: String
)

/**
 * Enum que describe los destinos principales con sus íconos y etiquetas.
 */
enum class AppDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val label: String,
    val route: Any // Mantengo Any para permitir objetos o data classes serializables.
) {
    LISTS(
        selectedIcon = Icons.Outlined.ListAlt, // No hay versión filled para ListAlt en baseline
        unselectedIcon = Icons.Outlined.ListAlt,
        label = "Listas",
        route = Lists
    ),
    PRODUCTS(
        selectedIcon = Icons.Filled.ShoppingCart,
        unselectedIcon = Icons.Outlined.ShoppingCart,
        label = "Productos",
        route = Products
    ),
    PROFILE(
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        label = "Perfil",
        route = Profile
    ),
    MORE(
        selectedIcon = Icons.Filled.Menu,
        unselectedIcon = Icons.Outlined.Menu,
        label = "Más",
        route = Settings // Default route, pero se manejará con menú desplegable
    );
}
