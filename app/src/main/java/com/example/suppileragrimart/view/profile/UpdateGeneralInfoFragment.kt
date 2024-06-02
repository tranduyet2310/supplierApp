package com.example.suppileragrimart.view.profile

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.suppileragrimart.R
import com.example.suppileragrimart.databinding.FragmentUpdateGeneralInfoBinding
import com.example.suppileragrimart.model.Supplier
import com.example.suppileragrimart.utils.Constants
import com.example.suppileragrimart.utils.LoginUtils
import com.example.suppileragrimart.utils.ProgressDialog
import com.example.suppileragrimart.utils.ScreenState
import com.example.suppileragrimart.utils.Utils
import com.example.suppileragrimart.utils.Validation
import com.example.suppileragrimart.viewmodel.SupplierViewModel
import com.google.android.material.snackbar.Snackbar


class UpdateGeneralInfoFragment : Fragment() {
    private lateinit var binding: FragmentUpdateGeneralInfoBinding
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
        binding = FragmentUpdateGeneralInfoBinding.inflate(inflater)
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
            val newContactName = binding.edtUserName.text.toString().trim()
            val newShopName = binding.edtShopName.text.toString().trim()
            val newEmail = binding.edtUserEmail.text.toString().trim()
            val newPhone = binding.edtPhoneNumber.text.toString().trim()
            val newCccd = binding.edtCMND.text.toString().trim()
            val newTax = binding.edtTax.text.toString().trim()
            val newDetail = binding.edtDetail.text.toString().trim()

            if (newContactName.isEmpty() || newShopName.isEmpty() || newEmail.isEmpty()
                || newPhone.isEmpty() || newCccd.isEmpty() || newTax.isEmpty() || newDetail.isEmpty()
            ) {
                showSnackbar(Constants.FIELD_REQUIRED)
                return@setOnClickListener
            }

            supplier.apply {
                contactName = newContactName
                shopName = newShopName
                email = newEmail
                phone = newPhone
                cccd = newCccd
                tax_number = newTax
                address = newDetail
            }

            if (validateField(supplier)) {
                val secretKey = loginUtils.getAESKey()
                val iv = loginUtils.getIv()
                val encryptedSupplier = Utils.encryptInfo(supplier, secretKey, iv)
                updateSupplier(encryptedSupplier)
            } else {
                return@setOnClickListener
            }
        }
    }

    private fun updateSupplier(supplier: Supplier) {
        val token = loginUtils.getSupplierToken()
        supplierViewModel.updateGeneralInfo(token, supplier.id, supplier).observe(
            requireActivity(), { state -> processSupplierResponse(state) }
        )
    }

    private fun validateField(supplier: Supplier): Boolean {
        if (!Validation.isValidName(supplier.contactName)) {
            binding.edtUserName.setError(getString(R.string.name_mininum_3_characters))
            binding.edtUserName.requestFocus()
            return false
        }

        if (!Validation.isValidShopName(supplier.shopName)) {
            binding.edtShopName.setError(getString(R.string.name_minimun_8_characters_))
            binding.edtShopName.requestFocus()
            return false
        }

        if (!Validation.isValidEmail(supplier.email)) {
            binding.edtUserEmail.setError(getString(R.string.format_invalid))
            binding.edtUserEmail.requestFocus()
            return false
        }

        if (!Validation.isValidPhone(supplier.phone)) {
            binding.edtPhoneNumber.setError(getString(R.string.phone_invalid))
            binding.edtPhoneNumber.requestFocus()
            return false
        }

        if (!Validation.isValidCCCD(supplier.cccd)) {
            binding.edtCMND.setError(getString(R.string.cccd_invalid))
            binding.edtCMND.requestFocus()
            return false
        }

        if (!Validation.isValidTaxNumber(supplier.tax_number)) {
            binding.edtTax.setError(getString(R.string.taxNumber_invalid))
            binding.edtTax.requestFocus()
            return false
        }
        return true
    }

    private fun showInfo() {
        binding.edtUserName.text = Editable.Factory.getInstance().newEditable(supplier.contactName)
        binding.edtShopName.text = Editable.Factory.getInstance().newEditable(supplier.shopName)
        binding.edtUserEmail.text = Editable.Factory.getInstance().newEditable(supplier.email)
        binding.edtPhoneNumber.text = Editable.Factory.getInstance().newEditable(supplier.phone)
        binding.edtCMND.text = Editable.Factory.getInstance().newEditable(supplier.cccd)
        binding.edtTax.text = Editable.Factory.getInstance().newEditable(supplier.tax_number)
        binding.edtDetail.text = Editable.Factory.getInstance().newEditable(supplier.address)

        binding.edtUserEmail.isEnabled = false
        binding.edtUserEmail.isClickable = false
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
                    navController.navigate(R.id.action_updateGeneralInfoFragment_to_userInfoFragment)
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