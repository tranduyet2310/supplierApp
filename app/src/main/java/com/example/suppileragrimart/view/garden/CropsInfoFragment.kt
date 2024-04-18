package com.example.suppileragrimart.view.garden

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.suppileragrimart.R
import com.example.suppileragrimart.adapter.CropsAdapter
import com.example.suppileragrimart.adapter.ItemLoadingAdapter
import com.example.suppileragrimart.adapter.WarehouseAdapter
import com.example.suppileragrimart.databinding.FragmentCropsInfoBinding
import com.example.suppileragrimart.model.FieldApiResponse
import com.example.suppileragrimart.model.Supplier
import com.example.suppileragrimart.model.Warehouse
import com.example.suppileragrimart.utils.Constants
import com.example.suppileragrimart.utils.LoginUtils
import com.example.suppileragrimart.utils.ProgressDialog
import com.example.suppileragrimart.utils.ScreenState
import com.example.suppileragrimart.viewmodel.FieldViewModel
import com.example.suppileragrimart.viewmodel.SupplierViewModel
import com.google.android.material.snackbar.Snackbar


class CropsInfoFragment : Fragment() {
    private lateinit var binding: FragmentCropsInfoBinding
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
    private lateinit var cropsAdapter: CropsAdapter
    private lateinit var fieldList: ArrayList<FieldApiResponse>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCropsInfoBinding.inflate(inflater)
        binding.toolbarLayout.titleToolbar.text = getString(R.string.garden_info)

        supplier = supplierViewModel.supplier
        setupRecycler()
        if (supplier != null) getFieldData()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        binding.btnUpdate.setOnClickListener {
            navController.navigate(R.id.action_cropsInfoFragment_to_updateCropsFragment)
        }

        binding.toolbarLayout.imgBack.setOnClickListener {
            navController.navigateUp()
        }

        cropsAdapter.onClick = {
            fieldViewModel.fieldData = it
            navController.navigate(R.id.action_cropsInfoFragment_to_gardenInfoFragment)
        }
    }

    private fun getFieldData() {
        fieldViewModel.getFieldBySupplierId(supplier!!.id).observe(
            requireActivity(), { state -> processFieldResponse(state) }
        )
    }

    private fun setupRecycler() {
        fieldList = arrayListOf()
        binding.rcvCrops.apply {
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            cropsAdapter = CropsAdapter(fieldList)
            adapter = cropsAdapter
        }
    }

    private fun processFieldResponse(state: ScreenState<ArrayList<FieldApiResponse>?>) {
        when (state) {
            is ScreenState.Loading -> {
                alertDialog = progressDialog.createAlertDialog(requireActivity())
            }

            is ScreenState.Success -> {
                if (state.data != null) {
                    alertDialog.dismiss()
                    fieldList.clear()
                    if (state.data.isEmpty()){
                        hideRecyclerView()
                    } else {
                        showRecyclerView()
                        fieldList.addAll(state.data)
                        cropsAdapter.notifyDataSetChanged()
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

    private fun showRecyclerView() {
        binding.rcvCrops.visibility = View.VISIBLE
        binding.textAnnounce.visibility = View.GONE
        binding.imagePlaceholder.visibility = View.GONE
    }

    private fun hideRecyclerView() {
        binding.rcvCrops.visibility = View.GONE
        binding.textAnnounce.visibility = View.VISIBLE
        binding.imagePlaceholder.visibility = View.VISIBLE
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

}