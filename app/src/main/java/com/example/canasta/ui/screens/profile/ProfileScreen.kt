package com.example.canasta.ui.screens.profile

import android.content.res.Configuration
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
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.canasta.R
import com.example.canasta.data.remote.models.GetUser
import com.example.canasta.data.repository.AuthRepository
import com.example.canasta.data.repository.UserRepository
import com.example.canasta.ui.components.common.CommonScreenHeader
import com.example.canasta.ui.components.profile.EditProfileSheet
import com.example.canasta.ui.components.profile.LogoutButton
import com.example.canasta.ui.components.profile.ProfileHeader
import com.example.canasta.ui.theme.Background
import com.example.canasta.ui.theme.Errors
import com.example.canasta.ui.theme.Success
import com.example.canasta.utils.DeviceUtils
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val authRepository = remember { AuthRepository(context) }
    val userRepository = remember { UserRepository() }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showEdit by remember { mutableStateOf(false) }
    var userProfile by remember { mutableStateOf<GetUser?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isUpdating by remember { mutableStateOf(false) }
    var lastSnackbarIsSuccess by remember { mutableStateOf(false) }

    // Detectar si estamos en orientación horizontal y si es tablet
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val isTablet = DeviceUtils.isTablet()

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
                    lastSnackbarIsSuccess = false
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.error_loading_profile)
                    )
                }
            )
        }
    }

    Scaffold(
        containerColor = Background,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    androidx.compose.material3.Snackbar(
                        snackbarData = data,
                        containerColor = if (lastSnackbarIsSuccess) Success else Errors,
                        contentColor = Color.White
                    )
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLoading) {
                // Mostrar indicador de carga
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                if (isTablet) {
                    // Diseño para Tablet: Layout de dos columnas
                    TabletProfileLayout(
                        userProfile = userProfile,
                        isUpdating = isUpdating,
                        onEditClick = { showEdit = true },
                        onLogout = {
                            authRepository.logout()
                            onLogout()
                        }
                    )
                } else {
                    // Diseño para Móvil: Layout vertical original
                    MobileProfileLayout(
                        userProfile = userProfile,
                        isUpdating = isUpdating,
                        isLandscape = isLandscape,
                        onEditClick = { showEdit = true },
                        onLogout = {
                            authRepository.logout()
                            onLogout()
                        }
                    )
                }

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
                                        lastSnackbarIsSuccess = true
                                        snackbarHostState.showSnackbar(context.getString(R.string.profile_updated))
                                    },
                                    onFailure = { error ->
                                        isUpdating = false
                                        lastSnackbarIsSuccess = false
                                        snackbarHostState.showSnackbar(
                                            context.getString(R.string.error_updating_profile)
                                        )
                                    }
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TabletProfileLayout(
    userProfile: GetUser?,
    isUpdating: Boolean,
    onEditClick: () -> Unit,
    onLogout: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Contenido principal centrado
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Título "Perfil" con icono de editar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CommonScreenHeader(
                    title = stringResource(R.string.profile),
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onEditClick,
                    enabled = !isUpdating
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.edit_profile)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Header con avatar, nombre, email y miembro desde (centrado)
            userProfile?.let { user ->
                ProfileHeader(
                    userName = "${user.name} ${user.surname}",
                    userEmail = user.email,
                    memberSince = user.createdAt,
                    avatarIndex = user.metadata?.get("avatarIndex")?.toIntOrNull() ?: 0
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(48.dp))

            // Botón de cerrar sesión centrado
            LogoutButton(
                onClick = onLogout
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun MobileProfileLayout(
    userProfile: GetUser?,
    isUpdating: Boolean,
    isLandscape: Boolean,
    onEditClick: () -> Unit,
    onLogout: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Contenido con scroll
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .then(
                    if (isLandscape) {
                        Modifier.navigationBarsPadding()
                    } else {
                        Modifier
                    }
                )
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título "Perfil" con icono de editar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CommonScreenHeader(
                    title = stringResource(R.string.profile),
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onEditClick,
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

            // En landscape, el botón va dentro del scroll
            if (isLandscape) {
                Spacer(modifier = Modifier.height(32.dp))

                LogoutButton(onClick = onLogout)
            } else {
                // En portrait, agregamos espacio para que el contenido no quede detrás del botón
                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        // En portrait, el botón se muestra fijo en la parte inferior
        if (!isLandscape) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                LogoutButton(onClick = onLogout)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen()
}
