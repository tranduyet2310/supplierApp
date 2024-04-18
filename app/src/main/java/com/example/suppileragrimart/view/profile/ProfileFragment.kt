package com.example.suppileragrimart.view.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.suppileragrimart.R
import com.example.suppileragrimart.databinding.FragmentProfileBinding
import com.example.suppileragrimart.view.loginRegister.LoginActivity


class ProfileFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        binding.linearLogOut.setOnClickListener(this)
        binding.linearOrderHistory.setOnClickListener(this)
        binding.linearShopInfo.setOnClickListener(this)
        binding.linearUserInfo.setOnClickListener(this)
        binding.linearGardenInfo.setOnClickListener(this)
        binding.constraintProfile.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.linearLogOut -> logOut()
            R.id.linearUserInfo -> updateUserInfoAccount()
            R.id.constraintProfile -> updateInfoAccount()
            R.id.linearShopInfo -> shopInfo()
            R.id.linearGardenInfo -> gardenInfo()
        }
    }

    private fun gardenInfo() {
        navController.navigate(R.id.action_profileFragment_to_cropsInfoFragment)
    }

    private fun shopInfo() {
        navController.navigate(R.id.action_profileFragment_to_shopInfoFragment)
    }

    private fun updateInfoAccount() {
        navController.navigate(R.id.action_profileFragment_to_userAccountFragment)
    }

    private fun updateUserInfoAccount() {
        navController.navigate(R.id.action_profileFragment_to_userInfoFragment)
    }

    private fun logOut() {
        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
    }

}