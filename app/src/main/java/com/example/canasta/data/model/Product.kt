package com.example.canasta.data.model

import com.example.canasta.data.network.model.NetworkCategoryId
import com.example.canasta.data.network.model.NetworkNewProduct
import com.example.canasta.data.network.model.NetworkProduct
import java.util.Date

class Product(
    var id: Int?,
    var name: String?,
    var category: Category? = null,
    var metadata: Map<String, String>? = null,
    var createdAt: Date? = null,
    var updatedAt: Date? = null
) {
    constructor(name: String?, categoryId: Int?, metadata: Map<String, String>? = null) : this(null, name,
        if (categoryId != null) Category(categoryId) else null, metadata
    )

    fun asNetworkNewModel(): NetworkNewProduct {
        return if (category != null)
            NetworkNewProduct(
                category =  NetworkCategoryId(category!!.id!!),
                name = name,
                metadata = metadata
            )
        else
            NetworkNewProduct(
                name = name,
                metadata = metadata
            )
    }

    fun asNetworkModel(): NetworkProduct {
        return NetworkProduct(
            id = id!!,
            category = category?.asNetworkModel(),
            name = name,
            metadata = metadata,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
