package ar.edu.itba.example.api.data.network.model

import ar.edu.itba.example.api.data.model.Product
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class NetworkNewProduct(
    var name: String?,
    var category: NetworkCategoryId? = null,
    var metadata: Map<String, String>? = null
)

@Serializable
class NetworkProduct(
    val id: Int,
    var name: String?,
    var category: NetworkCategory? = null,
    var metadata: Map<String, String>? = null,
    @Contextual
    var createdAt: Date? = null,
    @Contextual
    var updatedAt: Date? = null
) {
    fun asModel(): Product {
        return Product(
            id = id,
            name = name,
            category = category?.asModel(),
            metadata = metadata,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
