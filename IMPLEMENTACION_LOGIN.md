# Implementación de Autenticación con API

## Resumen

Se ha implementado el flujo completo de autenticación conectando el LoginScreen con la API backend. La implementación sigue una arquitectura limpia separando las responsabilidades en capas.

## Archivos Creados

### 1. Modelos de Datos (`data/remote/models/AuthModels.kt`)
- `RegistrationData`: Datos para registro de usuario
- `Credentials`: Email y contraseña para login
- `VerificationCode`: Código de verificación
- `AuthenticationToken`: Token JWT retornado por el login
- `NewUser`: Usuario recién creado/verificado
- `GetUser`: Datos de perfil de usuario

### 2. Servicio API (`data/remote/api/AuthApiService.kt`)
Interface Retrofit con los siguientes endpoints:
- `POST /api/users/register` - Registrar usuario
- `POST /api/users/login` - Iniciar sesión
- `POST /api/users/verify-account` - Verificar cuenta
- `POST /api/users/send-verification` - Reenviar código

### 3. Repository (`data/repository/AuthRepository.kt`)
Capa de abstracción para operaciones de autenticación:
- `register()` - Registra un nuevo usuario
- `login()` - Inicia sesión y guarda el token
- `verifyAccount()` - Verifica la cuenta con código
- `resendVerification()` - Reenvía el código de verificación
- `logout()` - Cierra sesión eliminando el token

### 4. ViewModel (`ui/screens/auth/AuthViewModel.kt`)
Gestiona el estado de UI con StateFlow:
- Estados: loading, error, modo login/registro/verificación
- Validaciones de campos
- Llamadas al repository
- Manejo de errores

### 5. LoginScreen Actualizado (`ui/screens/auth/loginScreen.kt`)
Conectado al ViewModel con:
- Formulario reactivo de registro/login
- Pantalla de verificación de código
- Estados de carga
- Mensajes de error
- Navegación automática al home después del login

## Archivos Modificados

### 1. ApiClient (`data/remote/ApiClient.kt`)
- Agregado `authService` para acceso al AuthApiService
- Ya incluía el `authInterceptor` para inyectar tokens en las peticiones

### 2. Navegación
- **AppDestination.kt**: Agregado objeto `Login` serializable
- **AppNavGraph.kt**: 
  - Agregada ruta `composable<Login>`
  - Cambiado `startDestination = Login`
  - Navegación a `Lists` después del login
- **AppNavigation.kt**: 
  - Actualizado `showBottomBar` para ocultar en Login (`false` en lugar de `true`)

## Flujo de Autenticación

### Registro
1. Usuario completa formulario (nombre, apellido, email, contraseña)
2. Click en "CREAR CUENTA"
3. Se llama a `POST /api/users/register`
4. Si es exitoso, cambia a pantalla de verificación
5. Usuario ingresa código recibido por email
6. Click en "VERIFICAR CUENTA"
7. Se llama a `POST /api/users/verify-account`
8. Si es exitoso, vuelve al modo login

### Login
1. Usuario ingresa email y contraseña
2. Click en "INICIAR SESIÓN"
3. Se llama a `POST /api/users/login`
4. El token JWT se guarda en `ApiClient.authInterceptor`
5. Se navega automáticamente a la pantalla principal (Lists)
6. Todas las peticiones futuras incluyen el token en el header `Authorization: Bearer <token>`

## Características Implementadas

✅ **Arquitectura en capas**: Models → API Service → Repository → ViewModel → UI
✅ **Manejo de estado**: StateFlow con estados reactivos
✅ **Validaciones**: Email, contraseña mínimo 6 caracteres, campos requeridos
✅ **Estados de UI**: Loading, error messages, modo login/registro/verificación
✅ **Navegación**: Flujo completo desde Login hasta Home
✅ **Gestión de tokens**: Almacenamiento y uso automático en peticiones
✅ **Reenvío de código**: Funcionalidad para reenviar código de verificación
✅ **UX**: Indicadores de carga, mensajes de error contextuales

## Uso de Librerías

- **Retrofit**: Cliente HTTP para comunicación con la API
- **OkHttp**: Cliente HTTP subyacente, interceptors y logging
- **Kotlinx Serialization**: Serialización/deserialización JSON
- **Jetpack Compose**: UI reactiva
- **Navigation Compose**: Navegación type-safe con objetos serializables
- **ViewModel & StateFlow**: Gestión de estado

## Configuración del Backend

La URL base está configurada en `ApiClient`:
```kotlin
private const val BASE_URL = "http://localhost:8080/"
```

Para cambiarla (por ejemplo, para usar un emulador Android):
```kotlin
private const val BASE_URL = "http://10.0.2.2:8080/"  // Android emulator
// o
private const val BASE_URL = "http://<TU_IP>:8080/"   // Dispositivo físico
```

## Próximos Pasos

- [ ] Persistencia del token (SharedPreferences o DataStore)
- [ ] Refresh token automático
- [ ] Manejo de sesión expirada
- [ ] Recuperación de contraseña
- [ ] Perfil de usuario editable

