package com.example.suppileragrimart.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.suppileragrimart.model.Cooperation
import com.example.suppileragrimart.model.CooperationApiRequest
import com.example.suppileragrimart.repository.CooperationRepository
import com.example.suppileragrimart.utils.ScreenState
import kotlinx.coroutines.flow.Flow

class CooperationViewModel(application: Application) : AndroidViewModel(application) {
    private val cooperationRepository = CooperationRepository(application)

    fun getCooperation(supplierId: Long, cooperationApiRequest: CooperationApiRequest
    ): Flow<PagingData<Cooperation>> {
        return cooperationRepository.getCooperationByFieldId(supplierId, cooperationApiRequest)
            .cachedIn(viewModelScope)
    }

    fun updateCooperationStatus(token: String, cooperationId: Long, cooperationResponse: Cooperation
    ): LiveData<ScreenState<Cooperation?>> {
        return cooperationRepository.updateCooperationStatus(token, cooperationId, cooperationResponse)
    }
}