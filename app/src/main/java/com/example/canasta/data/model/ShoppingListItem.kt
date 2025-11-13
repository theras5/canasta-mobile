package com.example.canasta.data.model

import com.example.canasta.data.network.api.model.NetworkNewShoppingListItem
import com.example.canasta.data.network.api.model.NetworkShoppingListItem
import java.util.*

class ShoppingListItem(
    var id: Int?,
    var shoppingListId: Int?,
    var productId: Int?,
    var productName: String?,
    var quantity: Int?,
    var isCompleted: Boolean?,
    var notes: String?,
    var createdAt: Date?,
    var updatedAt: Date?
) {
    // Convenience constructors
    constructor(id: Int) : this(id, null, null, null, null, null, null, null, null)

    constructor(
        productId: Int,
        productName: String,
        quantity: Int = 1,
        notes: String? = null
    ) : this(null, null, productId, productName, quantity, false, notes, null, null)

    // Conversion methods
    fun asNetworkNewModel(): NetworkNewShoppingListItem {
        return NetworkNewShoppingListItem(
            productId = productId,
            productName = productName,
            quantity = quantity,
            notes = notes
        )
    }

    fun asNetworkModel(): NetworkShoppingListItem {
        return NetworkShoppingListItem(
            id = id!!,
            shoppingListId = shoppingListId,
            productId = productId,
            productName = productName,
            quantity = quantity,
            isCompleted = isCompleted,
            notes = notes,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
