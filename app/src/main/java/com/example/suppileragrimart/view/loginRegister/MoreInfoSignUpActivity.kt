package com.example.suppileragrimart.view.loginRegister

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.suppileragrimart.R
import com.example.suppileragrimart.databinding.ActivityMoreInfoSignUpBinding
import com.example.suppileragrimart.model.AESResponse
import com.example.suppileragrimart.storage.RsaKey
import com.example.suppileragrimart.model.Supplier
import com.example.suppileragrimart.network.Api
import com.example.suppileragrimart.network.RetrofitClient
import com.example.suppileragrimart.utils.AES
import com.example.suppileragrimart.utils.Constants.FIELD_REQUIRED
import com.example.suppileragrimart.utils.Constants.SUPPLIER
import com.example.suppileragrimart.utils.LoginUtils
import com.example.suppileragrimart.utils.ProgressDialog
import com.example.suppileragrimart.utils.RSA
import com.example.suppileragrimart.storage.RsaKeyDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MoreInfoSignUpActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMoreInfoSignUpBinding

    private val loginUtils: LoginUtils by lazy {
        LoginUtils(applicationContext)
    }
    private val apiService: Api by lazy {
        RetrofitClient.getInstance().getApi()
    }

    private var supplier: Supplier? = null
    private lateinit var alertDialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoreInfoSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supplier = intent.getParcelableExtra(SUPPLIER)
        if (supplier == null) supplier = Supplier()

        binding.btnBack.setOnClickListener(this)
        binding.btnSave.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnBack -> goToSignUpActivity()
            R.id.btnSave -> registerAccount()
        }
    }

    private fun registerAccount() {
        val sellerType = binding.edtTypeSaler.text.toString().trim()
        val bankAccountNumber = binding.edtBankAccount.text.toString().trim()
        val bankAccountOwner = binding.edtAccountOwner.text.toString().trim()
        val bankName = binding.edtBankName.text.toString().trim()
        val bankBranchName = binding.edtBranch.text.toString().trim()

        if (sellerType.isEmpty() || bankAccountNumber.isEmpty() || bankAccountOwner.isEmpty()
            || bankName.isEmpty() || bankBranchName.isEmpty()
        ) {
            displayErrorSnackbar(FIELD_REQUIRED)
            return
        }

        if (!binding.checkboxAgree.isChecked) {
            displayErrorSnackbar(getString(R.string.need_agree))
            return
        }

        supplier!!.sellerType = sellerType
        supplier!!.bankAccountNumber = bankAccountNumber
        supplier!!.bankAccountOwner = bankAccountOwner
        supplier!!.bankName = bankName
        supplier!!.bankBranchName = bankBranchName

        lifecycleScope.launch(Dispatchers.IO) {
            getAESKey()
            delay(100)
            val encryptedSupplier = encryptDataField()
            createSupplierAccount(encryptedSupplier)
        }
    }

    private fun goToSignUpActivity() {
        val intent = Intent(applicationContext, SignUpActivity::class.java);
        intent.putExtra(SUPPLIER, supplier)
        startActivity(intent)
    }

    suspend fun getAESKey() {
        withContext(Dispatchers.Main) {
            val progressDialog = ProgressDialog()
            alertDialog = progressDialog.createAlertDialog(this@MoreInfoSignUpActivity)
        }
        withContext(Dispatchers.IO) {
            val aesResponse = AESResponse()
            aesResponse.rsaPublicKey = loginUtils.getRSAPublicKey()
            val response = apiService.createAESKeyRequest(aesResponse)
            if (response.isSuccessful) {
                val result = response.body()
                if (result != null) {
                    val rsa = RSA()
                    val rsaPrivateKeyInString = loginUtils.getRSAPrivateKey()
                    val rsaPublicKeyInString = loginUtils.getRSAPublicKey()
                    val rsaPrivateKey = rsa.getOriginalPrivateKey(rsaPrivateKeyInString)
                    val rsaPublicKey = rsa.getOriginalPublicKey(rsaPublicKeyInString)
                    rsa.setPrivateKey(rsaPrivateKey)
                    rsa.setPublicKey(rsaPublicKey)

                    val decryptAESKey = rsa.decrypt(result.aesKey)
                    val decryptIv = rsa.decrypt(result.iv)
                    val decryptAESResponse = AESResponse()
                    decryptAESResponse.aesKey = decryptAESKey
                    decryptAESResponse.iv = decryptIv
                    decryptAESResponse.rsaPublicKeyServer = result.rsaPublicKeyServer

                    loginUtils.saveResponseKeys(decryptAESResponse)

                    val rsaKey = RsaKey(0, supplier!!.email, rsaPublicKeyInString, rsaPrivateKeyInString)
                    RsaKeyDatabase.getDatabase(this@MoreInfoSignUpActivity).rsaKeyDao().addRsaKey(rsaKey)
                }
            }
        }
    }

    private fun encryptDataField(): Supplier {
        val encryptSupplier = Supplier()

        val aesKey = loginUtils.getAESKey()
        val iv = loginUtils.getIv()
        val aesAlgorithm = AES.getInstance()
        aesAlgorithm.initFromString(aesKey, iv)

        encryptSupplier.contactName = aesAlgorithm.encrypt(supplier!!.contactName)
        encryptSupplier.shopName = aesAlgorithm.encrypt(supplier!!.shopName)
        encryptSupplier.email = aesAlgorithm.encrypt(supplier!!.email)
        encryptSupplier.phone = aesAlgorithm.encrypt(supplier!!.phone)
        encryptSupplier.cccd = aesAlgorithm.encrypt(supplier!!.cccd)
        encryptSupplier.tax_number = aesAlgorithm.encrypt(supplier!!.tax_number)
        encryptSupplier.province = aesAlgorithm.encrypt(supplier!!.province)
        encryptSupplier.password = aesAlgorithm.encrypt(supplier!!.password)
        encryptSupplier.sellerType = aesAlgorithm.encrypt(supplier!!.sellerType)
        encryptSupplier.bankAccountNumber = aesAlgorithm.encrypt(supplier!!.bankAccountNumber)
        encryptSupplier.bankAccountOwner = aesAlgorithm.encrypt(supplier!!.bankAccountOwner)
        encryptSupplier.bankName = aesAlgorithm.encrypt(supplier!!.bankName)
        encryptSupplier.bankBranchName = aesAlgorithm.encrypt(supplier!!.bankBranchName)
        encryptSupplier.rsaPublicKey = supplier!!.rsaPublicKey

        val rsa = RSA()
        val rsaPublicServerKey = loginUtils.getRSAPublicServerKey()
        encryptSupplier.aesKey = rsa.encryptWithDestinationKey(rsaPublicServerKey, aesKey)
        encryptSupplier.iv = rsa.encryptWithDestinationKey(rsaPublicServerKey, iv)

        Log.d("TEST", "shopName: " + encryptSupplier.shopName)

        return encryptSupplier
    }

    suspend fun createSupplierAccount(supplier: Supplier) {
        withContext(Dispatchers.IO) {
            val response = apiService.createSupplier(supplier)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Log.d("TEST", "register successfully")
                }
            }
        }
        withContext(Dispatchers.Main) {
            alertDialog.dismiss()
            displayErrorSnackbar(getString(R.string.supplier_request))
            val intent = Intent(applicationContext, LoginActivity::class.java);
            startActivity(intent)
        }
    }

    private fun displayErrorSnackbar(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

}