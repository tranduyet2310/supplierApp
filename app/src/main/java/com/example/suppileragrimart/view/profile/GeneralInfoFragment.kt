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
import com.example.suppileragrimart.utils.Constants
import com.example.suppileragrimart.utils.ProgressDialog
import com.example.suppileragrimart.utils.ScreenState
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

    private var supplier: Supplier? = null
    private lateinit var alertDialog: AlertDialog
    private lateinit var currentSupplier: Supplier

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGeneralInfoBinding.inflate(inflater)

        supplier = supplierViewModel.supplier
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
                    showInfo(state.data)
                    currentSupplier = state.data
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
}