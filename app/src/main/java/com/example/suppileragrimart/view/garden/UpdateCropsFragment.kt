package com.example.suppileragrimart.view.garden

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.suppileragrimart.R
import com.example.suppileragrimart.databinding.FragmentUpdateCropsBinding
import com.example.suppileragrimart.model.FieldApiResponse
import com.example.suppileragrimart.model.Supplier
import com.example.suppileragrimart.utils.Constants
import com.example.suppileragrimart.utils.LoginUtils
import com.example.suppileragrimart.utils.ProgressDialog
import com.example.suppileragrimart.utils.ScreenState
import com.example.suppileragrimart.viewmodel.FieldViewModel
import com.example.suppileragrimart.viewmodel.SupplierViewModel
import com.google.android.material.snackbar.Snackbar


class UpdateCropsFragment : Fragment() {
    private lateinit var binding: FragmentUpdateCropsBinding
    private lateinit var navController: NavController

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

    private var supplier: Supplier? = null
    private lateinit var alertDialog: AlertDialog
    private var cropsTypeValues: ArrayList<String> = arrayListOf()
    private lateinit var selectedType: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpdateCropsBinding.inflate(inflater)
        binding.toolbarLayout.titleToolbar.text = getString(R.string.create_garden)

        supplier = supplierViewModel.supplier
        setupSpinner()
        if (supplier != null)
            Log.d("TEST", "id " + supplier!!.id)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        binding.toolbarLayout.imgBack.setOnClickListener {
            navController.navigateUp()
        }

        binding.btnBack.setOnClickListener {
            navController.navigateUp()
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

            createField(field)
        }

        setupSpinnerListener()
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

    private fun createField(fieldApiResponse: FieldApiResponse) {
        val token = loginUtils.getSupplierToken()
        fieldViewModel.createField(token, supplier!!.id, fieldApiResponse).observe(
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
                    showSnackbar(getString(R.string.create_garden_successfully))
                    navController.navigate(R.id.action_updateCropsFragment_to_cropsInfoFragment)
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