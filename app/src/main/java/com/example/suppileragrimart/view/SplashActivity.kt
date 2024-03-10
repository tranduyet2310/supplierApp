package com.example.suppileragrimart.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.suppileragrimart.databinding.ActivitySplashBinding
import com.example.suppileragrimart.view.loginRegister.LoginActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        val thread = object : Thread(){
            override fun run() {
                try {
                    sleep(3500)
                } catch (e: InterruptedException) {
                    throw RuntimeException(e)
                } finally {
                    val intent = Intent(applicationContext, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
        thread.start()
    }
}