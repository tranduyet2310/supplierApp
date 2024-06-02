package com.example.suppileragrimart.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.suppileragrimart.model.OrderBasicInfo
import com.example.suppileragrimart.model.OrderInfo
import com.example.suppileragrimart.model.OrderInfoApiRequest
import com.example.suppileragrimart.repository.OrderRepository
import com.example.suppileragrimart.utils.ScreenState
import kotlinx.coroutines.flow.Flow

class OrderViewModel(application: Application) : AndroidViewModel(application) {
    private val orderRepository: OrderRepository = OrderRepository(application)

    fun getOrderBySupplierId(orderInfoApiRequest: OrderInfoApiRequest, datePattern: String
    ): Flow<PagingData<OrderInfo>> {
        return orderRepository.getOrderBySupplierId(orderInfoApiRequest, datePattern).cachedIn(viewModelScope)
    }

    fun updateOrderStatus(token: String, orderId: Long, orderStatus: String): LiveData<ScreenState<OrderBasicInfo?>> {
        return orderRepository.updateOrderStatus(token, orderId, orderStatus)
    }
}