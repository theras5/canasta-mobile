package com.example.canasta.ui.components.lists

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.canasta.ui.components.common.CustomModal
import com.example.canasta.ui.components.common.ModalActionButton
import com.example.canasta.ui.components.common.ModalTextField
import com.example.canasta.ui.theme.Secondary
import com.example.canasta.ui.theme.Titles

/**
 * Modal para crear una nueva lista
 * 
 * @param onDismiss Callback cuando se cierra el modal
 * @param onCreateList Callback cuando se crea la lista con nombre e imagen seleccionada
 */
@Composable
fun CreateListModal(
    onDismiss: () -> Unit,
    onCreateList: (name: String, imageIcon: String?) -> Unit
) {
    var listName by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf<String?>(null) }

    // Lista de emojis comunes para listas de compras
    val emojiOptions = listOf(
        "🛒", "🏠", "🍎", "🥕", "🍞", "🥛",
        "🍕", "🍔", "🍗", "🎂", "🍱", "🍜",
        "💊", "🔨", "🛠️", "🎨", "📚", "👕",
        "🧴", "🧼", "🧽", "🧹", "🔧", "⚽"
    )

    CustomModal(
        title = "Agregar",
        onDismiss = onDismiss
    ) {
        // Campo de nombre
        ModalTextField(
            label = "Nombre de la lista",
            value = listName,
            onValueChange = { listName = it },
            placeholder = "Ej: Casa"
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Selector de emoji - fila horizontal scrolleable
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Ícono de la lista",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                fontStyle = FontStyle.Italic,
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
                emojiOptions.forEach { emoji ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (selectedEmoji == emoji) Secondary.copy(alpha = 0.2f)
                                else Color.White
                            )
                            .border(
                                width = if (selectedEmoji == emoji) 2.dp else 1.dp,
                                color = if (selectedEmoji == emoji) Secondary
                                else Color.Gray.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedEmoji = emoji },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = emoji,
                            fontSize = 24.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        
        // Botón de acción
        ModalActionButton(
            text = "Agregar",
            onClick = {
                if (listName.isNotBlank()) {
                    onCreateList(listName, selectedEmoji)
                    onDismiss()
                }
            },
            enabled = listName.isNotBlank()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CreateListModalPreview() {
    MaterialTheme {
        CreateListModal(onDismiss = {}, onCreateList = { _: String, _: String? -> })
    }
}
