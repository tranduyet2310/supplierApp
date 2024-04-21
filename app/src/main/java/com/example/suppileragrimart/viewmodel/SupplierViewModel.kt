package com.example.suppileragrimart.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.suppileragrimart.model.Image
import com.example.suppileragrimart.model.LoginApiResponse
import com.example.suppileragrimart.model.LoginRequest
import com.example.suppileragrimart.model.PasswordRequest
import com.example.suppileragrimart.model.Supplier
import com.example.suppileragrimart.repository.SupplierRepository
import com.example.suppileragrimart.utils.ScreenState
import okhttp3.MultipartBody
import java.security.PrivateKey
import java.security.PublicKey

class SupplierViewModel(
    private val supplierRepository: SupplierRepository = SupplierRepository()
) : ViewModel() {
    var supplier: Supplier? = null
    var isValidPublicKey: Boolean = false

    fun getSupplierById(supplierId: Long): LiveData<ScreenState<Supplier?>> {
        return supplierRepository.getSupplierById(supplierId);
    }

    fun updateGeneralInfo(
        token: String,
        supplierId: Long,
        supplier: Supplier
    ): LiveData<ScreenState<Supplier?>> {
        return supplierRepository.updateGeneralInfo(token, supplierId, supplier);
    }

    fun updateBankInfo(
        token: String,
        supplierId: Long,
        supplier: Supplier
    ): LiveData<ScreenState<Supplier?>> {
        return supplierRepository.updateBankInfo(token, supplierId, supplier);
    }

    fun getSupplierAvatar(
        supplierId: Long
    ): LiveData<ScreenState<Image?>> {
        return supplierRepository.getSupplierAvatar(supplierId);
    }

    fun changeSupplierAvatar(
        token: String,
        supplierId: Long,
        file: MultipartBody.Part
    ): LiveData<ScreenState<Supplier?>> {
        return supplierRepository.uploadAvatar(token, supplierId, file)
    }

    fun updateAccountInfo(
        token: String,
        supplierId: Long,
        supplier: Supplier
    ): LiveData<ScreenState<Supplier?>> {
        return supplierRepository.updateAccountInfo(token, supplierId, supplier);
    }

    fun changePassword(
        token: String,
        supplierId: Long,
        passwordRequest: PasswordRequest
    ): LiveData<ScreenState<Supplier?>> {
        return supplierRepository.changePassword(token, supplierId, passwordRequest);
    }
}