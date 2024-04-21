package com.example.suppileragrimart.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.suppileragrimart.model.Image
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
    fun getSupplierById(supplierId: Long): LiveData<ScreenState<Supplier?>> {
        val mutableLiveData = MutableLiveData<ScreenState<Supplier?>>()
        mutableLiveData.postValue(ScreenState.Loading(null))

        RetrofitClient.getInstance().getApi().getSupplierById(supplierId)
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

        RetrofitClient.getInstance().getApi().updateGeneralInfo(token, supplierId, supplier)
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

        RetrofitClient.getInstance().getApi().updateBankInfo(token, supplierId, supplier)
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

        RetrofitClient.getInstance().getApi().getSupplierAvatar(supplierId)
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

        RetrofitClient.getInstance().getApi().updateAvatar(token, supplierId, file)
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

        RetrofitClient.getInstance().getApi().updateAccountInfo(token, supplierId, supplier)
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

        RetrofitClient.getInstance().getApi().changePassword(token, supplierId, password)
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
}