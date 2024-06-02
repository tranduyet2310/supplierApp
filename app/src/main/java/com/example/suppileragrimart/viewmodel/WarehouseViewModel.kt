package com.example.suppileragrimart.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.suppileragrimart.model.MessageResponse
import com.example.suppileragrimart.model.Warehouse
import com.example.suppileragrimart.model.WarehouseApiRequest
import com.example.suppileragrimart.repository.WarehouseRepository
import com.example.suppileragrimart.utils.ScreenState
import kotlinx.coroutines.flow.Flow

class WarehouseViewModel(application: Application): AndroidViewModel(application) {
    private val warehouseRepository = WarehouseRepository(application)
    fun createWarehouse(token: String, supplierId: Long, warehouse: Warehouse
    ): LiveData<ScreenState<Warehouse?>> {
        return warehouseRepository.createNewWarehouse(token, supplierId, warehouse)
    }

    fun updateWarehouse(token: String, supplierId: Long, warehouseId: Long, warehouse: Warehouse
    ): LiveData<ScreenState<Warehouse?>> {
        return warehouseRepository.updateWarehouse(token, supplierId, warehouseId, warehouse)
    }

    fun updateState(token: String, supplierId: Long, warehouseId: Long, messageResponse: MessageResponse
    ): LiveData<ScreenState<Warehouse?>> {
        return warehouseRepository.updateState(token, supplierId, warehouseId, messageResponse)
    }

    fun deleteWarehouse(token: String, supplierId: Long, warehouseId: Long
    ): LiveData<ScreenState<MessageResponse?>> {
        return warehouseRepository.deleteWarehouse(token, supplierId, warehouseId)
    }

    fun getWarehouseBySupplierId(warehouseApiRequest: WarehouseApiRequest): Flow<PagingData<Warehouse>> {
        return warehouseRepository.getWarehouseBySupplierId(warehouseApiRequest).cachedIn(viewModelScope)
    }
}