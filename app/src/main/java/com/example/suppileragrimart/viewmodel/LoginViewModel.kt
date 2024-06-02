package com.example.suppileragrimart.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.suppileragrimart.model.LoginApiResponse
import com.example.suppileragrimart.model.LoginRequest
import com.example.suppileragrimart.repository.LoginRepository
import com.example.suppileragrimart.utils.ScreenState

class LoginViewModel (
    private val repository: LoginRepository = LoginRepository()
) : ViewModel() {

    fun getLoginResponseLiveData(loginRequest: LoginRequest): LiveData<ScreenState<LoginApiResponse?>> {
        return repository.getLoginResponseData(loginRequest)
    }
}