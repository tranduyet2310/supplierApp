package com.example.suppileragrimart.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.suppileragrimart.model.OrderBasicInfo
import com.example.suppileragrimart.model.OrderInfo
import com.example.suppileragrimart.model.OrderInfoApiRequest
import com.example.suppileragrimart.network.OrderPagingSource
import com.example.suppileragrimart.network.RetrofitClient
import com.example.suppileragrimart.utils.Constants
import com.example.suppileragrimart.utils.ScreenState
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalPagingApi::class)
class OrderRepository(context: Context) {
    private val apiService = RetrofitClient.getInstance().getApi()

    fun getOrderBySupplierId(
        orderInfoApiRequest: OrderInfoApiRequest,
        datePattern: String
    ): Flow<PagingData<OrderInfo>> {
        return Pager(
            PagingConfig(
                pageSize = Constants.DEFAULT_PAGE_SIZE.toInt(),
                enablePlaceholders = false
            )
        ) { OrderPagingSource(apiService, datePattern, orderInfoApiRequest) }.flow
    }

    fun updateOrderStatus(token: String, orderId: Long, orderStatus: String): LiveData<ScreenState<OrderBasicInfo?>> {
        val mutableLiveData = MutableLiveData<ScreenState<OrderBasicInfo?>>()
        mutableLiveData.postValue(ScreenState.Loading(null))

        apiService.updateOrderStatus(token, orderId, orderStatus)
            .enqueue(object : Callback<OrderBasicInfo> {
                override fun onResponse(
                    call: Call<OrderBasicInfo>,
                    response: Response<OrderBasicInfo>
                ) {
                    if (response.isSuccessful) {
                        mutableLiveData.postValue(ScreenState.Success(response.body()))
                    } else {
                        mutableLiveData.postValue(ScreenState.Error(Constants.SERVER_ERROR, null))
                    }
                }

                override fun onFailure(call: Call<OrderBasicInfo>, t: Throwable) {
                    val message = t.message.toString()
                    mutableLiveData.postValue(ScreenState.Error(message, null))
                }
            })

        return mutableLiveData
    }
}