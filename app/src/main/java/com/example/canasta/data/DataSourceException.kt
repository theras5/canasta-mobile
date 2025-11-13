package com.example.canasta.data

sealed class DataSourceException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class Network(message: String, cause: Throwable? = null) : DataSourceException(message, cause)
    class Server(val code: Int, message: String) : DataSourceException(message)
    class Authentication(message: String = "Authentication failed") : DataSourceException(message)
    class NotFound(message: String = "Resource not found") : DataSourceException(message)
    class Unknown(message: String, cause: Throwable? = null) : DataSourceException(message, cause)
}
