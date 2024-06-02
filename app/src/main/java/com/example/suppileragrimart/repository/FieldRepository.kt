package com.example.suppileragrimart.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.suppileragrimart.model.FieldApiResponse
import com.example.suppileragrimart.model.FieldDetail
import com.example.suppileragrimart.model.MessageResponse
import com.example.suppileragrimart.network.RetrofitClient
import com.example.suppileragrimart.utils.ScreenState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FieldRepository {
    fun getFieldBySupplierId(supplierId: Long): LiveData<ScreenState<ArrayList<FieldApiResponse>?>> {
        val mutableLiveData = MutableLiveData<ScreenState<ArrayList<FieldApiResponse>?>>()
        mutableLiveData.postValue(ScreenState.Loading(null))

        RetrofitClient.getInstance().getApi().getCropsField(supplierId)
            .enqueue(object : Callback<ArrayList<FieldApiResponse>?> {
                override fun onResponse(
                    call: Call<ArrayList<FieldApiResponse>?>,
                    response: Response<ArrayList<FieldApiResponse>?>
                ) {
                    if (response.isSuccessful) {
                        mutableLiveData.postValue(ScreenState.Success(response.body()))
                    }
                }

                override fun onFailure(call: Call<ArrayList<FieldApiResponse>?>, t: Throwable) {
                    val message = t.message.toString()
                    mutableLiveData.postValue(ScreenState.Error(message, null))
                }
            })

        return mutableLiveData
    }

    fun createField(token: String, supplierId: Long, fieldApiResponse: FieldApiResponse
    ): LiveData<ScreenState<FieldApiResponse?>> {
        val mutableLiveData = MutableLiveData<ScreenState<FieldApiResponse?>>()
        mutableLiveData.postValue(ScreenState.Loading(null))

        RetrofitClient.getInstance().getApi().createField(token, supplierId, fieldApiResponse)
            .enqueue(object : Callback<FieldApiResponse> {
                override fun onResponse(
                    call: Call<FieldApiResponse>,
                    response: Response<FieldApiResponse>
                ) {
                    if (response.isSuccessful) {
                        mutableLiveData.postValue(ScreenState.Success(response.body()))
                    } else {
                        mutableLiveData.postValue(ScreenState.Error("Supplier does not exists", null))
                    }
                }

                override fun onFailure(call: Call<FieldApiResponse>, t: Throwable) {
                    val message = t.message.toString()
                    mutableLiveData.postValue(ScreenState.Error(message, null))
                }
            })

        return mutableLiveData
    }

    fun updateField(token: String, fieldId: Long, fieldApiResponse: FieldApiResponse
    ): LiveData<ScreenState<FieldApiResponse?>> {
        val mutableLiveData = MutableLiveData<ScreenState<FieldApiResponse?>>()
        mutableLiveData.postValue(ScreenState.Loading(null))

        RetrofitClient.getInstance().getApi().updateField(token, fieldId, fieldApiResponse)
            .enqueue(object : Callback<FieldApiResponse> {
                override fun onResponse(
                    call: Call<FieldApiResponse>,
                    response: Response<FieldApiResponse>
                ) {
                    if (response.isSuccessful) {
                        mutableLiveData.postValue(ScreenState.Success(response.body()))
                    } else {
                        mutableLiveData.postValue(ScreenState.Error("Field does not exists", null))
                    }
                }

                override fun onFailure(call: Call<FieldApiResponse>, t: Throwable) {
                    val message = t.message.toString()
                    mutableLiveData.postValue(ScreenState.Error(message, null))
                }
            })

        return mutableLiveData
    }

    fun completeField(token: String, fieldId: Long): LiveData<ScreenState<FieldApiResponse?>> {
        val mutableLiveData = MutableLiveData<ScreenState<FieldApiResponse?>>()
        mutableLiveData.postValue(ScreenState.Loading(null))

        RetrofitClient.getInstance().getApi().completeField(token, fieldId)
            .enqueue(object : Callback<FieldApiResponse> {
                override fun onResponse(
                    call: Call<FieldApiResponse>,
                    response: Response<FieldApiResponse>
                ) {
                    if (response.isSuccessful) {
                        mutableLiveData.postValue(ScreenState.Success(response.body()))
                    } else {
                        mutableLiveData.postValue(ScreenState.Error("Field does not exists", null))
                    }
                }

                override fun onFailure(call: Call<FieldApiResponse>, t: Throwable) {
                    val message = t.message.toString()
                    mutableLiveData.postValue(ScreenState.Error(message, null))
                }
            })

        return mutableLiveData
    }

    fun createFieldDetail(token: String, fieldId: Long, fieldDetail: FieldDetail
    ): LiveData<ScreenState<FieldDetail?>> {
        val mutableLiveData = MutableLiveData<ScreenState<FieldDetail?>>()
        mutableLiveData.postValue(ScreenState.Loading(null))

        RetrofitClient.getInstance().getApi().createFieldDetail(token, fieldId, fieldDetail)
            .enqueue(object : Callback<FieldDetail> {
                override fun onResponse(
                    call: Call<FieldDetail>,
                    response: Response<FieldDetail>
                ) {
                    if (response.isSuccessful) {
                        mutableLiveData.postValue(ScreenState.Success(response.body()))
                    } else {
                        mutableLiveData.postValue(ScreenState.Error("Field does not exists", null))
                    }
                }

                override fun onFailure(call: Call<FieldDetail>, t: Throwable) {
                    val message = t.message.toString()
                    mutableLiveData.postValue(ScreenState.Error(message, null))
                }
            })

        return mutableLiveData
    }

    fun updateFieldDetail(token: String, fieldId: Long, fieldDetail: FieldDetail
    ): LiveData<ScreenState<FieldDetail?>> {
        val mutableLiveData = MutableLiveData<ScreenState<FieldDetail?>>()
        mutableLiveData.postValue(ScreenState.Loading(null))

        RetrofitClient.getInstance().getApi().updateFieldDetail(token, fieldId, fieldDetail)
            .enqueue(object : Callback<FieldDetail> {
                override fun onResponse(
                    call: Call<FieldDetail>,
                    response: Response<FieldDetail>
                ) {
                    if (response.isSuccessful) {
                        mutableLiveData.postValue(ScreenState.Success(response.body()))
                    } else {
                        mutableLiveData.postValue(ScreenState.Error("Field does not exists", null))
                    }
                }

                override fun onFailure(call: Call<FieldDetail>, t: Throwable) {
                    val message = t.message.toString()
                    mutableLiveData.postValue(ScreenState.Error(message, null))
                }
            })

        return mutableLiveData
    }

    fun deleteFieldDetail(token: String, fieldIDetailId: Long, ): LiveData<ScreenState<MessageResponse?>> {
        val mutableLiveData = MutableLiveData<ScreenState<MessageResponse?>>()
        mutableLiveData.postValue(ScreenState.Loading(null))

        RetrofitClient.getInstance().getApi().deleteFieldDetail(token, fieldIDetailId)
            .enqueue(object : Callback<MessageResponse> {
                override fun onResponse(
                    call: Call<MessageResponse>,
                    response: Response<MessageResponse>
                ) {
                    if (response.isSuccessful) {
                        mutableLiveData.postValue(ScreenState.Success(response.body()))
                    } else {
                        mutableLiveData.postValue(ScreenState.Error("Field does not exists", null))
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