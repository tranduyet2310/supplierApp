package com.example.suppileragrimart.network

import com.example.suppileragrimart.model.AESResponse
import com.example.suppileragrimart.model.CategoryApiResponse
import com.example.suppileragrimart.model.Cooperation
import com.example.suppileragrimart.model.CooperationApiResponse
import com.example.suppileragrimart.model.CooperativeOrderResponse
import com.example.suppileragrimart.model.CooperativePayment
import com.example.suppileragrimart.model.FieldApiResponse
import com.example.suppileragrimart.model.FieldDetail
import com.example.suppileragrimart.model.Image
import com.example.suppileragrimart.model.LoginApiResponse
import com.example.suppileragrimart.model.LoginRequest
import com.example.suppileragrimart.model.MessageResponse
import com.example.suppileragrimart.model.NotificationMessage
import com.example.suppileragrimart.model.OrderBasicInfo
import com.example.suppileragrimart.model.OrderInfoResponse
import com.example.suppileragrimart.model.OrderStatistic
import com.example.suppileragrimart.model.PasswordRequest
import com.example.suppileragrimart.model.Product
import com.example.suppileragrimart.model.ProductApiResponse
import com.example.suppileragrimart.model.RegisterApiResponse
import com.example.suppileragrimart.model.ReviewInfo
import com.example.suppileragrimart.model.ReviewStatisticResponse
import com.example.suppileragrimart.model.Supplier
import com.example.suppileragrimart.model.SupplierIntro
import com.example.suppileragrimart.model.UserAddress
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

    @PATCH("/api/suppliers/{supplierId}/rsa")
    fun updateRSAPubKey(
        @Header("Authorization") token: String,
        @Path("supplierId") supplierId: Long,
        @Body aesResponse: AESResponse
    ): Call<MessageResponse>

    @PATCH("/api/suppliers/{supplierId}/rsa")
    suspend fun updateRSAKey(
        @Header("Authorization") token: String,
        @Path("supplierId") supplierId: Long,
        @Body aesResponse: AESResponse
    ): Response<MessageResponse>

    @GET("/api/suppliers/{supplierId}/rsa")
    suspend fun getRSAPublicKey(@Path("supplierId") supplierId: Long): Response<AESResponse>

    // warehouse
    @POST("/api/suppliers/{supplierId}/warehouses")
    fun createWarehouse(
        @Header("Authorization") token: String,
        @Path("supplierId") supplierId: Long,
        @Body warehouse: Warehouse
    ): Call<Warehouse>

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
    @GET("api/products/suppliers/{supplierId}")
    suspend fun getProductBySupplierId(
        @Path("supplierId") supplierId: Long,
        @Query("pageNo") pageNo: String,
        @Query("sortBy") sortBy: String,
        @Query("sortDir") sortDir: String
    ): Response<ProductApiResponse>

    @GET("/api/categories")
    suspend fun getAllCategories(): Response<ArrayList<CategoryApiResponse>>

    @Multipart
    @POST("/api/products/suppliers/{id}")
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

    @PATCH("/api/products/{productId}/state")
    fun updateProductState(
        @Header("Authorization") token: String,
        @Path("productId") productId: Long
    ): Call<Product>

    @DELETE("/api/products/{supplierId}/{productId}")
    fun deleteProduct(
        @Header("Authorization") token: String,
        @Path("supplierId") supplierId: Long,
        @Path("productId") productId: Long
    ): Call<MessageResponse>
    @Multipart
    @PUT("/api/products/{supplierId}/{productId}")
    fun updateProductAll(
        @Header("Authorization") token: String,
        @Path("supplierId") supplierId: Long,
        @Path("productId") productId: Long,
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

    @PATCH("/api/products/{supplierId}/{productId}/info")
    fun updateProductInfo(
        @Header("Authorization") token: String,
        @Path("supplierId") supplierId: Long,
        @Path("productId") productId: Long,
        @Body product: Product
    ): Call<Product>

    // Supplier Intro
    @Multipart
    @POST("/api/shop/{id}")
    suspend fun createSupplierIntro(
        @Header("Authorization") token: String,
        @Path("id") supplierId: Long,
        @Part("description") description: String,
        @Part("type") type: String,
        @Part multipartFiles: List<MultipartBody.Part>
    ): Response<SupplierIntro>

    @Multipart
    @PUT("/api/shop/{id}/{introId}")
    suspend fun updateSupplierIntro(
        @Header("Authorization") token: String,
        @Path("id") supplierId: Long,
        @Path("introId") introId: Long,
        @Part("description") description: String,
        @Part("type") type: String,
        @Part multipartFiles: List<MultipartBody.Part>
    ): Response<SupplierIntro>

    @PATCH("/api/shop/{introId}")
    suspend fun updateDescriptionIntro(
        @Header("Authorization") token: String,
        @Path("introId") introId: Long,
        @Body supplierIntro: SupplierIntro
    ): Response<SupplierIntro>

    @GET("api/shop/{supplierId}")
    fun getAllSupplierIntro(
        @Path("supplierId") supplierId: Long
    ): Call<ArrayList<SupplierIntro>>

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

    @GET("api/field/{fieldId}/detail")
    suspend fun getFieldById(
        @Path("fieldId") fieldId: Long
    ): Response<FieldApiResponse>

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

    // Cooperation
    @GET("api/cooperation/{supplierId}/list/{fieldId}")
    suspend fun getCooperationBySupplierId(
        @Path("supplierId") supplierId: Long,
        @Path("fieldId") fieldId: Long,
        @Query("pageNo") pageNo: String,
        @Query("sortBy") sortBy: String,
        @Query("sortDir") sortDir: String
    ): Response<CooperationApiResponse>

    @PATCH("api/cooperation/{cooperationId}")
    fun updateCooperationStatus(
        @Header("Authorization") token: String,
        @Path("cooperationId") cooperationId: Long,
        @Body cooperationResponse: Cooperation
    ): Call<Cooperation>

    @GET("api/cooperation/{cooperationId}/general")
    suspend fun getCooperationByIdV2(@Path("cooperationId") cooperationId: Long): Response<Cooperation>

    @GET("/api/users/addresses/{id}")
    suspend fun getAddressByIdV2(@Path("id") addressId: Long): Response<UserAddress>

    @GET("api/cooperation/{fieldId}/calculate/{supplierId}")
    suspend fun calculateCurrentTotal(
        @Path("fieldId") fieldId: Long,
        @Path("supplierId") supplierId: Long
    ): Response<MessageResponse>

    @GET("api/cooperation/{fieldId}/remaining")
    suspend fun calculateRemainingCooperation(@Path("fieldId") fieldId: Long): Response<MessageResponse>

    // Supplier
    @GET("api/suppliers/{supplierId}")
    fun getSupplierById(
        @Header("Authorization") token: String,
        @Path("supplierId") supplierId: Long
    ): Call<Supplier>

    @GET("api/suppliers/{supplierId}")
    suspend fun getSupplierByIdV2(
        @Header("Authorization") token: String,
        @Path("supplierId") supplierId: Long
    ): Response<Supplier>

    @GET("api/suppliers/{supplierId}/v2")
    suspend fun getSupplierInfoById(
        @Header("Authorization") token: String,
        @Path("supplierId") supplierId: Long
    ): Response<Supplier>

    @PATCH("api/suppliers/{supplierId}/general")
    fun updateGeneralInfo(
        @Header("Authorization") token: String,
        @Path("supplierId") supplierId: Long,
        @Body supplier: Supplier
    ): Call<Supplier>

    @PATCH("api/suppliers/{supplierId}/bank")
    fun updateBankInfo(
        @Header("Authorization") token: String,
        @Path("supplierId") supplierId: Long,
        @Body supplier: Supplier
    ): Call<Supplier>

    @GET("api/suppliers/{supplierId}/image")
    fun getSupplierAvatar(
        @Path("supplierId") supplierId: Long
    ): Call<Image>

    @GET("api/suppliers/{supplierId}/image")
    suspend fun getSupplierAvatarV2(
        @Path("supplierId") supplierId: Long
    ): Response<Image>

    @Multipart
    @PATCH("api/suppliers/{supplierId}/avatar")
    fun updateAvatar(
        @Header("Authorization") token: String,
        @Path("supplierId") supplierId: Long,
        @Part file: MultipartBody.Part
    ): Call<Supplier>

    @PATCH("api/suppliers/{supplierId}/account")
    fun updateAccountInfo(
        @Header("Authorization") token: String,
        @Path("supplierId") supplierId: Long,
        @Body supplier: Supplier
    ): Call<Supplier>

    @PATCH("api/suppliers/{id}/password")
    fun changePassword(
        @Header("Authorization") token: String,
        @Path("id") supplierId: Long,
        @Body password: PasswordRequest
    ): Call<Supplier>

    // CooperativeOrder
    @GET("/api/cooperative/{supplierId}/list")
    suspend fun getCooperativeOrderBySupplierId(
        @Path("supplierId") userId: Long,
        @Query("pageNo") pageNo: String,
        @Query("sortBy") sortBy: String,
        @Query("sortDir") sortDir: String
    ): Response<CooperativeOrderResponse>

    @GET("/api/cooperative/{id}/info")
    suspend fun getCooperativeOrderById(@Path("id") id: Long): Response<CooperativePayment>

    // Order
    @GET("api/orders/{supplierId}/list")
    suspend fun getOrderBySupplierId(
        @Path("supplierId") supplierId: Long,
        @Query("date") date: String,
        @Query("pageNo") pageNo: String,
        @Query("sortBy") sortBy: String,
        @Query("sortDir") sortDir: String
    ): Response<OrderInfoResponse>

    @GET("api/orders/{supplierId}/info")
    suspend fun getOrderById(@Path("supplierId") supplierId: Long): Response<OrderBasicInfo>

    @PATCH("/api/orders/{orderId}/status")
    fun updateOrderStatus(
        @Header("Authorization") token: String,
        @Path("orderId") orderId: Long,
        @Query("orderStatus") orderStatus: String
    ): Call<OrderBasicInfo>

    @GET("api/users/addresses/{id}")
    suspend fun getAddressById(@Path("id") addressId: Long): Response<UserAddress>

    @GET("/api/orders/{supplierId}/statistic")
    suspend fun getOrderStatistic(
        @Path("supplierId") supplierId: Long,
        @Query("date") date: String
    ): Response<List<OrderStatistic>>

    @GET("/api/orders/{supplierId}/recent")
    suspend fun getRecentOrderStatistic(
        @Path("supplierId") supplierId: Long,
        @Query("date") date: String
    ): Response<List<OrderStatistic>>

    @GET("/api/orders/{supplierId}/total")
    suspend fun getStatistic(
        @Path("supplierId") supplierId: Long,
        @Query("date") date: String
    ): Response<OrderStatistic>

    // Review
    @GET("/api/reviews/{supplierId}/info")
    suspend fun getReviewInfo(
        @Path("supplierId") supplierId: Long,
    ): Response<List<ReviewInfo>>

    @GET("api/reviews/{productId}/average")
    suspend fun averageRating(@Path("productId") productId: Long): Response<ReviewStatisticResponse>

    @GET("/api/products/{productId}")
    suspend fun getProductById(@Path("productId") productId: Long): Response<Product>

    // search
    @GET("/api/products/suppliers/{supplierId}/search")
    suspend fun searchProduct(
        @Path("supplierId") supplierId: Long,
        @Query("query") query: String,
        @Query("pageNo") pageNo: String,
        @Query("sortBy") sortBy: String,
        @Query("sortDir") sortDir: String
    ): Response<ProductApiResponse>

    @GET("/api/suppliers/{supplierId}/search")
    suspend fun searchWarehouse(
        @Path("supplierId") supplierId: Long,
        @Query("query") query: String,
        @Query("pageNo") pageNo: String,
        @Query("sortBy") sortBy: String,
        @Query("sortDir") sortDir: String
    ): Response<WarehouseApiResponse>
    // notification
    @POST("api/notification")
    suspend fun sendNotification(
        @Body notificationMessage: NotificationMessage
    ): Response<MessageResponse>
    //
    @PATCH("api/suppliers/{id}/fcm")
    suspend fun updateFcmToken(
        @Header("Authorization") token: String,
        @Path("id") supplierId: Long,
        @Query("token") fcmToken: String
    ): Response<Supplier>
}