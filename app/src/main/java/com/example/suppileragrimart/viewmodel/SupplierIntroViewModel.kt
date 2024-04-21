package com.example.suppileragrimart.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.suppileragrimart.model.SupplierIntro
import com.example.suppileragrimart.repository.SupplierIntroRepository
import com.example.suppileragrimart.utils.ScreenState

class SupplierIntroViewModel(
    private val supplierIntroRepository: SupplierIntroRepository = SupplierIntroRepository()
) : ViewModel() {
    fun getAllSupplierIntro(supplierId: Long): LiveData<ScreenState<ArrayList<SupplierIntro>?>> {
        return supplierIntroRepository.getSupplierIntro(supplierId)
    }
}