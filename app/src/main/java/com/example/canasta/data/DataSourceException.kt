package com.example.canasta.data

class DataSourceException(
    var code: Int? = null,
    message: String,
) : Exception(message)