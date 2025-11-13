package com.example.canasta.data.network

import com.example.canasta.data.DataSourceException
import com.example.canasta.data.network.api.model.NetworkError
import kotlinx.serialization.json.Json
import retrofit2.Response
import java.io.IOException

abstract class RemoteDataSource {

    protected suspend fun <T> handleApiResponse(apiCall: suspend () -> Response<T>): T {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                response.body() ?: throw DataSourceException.Server(
                    code = response.code(),
                    message = "Empty response body"
                )
            } else {
                handleErrorResponse(response)
            }
        } catch (e: IOException) {
            throw DataSourceException.Network("Network error occurred", e)
        } catch (e: DataSourceException) {
            throw e
        } catch (e: Exception) {
            throw DataSourceException.Unknown("Unknown error occurred", e)
        }
    }

    private fun <T> handleErrorResponse(response: Response<T>): Nothing {
        val errorCode = response.code()
        val errorMessage = try {
            response.errorBody()?.string()?.let { errorBody ->
                Json.decodeFromString<NetworkError>(errorBody).message
            } ?: "HTTP $errorCode error"
        } catch (e: Exception) {
            "HTTP $errorCode error"
        }

        throw when (errorCode) {
            401 -> DataSourceException.Authentication(errorMessage)
            404 -> DataSourceException.NotFound(errorMessage)
            in 400..499 -> DataSourceException.Server(errorCode, errorMessage)
            in 500..599 -> DataSourceException.Server(errorCode, errorMessage)
            else -> DataSourceException.Unknown(errorMessage)
        }
    }
}
