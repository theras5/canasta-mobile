package com.example.canasta.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.canasta.R
import com.example.canasta.data.model.Product
import com.example.canasta.ui.components.common.AppScaffold
import com.example.canasta.ui.components.common.BottomNavBar
import com.example.canasta.ui.components.common.CategoryChips
import com.example.canasta.ui.components.common.ProductsSearchBar
import com.example.canasta.ui.components.products.ProductList
import com.example.canasta.ui.theme.CanastaTheme
import com.example.canasta.ui.theme.Primary
import com.example.canasta.ui.theme.Secondary
import com.example.canasta.ui.theme.Titles
import com.example.canasta.ui.theme.Background
import com.example.canasta.ui.theme.Warnings
import com.example.canasta.ui.theme.Errors
import com.example.canasta.ui.theme.Success


@Composable
fun LoginScreen() {
    val isLoginMode = remember { mutableStateOf(false) }
    AppScaffold (bottomBar = {BottomNavBar()}){ innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(0.85f),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
            ) {
                Column(
                    modifier = Modifier
                        .padding(32.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Icono de canasta
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Canasta Icon",
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // Título
                    Text(
                        text = if (isLoginMode.value) "Iniciar Sesión" else "Crear Cuenta",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Titles
                    )
                    // Subtítulo
                    Text(
                        text = if (isLoginMode.value) "Accede a tu cuenta de Canasta" else "Únete a Canasta y organiza tus compras",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    // Campos de texto
                    if (!isLoginMode.value) {
                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            label = { Text("Nombre") },
                            modifier = Modifier.fillMaxWidth()
                                .padding(top = 2.dp),
                        )
                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            label = { Text("Apellido") },
                            modifier = Modifier.fillMaxWidth()
                                .padding(top = 8.dp),
                        )
                    }
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        label = { Text("Correo electrónico") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        label = {
                            Text(
                                "Contraseña (mínimo 6 caracteres)",
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        visualTransformation = PasswordVisualTransformation()
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    // Botón crear cuenta
                    Button(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Text(if (isLoginMode.value) "INICIAR SESIÓN" else "CREAR CUENTA")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    // Link iniciar sesión
                    if (isLoginMode.value) {
                        Text(
                            text = "¿No tienes cuenta?",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Registrarse",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary),
                            color = Secondary,
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .clickable { isLoginMode.value = false }
                        )
                    } else {
                        Text(
                            text = "¿Ya tienes cuenta?",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Iniciar sesión",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary),
                            color = Secondary,
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .clickable { isLoginMode.value = true }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}
