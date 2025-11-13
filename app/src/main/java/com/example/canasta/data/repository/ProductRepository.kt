package com.example.canasta.data.repository

import com.example.canasta.data.model.Product
import com.example.canasta.data.network.ProductRemoteDataSource
import kotlinx.serialization.json.JsonElement

class ProductRepository(
    private val remoteDataSource: ProductRemoteDataSource
) {
    suspend fun createProduct(product: Product): Product {
        return remoteDataSource.createProduct(product.asNetworkNewModel()).asModel()
    }
}

