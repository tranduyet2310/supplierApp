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
import com.example.suppileragrimart.databinding.ActivityLoginBinding
import com.example.suppileragrimart.model.LoginApiResponse
import com.example.suppileragrimart.model.LoginRequest
import com.example.suppileragrimart.model.Supplier
import com.example.suppileragrimart.utils.Constants.EMAIL_REQUIRED
import com.example.suppileragrimart.utils.Constants.LOGIN_SUCCESS
import com.example.suppileragrimart.utils.Constants.PASSWORD_REQUIRED
import com.example.suppileragrimart.utils.LoginUtils
import com.example.suppileragrimart.utils.ProgressDialog
import com.example.suppileragrimart.utils.ScreenState
import com.example.suppileragrimart.utils.Utils
import com.example.suppileragrimart.view.MainActivity
import com.example.suppileragrimart.viewmodel.LoginViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by lazy {
        ViewModelProvider(this).get(LoginViewModel::class.java)
    }
    private val loginUtils: LoginUtils by lazy {
        LoginUtils(applicationContext)
    }
    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private lateinit var supplier: Supplier
    private lateinit var alertDialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkCertificate()

        binding.buttonLogin.setOnClickListener(this)
        binding.textViewSignUp.setOnClickListener(this)
        binding.forgetPassword.setOnClickListener(this)
    }

    private fun checkCertificate() {
        Utils.readRawResource(this, R.raw.mycert)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonLogin -> goToMainActivity()
            R.id.textViewSignUp -> signUp()
            R.id.forgetPassword -> forgetPassword()
        }
    }

    private fun forgetPassword() {
        val intent = Intent(applicationContext, PasswordAssistantActivity::class.java)
        startActivity(intent)
    }

    private fun signUp() {
        val intent = Intent(applicationContext, SignUpActivity::class.java)
        startActivity(intent)
    }

    private fun goToMainActivity() {
        val email = binding.inputEmail.text.toString()
        val password = binding.inputPassword.text.toString()
        if (email.isEmpty()) {
            binding.textFieldEmail.error = EMAIL_REQUIRED
            return
        }
        if (password.isEmpty()) {
            binding.textFieldPassword.error = PASSWORD_REQUIRED
            return
        }

        supplier = Supplier()
        supplier.email = email
        supplier.password = password

        signIn()

        val loginRequest = LoginRequest(email, password)
        loginViewModel.getLoginResponseLiveData(loginRequest)
            .observe(this, { state -> processLoginResponse(state) })
    }

    private fun processLoginResponse(state: ScreenState<LoginApiResponse?>) {
        when (state) {
            is ScreenState.Loading -> {
                val progressDialog = ProgressDialog()
                alertDialog = progressDialog.createAlertDialog(this)
            }

            is ScreenState.Success -> {
                if (state.data != null) {
                    alertDialog.dismiss()
                    if (state.data.isActive){
                        loginUtils.saveSupplierInfo(state.data, supplier)
                        displayErrorSnackbar(LOGIN_SUCCESS)
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        displayErrorSnackbar(getString(R.string.account_is_inactive))
                    }

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

    private fun signIn(){
        auth.signOut()
        auth.signInWithEmailAndPassword(supplier.email, supplier.password)
            .addOnCompleteListener(this){ task ->
                if (task.isSuccessful){
                    Log.d("TEST", "signInWithEmail:success")
                } else {
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun displayErrorSnackbar(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }
}