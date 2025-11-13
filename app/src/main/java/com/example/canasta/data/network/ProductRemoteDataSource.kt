package com.example.canasta.data.network

import com.example.canasta.data.network.api.ProductApiService
import com.example.canasta.data.network.api.model.NetworkNewProduct
import com.example.canasta.data.network.api.model.NetworkPagedProducts
import com.example.canasta.data.network.api.model.NetworkProduct

class ProductRemoteDataSource(
    private val productApiService: ProductApiService
) : RemoteDataSource() {

    suspend fun createProduct(product: NetworkNewProduct): NetworkProduct {
        return handleApiResponse {
            productApiService.createProduct(product)
        }
    }

    suspend fun getProducts(
        page: Int = 1,
        pageSize: Int = 20,
        category: String? = null,
        search: String? = null
    ): NetworkPagedProducts {
        return handleApiResponse {
            productApiService.getProducts(page, pageSize, category, search)
        }
    }

    suspend fun getProduct(id: Int): NetworkProduct {
        return handleApiResponse {
            productApiService.getProduct(id)
        }
    }

    suspend fun updateProduct(id: Int, product: NetworkNewProduct): NetworkProduct {
        return handleApiResponse {
            productApiService.updateProduct(id, product)
        }
    }

    suspend fun deleteProduct(id: Int) {
        handleApiResponse<Unit> {
            productApiService.deleteProduct(id)
        }
    }

    suspend fun getCategories(): List<String> {
        return handleApiResponse {
            productApiService.getCategories()
        }
    }
}
