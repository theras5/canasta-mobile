package ar.edu.itba.example.api.data.model

import ar.edu.itba.example.api.data.network.model.NetworkCategory
import ar.edu.itba.example.api.data.network.model.NetworkNewCategory
import java.util.Date

class Category(
    var id: Int?,
    var name: String?,
    var metadata: Map<String, String>?,
    var createdAt: Date?,
    var updatedAt: Date?
) {
    constructor(id: Int) : this(id, null, null, null, null)

    constructor(name: String, metadata: Map<String, String>? = null) : this(null, name, metadata, null, null)

    fun asNetworkNewModel(): NetworkNewCategory {
        return NetworkNewCategory(
            name = name,
            metadata = metadata
        )
    }

    fun asNetworkModel(): NetworkCategory {
        return NetworkCategory(
            id = id!!,
            name = name,
            metadata = metadata
        )
    }
}

