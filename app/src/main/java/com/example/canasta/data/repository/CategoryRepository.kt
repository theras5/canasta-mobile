package ar.edu.itba.example.api.data.repository

import ar.edu.itba.example.api.data.model.Category
import ar.edu.itba.example.api.data.network.CategoryRemoteDataSource

class CategoryRepository(
    private val remoteDataSource: CategoryRemoteDataSource
) {
    suspend fun createCategory(category: Category): Category {
        return remoteDataSource.createCategory(category.asNetworkNewModel()).asModel()
    }

    suspend fun getCategories(): List<Category> {
        return remoteDataSource.getCategories().map { it.asModel() }
    }
}

