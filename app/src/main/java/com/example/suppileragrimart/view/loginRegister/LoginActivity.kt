package com.example.suppileragrimart.view.loginRegister

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.suppileragrimart.R
import com.example.suppileragrimart.databinding.ActivityLoginBinding
import com.example.suppileragrimart.view.MainActivity

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonLogin.setOnClickListener(this)
        binding.textViewSignUp.setOnClickListener(this)
        binding.forgetPassword.setOnClickListener(this)
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
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
    }
}