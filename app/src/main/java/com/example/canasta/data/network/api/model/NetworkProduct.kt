package com.example.canasta.data.network.api.model

import com.example.canasta.data.model.Product
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class NetworkProduct(
    var id: Int,
    var name: String?,
    var category: String?,
    var description: String?,
    var price: Double?,
    var brand: String?,
    var imageUrl: String?,
    @Contextual
    var createdAt: Date? = null,
    @Contextual
    var updatedAt: Date? = null
) {
    fun asModel(): Product {
        return Product(
            id = id,
            name = name,
            category = category,
            description = description,
            price = price,
            brand = brand,
            imageUrl = imageUrl,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}

@Serializable
data class NetworkNewProduct(
    var name: String?,
    var category: String?,
    var description: String?,
    var price: Double?,
    var brand: String?,
    var imageUrl: String?
)

@Serializable
data class NetworkPagedProducts(
    val data: List<NetworkProduct>,
    val total: Int,
    val page: Int,
    val pageSize: Int
)
