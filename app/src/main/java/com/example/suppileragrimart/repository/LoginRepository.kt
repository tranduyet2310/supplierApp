package com.example.suppileragrimart.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.suppileragrimart.model.LoginApiResponse
import com.example.suppileragrimart.model.LoginRequest
import com.example.suppileragrimart.network.RetrofitClient
import com.example.suppileragrimart.utils.Constants.LOGIN_MESSAGE
import com.example.suppileragrimart.utils.ScreenState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginRepository {
    fun getLoginResponseData(loginRequest: LoginRequest): LiveData<ScreenState<LoginApiResponse?>> {
        val mutableLiveData = MutableLiveData<ScreenState<LoginApiResponse?>>()
        mutableLiveData.postValue(ScreenState.Loading(null))

        RetrofitClient.getInstance().getApi().loginUser(loginRequest)
            .enqueue(object : Callback<LoginApiResponse> {
                override fun onResponse(
                    call: Call<LoginApiResponse>,
                    response: Response<LoginApiResponse>
                ) {
                    if (response.isSuccessful) {
                        mutableLiveData.postValue(ScreenState.Success(response.body()))
                    } else {
                        mutableLiveData.postValue(ScreenState.Error(LOGIN_MESSAGE, null))
                    }
                }

                override fun onFailure(call: Call<LoginApiResponse>, t: Throwable) {
                    val message = t.message.toString()
                    mutableLiveData.postValue(ScreenState.Error(message, null))
                }
            })

        return mutableLiveData
    }
}