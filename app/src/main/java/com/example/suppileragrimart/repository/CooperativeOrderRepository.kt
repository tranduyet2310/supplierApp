package com.example.suppileragrimart.repository

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.suppileragrimart.model.CooperativePayment
import com.example.suppileragrimart.network.CooperativeOrderPagingSource
import com.example.suppileragrimart.network.RetrofitClient
import com.example.suppileragrimart.utils.Constants
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalPagingApi::class)
class CooperativeOrderRepository(context: Context) {
    private val apiService = RetrofitClient.getInstance().getApi()

    fun getCooperativeOrderBySupplierId(supplierId: Long): Flow<PagingData<CooperativePayment>> {
        return Pager(
            PagingConfig(
                pageSize = Constants.DEFAULT_PAGE_SIZE.toInt(),
                enablePlaceholders = false
            )
        ) {
            CooperativeOrderPagingSource(apiService, supplierId)
        }.flow
    }
}