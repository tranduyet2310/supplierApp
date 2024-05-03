package com.example.suppileragrimart.view.profile

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.suppileragrimart.R
import com.example.suppileragrimart.databinding.FragmentGeneralInfoBinding
import com.example.suppileragrimart.model.Product
import com.example.suppileragrimart.model.Supplier
import com.example.suppileragrimart.utils.AES
import com.example.suppileragrimart.utils.Constants
import com.example.suppileragrimart.utils.LoginUtils
import com.example.suppileragrimart.utils.ProgressDialog
import com.example.suppileragrimart.utils.ScreenState
import com.example.suppileragrimart.utils.Utils
import com.example.suppileragrimart.utils.Utils.Companion.decryptData
import com.example.suppileragrimart.viewmodel.SupplierViewModel
import com.google.android.material.snackbar.Snackbar


class GeneralInfoFragment : Fragment() {
    private lateinit var binding: FragmentGeneralInfoBinding

    private val supplierViewModel: SupplierViewModel by lazy {
        ViewModelProvider(requireActivity()).get(SupplierViewModel::class.java)
    }
    private val progressDialog: ProgressDialog by lazy {
        ProgressDialog()
    }
    private val loginUtils: LoginUtils by lazy {
        LoginUtils(requireContext())
    }

    private var supplier: Supplier? = null
    private lateinit var alertDialog: AlertDialog
    private lateinit var currentSupplier: Supplier
    private lateinit var secretKey: String
    private lateinit var iv: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGeneralInfoBinding.inflate(inflater)

        supplier = supplierViewModel.supplier
        secretKey = loginUtils.getAESKey()
        iv = loginUtils.getIv()
        if (supplier != null)
            getSupplierInfo()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSave.setOnClickListener {
            val b = Bundle().apply {
                putParcelable(Constants.SUPPLIER, currentSupplier)
            }
            this@GeneralInfoFragment.findNavController()
                .navigate(R.id.action_userInfoFragment_to_updateGeneralInfoFragment, b)
        }
    }

    private fun getSupplierInfo() {
        supplierViewModel.getSupplierById(supplier!!.id).observe(
            requireActivity(), { state -> processSupplierResponse(state) }
        )
    }

    private fun processSupplierResponse(state: ScreenState<Supplier?>) {
        when (state) {
            is ScreenState.Loading -> {
                alertDialog = progressDialog.createAlertDialog(requireActivity())
            }

            is ScreenState.Success -> {
                if (state.data != null) {
                    alertDialog.dismiss()
                    val data = decryptData(state.data, secretKey, iv)
                    showInfo(data)
                    currentSupplier = data
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

    private fun showInfo(supplier: Supplier) {
        binding.tvFullNameUser.text = supplier.contactName
        binding.tvShopName.text = supplier.shopName
        binding.tvCccd.text = supplier.cccd
        binding.tvEmail.text = supplier.email
        binding.tvPhoneNumber.text = supplier.phone
        binding.tvTaxNumber.text = supplier.tax_number
        if (!supplier.address.isEmpty()) {
            binding.tvDetail.text = supplier.address
        } else binding.tvDetail.text = supplier.province
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

//    private fun decryptData(supplier: Supplier?, secretKey: String, iv: String): Supplier {
//        val aes = AES.getInstance()
//        aes.initFromString(secretKey, iv)
//        val decryptedData = Supplier()
//
//        if (supplier != null) {
//            decryptedData.id = supplier.id
//            if (supplier.contactName != null)
//                decryptedData.contactName = aes.decrypt(supplier.contactName)
//            if (supplier.shopName != null)
//                decryptedData.shopName = aes.decrypt(supplier.shopName)
//            if (supplier.email != null)
//                decryptedData.email = aes.decrypt(supplier.email)
//            if (supplier.phone != null)
//                decryptedData.phone = aes.decrypt(supplier.phone)
//            if (supplier.cccd != null)
//                decryptedData.cccd = aes.decrypt(supplier.cccd)
//            if (supplier.tax_number != null)
//                decryptedData.tax_number = aes.decrypt(supplier.tax_number)
//            if (supplier.address != null)
//                decryptedData.address = aes.decrypt(supplier.address)
//            if (supplier.province != null)
//                decryptedData.province = aes.decrypt(supplier.province)
//            if (supplier.password != null)
//                decryptedData.password = aes.decrypt(supplier.password)
//            if (supplier.sellerType != null)
//                decryptedData.sellerType = aes.decrypt(supplier.sellerType)
//            if (supplier.bankAccountNumber != null)
//                decryptedData.bankAccountNumber = aes.decrypt(supplier.bankAccountNumber)
//            if (supplier.bankAccountOwner != null)
//                decryptedData.bankAccountOwner = aes.decrypt(supplier.bankAccountOwner)
//            if (supplier.bankName != null)
//                decryptedData.bankName = aes.decrypt(supplier.bankName)
//            if (supplier.bankBranchName != null)
//                decryptedData.bankBranchName = aes.decrypt(supplier.bankBranchName)
//            if (supplier.avatar != null)
//                decryptedData.avatar = supplier.avatar
//        }
//        return decryptedData
//    }

}