package com.example.suppileragrimart.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.suppileragrimart.model.AESResponse
import com.example.suppileragrimart.model.RegisterApiResponse
import com.example.suppileragrimart.model.Supplier
import com.example.suppileragrimart.network.RetrofitClient
import com.example.suppileragrimart.utils.ScreenState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterRepository {
    fun getRegisterResponseData(supplier: Supplier): LiveData<ScreenState<RegisterApiResponse?>> {
        val mutableLiveData = MutableLiveData<ScreenState<RegisterApiResponse?>>()
        mutableLiveData.postValue(ScreenState.Loading(null))

        RetrofitClient.getInstance().getApi().createUser(supplier)
            .enqueue(object : Callback<RegisterApiResponse> {
                override fun onResponse(
                    call: Call<RegisterApiResponse>,
                    response: Response<RegisterApiResponse>
                ) {
                    if (response.isSuccessful) {
                        mutableLiveData.postValue(ScreenState.Success(response.body()))
                    } else {
                        val message = "Địa chỉ email đã được đăng ký"
                        mutableLiveData.postValue(ScreenState.Error(message, null))
                    }
                }

                override fun onFailure(call: Call<RegisterApiResponse>, t: Throwable) {
                    val message = t.message.toString()
                    mutableLiveData.postValue(ScreenState.Error(message, null))
                }
            })
        return mutableLiveData
    }

    fun getAESKey(aesResponse: AESResponse): LiveData<ScreenState<AESResponse?>> {
        val mutableLiveData = MutableLiveData<ScreenState<AESResponse?>>()
        mutableLiveData.postValue(ScreenState.Loading(null))

        RetrofitClient.getInstance().getApi().getAESKey(aesResponse)
            .enqueue(object : Callback<AESResponse> {
                override fun onResponse(
                    call: Call<AESResponse>,
                    response: Response<AESResponse>
                ) {
                    if (response.isSuccessful) {
                        mutableLiveData.postValue(ScreenState.Success(response.body()))
                    } else {
                        val message = "Tạo khóa thất bại"
                        mutableLiveData.postValue(ScreenState.Error(message, null))
                    }
                }

                override fun onFailure(call: Call<AESResponse>, t: Throwable) {
                    val message = t.message.toString()
                    mutableLiveData.postValue(ScreenState.Error(message, null))
                }
            })
        return mutableLiveData
    }
}