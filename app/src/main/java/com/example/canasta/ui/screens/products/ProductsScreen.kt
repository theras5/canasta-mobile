package com.example.canasta.ui.screens.products

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.canasta.data.model.Product
import com.example.canasta.ui.components.common.AppScaffold
import com.example.canasta.ui.components.common.CategoryChips
import com.example.canasta.ui.components.common.ProductsSearchBar
import com.example.canasta.ui.components.products.ProductList
import com.example.canasta.ui.theme.CanastaTheme

@Composable
fun ProductsScreen() {
    AppScaffold(
        topBar = {
            Text(
                "Productos",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            ProductsSearchBar()
            CategoryChips(listOf("Lácteos", "Carnes", "Verduras", "Frutas", "Panadería"))
            val mockProducts = listOf(
                Product(1, "Aceite", "Condimentos"),
                Product(2, "Agua mineral", "Bebidas"),
                Product(3, "Banana", "Frutas")
            )
            ProductList(products = mockProducts)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductsScreenPreview() {
    CanastaTheme {
        ProductsScreen()
    }
}