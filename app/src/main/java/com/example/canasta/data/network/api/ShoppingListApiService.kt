package com.example.canasta.data.network.api

import com.example.canasta.data.network.api.model.NetworkNewShoppingList
import com.example.canasta.data.network.api.model.NetworkNewShoppingListItem
import com.example.canasta.data.network.api.model.NetworkPagedShoppingLists
import com.example.canasta.data.network.api.model.NetworkShoppingList
import com.example.canasta.data.network.api.model.NetworkShoppingListItem
import retrofit2.Response
import retrofit2.http.*

interface ShoppingListApiService {
    @POST("lists")
    suspend fun createShoppingList(@Body listData: NetworkNewShoppingList): Response<NetworkShoppingList>

    @GET("lists")
    suspend fun getShoppingLists(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("isShared") isShared: Boolean? = null,
        @Query("search") search: String? = null
    ): Response<NetworkPagedShoppingLists>

    @GET("lists/{id}")
    suspend fun getShoppingList(@Path("id") id: Int): Response<NetworkShoppingList>

    @PUT("lists/{id}")
    suspend fun updateShoppingList(
        @Path("id") id: Int,
        @Body listData: NetworkNewShoppingList
    ): Response<NetworkShoppingList>

    @DELETE("lists/{id}")
    suspend fun deleteShoppingList(@Path("id") id: Int): Response<Unit>

    // Shopping List Items endpoints
    @POST("lists/{listId}/items")
    suspend fun addItemToList(
        @Path("listId") listId: Int,
        @Body itemData: NetworkNewShoppingListItem
    ): Response<NetworkShoppingListItem>

    @GET("lists/{listId}/items")
    suspend fun getListItems(
        @Path("listId") listId: Int,
        @Query("isCompleted") isCompleted: Boolean? = null
    ): Response<List<NetworkShoppingListItem>>

    @PUT("lists/{listId}/items/{itemId}")
    suspend fun updateListItem(
        @Path("listId") listId: Int,
        @Path("itemId") itemId: Int,
        @Body itemData: NetworkNewShoppingListItem
    ): Response<NetworkShoppingListItem>

    @PATCH("lists/{listId}/items/{itemId}/toggle")
    suspend fun toggleItemCompleted(
        @Path("listId") listId: Int,
        @Path("itemId") itemId: Int
    ): Response<NetworkShoppingListItem>

    @DELETE("lists/{listId}/items/{itemId}")
    suspend fun removeItemFromList(
        @Path("listId") listId: Int,
        @Path("itemId") itemId: Int
    ): Response<Unit>

    // Sharing endpoints
    @POST("lists/{id}/share")
    suspend fun shareList(
        @Path("id") id: Int,
        @Body shareData: Map<String, String>
    ): Response<Unit>

    @DELETE("lists/{id}/share")
    suspend fun unshareList(@Path("id") id: Int): Response<Unit>
}
