package com.example.canasta.data.model

import java.util.*

class Product(
    var id: Int?,
    var name: String?,
    var category: String?,
    var description: String?,
    var price: Double?,
    var brand: String?,
    var imageUrl: String?,
    var createdAt: Date?,
    var updatedAt: Date?
) {
    // Convenience constructors
    constructor(id: Int) : this(id, null, null, null, null, null, null, null, null)

    constructor(
        name: String,
        category: String,
        description: String? = null,
        price: Double? = null,
        brand: String? = null,
        imageUrl: String? = null
    ) : this(null, name, category, description, price, brand, imageUrl, null, null)


}
