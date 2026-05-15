package com.example.stocklocal.data.remote.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    @SerializedName("accessToken")
    val accessToken: String? = null,
    @SerializedName("message")
    val message: String? = null
)

data class ProductResponse(
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("category")
    val category: String? = null
)

data class SearchResponse(
    @SerializedName("products")
    val products: List<ProductResponse>? = null
)