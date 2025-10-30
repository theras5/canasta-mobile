package com.example.canasta.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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


@Composable
fun LoginScreen() {
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
                        text = "Crear Cuenta",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    // Subtítulo
                    Text(
                        text = "Únete a Canasta y organiza tus compras",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // Campos de texto
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            label = { Text("Nombre") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            label = { Text("Apellido") },
                            modifier = Modifier.weight(1f)
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
                        label = { Text("Contraseña (mínimo 6 caracteres)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        visualTransformation = PasswordVisualTransformation()
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    // Botón crear cuenta
                    Button(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("CREAR CUENTA")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    // Link iniciar sesión
                    Text(
                        text = "¿Ya tienes cuenta?",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Iniciar sesión",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clickable { /* Navegar a login */ }
                    )
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
