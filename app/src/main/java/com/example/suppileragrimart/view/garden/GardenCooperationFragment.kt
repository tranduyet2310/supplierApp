package com.example.suppileragrimart.view.garden

import android.R
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.suppileragrimart.adapter.CooperationAdapter
import com.example.suppileragrimart.adapter.ItemLoadingAdapter
import com.example.suppileragrimart.databinding.FragmentCooperationBinding
import com.example.suppileragrimart.model.CooperationApiRequest
import com.example.suppileragrimart.model.FieldApiResponse
import com.example.suppileragrimart.model.Supplier
import com.example.suppileragrimart.utils.Constants
import com.example.suppileragrimart.utils.LoginUtils
import com.example.suppileragrimart.utils.ProgressDialog
import com.example.suppileragrimart.viewmodel.CooperationViewModel
import com.example.suppileragrimart.viewmodel.FieldViewModel
import com.example.suppileragrimart.viewmodel.SupplierViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class GardenCooperationFragment : Fragment() {
    private lateinit var binding: FragmentCooperationBinding

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
    private val cooperationViewModel: CooperationViewModel by lazy {
        ViewModelProvider(requireActivity()).get(CooperationViewModel::class.java)
    }

    private var supplier: Supplier? = null
    private var currentField: FieldApiResponse? = null
    private lateinit var alertDialog: AlertDialog
    private var sortByValues: ArrayList<String> = arrayListOf()
    private lateinit var cooperationAdapter: CooperationAdapter
    private lateinit var selectedType: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCooperationBinding.inflate(inflater)

        supplier = supplierViewModel.supplier
        currentField = fieldViewModel.fieldData
        setupRecyclerView()
        if (currentField != null && supplier != null) {
            getCooperations()
        }

        return binding.root
    }

    private fun getCooperations() {
        val cooperationApiRequest = CooperationApiRequest()
        cooperationApiRequest.id = currentField!!.id
        lifecycleScope.launch {
            cooperationViewModel.getCooperation(supplier!!.id, cooperationApiRequest)
                .collectLatest { pagingData ->
                    cooperationAdapter.addLoadStateListener { loadState ->
                        if (loadState.source.refresh is LoadState.NotLoading &&
                            loadState.append.endOfPaginationReached &&
                            cooperationAdapter.itemCount < 1
                        ) {
                            hideRecyclerView()
                        } else {
                            showRecyclerView()
                        }
                    }
                    cooperationAdapter.submitData(pagingData)
                }
        }
    }

    private fun setupRecyclerView() {
        binding.rcvCultivation.apply {
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            cooperationAdapter = CooperationAdapter()
            adapter = cooperationAdapter.withLoadStateHeaderAndFooter(
                header = ItemLoadingAdapter { cooperationAdapter.retry() },
                footer = ItemLoadingAdapter { cooperationAdapter.retry() }
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        setupSpinnerListener()

        cooperationAdapter.onClick = {
//            val b = Bundle().apply {
//                putParcelable(Constants.COOPERATION_KEY, it)
//            }
            val action = GardenGeneralFragmentDirections.actionGardenInfoFragmentToCooperationDetailFragment2(it)
            this@GardenCooperationFragment.findNavController().navigate(action)
        }
    }

    private fun setupSpinner(fieldNames: ArrayList<String>) {
        sortByValues.addAll(fieldNames)
        val spinnerAdapter = ArrayAdapter(
            requireContext(), R.layout.simple_spinner_dropdown_item,
            sortByValues
        )
        binding.spinnerSortBy.adapter = spinnerAdapter
    }

    private fun setupSpinnerListener() {
        binding.spinnerSortBy.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedType = sortByValues[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun hideRecyclerView() {
        binding.imgPlaceholder.visibility = View.VISIBLE
        binding.tvPlaceholder.visibility = View.VISIBLE
        binding.rcvCultivation.visibility = View.GONE
    }

    private fun showRecyclerView() {
        binding.imgPlaceholder.visibility = View.GONE
        binding.tvPlaceholder.visibility = View.GONE
        binding.rcvCultivation.visibility = View.VISIBLE
    }

    private fun displayErrorSnackbar(errorMessage: String) {
        Snackbar.make(requireView(), errorMessage, Snackbar.LENGTH_SHORT).show()
    }
}