package com.example.suppileragrimart.view.loginRegister

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.suppileragrimart.R
import com.example.suppileragrimart.databinding.ActivityMoreInfoSignUpBinding
import com.example.suppileragrimart.model.AESResponse
import com.example.suppileragrimart.model.LoginApiResponse
import com.example.suppileragrimart.model.Supplier
import com.example.suppileragrimart.utils.Constants
import com.example.suppileragrimart.utils.Constants.SUPPLIER
import com.example.suppileragrimart.utils.LoginUtils
import com.example.suppileragrimart.utils.ProgressDialog
import com.example.suppileragrimart.utils.RSA
import com.example.suppileragrimart.utils.ScreenState
import com.example.suppileragrimart.view.MainActivity
import com.example.suppileragrimart.viewmodel.RegisterViewModel
import com.example.suppileragrimart.viewmodel.SupplierViewModel

class MoreInfoSignUpActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMoreInfoSignUpBinding

    private val registerViewModel: RegisterViewModel by lazy {
        ViewModelProvider(this).get(RegisterViewModel::class.java)
    }
    private val supplierViewModel: SupplierViewModel by lazy {
        ViewModelProvider(this).get(SupplierViewModel::class.java)
    }
    private val loginUtils: LoginUtils by lazy {
        LoginUtils(applicationContext)
    }
    private var supplier: Supplier? = null
    private lateinit var alertDialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoreInfoSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supplier = intent.getParcelableExtra(SUPPLIER)

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



//        val intent = Intent(applicationContext, LoginActivity::class.java);
//        startActivity(intent)
        val aesResponse = AESResponse()
        aesResponse.rsaPublicKey = loginUtils.getRSAPublicKey()
        registerViewModel.getAESKey(aesResponse)
            .observe(this, { state -> processAESResponse(state) })
    }

    private fun goToSignUpActivity() {
        val intent = Intent(applicationContext, SignUpActivity::class.java);
        intent.putExtra(SUPPLIER, supplier)
        startActivity(intent)
    }

    private fun processAESResponse(state: ScreenState<AESResponse?>) {
        when (state) {
            is ScreenState.Loading -> {
                val progressDialog = ProgressDialog()
                alertDialog = progressDialog.createAlertDialog(this)
            }

            is ScreenState.Success -> {
                if (state.data != null) {
                    alertDialog.dismiss()
                    val rsa = RSA()
                    val rsaPrivateKey = rsa.getOriginalPrivateKey(loginUtils.getRSAPrivateKey())
                    val rsaPublicKey = rsa.getOriginalPublicKey(loginUtils.getRSAPublicKey())
                    rsa.setPrivateKey(rsaPrivateKey)
                    rsa.setPublicKey(rsaPublicKey)
                    val decryptAESKey = rsa.decrypt(state.data.aesKey)
                    val decryptIv = rsa.decrypt(state.data.iv)
                    val decryptAESResponse = AESResponse()
                    decryptAESResponse.aesKey = decryptAESKey
                    decryptAESResponse.iv = decryptIv
                    decryptAESResponse.rsaPublicKeyServer = state.data.rsaPublicKeyServer

                    loginUtils.saveResponseKeys(decryptAESResponse)
                    displayErrorSnackbar(Constants.LOGIN_SUCCESS)
                }
            }

            is ScreenState.Error -> {
                alertDialog.dismiss()
                if (state.message != null) {
                    displayErrorSnackbar(state.message)
                }
            }
        }
    }

    private fun displayErrorSnackbar(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

}