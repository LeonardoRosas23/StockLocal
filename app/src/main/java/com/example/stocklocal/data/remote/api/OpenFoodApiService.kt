package com.example.stocklocal.data.remote.api

import com.example.stocklocal.data.remote.model.OpenFoodResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface OpenFoodApiService {

    @GET("product/{productName}.json")
    suspend fun searchProduct(
        @Path("productName") productName: String
    ): OpenFoodResponse
}