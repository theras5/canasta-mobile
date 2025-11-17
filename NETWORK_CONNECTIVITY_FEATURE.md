# Network Connectivity Feature

## Descripción

Se ha implementado un indicador visual que muestra cuando no hay conexión a Internet. Esta funcionalidad es útil para informar al usuario sobre problemas de conectividad.

## Implementación

### Archivos creados:

1. **NetworkConnectivityManager.kt** (`app/src/main/java/com/example/canasta/utils/`)
   - Monitorea el estado de conectividad en tiempo real usando `ConnectivityManager`
   - Emite un `Flow<Boolean>` que indica si hay conexión a Internet
   - Utiliza `NetworkCallback` para detectar cambios en la red

2. **NetworkStatusBanner.kt** (`app/src/main/java/com/example/canasta/ui/components/common/`)
   - Componente visual que muestra un banner rojo con el mensaje "Sin conexión a Internet"
   - Aparece con animación cuando se pierde la conexión
   - Desaparece automáticamente cuando se recupera la conexión

### Archivos modificados:

1. **AndroidManifest.xml**
   - Se agregó el permiso `ACCESS_NETWORK_STATE` necesario para monitorear la conectividad

2. **AppNavigation.kt**
   - Se integró el `NetworkStatusBanner` tanto en el modo móvil como en el modo tablet
   - En móviles: aparece en el `topBar` del `Scaffold`
   - En tablets: aparece en la parte superior antes del contenido principal

3. **strings.xml** (es e en)
   - Se agregó la cadena `no_internet_connection` en español e inglés

## Cómo probarlo

Para probar esta funcionalidad:

1. **Modo avión**: Activa el modo avión en el dispositivo
2. **Wi-Fi/Datos móviles**: Desactiva Wi-Fi y datos móviles
3. Observa que aparece el banner rojo con el mensaje "Sin conexión a Internet"
4. Al reactivar la conexión, el banner desaparece automáticamente

## Características

- ✅ Detección en tiempo real de cambios en la conectividad
- ✅ Soporte multiidioma (español e inglés)
- ✅ Animaciones suaves al aparecer/desaparecer
- ✅ Compatible con modo móvil y tablet
- ✅ No requiere permisos adicionales del usuario (solo ACCESS_NETWORK_STATE en el manifest)
- ✅ Diseño consistente con la UI de la aplicación

## Notas técnicas

- El monitoreo de red usa el API moderno de Android (`NetworkCallback`)
- El estado se observa mediante Kotlin Flow para integración reactiva con Compose
- Se verifica tanto la disponibilidad de red como la validación de Internet real

