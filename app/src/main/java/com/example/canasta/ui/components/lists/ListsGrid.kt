package com.example.canasta.ui.components.lists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Datos de una lista
 */
data class ShoppingList(
    val id: String,
    val name: String,
    val productCount: Int,
    val icon: String,
    val isFavorite: Boolean = false
)

/**
 * Componente que muestra una lista vertical de tarjetas de listas
 *
 * @param lists Lista de listas de compras
 * @param onListClick Callback cuando se hace clic en una lista
 * @param onToggleFavorite Callback cuando se marca/desmarca como favorito
 * @param onDeleteList Callback cuando se elimina una lista
 * @param modifier Modificador opcional
 */
@Composable
fun ListsGrid(
    lists: List<ShoppingList>,
    onListClick: (ShoppingList) -> Unit = {},
    onToggleFavorite: (ShoppingList) -> Unit = {},
    onDeleteList: (ShoppingList) -> Unit = {},
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp), // solo vertical para no achicar ancho
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(lists) { list ->
            ListCard(
                listName = list.name,
                productCount = list.productCount,
                icon = list.icon,
                onClick = { onListClick(list) },
                onDelete = { onDeleteList(list) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListsGridPreview() {
    MaterialTheme {
        val sampleLists = listOf(
            ShoppingList("1", "Casa", 38, "🏠", true),
            ShoppingList("2", "Supermercado", 15, "🛒", false),
            ShoppingList("3", "Farmacia", 7, "💊", true),
            ShoppingList("4", "Ferretería", 12, "🔨", false),
            ShoppingList("5", "Verdulería", 20, "🥕", false)
        )

        ListsGrid(lists = sampleLists)
    }
}
