package com.example.canasta.data.remote.api

import com.example.canasta.data.remote.models.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Servicio de API para shopping lists y sus items
 */
interface ShoppingListApiService {

    /**
     * Obtener todas las shopping lists
     */
    @GET("api/shopping-lists")
    suspend fun getShoppingLists(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 100,
        @Query("sort_by") sortBy: String = "updatedAt",
        @Query("order") order: String = "DESC"
    ): Response<ShoppingListsPaginated>

    /**
     * Obtener una shopping list por ID
     */
    @GET("api/shopping-lists/{id}")
    suspend fun getShoppingListById(
        @Path("id") id: Long
    ): Response<ShoppingListResponse>

    /**
     * Crear una nueva shopping list
     */
    @POST("api/shopping-lists")
    suspend fun createShoppingList(
        @Body request: ShoppingListCreate
    ): Response<ShoppingListResponse>

    /**
     * Actualizar una shopping list
     */
    @PUT("api/shopping-lists/{id}")
    suspend fun updateShoppingList(
        @Path("id") id: Long,
        @Body request: ShoppingListUpdate
    ): Response<ShoppingListResponse>

    /**
     * Eliminar una shopping list
     */
    @DELETE("api/shopping-lists/{id}")
    suspend fun deleteShoppingList(
        @Path("id") id: Long
    ): Response<Unit>

    /**
     * Obtener los items de una shopping list
     */
    @GET("api/shopping-lists/{id}/items")
    suspend fun getListItems(
        @Path("id") listId: Long,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 100,
        @Query("category_id") categoryId: Long? = null
    ): Response<ListItemsPaginated>

    /**
     * Agregar un item a la shopping list
     */
    @POST("api/shopping-lists/{id}/items")
    suspend fun addListItem(
        @Path("id") listId: Long,
        @Body request: ListItemCreate
    ): Response<ListItemResponse>

    /**
     * Actualizar un item de la shopping list
     */
    @PUT("api/shopping-lists/{id}/items/{item_id}")
    suspend fun updateListItem(
        @Path("id") listId: Long,
        @Path("item_id") itemId: Long,
        @Body request: ListItemUpdate
    ): Response<ListItemResponse>

    /**
     * Toggle purchased status de un item
     */
    @PATCH("api/shopping-lists/{id}/items/{item_id}")
    suspend fun toggleListItemPurchased(
        @Path("id") listId: Long,
        @Path("item_id") itemId: Long,
        @Body request: TogglePurchasedRequest
    ): Response<ListItemResponse>

    /**
     * Eliminar un item de la shopping list
     */
    @DELETE("api/shopping-lists/{id}/items/{item_id}")
    suspend fun deleteListItem(
        @Path("id") listId: Long,
        @Path("item_id") itemId: Long
    ): Response<Unit>
}

