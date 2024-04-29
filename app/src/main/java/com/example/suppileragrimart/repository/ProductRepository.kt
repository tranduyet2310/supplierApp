package com.example.suppileragrimart.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.suppileragrimart.model.MessageResponse
import com.example.suppileragrimart.model.Product
import com.example.suppileragrimart.model.ProductApiRequest
import com.example.suppileragrimart.network.ProductPagingSource
import com.example.suppileragrimart.network.RetrofitClient
import com.example.suppileragrimart.utils.Constants
import com.example.suppileragrimart.utils.ScreenState
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalPagingApi::class)
class ProductRepository(context: Context) {
    private val apiService = RetrofitClient.getInstance().getApi()

    fun getProductBySupplierId(productApiRequest: ProductApiRequest): Flow<PagingData<Product>> {
        return Pager(
            PagingConfig(
                pageSize = Constants.DEFAULT_PAGE_SIZE.toInt(),
                enablePlaceholders = false
            )
        ) { ProductPagingSource(apiService, productApiRequest) }.flow
    }

    fun createProduct(
        token: String,
        supplierId: Long,
        product: Product,
        file: List<MultipartBody.Part>
    ): LiveData<ScreenState<Product?>> {
        val mutableLiveData = MutableLiveData<ScreenState<Product?>>()
        mutableLiveData.postValue(ScreenState.Loading(null))

        val productName = product.productName
        val description = product.description
        val standardPrice = product.standardPrice
        val discountPrice = product.discountPrice
        val quantity = product.productQuantity
        val isActive = product.isActive
        val isNew = product.isNew
        val isAvailable = product.isAvailable
        val categoryName = product.productCategory
        val subCategoryName = product.productSubcategory
        val warehouseName = product.warehouseName

        apiService.createProduct(
            token, supplierId,
            productName, description, standardPrice, discountPrice, quantity, isActive,
            isNew, isAvailable, categoryName, subCategoryName, warehouseName, file
        ).enqueue(object : Callback<Product> {
            override fun onResponse(
                call: Call<Product>,
                response: Response<Product>
            ) {
                if (response.isSuccessful) {
                    mutableLiveData.postValue(ScreenState.Success(response.body()))
                }
                else {
                    mutableLiveData.postValue(ScreenState.Error("Lỗi phía máy chủ", null))
                }
            }

            override fun onFailure(call: Call<Product>, t: Throwable) {
                val message = t.message.toString()
                mutableLiveData.postValue(ScreenState.Error(message, null))
            }
        })

        return mutableLiveData
    }

    fun updateProductAll(
        token: String,
        supplierId: Long,
        productId: Long,
        product: Product,
        file: List<MultipartBody.Part>
    ): LiveData<ScreenState<Product?>> {
        val mutableLiveData = MutableLiveData<ScreenState<Product?>>()
        mutableLiveData.postValue(ScreenState.Loading(null))

        val productName = product.productName
        val description = product.description
        val standardPrice = product.standardPrice
        val discountPrice = product.discountPrice
        val quantity = product.productQuantity
        val isActive = product.isActive
        val isNew = product.isNew
        val isAvailable = product.isAvailable
        val categoryName = product.productCategory
        val subCategoryName = product.productSubcategory
        val warehouseName = product.warehouseName

        apiService.updateProductAll(token, supplierId, productId, productName, description,
            standardPrice, discountPrice, quantity, isActive, isNew, isAvailable, categoryName,
            subCategoryName, warehouseName, file).enqueue(object : Callback<Product> {
            override fun onResponse(
                call: Call<Product>,
                response: Response<Product>
            ) {
                if (response.isSuccessful) {
                    mutableLiveData.postValue(ScreenState.Success(response.body()))
                }
                else {
                    mutableLiveData.postValue(ScreenState.Error("Lỗi phía máy chủ", null))
                }
            }

            override fun onFailure(call: Call<Product>, t: Throwable) {
                val message = t.message.toString()
                mutableLiveData.postValue(ScreenState.Error(message, null))
            }
        })

        return mutableLiveData
    }

    fun updateProductInfo(token: String, supplierId: Long, productId: Long, product: Product):
            LiveData<ScreenState<Product?>> {
        val mutableLiveData = MutableLiveData<ScreenState<Product?>>()
        mutableLiveData.postValue(ScreenState.Loading(null))

        apiService.updateProductInfo(token, supplierId, productId, product).enqueue(object : Callback<Product> {
            override fun onResponse(
                call: Call<Product>,
                response: Response<Product>
            ) {
                if (response.isSuccessful) {
                    mutableLiveData.postValue(ScreenState.Success(response.body()))
                }
                else {
                    mutableLiveData.postValue(ScreenState.Error("Lỗi phía máy chủ", null))
                }
            }

            override fun onFailure(call: Call<Product>, t: Throwable) {
                val message = t.message.toString()
                mutableLiveData.postValue(ScreenState.Error(message, null))
            }
        })

        return mutableLiveData
    }

    fun updateProductState(token: String, productId: Long): LiveData<ScreenState<Product?>> {
        val mutableLiveData = MutableLiveData<ScreenState<Product?>>()
        mutableLiveData.postValue(ScreenState.Loading(null))
        apiService.updateProductState(token, productId)
            .enqueue(object : Callback<Product> {
            override fun onResponse(call: Call<Product>, response: Response<Product>) {
                if (response.isSuccessful) {
                    mutableLiveData.postValue(ScreenState.Success(response.body()))
                }
                else {
                    mutableLiveData.postValue(ScreenState.Error("Lỗi phía máy chủ", null))
                }
            }
            override fun onFailure(call: Call<Product>, t: Throwable) {
                val message = t.message.toString()
                mutableLiveData.postValue(ScreenState.Error(message, null))
            }
        })

        return mutableLiveData
    }

    fun deleteProduct(token: String, supplierId: Long, productId: Long): LiveData<ScreenState<MessageResponse?>> {
        val mutableLiveData = MutableLiveData<ScreenState<MessageResponse?>>()
        mutableLiveData.postValue(ScreenState.Loading(null))
        apiService.deleteProduct(token, supplierId, productId)
            .enqueue(object : Callback<MessageResponse> {
                override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {

                    if (response.isSuccessful) {
                        mutableLiveData.postValue(ScreenState.Success(response.body()))
                    }
                    else {
                        mutableLiveData.postValue(ScreenState.Error("Lỗi phía máy chủ", null))
                    }
                }

                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    val message = t.message.toString()
                    mutableLiveData.postValue(ScreenState.Error(message, null))
                }
            })

        return mutableLiveData
    }
}