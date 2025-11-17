package com.example.canasta.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.canasta.ui.theme.Secondary
import com.example.canasta.ui.theme.Titles

/**
 * Modal para editar cantidad y unidad de un producto, similar a la versión web.
 */
@Composable
fun EditProductModal(
    productName: String,
    initialQuantity: Double,
    initialUnit: String,
    units: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (Double, String) -> Unit
) {
    var quantityText by remember { mutableStateOf(if (initialQuantity == 0.0) "" else initialQuantity.toString()) }
    var selectedUnit by remember { mutableStateOf(initialUnit.ifBlank { units.firstOrNull() ?: "unidades" }) }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    CustomModal(title = "Editar ${productName}", onDismiss = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = productName,
                color = Titles,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Campo de cantidad numérica
            OutlinedTextField(
                value = quantityText,
                onValueChange = { newValue ->
                    // Solo permitir números y un punto decimal
                    if (newValue.isEmpty() || newValue.matches(Regex("[0-9]*\\.?[0-9]*"))) {
                        quantityText = newValue
                    }
                },
                label = { Text("Cantidad") },
                placeholder = { Text("Ej: 1, 2.5") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Selector de unidad: fila horizontal scrolleable (como selector de emojis)
            Text(
                text = "Unidad",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Titles,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5))
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val options = if (units.isNotEmpty()) units else listOf("unidades", "kg", "gr", "lt", "ml", "paquete")
                options.forEach { unitOption ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .height(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (selectedUnit == unitOption) Secondary.copy(alpha = 0.2f)
                                else Color.White
                            )
                            .border(
                                width = if (selectedUnit == unitOption) 2.dp else 1.dp,
                                color = if (selectedUnit == unitOption) Secondary else Color.Gray.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedUnit = unitOption }
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = unitOption, fontSize = 14.sp, color = Color.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onDismiss) {
                    Text(text = "Cancelar", color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(8.dp))

                val quantityDouble = quantityText.toDoubleOrNull()
                val isSaveEnabled = quantityDouble != null && quantityDouble > 0 && selectedUnit.isNotBlank()

                Button(
                    onClick = {
                        quantityDouble?.let { onConfirm(it, selectedUnit) }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Secondary),
                    enabled = isSaveEnabled
                ) {
                    Text(text = "Guardar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
