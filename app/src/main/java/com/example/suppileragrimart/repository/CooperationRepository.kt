package com.example.suppileragrimart.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.suppileragrimart.model.Cooperation
import com.example.suppileragrimart.model.CooperationApiRequest
import com.example.suppileragrimart.network.CooperationPagingSource
import com.example.suppileragrimart.network.RetrofitClient
import com.example.suppileragrimart.utils.Constants
import com.example.suppileragrimart.utils.ScreenState
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalPagingApi::class)
class CooperationRepository(context: Context) {
    private val apiService = RetrofitClient.getInstance().getApi()

    fun getCooperationByFieldId(supplierId: Long, cooperationApiRequest: CooperationApiRequest
    ): Flow<PagingData<Cooperation>> {
        return Pager(
            PagingConfig(
                pageSize = Constants.DEFAULT_PAGE_SIZE.toInt(),
                enablePlaceholders = false
            )
        ) {
            CooperationPagingSource(apiService, supplierId, cooperationApiRequest)
        }.flow
    }

    fun updateCooperationStatus(token: String, cooperationId: Long, cooperationResponse: Cooperation
    ): LiveData<ScreenState<Cooperation?>> {
        val mutableLiveData = MutableLiveData<ScreenState<Cooperation?>>()
        mutableLiveData.postValue(ScreenState.Loading(null))

        apiService.updateCooperationStatus(token, cooperationId, cooperationResponse)
            .enqueue(object : Callback<Cooperation> {
                override fun onResponse(
                    call: Call<Cooperation>,
                    response: Response<Cooperation>
                ) {
                    if (response.isSuccessful) {
                        mutableLiveData.postValue(ScreenState.Success(response.body()))
                    } else {
                        mutableLiveData.postValue(ScreenState.Error(Constants.SERVER_ERROR, null))
                    }
                }

                override fun onFailure(call: Call<Cooperation>, t: Throwable) {
                    val message = t.message.toString()
                    mutableLiveData.postValue(ScreenState.Error(message, null))
                }
            })

        return mutableLiveData
    }
}