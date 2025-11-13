package com.example.canasta.data.repository

import com.example.canasta.data.model.Category
import com.example.canasta.data.network.CategoryRemoteDataSource

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

