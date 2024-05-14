package com.example.suppileragrimart.utils

import android.content.Context
import com.example.suppileragrimart.model.AESResponse
import com.example.suppileragrimart.model.LoginApiResponse
import com.example.suppileragrimart.model.Supplier
import com.example.suppileragrimart.utils.Constants.AES_KEY
import com.example.suppileragrimart.utils.Constants.AVATAR
import com.example.suppileragrimart.utils.Constants.EMAIL
import com.example.suppileragrimart.utils.Constants.FCM
import com.example.suppileragrimart.utils.Constants.ID
import com.example.suppileragrimart.utils.Constants.IV
import com.example.suppileragrimart.utils.Constants.NAME
import com.example.suppileragrimart.utils.Constants.PASSWORD
import com.example.suppileragrimart.utils.Constants.PHONE
import com.example.suppileragrimart.utils.Constants.RSA_PRIVATE_KEY
import com.example.suppileragrimart.utils.Constants.RSA_PUBLIC_KEY
import com.example.suppileragrimart.utils.Constants.RSA_PUBLIC_SERVER_KEY
import com.example.suppileragrimart.utils.Constants.SHARED_PREF_NAME
import com.example.suppileragrimart.utils.Constants.TOKEN

class LoginUtils(private val mCtx: Context) {
    fun saveSupplierInfo(response: LoginApiResponse, supplier: Supplier) {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putLong(ID, response.getSupplierId())
        editor.putString(EMAIL, supplier.getEmail())
        editor.putString(PASSWORD, supplier.getPassword())
        editor.putString(TOKEN, "Bearer ${response.getAccessToken()}")
        editor.apply()
    }

    fun saveResponseKeys(aesResponse: AESResponse) {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString(AES_KEY, aesResponse.aesKey)
        editor.putString(IV, aesResponse.iv)
        editor.putString(RSA_PUBLIC_SERVER_KEY, aesResponse.rsaPublicKeyServer)
        editor.apply()
    }

    fun saveRSAKey(privateKey: String, publicKey: String){
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString(RSA_PUBLIC_KEY, publicKey)
        editor.putString(RSA_PRIVATE_KEY, privateKey)
        editor.apply()
    }

    fun saveSupplierInfo(supplier: Supplier) {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putLong(ID, supplier.getId())
        editor.putString(NAME, supplier.getContactName())
        editor.putString(EMAIL, supplier.getEmail())
        editor.putString(PASSWORD, supplier.getPassword())
        editor.putString(PHONE, supplier.getPhone())
        editor.putString(AVATAR, supplier.getAvatar())
        editor.apply()
    }

    fun getSupplierInfo(): Supplier {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return Supplier(
            sharedPreferences.getLong(ID, -1L),
            sharedPreferences.getString(NAME, null),
            sharedPreferences.getString(EMAIL, null),
            sharedPreferences.getString(PHONE, null),
            sharedPreferences.getString(PASSWORD, null),
            sharedPreferences.getString(AVATAR, null)
        )
    }

    fun getSupplierToken(): String {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(TOKEN, "") ?: ""
    }

    fun getRSAPublicKey(): String{
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(RSA_PUBLIC_KEY, null) ?: ""
    }

    fun getRSAPrivateKey(): String{
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(RSA_PRIVATE_KEY, null) ?: ""
    }

    fun getRSAPublicServerKey(): String {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(RSA_PUBLIC_SERVER_KEY, null) ?: ""
    }

    fun getAESKey(): String{
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(AES_KEY, null) ?: ""
    }

    fun getIv(): String {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(IV, null) ?: ""
    }

    fun clearAll() {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear().commit()
    }

    fun saveFcmToken(token: String) {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(FCM, token)
        editor.apply()
    }

    fun getFcmToken(): String? {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(FCM, null)
    }
}