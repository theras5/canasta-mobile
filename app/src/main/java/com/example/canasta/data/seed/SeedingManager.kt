package com.example.canasta.data.seed

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.canasta.data.repository.CategoryRepository
import com.example.canasta.data.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Realiza un seeding de datos por defecto (categorías y productos) para usuarios nuevos.
 * Solo actúa si el usuario NO tiene ninguna categoría NI ningún producto.
 * Además, es idempotente por usuario usando SharedPreferences (marcado por email).
 */
class SeedingManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val categoryRepo = CategoryRepository()
    private val productRepo = ProductRepository()

    companion object {
        private const val PREFS_NAME = "canasta_seed_prefs"
        private const val KEY_PREFIX_SEEDED = "seeded_" // + email
        private const val TAG = "SeedingManager"
    }

    /**
     * Semilla categorías y productos por defecto si el usuario no tiene nada aún.
     * @return true si se ejecutó el seeding; false si no fue necesario o ya se hizo.
     */
    suspend fun seedDefaultsIfEmpty(userEmail: String): Boolean = withContext(Dispatchers.IO) {
        val key = KEY_PREFIX_SEEDED + userEmail.lowercase().trim()
        if (prefs.getBoolean(key, false)) return@withContext false

        try {
            // Consultas mínimas para chequear si está vacío
            val categoriesEmpty = categoryRepo.getCategories(perPage = 1)
                .getOrDefault(emptyList()).isEmpty()
            val productsEmpty = productRepo.getProducts(perPage = 1)
                .getOrDefault(emptyList()).isEmpty()

            if (!(categoriesEmpty && productsEmpty)) {
                // No cumple la condición de vacío total; marcamos como "no necesita seeding" para evitar chequeos en cada login
                prefs.edit().putBoolean(key, true).apply()
                return@withContext false
            }

            // Crear categorías por defecto
            val createdCategories = mutableMapOf<String, Long>()
            for ((name, icon) in defaultCategories) {
                val res = categoryRepo.createCategory(name, metadata = mapOf("icon" to icon))
                res.onSuccess { cat ->
                    createdCategories[name] = cat.id
                }.onFailure { e ->
                    Log.w(TAG, "No se pudo crear categoría '$name': ${e.message}")
                }
            }

            // Crear productos por defecto (intentamos mapear categoría por nombre si existe)
            for ((name, categoryName) in defaultProducts) {
                val categoryId = createdCategories[categoryName]
                val res = productRepo.createProduct(name, categoryId = categoryId, metadata = null)
                res.onFailure { e ->
                    Log.w(TAG, "No se pudo crear producto '$name': ${e.message}")
                }
            }

            prefs.edit().putBoolean(key, true).apply()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error durante seeding: ${e.message}", e)
            false
        }
    }

    // Datos por defecto (simples y genéricos)
    private val defaultCategories: List<Pair<String, String>> = listOf(
        "Lácteos" to "egg",
        "Bebidas" to "water_drop",
        "Frutas" to "apple",
        "Verduras" to "grass",
        "Almacén" to "shopping_cart",
        "Limpieza" to "cleaning_services"
    )

    // Producto -> Categoría
    private val defaultProducts: List<Pair<String, String>> = listOf(
        "Leche" to "Lácteos",
        "Yogur" to "Lácteos",
        "Agua" to "Bebidas",
        "Jugo" to "Bebidas",
        "Manzana" to "Frutas",
        "Banana" to "Frutas",
        "Tomate" to "Verduras",
        "Lechuga" to "Verduras",
        "Arroz" to "Almacén",
        "Fideos" to "Almacén",
        "Detergente" to "Limpieza",
        "Lavandina" to "Limpieza"
    )
}

