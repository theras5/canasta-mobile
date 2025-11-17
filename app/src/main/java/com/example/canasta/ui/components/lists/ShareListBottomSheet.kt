package com.example.canasta.ui.components.lists

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.canasta.data.remote.models.SharedUser
import com.example.canasta.ui.components.common.ModalTextField
import com.example.canasta.ui.theme.Primary
import com.example.canasta.ui.theme.Secondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareListBottomSheet(
    sharedUsers: List<SharedUser>,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onDismissRequest: () -> Unit,
    onShareWithEmail: (String) -> Unit,
    onRevokeAccess: (SharedUser) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }

    // Función para validar email
    fun validateEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }

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
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    tint = Secondary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Compartir lista",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Campo Email
            ModalTextField(
                label = "Email del usuario",
                value = email,
                onValueChange = {
                    email = it
                    emailError = null
                },
                placeholder = "ejemplo@correo.com"
            )

            // Error de validación del campo email
            if (emailError != null) {
                Text(
                    text = emailError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            // Mostrar error general de la API
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            // Botón Compartir
            Button(
                onClick = {
                    when {
                        email.isBlank() -> emailError = "El email es requerido"
                        !validateEmail(email) -> emailError = "Email inválido"
                        else -> {
                            onShareWithEmail(email)
                            email = ""
                            emailError = null
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Secondary),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(text = "Compartir")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sección de usuarios compartidos
            Text(
                text = "Compartido con:",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            if (sharedUsers.isEmpty()) {
                Text(
                    text = "Aún no has compartido esta lista con nadie",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(sharedUsers) { user ->
                        SharedUserItem(
                            user = user,
                            onRevokeAccess = { onRevokeAccess(user) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón Cerrar
            OutlinedButton(
                onClick = onDismissRequest,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Gray
                )
            ) {
                Text(text = "Cerrar")
            }
        }
    }
}

@Composable
private fun SharedUserItem(
    user: SharedUser,
    onRevokeAccess: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5), MaterialTheme.shapes.medium)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Información del usuario
            Column {
                Text(
                    text = "${user.name} ${user.surname}",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
                Text(
                    text = user.email,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }

        // Botón Revocar
        IconButton(onClick = onRevokeAccess) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Revocar acceso",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}
