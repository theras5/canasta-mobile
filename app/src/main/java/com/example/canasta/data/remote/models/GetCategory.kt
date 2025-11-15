package com.example.canasta.data.remote.models

import kotlinx.serialization.Serializable

/**
 * Modelo de categoría que se recibe del servidor
 */
@Serializable
data class GetCategory(
    val id: Long,
    val name: String,
    val metadata: Map<String, String>? = null,
    val updatedAt: String,
    val createdAt: String
) {
    // Obtiene el ícono guardado en metadata o usa uno por defecto
    val icon: String
        get() = metadata?.get("icon") ?: "category"
}

/**
 * Lista de íconos disponibles para categorías
 */
object CategoryIcons {
    val icons = listOf(
        "category" to "Categoría",
        "shopping_cart" to "Carrito",
        "restaurant" to "Restaurante",
        "local_pizza" to "Pizza",
        "fastfood" to "Comida Rápida",
        "bakery_dining" to "Panadería",
        "local_cafe" to "Café",
        "local_bar" to "Bar",
        "icecream" to "Helado",
        "cake" to "Pastel",
        "egg" to "Huevo",
        "lunch_dining" to "Almuerzo",
        "kitchen" to "Cocina",
        "dining" to "Comedor",
        "water_drop" to "Bebida",
        "apple" to "Fruta",
        "grass" to "Vegetales",
        "eco" to "Orgánico",
        "spa" to "Salud",
        "cleaning_services" to "Limpieza",
        "pets" to "Mascotas",
        "local_florist" to "Flores",
        "child_care" to "Bebé",
        "medical_services" to "Medicina"
    )
}

