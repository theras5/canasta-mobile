package com.example.canasta.data.network.api.model

import com.example.canasta.data.model.ShoppingList
import com.example.canasta.data.model.ShoppingListItem
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class NetworkShoppingList(
    var id: Int,
    var name: String?,
    var description: String?,
    var isShared: Boolean?,
    var ownerId: Int?,
    var items: List<NetworkShoppingListItem>?,
    @Contextual
    var createdAt: Date? = null,
    @Contextual
    var updatedAt: Date? = null
) {
    fun asModel(): ShoppingList {
        return ShoppingList(
            id = id,
            name = name,
            description = description,
            isShared = isShared,
            ownerId = ownerId,
            items = items?.map { it.asModel() },
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}

@Serializable
data class NetworkNewShoppingList(
    var name: String?,
    var description: String?,
    var isShared: Boolean?
)

@Serializable
data class NetworkShoppingListItem(
    var id: Int,
    var shoppingListId: Int?,
    var productId: Int?,
    var productName: String?,
    var quantity: Int?,
    var isCompleted: Boolean?,
    var notes: String?,
    @Contextual
    var createdAt: Date? = null,
    @Contextual
    var updatedAt: Date? = null
) {
    fun asModel(): ShoppingListItem {
        return ShoppingListItem(
            id = id,
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

@Serializable
data class NetworkNewShoppingListItem(
    var productId: Int?,
    var productName: String?,
    var quantity: Int?,
    var notes: String?
)

@Serializable
data class NetworkPagedShoppingLists(
    val data: List<NetworkShoppingList>,
    val total: Int,
    val page: Int,
    val pageSize: Int
)
