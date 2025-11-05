# Componentes de Modal Reutilizables

## Descripción

Este conjunto de componentes proporciona un sistema de modales reutilizables con el estilo personalizado de la aplicación.

## Componentes

### 1. `CustomModal`
Modal base reutilizable con diseño personalizado.

**Parámetros:**
- `title: String` - Título del modal
- `onDismiss: () -> Unit` - Callback cuando se cierra el modal
- `content: @Composable () -> Unit` - Contenido personalizado del modal

**Ejemplo:**
```kotlin
CustomModal(
    title = "Mi Modal",
    onDismiss = { /* cerrar modal */ }
) {
    // Contenido aquí
    Text("Hola mundo")
}
```

### 2. `ModalActionButton`
Botón de acción estilizado para usar dentro de modales.

**Parámetros:**
- `text: String` - Texto del botón
- `onClick: () -> Unit` - Callback al presionar
- `modifier: Modifier` - Modificador opcional
- `enabled: Boolean` - Si el botón está habilitado (default: true)

**Ejemplo:**
```kotlin
ModalActionButton(
    text = "Guardar",
    onClick = { /* guardar datos */ },
    enabled = isFormValid
)
```

### 3. `ModalTextField`
Campo de texto con label estilizado para formularios en modales.

**Parámetros:**
- `label: String` - Etiqueta del campo
- `value: String` - Valor actual
- `onValueChange: (String) -> Unit` - Callback cuando cambia el valor
- `placeholder: String` - Texto de placeholder (default: "")
- `modifier: Modifier` - Modificador opcional

**Ejemplo:**
```kotlin
ModalTextField(
    label = "Nombre",
    value = nombre,
    onValueChange = { nombre = it },
    placeholder = "Ej: Juan Pérez"
)
```

### 4. `ModalDropdown`
Selector desplegable con label para formularios en modales.

**Parámetros:**
- `label: String` - Etiqueta del selector
- `selectedValue: String?` - Valor seleccionado actual
- `placeholder: String` - Texto cuando no hay selección
- `onClick: () -> Unit` - Callback cuando se hace clic
- `modifier: Modifier` - Modificador opcional

**Ejemplo:**
```kotlin
ModalDropdown(
    label = "Categoría",
    selectedValue = categoriaSeleccionada,
    placeholder = "Seleccione una categoría",
    onClick = { mostrarSelectorCategorias = true }
)
```

## Ejemplo Completo: CreateListModal

```kotlin
@Composable
fun CreateListModal(
    onDismiss: () -> Unit,
    onCreateList: (name: String, imageIcon: String?) -> Unit
) {
    var listName by remember { mutableStateOf("") }
    var selectedImage by remember { mutableStateOf<String?>(null) }
    
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
        
        // Selector de imagen
        ModalDropdown(
            label = "Imagen de la lista",
            selectedValue = selectedImage,
            placeholder = "Seleccione una imagen",
            onClick = { 
                // Mostrar selector de imagen
            }
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Botón de acción
        ModalActionButton(
            text = "Agregar",
            onClick = {
                if (listName.isNotBlank()) {
                    onCreateList(listName, selectedImage)
                    onDismiss()
                }
            },
            enabled = listName.isNotBlank()
        )
    }
}
```

## Uso en una Pantalla

```kotlin
@Composable
fun MyScreen() {
    var showModal by remember { mutableStateOf(false) }
    
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showModal = true }
            ) {
                Icon(Icons.Default.Add, "Agregar")
            }
        }
    ) { padding ->
        // Contenido de la pantalla
    }
    
    if (showModal) {
        CreateListModal(
            onDismiss = { showModal = false },
            onCreateList = { name, image ->
                // Procesar los datos
                println("Nueva lista: $name")
            }
        )
    }
}
```

## Personalización

Los componentes utilizan los colores del tema de la aplicación:
- **Primary**: Color primario para elementos principales
- **Secondary**: Color secundario (usado en botones de acción - naranja)
- **Titles**: Color para títulos y texto importante
- **Background**: Color de fondo

Para crear tu propio modal personalizado, simplemente usa `CustomModal` y añade tu contenido dentro del bloque `content`.

