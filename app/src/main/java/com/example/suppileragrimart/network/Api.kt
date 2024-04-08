package com.example.suppileragrimart.network

import com.example.suppileragrimart.model.AESResponse
import com.example.suppileragrimart.model.LoginApiResponse
import com.example.suppileragrimart.model.LoginRequest
import com.example.suppileragrimart.model.RegisterApiResponse
import com.example.suppileragrimart.model.Supplier
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface Api {
    @POST("/api/auth/register/supplier")
    fun createUser(@Body supplier: Supplier): Call<RegisterApiResponse>

    @POST("/api/auth/register/supplier")
    suspend fun createSupplier(@Body supplier: Supplier): Response<RegisterApiResponse>

    @POST("/api/auth/login/supplier")
    fun loginUser(@Body loginRequest: LoginRequest): Call<LoginApiResponse>

    @POST("/api/auth/keys")
    fun getAESKey(@Body aesResponse: AESResponse): Call<AESResponse>

    @POST("/api/auth/keys")
    suspend fun createAESKeyRequest(@Body aesResponse: AESResponse): Response<AESResponse>
}