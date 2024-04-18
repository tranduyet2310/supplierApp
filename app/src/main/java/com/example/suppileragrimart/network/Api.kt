package com.example.suppileragrimart.network

import com.example.suppileragrimart.model.AESResponse
import com.example.suppileragrimart.model.CategoryApiResponse
import com.example.suppileragrimart.model.FieldApiResponse
import com.example.suppileragrimart.model.FieldDetail
import com.example.suppileragrimart.model.LoginApiResponse
import com.example.suppileragrimart.model.LoginRequest
import com.example.suppileragrimart.model.MessageResponse
import com.example.suppileragrimart.model.Product
import com.example.suppileragrimart.model.ProductApiResponse
import com.example.suppileragrimart.model.RegisterApiResponse
import com.example.suppileragrimart.model.Supplier
import com.example.suppileragrimart.model.Warehouse
import com.example.suppileragrimart.model.WarehouseApiResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.ArrayList

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

    @POST("/api/auth/key")
    suspend fun getSessionKey(@Body aesResponse: AESResponse): Response<AESResponse>

    // warehouse
    @POST("/api/suppliers/{supplierId}/warehousese")
    fun createWarehouse(
        @Header("Authorization") token: String,
        @Path("supplierId") supplierId: Long,
        @Body warehouse: Warehouse
    ): Call<Warehouse>

    @GET("/api/suppliers/{supplierId}/search")
    suspend fun searchProduct(
        @Path("supplierId") supplierId: Long,
        @Query("query") query: String,
        @Query("pageNo") pageNo: String,
        @Query("sortBy") sortBy: String,
        @Query("sortDir") sortDir: String
    ): Response<WarehouseApiResponse>

    @GET("/api/suppliers/warehouses")
    suspend fun getWarehouseBySupplierId(
        @Query("supplierId") supplierId: Long,
        @Query("pageNo") pageNo: String,
        @Query("sortBy") sortBy: String,
        @Query("sortDir") sortDir: String
    ): Response<WarehouseApiResponse>

    @GET("/api/suppliers/{supplierId}/warehouses")
    suspend fun getAllWarehouseBySupplierId(
        @Path("supplierId") supplierId: Long
    ): Response<ArrayList<Warehouse>>

    @DELETE("/api/suppliers/{supplierId}/warehouses/{id}")
    fun deleteWarehouse(
        @Header("Authorization") token: String,
        @Path("supplierId") supplierId: Long,
        @Path("id") warehouseId: Long
    ): Call<MessageResponse>

    @PUT("/api/suppliers/{supplierId}/warehouses/{id}")
    fun updateWarehouse(
        @Header("Authorization") token: String,
        @Path("supplierId") supplierId: Long,
        @Path("id") warehouseId: Long,
        @Body warehouse: Warehouse
    ): Call<Warehouse>

    @PATCH("/api/suppliers/{supplierId}/warehouses/{id}")
    fun updateState(
        @Header("Authorization") token: String,
        @Path("supplierId") supplierId: Long,
        @Path("id") warehouseId: Long,
        @Body messageResponse: MessageResponse
    ): Call<Warehouse>

    // Product
    @GET("api/products/suppliers/{supplierId}/v2")
    suspend fun getProductBySupplierId(
        @Path("supplierId") supplierId: Long,
        @Query("pageNo") pageNo: String,
        @Query("sortBy") sortBy: String,
        @Query("sortDir") sortDir: String
    ): Response<ProductApiResponse>

    @GET("/api/categories")
    suspend fun getAllCategories(): Response<ArrayList<CategoryApiResponse>>

    @Multipart
    @POST("/api/products/{id}/v2")
    fun createProduct(
        @Header("Authorization") token: String,
        @Path("id") supplierId: Long,
        @Part("productName") productName: String,
        @Part("description") description: String,
        @Part("standardPrice") standardPrice: Long,
        @Part("discountPrice") discountPrice: Long,
        @Part("quantity") quantity: Int,
        @Part("isActive") isActive: Boolean,
        @Part("isNew") isNew: Boolean,
        @Part("isAvailable") isAvailable: Boolean,
        @Part("categoryName") categoryName: String,
        @Part("subCategoryName") subCategoryName: String,
        @Part("warehouseName") warehouseName: String,
        @Part multipartFiles: List<MultipartBody.Part>
    ): Call<Product>

    // Garden
    @GET("api/field/{supplierId}")
    fun getCropsField(
        @Path("supplierId") supplierId: Long
    ): Call<ArrayList<FieldApiResponse>>

    @POST("api/field/{supplierId}")
    fun createField(
        @Header("Authorization") token: String,
        @Path("supplierId") supplierId: Long,
        @Body fieldApiResponse: FieldApiResponse
    ): Call<FieldApiResponse>

    @PATCH("api/field/{fieldId}")
    suspend fun updateYield(
        @Header("Authorization") token: String,
        @Path("fieldId") fieldId: Long,
        @Body fieldApiResponse: FieldApiResponse
    ): Response<FieldApiResponse>

    @PUT("api/field/{fieldId}")
    fun updateField(
        @Header("Authorization") token: String,
        @Path("fieldId") fieldId: Long,
        @Body fieldApiResponse: FieldApiResponse
    ): Call<FieldApiResponse>

    @PATCH("api/field/{fieldId}/complete")
    fun completeField(
        @Header("Authorization") token: String,
        @Path("fieldId") fieldId: Long
    ): Call<FieldApiResponse>

    // Field Detail
    @POST("api/field_detail/{fieldId}")
    fun createFieldDetail(
        @Header("Authorization") token: String,
        @Path("fieldId") fieldId: Long,
        @Body fieldDetail: FieldDetail
    ): Call<FieldDetail>

    @POST("api/field_detail/{fieldId}")
    suspend fun createFieldDetailV2(
        @Header("Authorization") token: String,
        @Path("fieldId") fieldId: Long,
        @Body fieldDetail: FieldDetail
    ): Response<FieldDetail>

    @PUT("api/field_detail/{fieldId}")
    suspend fun updateFieldDetailV2(
        @Header("Authorization") token: String,
        @Path("fieldId") fieldId: Long,
        @Body fieldDetail: FieldDetail
    ): Response<FieldDetail>

    @PUT("api/field_detail/{fieldId}")
    fun updateFieldDetail(
        @Header("Authorization") token: String,
        @Path("fieldId") fieldId: Long,
        @Body fieldDetail: FieldDetail
    ): Call<FieldDetail>

    @DELETE("api/field_detail/{fieldDetailId}")
    fun deleteFieldDetail(
        @Header("Authorization") token: String,
        @Path("fieldDetailId") fieldId: Long,
    ): Call<MessageResponse>

}