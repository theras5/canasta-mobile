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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.canasta.R
import com.example.canasta.ui.components.common.ModalActionButton
import com.example.canasta.ui.components.common.ModalTextField
import com.example.canasta.ui.theme.Secondary
import com.example.canasta.ui.theme.Titles

/**
 * Bottom sheet para crear una nueva lista
 *
 * @param onDismiss Callback cuando se cierra el bottom sheet
 * @param onCreateList Callback cuando se crea la lista con nombre e imagen seleccionada
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateListModal(
    onDismiss: () -> Unit,
    onCreateList: (name: String, imageIcon: String?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var listName by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf<String?>(null) }

    // Lista de emojis comunes para listas de compras
    val emojiOptions = listOf(
        "🛒", "🏠", "🍎", "🥕", "🍞", "🥛",
        "🍕", "🍔", "🍗", "🎂", "🍱", "🍜",
        "💊", "🔨", "🛠️", "🎨", "📚", "👕",
        "🧴", "🧼", "🧽", "🧹", "🔧", "⚽"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        dragHandle = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .width(40.dp)
                        .height(4.dp)
                        .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            // Título
            Text(
                text = stringResource(R.string.add_list_title),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 20.dp)
            )
            // Campo de nombre
            ModalTextField(
                label = stringResource(R.string.list_name_label),
                value = listName,
                onValueChange = { listName = it },
                placeholder = stringResource(R.string.list_name_placeholder)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Selector de emoji - fila horizontal scrolleable
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.list_icon_label),
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
                text = stringResource(R.string.add_button),
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
}

@Preview(showBackground = true)
@Composable
fun CreateListModalPreview() {
    MaterialTheme {
        CreateListModal(onDismiss = {}, onCreateList = { _: String, _: String? -> })
    }
}
