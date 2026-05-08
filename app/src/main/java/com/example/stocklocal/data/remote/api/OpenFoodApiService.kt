package com.example.stocklocal.data.remote.api

import com.example.stocklocal.data.remote.model.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenFoodApiService {

    @GET("search.php")
    suspend fun searchProduct(
        @Query("s") productName: String
    ): SearchResponse
}