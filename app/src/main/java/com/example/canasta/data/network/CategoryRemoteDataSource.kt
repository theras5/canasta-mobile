package com.example.canasta.data.network

import com.example.canasta.data.network.api.CategoryApiService
import com.example.canasta.data.network.model.NetworkCategory
import com.example.canasta.data.network.model.NetworkNewCategory
import com.example.canasta.data.network.model.NetworkPagedCategories
import kotlinx.serialization.json.JsonElement

class CategoryRemoteDataSource(
    private val categoryApiService: CategoryApiService
) : RemoteDataSource() {

    suspend fun createCategory(category: NetworkNewCategory): NetworkCategory {
        return handleApiResponse {
            categoryApiService.createCategory(category)
        }
    }

    suspend fun getCategories(): List<NetworkCategory> {
        val response: NetworkPagedCategories = handleApiResponse {
            categoryApiService.getCategories()
        }

        return response.data
    }
}

