package com.example.suppileragrimart.view.garden

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.suppileragrimart.R
import com.example.suppileragrimart.databinding.FragmentGardenUpdateBinding
import com.example.suppileragrimart.model.FieldApiResponse
import com.example.suppileragrimart.model.Supplier
import com.example.suppileragrimart.network.Api
import com.example.suppileragrimart.network.RetrofitClient
import com.example.suppileragrimart.utils.Constants
import com.example.suppileragrimart.utils.LoginUtils
import com.example.suppileragrimart.utils.ProgressDialog
import com.example.suppileragrimart.utils.ScreenState
import com.example.suppileragrimart.viewmodel.FieldViewModel
import com.example.suppileragrimart.viewmodel.SupplierViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class GardenUpdateFragment : Fragment() {
    private lateinit var binding: FragmentGardenUpdateBinding

    private val fieldViewModel: FieldViewModel by lazy {
        ViewModelProvider(requireActivity()).get(FieldViewModel::class.java)
    }
    private val loginUtils: LoginUtils by lazy {
        LoginUtils(requireContext())
    }
    private val progressDialog: ProgressDialog by lazy {
        ProgressDialog()
    }
    private val supplierViewModel: SupplierViewModel by lazy {
        ViewModelProvider(requireActivity()).get(SupplierViewModel::class.java)
    }
    private val apiService: Api by lazy {
        RetrofitClient.getInstance().getApi()
    }

    private var supplier: Supplier? = null
    private var currentField: FieldApiResponse? = null
    private lateinit var alertDialog: AlertDialog
    private var cropsTypeValues: ArrayList<String> = arrayListOf()
    private lateinit var selectedType: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGardenUpdateBinding.inflate(inflater)

        supplier = supplierViewModel.supplier
        currentField = fieldViewModel.fieldData
        setupSpinner()
        if (currentField != null)
            showInfo()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSpinnerListener()

        binding.btnComplete.setOnClickListener {
            lifecycleScope.launch {
                val check = checkRemainingCooperation()
                if(check) {
                    completeField()
                }
                else {
                    withContext(Dispatchers.Main){
                        showSnackbar("Vẫn còn đơn hàng chưa hoàn thành!")
                    }
                }
            }
        }

        binding.btnSave.setOnClickListener {
            val season = binding.edtCurrentCrop.text.toString().trim()
            val crops = binding.edtCurrentCrops.text.toString().trim()
            val area = binding.edtLandArea.text.toString().trim()

            if (season.isEmpty() || crops.isEmpty() || area.isEmpty()) {
                showSnackbar(Constants.FIELD_REQUIRED)
                return@setOnClickListener
            }

            val field = FieldApiResponse()
            field.season = season
            field.cropsType = selectedType
            field.cropsName = crops
            field.area = area

            updateField(field)
        }
    }

    suspend fun checkRemainingCooperation(): Boolean {
        return withContext(Dispatchers.IO) {
            val response = apiService.calculateRemainingCooperation(currentField!!.id)
            if (response.isSuccessful) {
                response.body()?.let {
                    val remainingCooperation = it.message.toLong()
                    remainingCooperation == 0L
                } ?: false
            } else {
                false
            }
        }
    }

    private fun completeField() {
        val token = loginUtils.getSupplierToken()
        fieldViewModel.completeField(token, currentField!!.id).observe(
            requireActivity(), { state -> processFieldResponse(state) }
        )
    }

    private fun showInfo() {
        binding.edtCurrentCrop.text = Editable.Factory.getInstance().newEditable(currentField!!.season)
        binding.edtCurrentCrops.text = Editable.Factory.getInstance().newEditable(currentField!!.cropsName)
        binding.edtLandArea.text = Editable.Factory.getInstance().newEditable(currentField!!.area)
        if (currentField!!.cropsType.equals(Constants.LONG_TERM_PLANT))
            binding.spCropsType.setSelection(0)
        else binding.spCropsType.setSelection(1)
    }

    private fun setupSpinner() {
        cropsTypeValues.add(Constants.LONG_TERM_PLANT)
        cropsTypeValues.add(Constants.SHORT_TERM_PLANT)
        val spinnerAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item,
            cropsTypeValues
        )
        binding.spCropsType.adapter = spinnerAdapter
    }

    private fun setupSpinnerListener() {
        binding.spCropsType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedType = cropsTypeValues[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun updateField(fieldApiResponse: FieldApiResponse) {
        val token = loginUtils.getSupplierToken()
        fieldViewModel.updateField(token, currentField!!.id, fieldApiResponse).observe(
            requireActivity(), { state -> processFieldResponse(state) }
        )
    }

    private fun processFieldResponse(state: ScreenState<FieldApiResponse?>) {
        when (state) {
            is ScreenState.Loading -> {
                alertDialog = progressDialog.createAlertDialog(requireActivity())
            }

            is ScreenState.Success -> {
                if (state.data != null) {
                    alertDialog.dismiss()
                    showSnackbar(getString(R.string.update_garden_successfully))
                    fieldViewModel.fieldData = state.data
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