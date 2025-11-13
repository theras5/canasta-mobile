package ar.edu.itba.example.api.data.network.model

import ar.edu.itba.example.api.data.model.Category
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class NetworkCategoryId(
    var id: Int
)

@Serializable
data class NetworkNewCategory(
    var name: String?,
    var metadata: Map<String, String>? = null
)

@Serializable
data class NetworkCategory(
    var id: Int,
    var name: String?,
    var metadata: Map<String, String>? = null,
    @Contextual
    var createdAt: Date? = null,
    @Contextual
    var updatedAt: Date? = null
) {
    fun asModel(): Category {
        return Category(
            id = id,
            name = name,
            metadata = metadata,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}

