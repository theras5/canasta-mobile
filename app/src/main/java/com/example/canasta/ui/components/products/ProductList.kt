package com.example.canasta.ui.components.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.canasta.data.model.Product
import com.example.canasta.ui.theme.CanastaTheme

@Composable
fun ProductList(products: List<Product>) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = products,
            key = { product -> product.id }
        ) { product ->
            ProductCard(product = product)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductListPreview() {
    CanastaTheme {
        val mockProducts = listOf(
            Product(1, "Aceite", "Condimentos"),
            Product(2, "Agua mineral", "Bebidas"),
            Product(3, "Banana", "Frutas")
        )
        ProductList(products = mockProducts)
    }
}