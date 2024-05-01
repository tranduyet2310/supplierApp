package com.example.suppileragrimart.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.suppileragrimart.model.AESResponse
import com.example.suppileragrimart.model.Image
import com.example.suppileragrimart.model.MessageResponse
import com.example.suppileragrimart.model.PasswordRequest
import com.example.suppileragrimart.model.Supplier
import com.example.suppileragrimart.network.RetrofitClient
import com.example.suppileragrimart.utils.Constants
import com.example.suppileragrimart.utils.ScreenState
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SupplierRepository {
    private val apiService = RetrofitClient.getInstance().getApi()

    fun getSupplierById(supplierId: Long): LiveData<ScreenState<Supplier?>> {
        val mutableLiveData = MutableLiveData<ScreenState<Supplier?>>()
        mutableLiveData.postValue(ScreenState.Loading(null))

        apiService.getSupplierById(supplierId)
            .enqueue(object : Callback<Supplier> {
                override fun onResponse(
                    call: Call<Supplier>,
                    response: Response<Supplier>
                ) {
                    if (response.isSuccessful) {
                        mutableLiveData.postValue(ScreenState.Success(response.body()))
                    }
                }

                override fun onFailure(call: Call<Supplier>, t: Throwable) {
                    val message = t.message.toString()
                    mutableLiveData.postValue(ScreenState.Error(message, null))
                }
            })

        return mutableLiveData
    }

    fun updateGeneralInfo(
        token: String,
        supplierId: Long,
        supplier: Supplier
    ): LiveData<ScreenState<Supplier?>> {
        val mutableLiveData = MutableLiveData<ScreenState<Supplier?>>()
        mutableLiveData.postValue(ScreenState.Loading(null))

        apiService.updateGeneralInfo(token, supplierId, supplier)
            .enqueue(object : Callback<Supplier> {
                override fun onResponse(
                    call: Call<Supplier>,
                    response: Response<Supplier>
                ) {
                    if (response.isSuccessful) {
                        mutableLiveData.postValue(ScreenState.Success(response.body()))
                    }
                }

                override fun onFailure(call: Call<Supplier>, t: Throwable) {
                    val message = t.message.toString()
                    mutableLiveData.postValue(ScreenState.Error(message, null))
                }
            })

        return mutableLiveData
    }

    fun updateBankInfo(
        token: String,
        supplierId: Long,
        supplier: Supplier
    ): LiveData<ScreenState<Supplier?>> {
        val mutableLiveData = MutableLiveData<ScreenState<Supplier?>>()
        mutableLiveData.postValue(ScreenState.Loading(null))

        apiService.updateBankInfo(token, supplierId, supplier)
            .enqueue(object : Callback<Supplier> {
                override fun onResponse(
                    call: Call<Supplier>,
                    response: Response<Supplier>
                ) {
                    if (response.isSuccessful) {
                        mutableLiveData.postValue(ScreenState.Success(response.body()))
                    }
                }

                override fun onFailure(call: Call<Supplier>, t: Throwable) {
                    val message = t.message.toString()
                    mutableLiveData.postValue(ScreenState.Error(message, null))
                }
            })

        return mutableLiveData
    }

    fun getSupplierAvatar(supplierId: Long): LiveData<ScreenState<Image?>> {
        val mutableLiveData = MutableLiveData<ScreenState<Image?>>()
        mutableLiveData.postValue(ScreenState.Loading(null))

        apiService.getSupplierAvatar(supplierId)
            .enqueue(object : Callback<Image> {
                override fun onResponse(
                    call: Call<Image>,
                    response: Response<Image>
                ) {
                    if (response.isSuccessful) {
                        mutableLiveData.postValue(ScreenState.Success(response.body()))
                    }
                }

                override fun onFailure(call: Call<Image>, t: Throwable) {
                    val message = t.message.toString()
                    mutableLiveData.postValue(ScreenState.Error(message, null))
                }
            })

        return mutableLiveData
    }

    fun uploadAvatar(
        token: String,
        supplierId: Long,
        file: MultipartBody.Part
    ): LiveData<ScreenState<Supplier?>> {
        val mutableLiveData = MutableLiveData<ScreenState<Supplier?>>()
        mutableLiveData.postValue(ScreenState.Loading(null))

        apiService.updateAvatar(token, supplierId, file)
            .enqueue(object : Callback<Supplier> {
                override fun onResponse(
                    call: Call<Supplier>,
                    response: Response<Supplier>
                ) {
                    if (response.isSuccessful) {
                        mutableLiveData.postValue(ScreenState.Success(response.body()))
                    } else {
                        mutableLiveData.postValue(ScreenState.Error(Constants.SERVER_ERROR, null))
                    }
                }

                override fun onFailure(call: Call<Supplier>, t: Throwable) {
                    val message = t.message.toString()
                    mutableLiveData.postValue(ScreenState.Error(message, null))
                }
            })

        return mutableLiveData
    }

    fun updateAccountInfo(
        token: String,
        supplierId: Long,
        supplier: Supplier
    ): LiveData<ScreenState<Supplier?>> {
        val mutableLiveData = MutableLiveData<ScreenState<Supplier?>>()
        mutableLiveData.postValue(ScreenState.Loading(null))

        apiService.updateAccountInfo(token, supplierId, supplier)
            .enqueue(object : Callback<Supplier> {
                override fun onResponse(
                    call: Call<Supplier>,
                    response: Response<Supplier>
                ) {
                    if (response.isSuccessful) {
                        mutableLiveData.postValue(ScreenState.Success(response.body()))
                    } else {
                        mutableLiveData.postValue(ScreenState.Error(Constants.SERVER_ERROR, null))
                    }
                }

                override fun onFailure(call: Call<Supplier>, t: Throwable) {
                    val message = t.message.toString()
                    mutableLiveData.postValue(ScreenState.Error(message, null))
                }
            })

        return mutableLiveData
    }

    fun changePassword(
        token: String,
        supplierId: Long,
        password: PasswordRequest
    ): LiveData<ScreenState<Supplier?>> {
        val mutableLiveData = MutableLiveData<ScreenState<Supplier?>>()
        mutableLiveData.postValue(ScreenState.Loading(null))

        apiService.changePassword(token, supplierId, password)
            .enqueue(object : Callback<Supplier> {
                override fun onResponse(
                    call: Call<Supplier>,
                    response: Response<Supplier>
                ) {
                    if (response.isSuccessful) {
                        mutableLiveData.postValue(ScreenState.Success(response.body()))
                    } else {
                        mutableLiveData.postValue(ScreenState.Error(Constants.SERVER_ERROR, null))
                    }
                }

                override fun onFailure(call: Call<Supplier>, t: Throwable) {
                    val message = t.message.toString()
                    mutableLiveData.postValue(ScreenState.Error(message, null))
                }
            })

        return mutableLiveData
    }

    fun updateRSAKey(
        token: String,
        supplierId: Long,
        dto: AESResponse
    ): LiveData<ScreenState<MessageResponse?>> {
        val mutableLiveData = MutableLiveData<ScreenState<MessageResponse?>>()
        mutableLiveData.postValue(ScreenState.Loading(null))

        apiService.updateRSAPubKey(token, supplierId, dto)
            .enqueue(object : Callback<MessageResponse> {
                override fun onResponse(
                    call: Call<MessageResponse>,
                    response: Response<MessageResponse>
                ) {
                    if (response.isSuccessful) {
                        mutableLiveData.postValue(ScreenState.Success(response.body()))
                    } else {
                        mutableLiveData.postValue(ScreenState.Error(Constants.SERVER_ERROR, null))
                    }
                }

                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    val message = t.message.toString()
                    mutableLiveData.postValue(ScreenState.Error(message, null))
                }
            })

        return mutableLiveData
    }
}