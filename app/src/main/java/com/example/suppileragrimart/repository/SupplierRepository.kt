package com.example.suppileragrimart.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.suppileragrimart.model.Supplier
import com.example.suppileragrimart.network.RetrofitClient
import com.example.suppileragrimart.utils.ScreenState
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
}