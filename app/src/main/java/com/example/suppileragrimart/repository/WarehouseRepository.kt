package com.example.suppileragrimart.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.suppileragrimart.model.MessageResponse
import com.example.suppileragrimart.model.Warehouse
import com.example.suppileragrimart.model.WarehouseApiRequest
import com.example.suppileragrimart.network.RetrofitClient
import com.example.suppileragrimart.network.WarehousePagingSource
import com.example.suppileragrimart.utils.Constants.DEFAULT_PAGE_SIZE
import com.example.suppileragrimart.utils.ScreenState
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
@OptIn(ExperimentalPagingApi::class)
class WarehouseRepository(context: Context) {

    private val apiService = RetrofitClient.getInstance().getApi()
    fun createNewWarehouse(
        token: String,
        supplierId: Long,
        warehouse: Warehouse
    ): LiveData<ScreenState<Warehouse?>> {
        val mutableLiveData = MutableLiveData<ScreenState<Warehouse?>>()
        mutableLiveData.postValue(ScreenState.Loading(null))

        apiService.createWarehouse(token, supplierId, warehouse)
            .enqueue(object : Callback<Warehouse> {
                override fun onResponse(
                    call: Call<Warehouse>,
                    response: Response<Warehouse>
                ) {
                    if (response.isSuccessful) {
                        mutableLiveData.postValue(ScreenState.Success(response.body()))
                    } else {
                        val message = "Supplier không tồn tại"
                        mutableLiveData.postValue(ScreenState.Error(message, null))
                    }
                }

                override fun onFailure(call: Call<Warehouse>, t: Throwable) {
                    val message = t.message.toString()
                    mutableLiveData.postValue(ScreenState.Error(message, null))
                }
            })
        return mutableLiveData
    }

    fun updateWarehouse(
        token: String,
        supplierId: Long,
        warehouseId: Long,
        warehouse: Warehouse
    ): LiveData<ScreenState<Warehouse?>> {
        val mutableLiveData = MutableLiveData<ScreenState<Warehouse?>>()
        mutableLiveData.postValue(ScreenState.Loading(null))

        apiService.updateWarehouse(token, supplierId, warehouseId, warehouse)
            .enqueue(object : Callback<Warehouse> {
                override fun onResponse(
                    call: Call<Warehouse>,
                    response: Response<Warehouse>
                ) {
                    if (response.isSuccessful) {
                        mutableLiveData.postValue(ScreenState.Success(response.body()))
                    }
                }

                override fun onFailure(call: Call<Warehouse>, t: Throwable) {
                    val message = t.message.toString()
                    mutableLiveData.postValue(ScreenState.Error(message, null))
                }
            })
        return mutableLiveData
    }

    fun updateState(
        token: String,
        supplierId: Long,
        warehouseId: Long,
        messageResponse: MessageResponse
    ): LiveData<ScreenState<Warehouse?>> {
        val mutableLiveData = MutableLiveData<ScreenState<Warehouse?>>()
        mutableLiveData.postValue(ScreenState.Loading(null))

        apiService.updateState(token, supplierId, warehouseId, messageResponse)
            .enqueue(object : Callback<Warehouse> {
                override fun onResponse(
                    call: Call<Warehouse>,
                    response: Response<Warehouse>
                ) {
                    if (response.isSuccessful) {
                        mutableLiveData.postValue(ScreenState.Success(response.body()))
                    }
                }

                override fun onFailure(call: Call<Warehouse>, t: Throwable) {
                    val message = t.message.toString()
                    mutableLiveData.postValue(ScreenState.Error(message, null))
                }
            })
        return mutableLiveData
    }

    fun deleteWarehouse(
        token: String,
        supplierId: Long,
        warehouseId: Long
    ): LiveData<ScreenState<MessageResponse?>> {
        val mutableLiveData = MutableLiveData<ScreenState<MessageResponse?>>()
        mutableLiveData.postValue(ScreenState.Loading(null))

        apiService.deleteWarehouse(token, supplierId, warehouseId)
            .enqueue(object : Callback<MessageResponse> {
                override fun onResponse(
                    call: Call<MessageResponse>,
                    response: Response<MessageResponse>
                ) {
                    if (response.isSuccessful) {
                        mutableLiveData.postValue(ScreenState.Success(response.body()))
                    }
                }

                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    val message = t.message.toString()
                    mutableLiveData.postValue(ScreenState.Error(message, null))
                }
            })
        return mutableLiveData
    }

    fun getWarehouseBySupplierId(
        warehouseApiRequest: WarehouseApiRequest
//        secretKey: String,
//        iv: String
    ): Flow<PagingData<Warehouse>> {
        return Pager(
            PagingConfig(
                pageSize = DEFAULT_PAGE_SIZE.toInt(),
                enablePlaceholders = false
            )
        ) {
//            WarehousePagingSource(apiService, warehouseApiRequest, secretKey, iv)
            WarehousePagingSource(apiService, warehouseApiRequest)
        }.flow
    }
}