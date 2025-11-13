package com.example.canasta.data.network

import com.example.canasta.data.network.api.ShoppingListApiService
import com.example.canasta.data.network.api.model.NetworkNewShoppingList
import com.example.canasta.data.network.api.model.NetworkNewShoppingListItem
import com.example.canasta.data.network.api.model.NetworkPagedShoppingLists
import com.example.canasta.data.network.api.model.NetworkShoppingList
import com.example.canasta.data.network.api.model.NetworkShoppingListItem

class ShoppingListRemoteDataSource(
    private val shoppingListApiService: ShoppingListApiService
) : RemoteDataSource() {

    suspend fun createShoppingList(list: NetworkNewShoppingList): NetworkShoppingList {
        return handleApiResponse {
            shoppingListApiService.createShoppingList(list)
        }
    }

    suspend fun getShoppingLists(
        page: Int = 1,
        pageSize: Int = 20,
        isShared: Boolean? = null,
        search: String? = null
    ): NetworkPagedShoppingLists {
        return handleApiResponse {
            shoppingListApiService.getShoppingLists(page, pageSize, isShared, search)
        }
    }

    suspend fun getShoppingList(id: Int): NetworkShoppingList {
        return handleApiResponse {
            shoppingListApiService.getShoppingList(id)
        }
    }

    suspend fun updateShoppingList(id: Int, list: NetworkNewShoppingList): NetworkShoppingList {
        return handleApiResponse {
            shoppingListApiService.updateShoppingList(id, list)
        }
    }

    suspend fun deleteShoppingList(id: Int) {
        handleApiResponse<Unit> {
            shoppingListApiService.deleteShoppingList(id)
        }
    }

    // Shopping List Items methods
    suspend fun addItemToList(listId: Int, item: NetworkNewShoppingListItem): NetworkShoppingListItem {
        return handleApiResponse {
            shoppingListApiService.addItemToList(listId, item)
        }
    }

    suspend fun getListItems(listId: Int, isCompleted: Boolean? = null): List<NetworkShoppingListItem> {
        return handleApiResponse {
            shoppingListApiService.getListItems(listId, isCompleted)
        }
    }

    suspend fun updateListItem(
        listId: Int,
        itemId: Int,
        item: NetworkNewShoppingListItem
    ): NetworkShoppingListItem {
        return handleApiResponse {
            shoppingListApiService.updateListItem(listId, itemId, item)
        }
    }

    suspend fun toggleItemCompleted(listId: Int, itemId: Int): NetworkShoppingListItem {
        return handleApiResponse {
            shoppingListApiService.toggleItemCompleted(listId, itemId)
        }
    }

    suspend fun removeItemFromList(listId: Int, itemId: Int) {
        handleApiResponse<Unit> {
            shoppingListApiService.removeItemFromList(listId, itemId)
        }
    }

    // Sharing methods
    suspend fun shareList(id: Int, email: String) {
        val shareData = mapOf("email" to email)
        handleApiResponse<Unit> {
            shoppingListApiService.shareList(id, shareData)
        }
    }

    suspend fun unshareList(id: Int) {
        handleApiResponse<Unit> {
            shoppingListApiService.unshareList(id)
        }
    }
}
