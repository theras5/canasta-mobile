package com.example.canasta.data.network

import com.example.canasta.data.network.api.ProductApiService
import com.example.canasta.data.network.model.NetworkNewProduct
import com.example.canasta.data.network.model.NetworkProduct

class ProductRemoteDataSource(
    private val productApiService: ProductApiService
) : RemoteDataSource() {

    suspend fun createProduct(product: NetworkNewProduct): NetworkProduct {
        return handleApiResponse {
            productApiService.createProduct(product)
        }
    }
}

