package com.example.suppileragrimart.view.profile

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.suppileragrimart.R
import com.example.suppileragrimart.databinding.FragmentUpdateBankInfoBinding
import com.example.suppileragrimart.model.Supplier
import com.example.suppileragrimart.utils.Constants
import com.example.suppileragrimart.utils.LoginUtils
import com.example.suppileragrimart.utils.ProgressDialog
import com.example.suppileragrimart.utils.ScreenState
import com.example.suppileragrimart.utils.Utils
import com.example.suppileragrimart.viewmodel.SupplierViewModel
import com.google.android.material.snackbar.Snackbar


class UpdateBankInfoFragment : Fragment() {
    private lateinit var binding: FragmentUpdateBankInfoBinding
    private lateinit var navController: NavController

    private val supplierViewModel: SupplierViewModel by lazy {
        ViewModelProvider(requireActivity()).get(SupplierViewModel::class.java)
    }
    private val progressDialog: ProgressDialog by lazy {
        ProgressDialog()
    }
    private val loginUtils: LoginUtils by lazy {
        LoginUtils(requireContext())
    }

    val args: UpdateGeneralInfoFragmentArgs by navArgs()
    private lateinit var supplier: Supplier
    private lateinit var alertDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpdateBankInfoBinding.inflate(inflater)
        binding.toolbarLayout.titleToolbar.text = getString(R.string.update_info)

        supplier = args.supplier
        showInfo()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        binding.toolbarLayout.imgBack.setOnClickListener {
            navController.navigateUp()
        }

        binding.btnContinue.setOnClickListener {
            val newSellerType = binding.edtTypeSaler.text.toString().trim()
            val newAccountNumber = binding.edtBankAccount.text.toString().trim()
            val newAccountOwner = binding.edtAccountOwner.text.toString().trim()
            val newBankName = binding.edtBankName.text.toString().trim()
            val newBankBranch = binding.edtBranch.text.toString().trim()


            if (newSellerType.isEmpty() || newAccountNumber.isEmpty() || newAccountOwner.isEmpty()
                || newBankName.isEmpty() || newBankBranch.isEmpty()
            ) {
                showSnackbar(Constants.FIELD_REQUIRED)
                return@setOnClickListener
            }

            supplier.apply {
                sellerType = newSellerType
                bankAccountNumber = newAccountNumber
                bankAccountOwner = newAccountOwner
                bankName = newBankName
                bankBranchName = newBankBranch
            }

            val secretKey = loginUtils.getAESKey()
            val iv = loginUtils.getIv()
            val encryptedSupplier = Utils.encryptInfo(supplier, secretKey, iv)
            updateSupplier(encryptedSupplier)
        }
    }

    private fun updateSupplier(supplier: Supplier) {
        val token = loginUtils.getSupplierToken()
        supplierViewModel.updateBankInfo(token, supplier.id, supplier).observe(
            requireActivity(), { state -> processSupplierResponse(state) }
        )
    }

    private fun showInfo() {
        binding.edtTypeSaler.text = Editable.Factory.getInstance().newEditable(supplier.sellerType)
        binding.edtBankAccount.text = Editable.Factory.getInstance().newEditable(supplier.bankAccountNumber)
        binding.edtAccountOwner.text = Editable.Factory.getInstance().newEditable(supplier.bankAccountOwner)
        binding.edtBankName.text = Editable.Factory.getInstance().newEditable(supplier.bankName)
        binding.edtBranch.text = Editable.Factory.getInstance().newEditable(supplier.bankBranchName)
    }

    private fun processSupplierResponse(state: ScreenState<Supplier?>) {
        when (state) {
            is ScreenState.Loading -> {
                alertDialog = progressDialog.createAlertDialog(requireActivity())
            }

            is ScreenState.Success -> {
                if (state.data != null) {
                    alertDialog.dismiss()
                    showSnackbar(getString(R.string.update_success))
                    navController.navigate(R.id.action_updateBankInfoFragment_to_userInfoFragment)
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

    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }
}