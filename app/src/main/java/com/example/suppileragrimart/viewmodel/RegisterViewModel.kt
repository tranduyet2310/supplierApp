package com.example.suppileragrimart.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.suppileragrimart.model.AESResponse
import com.example.suppileragrimart.model.RegisterApiResponse
import com.example.suppileragrimart.model.Supplier
import com.example.suppileragrimart.repository.RegisterRepository
import com.example.suppileragrimart.utils.ScreenState

class RegisterViewModel(
    private val registerRepository: RegisterRepository = RegisterRepository()
) : ViewModel() {

    fun getRegisterResponseLiveData(supplier: Supplier): LiveData<ScreenState<RegisterApiResponse?>> {
        return registerRepository.getRegisterResponseData(supplier)
    }

    fun getAESKey(aesResponse: AESResponse): LiveData<ScreenState<AESResponse?>>{
        return registerRepository.getAESKey(aesResponse);
    }
}