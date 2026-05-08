package com.example.stocklocal.data.remote.model

import com.google.gson.annotations.SerializedName

data class ProductResponse(
    @SerializedName("strMeal")
    val title: String? = null,
    @SerializedName("strCategory")
    val category: String? = null
)

data class OpenFoodResponse(
    @SerializedName("status")
    val status: Int = 0,
    @SerializedName("product")
    val product: ProductResponse? = null
)

data class SearchResponse(
    @SerializedName("meals")
    val products: List<ProductResponse>? = null
)