package com.example.canasta.ui.components.products

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.canasta.data.remote.models.GetCategory
import com.example.canasta.data.remote.models.Product
import com.example.canasta.ui.theme.CanastaTheme
import com.example.canasta.ui.theme.Secondary

@Composable
fun RemoteProductCard(
    product: Product,
    onEditClick: (Product) -> Unit = {},
    onDeleteClick: (Product) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = androidx.compose.ui.graphics.Color(0xFFCED7CE)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Icon(
                imageVector = categoryIcon(product.category?.metadata?.get("icon") ?: "category"),
                contentDescription = product.category?.name ?: "Sin categoría",
                modifier = Modifier.size(40.dp),
                tint = Secondary // usar mismo naranja que FAB
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = product.category?.name ?: "Sin categoría",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Botones de acción directos
            IconButton(onClick = { onEditClick(product) }) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar")
            }
            IconButton(onClick = { onDeleteClick(product) }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar")
            }
        }
    }
}

// Mapeo simple a íconos seguros de Material. Si el nombre no se reconoce, usar Category.
private fun categoryIcon(name: String) = when (name) {
    "shopping_cart" -> Icons.Filled.ShoppingCart
    // Se podrían añadir más mapeos si se confirman los íconos disponibles.
    else -> Icons.Filled.Category
}

@Preview(showBackground = true)
@Composable
fun RemoteProductCardPreview() {
    CanastaTheme {
        RemoteProductCard(
            product = Product(
                id = 1L,
                name = "Aceite de Oliva",
                metadata = mapOf("brand" to "La Española", "size" to "500ml"),
                createdAt = "2023-01-01T00:00:00Z",
                updatedAt = "2023-01-01T00:00:00Z",
                category = GetCategory(
                    id = 1L,
                    name = "Condimentos",
                    metadata = mapOf("icon" to "shopping_cart"),
                    createdAt = "2023-01-01T00:00:00Z",
                    updatedAt = "2023-01-01T00:00:00Z"
                )
            )
        )
    }
}
