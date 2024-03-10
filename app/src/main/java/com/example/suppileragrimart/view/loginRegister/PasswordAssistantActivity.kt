package com.example.suppileragrimart.view.loginRegister

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.suppileragrimart.R
import com.example.suppileragrimart.databinding.ActivityPasswordAssistantBinding

class PasswordAssistantActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityPasswordAssistantBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordAssistantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnContinue.setOnClickListener{
            val intent = Intent(applicationContext, AuthenticationActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onClick(v: View?) {

    }
}