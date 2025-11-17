package com.example.canasta.ui.screens.categories

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.LocalPizza
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.Icecream
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Dining
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.canasta.data.remote.models.CategoryIcons
import com.example.canasta.data.remote.models.GetCategory
import com.example.canasta.ui.components.common.CommonFab
import com.example.canasta.ui.components.common.CommonScreenHeader
import com.example.canasta.ui.theme.Secondary
import com.example.canasta.ui.theme.Background
import com.example.canasta.utils.DeviceUtils

@Composable
fun CategoriesScreen(
    onBackClick: () -> Unit = {},
    viewModel: CategoriesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val isTablet = DeviceUtils.isTablet()

    var showCreateDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<GetCategory?>(null) }

    Scaffold(
        containerColor = Background,
        floatingActionButton = {
            CommonFab(
                icon = Icons.Filled.Add,
                contentDescription = "Agregar categoría",
                onClick = { showCreateDialog = true }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
        ) {
            CommonScreenHeader(title = "Categorías")
            Text(
                text = "Gestiona las categorías de productos",
                fontSize = 16.sp,
                color = Color(0xFF666666),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Mensaje de error
            errorMessage?.let { message ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = message,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            // Contenido según el estado
            when (uiState) {
                is CategoriesUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is CategoriesUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = (uiState as CategoriesUiState.Error).message,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.loadCategories() }) {
                                Text("Reintentar")
                            }
                        }
                    }
                }
                is CategoriesUiState.Success -> {
                    if (categories.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay categorías. Presiona + para crear una.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray
                            )
                        }
                    } else {
                        if (isTablet) {
                            // Tablet: Grid de 2 columnas
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(bottom = 80.dp) // Espacio para el FAB
                            ) {
                                items(categories) { category ->
                                    CategoryItem(
                                        category = category,
                                        onEditClick = {
                                            selectedCategory = category
                                            showEditDialog = true
                                        },
                                        onDeleteClick = {
                                            selectedCategory = category
                                            showDeleteDialog = true
                                        }
                                    )
                                }
                            }
                        } else {
                            // Móvil: Lista de 1 columna
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(bottom = 80.dp) // Espacio para el FAB
                            ) {
                                items(categories) { category ->
                                    CategoryItem(
                                        category = category,
                                        onEditClick = {
                                            selectedCategory = category
                                            showEditDialog = true
                                        },
                                        onDeleteClick = {
                                            selectedCategory = category
                                            showDeleteDialog = true
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Diálogo para crear categoría
    if (showCreateDialog) {
        CreateCategoryDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { name, icon ->
                val metadata = mapOf("icon" to icon)
                viewModel.createCategory(name, metadata)
                showCreateDialog = false
            }
        )
    }

    // Diálogo para editar categoría
    if (showEditDialog && selectedCategory != null) {
        EditCategoryDialog(
            category = selectedCategory!!,
            onDismiss = {
                showEditDialog = false
                selectedCategory = null
            },
            onConfirm = { name, icon ->
                val metadata = mapOf("icon" to icon)
                viewModel.updateCategory(selectedCategory!!.id, name, metadata)
                showEditDialog = false
                selectedCategory = null
            }
        )
    }

    // Diálogo para confirmar eliminación
    if (showDeleteDialog && selectedCategory != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                selectedCategory = null
            },
            title = { Text("Eliminar categoría") },
            text = { Text("¿Estás seguro de que deseas eliminar \"${selectedCategory!!.name}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCategory(selectedCategory!!.id)
                        showDeleteDialog = false
                        selectedCategory = null
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        selectedCategory = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun CategoryItem(
    category: GetCategory,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Ícono de la categoría
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = Secondary.copy(alpha = 0.1f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = getIconForCategory(category.icon),
                            contentDescription = category.name,
                            tint = Secondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Nombre de la categoría
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Row {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

// Función helper para obtener el ícono basado en el nombre
@Composable
fun getIconForCategory(iconName: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (iconName) {
        "shopping_cart" -> androidx.compose.material.icons.Icons.Default.ShoppingCart
        "restaurant" -> androidx.compose.material.icons.Icons.Default.Restaurant
        "local_pizza" -> androidx.compose.material.icons.Icons.Default.LocalPizza
        "fastfood" -> androidx.compose.material.icons.Icons.Default.Fastfood
        "bakery_dining" -> androidx.compose.material.icons.Icons.Default.BakeryDining
        "local_cafe" -> androidx.compose.material.icons.Icons.Default.LocalCafe
        "local_bar" -> androidx.compose.material.icons.Icons.Default.LocalBar
        "icecream" -> androidx.compose.material.icons.Icons.Default.Icecream
        "cake" -> androidx.compose.material.icons.Icons.Default.Cake
        "egg" -> androidx.compose.material.icons.Icons.Default.Egg
        "lunch_dining" -> androidx.compose.material.icons.Icons.Default.LunchDining
        "kitchen" -> androidx.compose.material.icons.Icons.Default.Kitchen
        "dining" -> androidx.compose.material.icons.Icons.Default.Dining
        "water_drop" -> androidx.compose.material.icons.Icons.Default.WaterDrop
        "grass" -> androidx.compose.material.icons.Icons.Default.Grass
        "eco" -> androidx.compose.material.icons.Icons.Default.Eco
        "spa" -> androidx.compose.material.icons.Icons.Default.Spa
        "cleaning_services" -> androidx.compose.material.icons.Icons.Default.CleaningServices
        "pets" -> androidx.compose.material.icons.Icons.Default.Pets
        "local_florist" -> androidx.compose.material.icons.Icons.Default.LocalFlorist
        "child_care" -> androidx.compose.material.icons.Icons.Default.ChildCare
        "medical_services" -> androidx.compose.material.icons.Icons.Default.MedicalServices
        else -> androidx.compose.material.icons.Icons.Default.Category
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var name by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf("category") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
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
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Secondary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Nueva categoría",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Campo de nombre
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Selecciona un ícono:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )

            // Selector de íconos con scroll horizontal
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(CategoryIcons.icons) { (iconName, _) ->
                    IconOption(
                        iconName = iconName,
                        isSelected = selectedIcon == iconName,
                        onClick = { selectedIcon = iconName }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botones
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name.trim(), selectedIcon)
                        onDismiss()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Secondary),
                enabled = name.isNotBlank()
            ) {
                Text("Crear")
            }

            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Gray
                )
            ) {
                Text("Cancelar")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCategoryDialog(
    category: GetCategory,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var name by remember { mutableStateOf(category.name) }
    var selectedIcon by remember { mutableStateOf(category.icon) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
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
                    tint = Secondary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Editar categoría",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Campo de nombre
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Selecciona un ícono:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )

            // Selector de íconos con scroll horizontal
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(CategoryIcons.icons) { (iconName, _) ->
                    IconOption(
                        iconName = iconName,
                        isSelected = selectedIcon == iconName,
                        onClick = { selectedIcon = iconName }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botones
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name.trim(), selectedIcon)
                        onDismiss()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Secondary),
                enabled = name.isNotBlank()
            ) {
                Text("Guardar")
            }

            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Gray
                )
            ) {
                Text("Cancelar")
            }
        }
    }
}

@Composable
fun IconOption(
    iconName: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = if (isSelected) Secondary else Color.Gray.copy(alpha = 0.1f),
        modifier = Modifier
            .size(48.dp)
            .clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = getIconForCategory(iconName),
                contentDescription = iconName,
                tint = if (isSelected) Color.White else Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
