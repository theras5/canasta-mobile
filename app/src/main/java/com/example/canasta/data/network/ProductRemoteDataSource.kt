package ar.edu.itba.example.api.data.network

import ar.edu.itba.example.api.data.network.api.ProductApiService
import ar.edu.itba.example.api.data.network.model.NetworkNewProduct
import ar.edu.itba.example.api.data.network.model.NetworkProduct

class ProductRemoteDataSource(
    private val productApiService: ProductApiService
) : RemoteDataSource() {

    suspend fun createProduct(product: NetworkNewProduct): NetworkProduct {
        return handleApiResponse {
            productApiService.createProduct(product)
        }
    }
}

