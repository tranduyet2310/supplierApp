package com.example.suppileragrimart.view.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.suppileragrimart.R
import com.example.suppileragrimart.databinding.FragmentProfileBinding
import com.example.suppileragrimart.model.Image
import com.example.suppileragrimart.model.Supplier
import com.example.suppileragrimart.network.RetrofitClient
import com.example.suppileragrimart.utils.GlideApp
import com.example.suppileragrimart.utils.LoginUtils
import com.example.suppileragrimart.utils.ProgressDialog
import com.example.suppileragrimart.utils.ScreenState
import com.example.suppileragrimart.utils.Utils
import com.example.suppileragrimart.utils.Utils.Companion.decryptData
import com.example.suppileragrimart.view.loginRegister.LoginActivity
import com.example.suppileragrimart.viewmodel.SupplierViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response


class ProfileFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var navController: NavController

    private val supplierViewModel: SupplierViewModel by lazy {
        ViewModelProvider(requireActivity()).get(SupplierViewModel::class.java)
    }
    private val loginUtils: LoginUtils by lazy {
        LoginUtils(requireContext())
    }
    private val progressDialog: ProgressDialog by lazy {
        ProgressDialog()
    }

    private lateinit var alertDialog: AlertDialog
    private var supplier: Supplier? = null
    private lateinit var secretKey: String
    private lateinit var iv: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater)

        supplier = supplierViewModel.supplier
        secretKey = loginUtils.getAESKey()
        iv = loginUtils.getIv()
        if (supplier != null) {
            if (supplier!!.contactName != null) binding.tvUserName.text = supplier!!.contactName
            else{
                getSupplierInfo()
            }

            if (supplier!!.avatar != null) showUserAvatar(supplier!!.avatar)
            else getSupplierAvatar()
        }

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
        when (v?.id) {
            R.id.linearLogOut -> logOut()
            R.id.linearUserInfo -> updateUserInfoAccount()
            R.id.constraintProfile -> updateInfoAccount()
            R.id.linearShopInfo -> shopInfo()
            R.id.linearGardenInfo -> gardenInfo()
            R.id.linearOrderHistory -> cooperativeOrder()
        }
    }

    private fun cooperativeOrder() {
        navController.navigate(R.id.action_profileFragment_to_cooperativeOrderFragment)
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
        loginUtils.clearAll()
        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun getSupplierInfo() {
        lifecycleScope.launch {
            getSupplierById()
            delay(500)
            withContext(Dispatchers.Main){
                binding.tvUserName.text = supplier!!.contactName
            }
        }
    }

    suspend fun getSupplierById(){
        withContext(Dispatchers.IO) {
            val response = RetrofitClient.getInstance().getApi().getSupplierById(supplier!!.id)
            response.enqueue(object : retrofit2.Callback<Supplier> {
                override fun onResponse(call: Call<Supplier>, response: Response<Supplier>) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            val data = decryptData(response.body()!!, secretKey, iv)
                            supplier!!.contactName = data.contactName
                            supplier!!.phone = data.phone
                            supplierViewModel.supplier = supplier
                            loginUtils.saveSupplierInfo(supplier!!)
                        }
                    }
                }

                override fun onFailure(call: Call<Supplier>, t: Throwable) {
                    Log.d("TEST", "failure " + t.message)
                }
            })
        }
    }

    private fun getSupplierAvatar() {
        supplierViewModel.getSupplierAvatar(supplier!!.id).observe(
            requireActivity(), { state -> processSupplierAvatar(state) }
        )
    }

    private fun processSupplierAvatar(state: ScreenState<Image?>) {
        when (state) {
            is ScreenState.Loading -> {
                alertDialog = progressDialog.createAlertDialog(requireActivity())
            }

            is ScreenState.Success -> {
                if (state.data != null) {
                    alertDialog.dismiss()
                    if (state.data.imageUrl != null) {
                        supplier!!.avatar = state.data.imageUrl
                        loginUtils.saveSupplierInfo(supplier!!)
                        supplierViewModel.supplier = supplier
                        showUserAvatar(state.data.imageUrl)
                    }
                }
            }

            is ScreenState.Error -> {
                alertDialog.dismiss()
                if (state.message != null) {
                    showSnackbar(state.message)
                }
            }
        }
    }

    private fun showUserAvatar(imageUrl: String) {
        val modifiedUrl = imageUrl.replace("http://", "https://")
        GlideApp.with(requireContext())
            .load(modifiedUrl)
            .into(binding.imageUser)
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }
}