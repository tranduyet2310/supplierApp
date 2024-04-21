package com.example.suppileragrimart.view.profile

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.suppileragrimart.R
import com.example.suppileragrimart.databinding.FragmentChangePasswordBinding
import com.example.suppileragrimart.model.PasswordRequest
import com.example.suppileragrimart.model.Supplier
import com.example.suppileragrimart.utils.Constants
import com.example.suppileragrimart.utils.LoginUtils
import com.example.suppileragrimart.utils.ProgressDialog
import com.example.suppileragrimart.utils.ScreenState
import com.example.suppileragrimart.viewmodel.SupplierViewModel
import com.google.android.material.snackbar.Snackbar

class ChangePasswordFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController

    private val progressDialog: ProgressDialog by lazy {
        ProgressDialog()
    }
    private val loginUtils: LoginUtils by lazy {
        LoginUtils(requireContext())
    }
    private val supplierViewModel: SupplierViewModel by lazy {
        ViewModelProvider(requireActivity()).get(SupplierViewModel::class.java)
    }

    private lateinit var alertDialog: AlertDialog
    private var supplier: Supplier? = null
    private lateinit var passwordRequest: PasswordRequest
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        binding.toolbarLayout.titleToolbar.text = getString(R.string.password_assistance)

        supplier = supplierViewModel.supplier

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        binding.cancel.setOnClickListener(this)
        binding.saveChanges.setOnClickListener(this)

        binding.toolbarLayout.imgBack.setOnClickListener {
            navController.navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cancel -> navController.navigateUp()
            R.id.saveChanges -> changePassword()
        }
    }

    private fun changePassword() {
        val currentPass = binding.edtCurrentPassword.text.toString().trim()
        val newPass = binding.edtNewPassword.text.toString().trim()
        val retypePass = binding.edtRetypePassword.text.toString().trim()

        if (currentPass.isEmpty() || newPass.isEmpty() || retypePass.isEmpty()) {
            displayErrorSnackbar(Constants.FIELD_REQUIRED)
        } else if (!supplier?.password.equals(currentPass)) {
            displayErrorSnackbar(getString(R.string.current_pass_not_correct))
        } else if (newPass.length < 8) {
            displayErrorSnackbar(getString(R.string.using_strong_password))
        } else if (!newPass.equals(retypePass)) {
            displayErrorSnackbar(getString(R.string.retype_pass_not_correct))
        } else {
            passwordRequest = PasswordRequest(currentPass, newPass)
            val token = loginUtils.getSupplierToken()
            supplierViewModel.changePassword(token, supplier!!.id, passwordRequest).observe(
                requireActivity(), { state -> processUserPassword(state) }
            )
        }
    }

    private fun processUserPassword(state: ScreenState<Supplier?>) {
        when (state) {
            is ScreenState.Loading -> {
                alertDialog = progressDialog.createAlertDialog(requireActivity())
            }

            is ScreenState.Success -> {
                if (state.data != null) {
                    alertDialog.dismiss()
                    supplier!!.password = passwordRequest.newPass
                    loginUtils.saveSupplierInfo(supplier!!)
                    supplierViewModel.supplier = supplier
                    goToProfileFragment()
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

    private fun goToProfileFragment() {
        Toast.makeText(requireContext(), getString(R.string.change_password), Toast.LENGTH_SHORT).show()
        navController.navigate(R.id.action_changePasswordFragment_to_userAccountFragment)
    }

    private fun displayErrorSnackbar(errorMessage: String) {
        Snackbar.make(requireView(), errorMessage, Snackbar.LENGTH_INDEFINITE)
            .apply { setAction(Constants.RETRY) { dismiss() } }
            .show()
    }
}