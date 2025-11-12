package com.example.canasta.ui.components.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.canasta.ui.theme.Primary

/**
 * Datos de un producto en una lista
 */
data class ListProduct(
    val id: String,
    val name: String,
    val description: String = "",
    val isChecked: Boolean = false
)

/**
 * Tarjeta de producto en una lista con checkbox
 *
 * @param product Producto a mostrar
 * @param onCheckedChange Callback cuando cambia el estado del checkbox
 * @param onDelete Callback cuando se elimina el producto
 * @param modifier Modificador opcional
 */
@Composable
fun ProductItemCard(
    product: ListProduct,
    onCheckedChange: (Boolean) -> Unit = {},
    onDelete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (product.isChecked) Primary else Color(0xFFF5F5F5)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Checkbox
                Checkbox(
                    checked = product.isChecked,
                    onCheckedChange = onCheckedChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color.White,
                        uncheckedColor = Color.Gray,
                        checkmarkColor = Primary
                    ),
                    modifier = Modifier.size(24.dp)
                )

                // Información del producto
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp, end = 8.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = product.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (product.isChecked) Color.White else Color.Black,
                        textDecoration = if (product.isChecked) TextDecoration.LineThrough else TextDecoration.None
                    )
                    if (product.description.isNotEmpty()) {
                        Text(
                            text = product.description,
                            fontSize = 14.sp,
                            color = if (product.isChecked) Color.White.copy(alpha = 0.8f) else Color.Gray,
                            textDecoration = if (product.isChecked) TextDecoration.LineThrough else TextDecoration.None
                        )
                    }
                }
            }

            // Menú de opciones
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
            ) {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Más opciones",
                        tint = if (product.isChecked) Color.White else Color.Gray
                    )
                }

                // Menú desplegable
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Eliminar producto") },
                        onClick = {
                            onDelete()
                            showMenu = false
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductItemCardPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProductItemCard(
                product = ListProduct(
                    id = "1",
                    name = "Leche",
                    description = "2L, 2%",
                    isChecked = false
                )
            )

            ProductItemCard(
                product = ListProduct(
                    id = "2",
                    name = "Queso",
                    description = "Cheddar",
                    isChecked = true
                )
            )

            ProductItemCard(
                product = ListProduct(
                    id = "3",
                    name = "Yogurt",
                    description = "2L, Frutilla",
                    isChecked = false
                )
            )
        }
    }
}

