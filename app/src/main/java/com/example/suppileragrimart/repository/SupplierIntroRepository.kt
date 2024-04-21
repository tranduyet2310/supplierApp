package com.example.suppileragrimart.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.suppileragrimart.model.Supplier
import com.example.suppileragrimart.model.SupplierIntro
import com.example.suppileragrimart.network.RetrofitClient
import com.example.suppileragrimart.utils.ScreenState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SupplierIntroRepository {
    fun getSupplierIntro(supplierId: Long): LiveData<ScreenState<ArrayList<SupplierIntro>?>> {
        val mutableLiveData = MutableLiveData<ScreenState<ArrayList<SupplierIntro>?>>()
        mutableLiveData.postValue(ScreenState.Loading(null))

        RetrofitClient.getInstance().getApi().getAllSupplierIntro(supplierId)
            .enqueue(object : Callback<ArrayList<SupplierIntro>> {
                override fun onResponse(
                    call: Call<ArrayList<SupplierIntro>>,
                    response: Response<ArrayList<SupplierIntro>>
                ) {
                    if (response.isSuccessful) {
                        mutableLiveData.postValue(ScreenState.Success(response.body()))
                    }
                }

                override fun onFailure(call: Call<ArrayList<SupplierIntro>>, t: Throwable) {
                    val message = t.message.toString()
                    mutableLiveData.postValue(ScreenState.Error(message, null))
                }
            })

        return mutableLiveData
    }
}