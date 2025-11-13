package com.example.canasta.data.repository

import com.example.canasta.data.model.Product
import com.example.canasta.data.network.ProductRemoteDataSource
import com.example.canasta.data.network.api.model.NetworkNewProduct

class ProductRepository(
    private val remoteDataSource: ProductRemoteDataSource
) {
    suspend fun createProduct(product: Product): Product {
        val networkProduct = NetworkNewProduct(
            name = product.name,
            category = product.category,
            description = product.description,
            price = product.price,
            brand = product.brand,
            imageUrl = product.imageUrl
        )
        return remoteDataSource.createProduct(networkProduct).asModel()
    }

    suspend fun getProducts(
        page: Int = 1,
        pageSize: Int = 20,
        category: String? = null,
        search: String? = null
    ): List<Product> {
        val response = remoteDataSource.getProducts(page, pageSize, category, search)
        return response.data.map { it.asModel() }
    }

    suspend fun getProduct(id: Int): Product {
        return remoteDataSource.getProduct(id).asModel()
    }

    suspend fun updateProduct(product: Product): Product {
        val networkProduct = NetworkNewProduct(
            name = product.name,
            category = product.category,
            description = product.description,
            price = product.price,
            brand = product.brand,
            imageUrl = product.imageUrl
        )
        return remoteDataSource.updateProduct(product.id!!, networkProduct).asModel()
    }

    suspend fun deleteProduct(id: Int) {
        remoteDataSource.deleteProduct(id)
    }

    suspend fun getCategories(): List<String> {
        return remoteDataSource.getCategories()
    }
}
