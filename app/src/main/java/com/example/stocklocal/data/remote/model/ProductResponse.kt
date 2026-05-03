package com.example.stocklocal.data.remote.model

import com.google.gson.annotations.SerializedName

data class ProductResponse(
    @SerializedName("product_name")
    val productName: String? = null,
    @SerializedName("categories")
    val categories: String? = null,
    @SerializedName("generic_name")
    val genericName: String? = null
)

data class OpenFoodResponse(
    @SerializedName("status")
    val status: Int = 0,
    @SerializedName("product")
    val product: ProductResponse? = null
)