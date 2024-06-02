package com.example.suppileragrimart.view.loginRegister

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.suppileragrimart.databinding.ActivityChangePasswordBinding

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.saveChanges.setOnClickListener{
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}