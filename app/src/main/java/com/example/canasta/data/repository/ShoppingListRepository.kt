package com.example.canasta.data.repository

import com.example.canasta.data.model.ShoppingList
import com.example.canasta.data.model.ShoppingListItem
import com.example.canasta.data.network.ShoppingListRemoteDataSource

class ShoppingListRepository(
    private val remoteDataSource: ShoppingListRemoteDataSource
) {
    suspend fun createShoppingList(list: ShoppingList): ShoppingList {
        return remoteDataSource.createShoppingList(list.asNetworkNewModel()).asModel()
    }

    suspend fun getShoppingLists(
        page: Int = 1,
        pageSize: Int = 20,
        isShared: Boolean? = null,
        search: String? = null
    ): List<ShoppingList> {
        val response = remoteDataSource.getShoppingLists(page, pageSize, isShared, search)
        return response.data.map { it.asModel() }
    }

    suspend fun getShoppingList(id: Int): ShoppingList {
        return remoteDataSource.getShoppingList(id).asModel()
    }

    suspend fun updateShoppingList(list: ShoppingList): ShoppingList {
        return remoteDataSource.updateShoppingList(list.id!!, list.asNetworkNewModel()).asModel()
    }

    suspend fun deleteShoppingList(id: Int) {
        remoteDataSource.deleteShoppingList(id)
    }

    // Shopping List Items methods
    suspend fun addItemToList(listId: Int, item: ShoppingListItem): ShoppingListItem {
        return remoteDataSource.addItemToList(listId, item.asNetworkNewModel()).asModel()
    }

    suspend fun getListItems(listId: Int, isCompleted: Boolean? = null): List<ShoppingListItem> {
        return remoteDataSource.getListItems(listId, isCompleted).map { it.asModel() }
    }

    suspend fun updateListItem(
        listId: Int,
        item: ShoppingListItem
    ): ShoppingListItem {
        return remoteDataSource.updateListItem(listId, item.id!!, item.asNetworkNewModel()).asModel()
    }

    suspend fun toggleItemCompleted(listId: Int, itemId: Int): ShoppingListItem {
        return remoteDataSource.toggleItemCompleted(listId, itemId).asModel()
    }

    suspend fun removeItemFromList(listId: Int, itemId: Int) {
        remoteDataSource.removeItemFromList(listId, itemId)
    }

    // Sharing methods
    suspend fun shareList(id: Int, email: String) {
        remoteDataSource.shareList(id, email)
    }

    suspend fun unshareList(id: Int) {
        remoteDataSource.unshareList(id)
    }
}
