package com.example.stocklocal.data.remote.api

import com.example.stocklocal.data.remote.model.LoginRequest
import com.example.stocklocal.data.remote.model.LoginResponse
import com.example.stocklocal.data.remote.model.SearchResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface OpenFoodApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("products/search")
    suspend fun searchProduct(
        @Query("q") productName: String,
        @Query("limit") limit: Int = 1
    ): SearchResponse
}