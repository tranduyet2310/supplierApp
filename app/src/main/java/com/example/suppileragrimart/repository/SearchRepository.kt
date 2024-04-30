package com.example.suppileragrimart.repository

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.suppileragrimart.model.Product
import com.example.suppileragrimart.model.SearchApiRequest
import com.example.suppileragrimart.model.Warehouse
import com.example.suppileragrimart.network.RetrofitClient
import com.example.suppileragrimart.network.SearchProductPagingSource
import com.example.suppileragrimart.network.WarehouseSearchPagingSource
import com.example.suppileragrimart.utils.Constants
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalPagingApi::class)
class SearchRepository(context: Context) {
    private val apiService = RetrofitClient.getInstance().getApi()
    fun searchWarehouse(
        searchApiRequest: SearchApiRequest,
        supplierId: Long
    ): Flow<PagingData<Warehouse>> {
        return Pager(
            PagingConfig(
                pageSize = Constants.DEFAULT_PAGE_SIZE.toInt(),
                enablePlaceholders = false
            )
        ) {
            WarehouseSearchPagingSource(apiService, searchApiRequest, supplierId)
        }.flow
    }

    fun searchProduct(
        searchApiRequest: SearchApiRequest,
        supplierId: Long
    ): Flow<PagingData<Product>> {
        return Pager(
            PagingConfig(
                pageSize = Constants.DEFAULT_PAGE_SIZE.toInt(),
                enablePlaceholders = false
            )
        ) {
            SearchProductPagingSource(apiService, supplierId, searchApiRequest)
        }.flow
    }
}