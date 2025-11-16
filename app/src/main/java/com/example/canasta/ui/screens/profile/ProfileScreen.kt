package com.example.canasta.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.canasta.R
import com.example.canasta.data.remote.models.GetUser
import com.example.canasta.data.repository.AuthRepository
import com.example.canasta.data.repository.UserRepository
import com.example.canasta.ui.components.profile.EditProfileSheet
import com.example.canasta.ui.components.profile.LogoutButton
import com.example.canasta.ui.components.profile.ProfileHeader
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository(context) }
    val userRepository = remember { UserRepository() }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showEdit by remember { mutableStateOf(false) }
    var userProfile by remember { mutableStateOf<GetUser?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isUpdating by remember { mutableStateOf(false) }

    // Cargar perfil de usuario al iniciar
    LaunchedEffect(Unit) {
        scope.launch {
            userRepository.getUserProfile().fold(
                onSuccess = { user ->
                    userProfile = user
                    isLoading = false
                },
                onFailure = { error ->
                    isLoading = false
                    snackbarHostState.showSnackbar(
                        message = "Error al cargar el perfil: ${error.message}"
                    )
                }
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        if (isLoading) {
            // Mostrar indicador de carga
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            // Contenido con scroll
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Título "Perfil" con icono de editar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.profile),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = { showEdit = true },
                        enabled = !isUpdating
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.edit_profile)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Header con avatar, nombre y email
                userProfile?.let { user ->
                    ProfileHeader(
                        userName = "${user.name} ${user.surname}",
                        userEmail = user.email,
                        memberSince = user.createdAt,
                        avatarIndex = user.metadata?.get("avatarIndex")?.toIntOrNull() ?: 0
                    )
                }

                // Espacio extra para que el contenido no quede escondido detrás del botón
                Spacer(modifier = Modifier.height(160.dp))
            }

            // Botón de cerrar sesión fijo, con padding para barras del sistema y separación de la bottom bar
            LogoutButton(
                onClick = {
                    authRepository.logout()
                    onLogout()
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 24.dp)
            )

            if (showEdit) {
                EditProfileSheet(
                    currentFirstName = userProfile?.name ?: "",
                    currentLastName = userProfile?.surname ?: "",
                    currentAvatarIndex = userProfile?.metadata?.get("avatarIndex")?.toIntOrNull() ?: 0,
                    onDismissRequest = { showEdit = false },
                    onConfirm = { firstName, lastName, avatarIndex ->
                        isUpdating = true
                        scope.launch {
                            val metadata = mapOf("avatarIndex" to avatarIndex.toString())
                            userRepository.updateUserProfile(
                                name = firstName,
                                surname = lastName,
                                metadata = metadata
                            ).fold(
                                onSuccess = { updatedUser ->
                                    userProfile = updatedUser
                                    isUpdating = false
                                    snackbarHostState.showSnackbar(context.getString(R.string.profile_updated))
                                },
                                onFailure = { error ->
                                    isUpdating = false
                                    snackbarHostState.showSnackbar(
                                        context.getString(R.string.error_updating_profile, error.message ?: "")
                                    )
                                }
                            )
                        }
                    }
                )
            }
        }

        // Snackbar para mensajes
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen()
}
