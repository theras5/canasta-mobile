package com.example.canasta.ui.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.canasta.R
import com.example.canasta.ui.theme.Secondary
import com.example.canasta.ui.theme.Titles

/**
 * Modal para editar la descripción/cantidad de un producto
 *
 * @param productName Nombre del producto
 * @param currentDescription Descripción/cantidad actual
 * @param onDismiss Callback al cerrar
 * @param onConfirm Callback al confirmar con la nueva descripción
 */
@Composable
fun EditProductModal(
    productName: String,
    currentDescription: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var description by remember { mutableStateOf(currentDescription) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    CustomModal(title = stringResource(R.string.edit_product_quantity), onDismiss = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = productName,
                color = Titles,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.quantity_and_unit)) },
                placeholder = { Text(stringResource(R.string.quantity_placeholder)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onDismiss) {
                    Text(text = stringResource(R.string.cancel), color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { onConfirm(description) },
                    colors = ButtonDefaults.buttonColors(containerColor = Secondary),
                    enabled = description.isNotBlank()
                ) {
                    Text(text = stringResource(R.string.save), color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

