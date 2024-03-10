package com.example.suppileragrimart.view.loginRegister

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.suppileragrimart.R
import com.example.suppileragrimart.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tvLogin.setOnClickListener {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
        }
        binding.btnContinue.setOnClickListener{
            val intent = Intent(applicationContext, MoreInfoSignUpActivity::class.java)
            startActivity(intent)
        }
    }
}
