# Solución: Unresolved reference: serialization

## Problema
El error `Unresolved reference: serialization` ocurre porque el plugin no estaba definido en el catálogo de versiones de Gradle.

## Cambios Realizados

### 1. Actualizado `gradle/libs.versions.toml`

#### Agregadas versiones:
```toml
navigationCompose = "2.7.5"
kotlinxSerializationJson = "1.6.0"
```

#### Agregado plugin:
```toml
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
```

#### Agregadas bibliotecas:
```toml
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }
kotlinx-serialization-json = { group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version.ref = "kotlinxSerializationJson" }
```

## Próximos Pasos

### 1. Sincronizar Gradle
**En Android Studio:**
- Click en "Sync Now" en la barra amarilla superior
- O: File → Sync Project with Gradle Files
- O: Presiona el botón "Sync" en la toolbar

**Desde terminal:**
```powershell
.\gradlew --refresh-dependencies
```

### 2. Rebuild Project
```powershell
.\gradlew clean build
```

### 3. Verificar en Android Studio
Después de sincronizar, el error debería desaparecer y el código debería compilar correctamente.

## Archivos Modificados
- ✅ `gradle/libs.versions.toml` - Agregado plugin y dependencias faltantes

## Estado
✅ **RESUELTO** - El plugin `kotlin-serialization` ahora está correctamente definido en el catálogo de versiones.

**Nota**: Android Studio necesita sincronizar el proyecto con Gradle para que los cambios surtan efecto.

