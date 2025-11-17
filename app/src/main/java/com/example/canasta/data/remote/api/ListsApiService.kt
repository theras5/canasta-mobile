package com.example.canasta.data.remote.api

import com.example.canasta.data.remote.models.ListItemCreateDto
import com.example.canasta.data.remote.models.ListItemDto
import com.example.canasta.data.remote.models.ListItemPagedResponseDto
import com.example.canasta.data.remote.models.ShoppingListCreateDto
import com.example.canasta.data.remote.models.ShoppingListDto
import com.example.canasta.data.remote.models.ShoppingListPagedResponseDto
import com.example.canasta.data.remote.models.TogglePurchasedBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ListsApiService {

    @GET("api/shopping-lists")
    suspend fun getShoppingLists(
        @Query("name") name: String? = null,
        @Query("page") page: Int? = null,
        @Query("per_page") perPage: Int? = null
    ): ShoppingListPagedResponseDto

    @POST("api/shopping-lists")
    suspend fun createShoppingList(
        @Body body: ShoppingListCreateDto
    ): ShoppingListDto

    @GET("api/shopping-lists/{id}/items")
    suspend fun getItemsForList(
        @Path("id") listId: Long,
        @Query("purchased") purchased: Boolean? = null,
        @Query("page") page: Int? = null,
        @Query("per_page") perPage: Int? = null,
        @Query("sort_by") sortBy: String? = null,
        @Query("order") order: String? = null,
        @Query("pantry_id") pantryId: Long? = null,
        @Query("category_id") categoryId: Long? = null,
        @Query("search") search: String? = null
    ): ListItemPagedResponseDto

    @POST("api/shopping-lists/{id}/items")
    suspend fun addItemToList(
        @Path("id") listId: Long,
        @Body item: ListItemCreateDto
    ): ListItemDto

    @PATCH("api/shopping-lists/{id}/items/{item_id}")
    suspend fun togglePurchased(
        @Path("id") listId: Long,
        @Path("item_id") itemId: Long,
        @Body body: TogglePurchasedBody
    ): ListItemDto
}

