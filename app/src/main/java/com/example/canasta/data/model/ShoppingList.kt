package com.example.canasta.data.model

import com.example.canasta.data.network.api.model.NetworkNewShoppingList
import com.example.canasta.data.network.api.model.NetworkShoppingList
import java.util.*

class ShoppingList(
    var id: Int?,
    var name: String?,
    var description: String?,
    var isShared: Boolean?,
    var ownerId: Int?,
    var items: List<ShoppingListItem>?,
    var createdAt: Date?,
    var updatedAt: Date?
) {
    // Convenience constructors
    constructor(id: Int) : this(id, null, null, null, null, null, null, null)

    constructor(
        name: String,
        description: String? = null,
        isShared: Boolean = false
    ) : this(null, name, description, isShared, null, null, null, null)

    // Conversion methods
    fun asNetworkNewModel(): NetworkNewShoppingList {
        return NetworkNewShoppingList(
            name = name,
            description = description,
            isShared = isShared
        )
    }

    fun asNetworkModel(): NetworkShoppingList {
        return NetworkShoppingList(
            id = id!!,
            name = name,
            description = description,
            isShared = isShared,
            ownerId = ownerId,
            items = items?.map { it.asNetworkModel() },
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
