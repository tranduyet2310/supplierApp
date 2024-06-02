package com.example.suppileragrimart.view.warehouse

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
import com.example.suppileragrimart.databinding.FragmentEditWarehouseBinding
import com.example.suppileragrimart.model.MessageResponse
import com.example.suppileragrimart.model.Supplier
import com.example.suppileragrimart.model.Warehouse
import com.example.suppileragrimart.utils.AES
import com.example.suppileragrimart.utils.Constants
import com.example.suppileragrimart.utils.LoginUtils
import com.example.suppileragrimart.utils.ProgressDialog
import com.example.suppileragrimart.utils.ScreenState
import com.example.suppileragrimart.viewmodel.SupplierViewModel
import com.example.suppileragrimart.viewmodel.WarehouseViewModel
import com.google.android.material.snackbar.Snackbar


class EditWarehouseFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentEditWarehouseBinding
    private lateinit var navController: NavController

    private val loginUtils: LoginUtils by lazy {
        LoginUtils(requireContext())
    }
    private val warehouseViewModel: WarehouseViewModel by lazy {
        ViewModelProvider(requireActivity()).get(WarehouseViewModel::class.java)
    }
    private val supplierViewModel: SupplierViewModel by lazy {
        ViewModelProvider(requireActivity()).get(SupplierViewModel::class.java)
    }

    private var supplier: Supplier? = null
    private lateinit var warehouse: Warehouse
    private lateinit var alertDialog: AlertDialog
    private val args: EditWarehouseFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditWarehouseBinding.inflate(inflater)
        binding.toolbarLayout.titleToolbar.text = getString(R.string.update_warehouse)

        supplier = supplierViewModel.supplier
        if (supplier == null) {
            showSnackbar("Không tìm thấy id")
            binding.btnSave.isEnabled = false
        }

        warehouse = args.warehouse
        showData()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        binding.btnCancel.setOnClickListener(this)
        binding.btnSave.setOnClickListener(this)

        binding.toolbarLayout.imgBack.setOnClickListener {
            navController.navigateUp()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnCancel -> deleteWarehouse()
            R.id.btnSave -> updateWarehouse()
        }
    }

    private fun showData() {
        binding.inputName.text = Editable.Factory.getInstance().newEditable(warehouse.warehouseName)
        binding.inputPerson.text = Editable.Factory.getInstance().newEditable(warehouse.contact)
        binding.inputPhone.text = Editable.Factory.getInstance().newEditable(warehouse.phone)
        binding.inputEmail.text = Editable.Factory.getInstance().newEditable(warehouse.email)
        binding.inputProvince.text = Editable.Factory.getInstance().newEditable(warehouse.province)
        binding.inputDistrict.text = Editable.Factory.getInstance().newEditable(warehouse.district)
        binding.inputWard.text = Editable.Factory.getInstance().newEditable(warehouse.commune)
        binding.inputDetail.text = Editable.Factory.getInstance().newEditable(warehouse.detail)
        binding.checkboxWarehouse.isChecked = warehouse.isActive
    }

    private fun updateWarehouse() {
        val warehouseName = binding.inputName.text.toString().trim()
        val contact = binding.inputPerson.text.toString().trim()
        val phone = binding.inputPhone.text.toString().trim()
        val email = binding.inputEmail.text.toString().trim()
        val province = binding.inputProvince.text.toString().trim()
        val district = binding.inputDistrict.text.toString().trim()
        val commune = binding.inputWard.text.toString().trim()
        val detail = binding.inputDetail.text.toString().trim()
        val isActive = binding.checkboxWarehouse.isChecked

        if (warehouseName.isEmpty() || contact.isEmpty() || phone.isEmpty()
            || email.isEmpty() || province.isEmpty() || district.isEmpty()
            || commune.isEmpty() || detail.isEmpty()
        ) {
            showSnackbar(Constants.FIELD_REQUIRED)
            return
        }

        warehouse.warehouseName = warehouseName
        warehouse.contact = contact
        warehouse.phone = phone
        warehouse.email = email
        warehouse.province = province
        warehouse.district = district
        warehouse.commune = commune
        warehouse.detail = detail
        warehouse.isActive = isActive

//        val encryptedWarehouse = encryptInfo(warehouse)

        val token = loginUtils.getSupplierToken()
        warehouseViewModel.updateWarehouse(token, supplier!!.id, warehouse.id, warehouse)
            .observe(
                requireActivity(), { state -> processWarehouseResponse(state) }
            )
    }

    private fun encryptInfo(warehouse: Warehouse): Warehouse {
        val encryptedWarehouse = Warehouse()

        val aesKey = loginUtils.getAESKey()
        val iv = loginUtils.getIv()
        val aes = AES.getInstance()
        aes.initFromString(aesKey, iv)

        encryptedWarehouse.warehouseName = aes.encrypt(warehouse.warehouseName)
        encryptedWarehouse.contact = aes.encrypt(warehouse.contact)
        encryptedWarehouse.phone = aes.encrypt(warehouse.phone)
        encryptedWarehouse.email = aes.encrypt(warehouse.email)
        encryptedWarehouse.province = aes.encrypt(warehouse.province)
        encryptedWarehouse.district = aes.encrypt(warehouse.district)
        encryptedWarehouse.commune = aes.encrypt(warehouse.commune)
        encryptedWarehouse.detail = aes.encrypt(warehouse.detail)
        encryptedWarehouse.isActive = warehouse.isActive

        return encryptedWarehouse
    }

    private fun deleteWarehouse() {
        val dialog = AlertDialog.Builder(requireContext()).apply {
            setTitle(getString(R.string.check_out))
            setMessage(getString(R.string.warehouse_question))
            setNegativeButton(getString(R.string.cancel)) { dialogs, _ ->
                dialogs.dismiss()
            }
            setPositiveButton(getString(R.string.ok)) { dialogs, _ ->
                val token = loginUtils.getSupplierToken()
                warehouseViewModel.deleteWarehouse(token, supplier!!.id, warehouse.id).observe(
                    requireActivity(), { state -> handleDeleteWarehouse(state) }
                )
                dialogs.dismiss()
            }
        }
        dialog.create()
        dialog.show()
    }

    private fun processWarehouseResponse(state: ScreenState<Warehouse?>) {
        when (state) {
            is ScreenState.Loading -> {
                val progressDialog = ProgressDialog()
                alertDialog = progressDialog.createAlertDialog(requireActivity())
            }

            is ScreenState.Success -> {
                if (state.data != null) {
                    alertDialog.dismiss()
                    showSnackbar(getString(R.string.updated_warehouse))
                    navController.navigate(R.id.action_editWarehouseFragment2_to_warehouseFragment)
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

    private fun handleDeleteWarehouse(state: ScreenState<MessageResponse?>) {
        when (state) {
            is ScreenState.Loading -> {
                val progressDialog = ProgressDialog()
                alertDialog = progressDialog.createAlertDialog(requireActivity())
            }

            is ScreenState.Success -> {
                if (state.data != null) {
                    alertDialog.dismiss()
                    showSnackbar(getString(R.string.delete_warehouse))
                    navController.navigate(R.id.action_editWarehouseFragment2_to_warehouseFragment)
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