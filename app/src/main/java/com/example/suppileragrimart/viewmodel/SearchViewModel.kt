package com.example.suppileragrimart.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.suppileragrimart.model.SearchApiRequest
import com.example.suppileragrimart.model.Warehouse
import com.example.suppileragrimart.repository.SearchRepository
import kotlinx.coroutines.flow.Flow

class SearchViewModel(application: Application): AndroidViewModel(application) {
    private val searchRepository = SearchRepository(application)

    fun searchWarehouse(searchApiRequest: SearchApiRequest, supplierId: Long): Flow<PagingData<Warehouse>> {
        return searchRepository.searchWarehouse(searchApiRequest, supplierId).cachedIn(viewModelScope)
    }
}