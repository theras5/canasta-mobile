package com.example.canasta.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.canasta.ui.theme.Titles
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.MaterialTheme

/**
 * Campo de entrada de texto para modales con label personalizado
 *
 * @param label Etiqueta del campo
 * @param value Valor actual del campo
 * @param onValueChange Callback cuando cambia el valor
 * @param placeholder Texto de placeholder
 * @param modifier Modificador opcional
 */
@Composable
fun ModalTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            fontStyle = FontStyle.Italic,
            color = Titles,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color.Gray.copy(alpha = 0.6f),
                    fontSize = 16.sp
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.2f),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color(0xFFF5F5F5),
                focusedTextColor = Titles,
                unfocusedTextColor = Titles
            ),
            singleLine = true
        )
    }
}

/**
 * Selector desplegable para modales con label personalizado
 *
 * @param label Etiqueta del selector
 * @param selectedValue Valor seleccionado actual
 * @param placeholder Texto cuando no hay selección
 * @param onClick Callback cuando se hace clic en el selector
 * @param modifier Modificador opcional
 */
@Composable
fun ModalDropdown(
    label: String,
    selectedValue: String?,
    placeholder: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            fontStyle = FontStyle.Italic,
            color = Titles,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(
                    color = Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color.Gray.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedValue ?: placeholder,
                    color = if (selectedValue != null) Titles else Color.Gray.copy(alpha = 0.6f),
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Desplegar",
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ModalTextFieldPreview() {
    MaterialTheme {
        ModalTextField(
            label = "Etiqueta",
            value = "Ejemplo",
            onValueChange = {},
            placeholder = "Placeholder"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ModalDropdownPreview() {
    MaterialTheme {
        ModalDropdown(
            label = "Categoría",
            selectedValue = null,
            placeholder = "Seleccione",
            onClick = {}
        )
    }
}
