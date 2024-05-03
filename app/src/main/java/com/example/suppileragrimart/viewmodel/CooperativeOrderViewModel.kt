package com.example.suppileragrimart.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.suppileragrimart.model.CooperativePayment
import com.example.suppileragrimart.repository.CooperativeOrderRepository
import kotlinx.coroutines.flow.Flow

class CooperativeOrderViewModel(application: Application) : AndroidViewModel(application) {
    private val cooperativeOrderRepository: CooperativeOrderRepository = CooperativeOrderRepository(application)

    fun getCooperativeOrderBySupplierId(supplierId: Long): Flow<PagingData<CooperativePayment>> {
        return cooperativeOrderRepository.getCooperativeOrderBySupplierId(supplierId).cachedIn(viewModelScope)
    }
}