package ar.edu.itba.example.api.data.repository

import ar.edu.itba.example.api.data.model.Product
import ar.edu.itba.example.api.data.network.ProductRemoteDataSource
import kotlinx.serialization.json.JsonElement

class ProductRepository(
    private val remoteDataSource: ProductRemoteDataSource
) {
    suspend fun createProduct(product: Product): Product {
        return remoteDataSource.createProduct(product.asNetworkNewModel()).asModel()
    }
}

