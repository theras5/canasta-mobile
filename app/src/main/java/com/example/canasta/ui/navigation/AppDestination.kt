package com.example.canasta.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.canasta.R
import kotlinx.serialization.Serializable

/**
 * Destinos serializables para navegación type-safe.
 * Se usan objetos @Serializable como "route" para poder codificarlos en argumentos.
 */
@Serializable
object Splash

@Serializable
object Login

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
    @StringRes val labelRes: Int,
    val route: Any // Mantengo Any para permitir objetos o data classes serializables.
) {
    LISTS(
        selectedIcon = Icons.Outlined.ListAlt,
        unselectedIcon = Icons.Outlined.ListAlt,
        labelRes = R.string.nav_lists,
        route = Lists
    ),
    PRODUCTS(
        selectedIcon = Icons.Filled.ShoppingCart,
        unselectedIcon = Icons.Outlined.ShoppingCart,
        labelRes = R.string.nav_products,
        route = Products
    ),
    PROFILE(
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        labelRes = R.string.nav_profile,
        route = Profile
    ),
    MORE(
        selectedIcon = Icons.Filled.Menu,
        unselectedIcon = Icons.Outlined.Menu,
        labelRes = R.string.nav_more,
        route = Settings // Default route, pero se manejará con menú desplegable
    );
}
