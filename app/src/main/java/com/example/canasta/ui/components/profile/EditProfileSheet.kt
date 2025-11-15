package com.example.canasta.ui.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.canasta.ui.components.common.ModalTextField
import com.example.canasta.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileSheet(
    currentFirstName: String = "",
    currentLastName: String = "",
    currentAvatarIndex: Int = 0,
    onDismissRequest: () -> Unit,
    onConfirm: (firstName: String, lastName: String, avatarIndex: Int) -> Unit = { _, _, _ -> }
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var firstName by remember { mutableStateOf(currentFirstName) }
    var lastName by remember { mutableStateOf(currentLastName) }
    var selectedAvatar by remember { mutableStateOf(currentAvatarIndex) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header con icono
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Editar Perfil",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Campo Nombre
            ModalTextField(
                label = "Nombre *",
                value = firstName,
                onValueChange = { firstName = it },
                placeholder = "Ingresá tu nombre"
            )

            // Campo Apellido
            ModalTextField(
                label = "Apellido *",
                value = lastName,
                onValueChange = { lastName = it },
                placeholder = "Ingresá tu apellido"
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Selector de avatar
            Text(
                text = "Elige tu avatar",
                fontSize = 16.sp,
                color = Color.Gray
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(AvatarOption.entries.toList()) { avatar ->
                    val index = avatar.ordinal
                    AvatarItem(
                        avatarOption = avatar,
                        isSelected = selectedAvatar == index,
                        onClick = { selectedAvatar = index }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botones
            Button(
                onClick = {
                    onConfirm(firstName, lastName, selectedAvatar)
                    onDismissRequest()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text(text = "GUARDAR CAMBIOS")
            }

            OutlinedButton(
                onClick = onDismissRequest,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Gray
                )
            ) {
                Text(text = "CANCELAR")
            }
        }
    }
}

@Composable
private fun AvatarItem(
    avatarOption: AvatarOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(avatarOption.color)
            .border(
                width = if (isSelected) 3.dp else 0.dp,
                color = if (isSelected) Primary else Color.Transparent,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = avatarOption.icon,
            contentDescription = "Avatar ${avatarOption.name}",
            tint = Color.White,
            modifier = Modifier.size(36.dp)
        )
    }
}

