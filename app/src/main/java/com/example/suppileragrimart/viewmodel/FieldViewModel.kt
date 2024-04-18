package com.example.suppileragrimart.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.suppileragrimart.model.FieldApiResponse
import com.example.suppileragrimart.model.FieldDetail
import com.example.suppileragrimart.model.MessageResponse
import com.example.suppileragrimart.repository.FieldRepository
import com.example.suppileragrimart.utils.ScreenState

class FieldViewModel(
    private val fieldRepository: FieldRepository = FieldRepository()
) : ViewModel() {

    public var fieldData: FieldApiResponse? = null
    fun getFieldBySupplierId(supplierId: Long): LiveData<ScreenState<ArrayList<FieldApiResponse>?>> {
        return fieldRepository.getFieldBySupplierId(supplierId)
    }

    fun createField(
        token: String,
        supplierId: Long,
        fieldApiResponse: FieldApiResponse
    ): LiveData<ScreenState<FieldApiResponse?>> {
        return fieldRepository.createField(token, supplierId, fieldApiResponse)
    }

    fun updateField(
        token: String,
        fieldId: Long,
        fieldApiResponse: FieldApiResponse
    ): LiveData<ScreenState<FieldApiResponse?>> {
        return fieldRepository.updateField(token, fieldId, fieldApiResponse)
    }

    fun completeField(
        token: String,
        fieldId: Long
    ): LiveData<ScreenState<FieldApiResponse?>> {
        return fieldRepository.completeField(token, fieldId)
    }

    fun createFieldDetail(
        token: String,
        fieldId: Long,
        fieldDetail: FieldDetail
    ): LiveData<ScreenState<FieldDetail?>> {
        return fieldRepository.createFieldDetail(token, fieldId, fieldDetail)
    }

    fun updateFieldDetail(
        token: String,
        fieldId: Long,
        fieldDetail: FieldDetail
    ): LiveData<ScreenState<FieldDetail?>> {
        return fieldRepository.updateFieldDetail(token, fieldId, fieldDetail)
    }

    fun deleteFieldDetail(
        token: String,
        fieldDetailId: Long,
    ): LiveData<ScreenState<MessageResponse?>> {
        return fieldRepository.deleteFieldDetail(token, fieldDetailId)
    }
}