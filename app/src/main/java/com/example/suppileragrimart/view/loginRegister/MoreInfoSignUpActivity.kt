package com.example.suppileragrimart.view.loginRegister

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.suppileragrimart.R
import com.example.suppileragrimart.databinding.ActivityMoreInfoSignUpBinding
import com.google.android.gms.common.api.GoogleApiClient

class MoreInfoSignUpActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMoreInfoSignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoreInfoSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBack.setOnClickListener{
            val intent = Intent(applicationContext, SignUpActivity::class.java);
            startActivity(intent)
        }
        binding.btnSave.setOnClickListener {
            val intent = Intent(applicationContext, LoginActivity::class.java);
            startActivity(intent)
        }
        binding.checkboxRobot.setOnClickListener(this)


    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.checkboxRobot -> checkRobot()
        }
    }

    private fun checkRobot() {
        Toast.makeText(applicationContext, "Not a robot", Toast.LENGTH_SHORT).show()
    }
}