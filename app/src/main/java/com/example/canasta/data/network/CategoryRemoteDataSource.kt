package ar.edu.itba.example.api.data.network

import ar.edu.itba.example.api.data.network.api.CategoryApiService
import ar.edu.itba.example.api.data.network.model.NetworkCategory
import ar.edu.itba.example.api.data.network.model.NetworkNewCategory
import ar.edu.itba.example.api.data.network.model.NetworkPagedCategories
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

