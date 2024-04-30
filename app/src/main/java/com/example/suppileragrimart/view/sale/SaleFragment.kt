package com.example.suppileragrimart.view.sale

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
import androidx.navigation.Navigation
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.suppileragrimart.R
import com.example.suppileragrimart.adapter.ItemLoadingAdapter
import com.example.suppileragrimart.adapter.OrderAdapter
import com.example.suppileragrimart.databinding.FragmentSaleBinding
import com.example.suppileragrimart.model.OrderInfoApiRequest
import com.example.suppileragrimart.model.Supplier
import com.example.suppileragrimart.utils.Constants
import com.example.suppileragrimart.utils.LoginUtils
import com.example.suppileragrimart.utils.ProgressDialog
import com.example.suppileragrimart.utils.Utils.Companion.getCurrentMonth
import com.example.suppileragrimart.utils.Utils.Companion.getMonth
import com.example.suppileragrimart.utils.Utils.Companion.updateMonthInString
import com.example.suppileragrimart.viewmodel.OrderViewModel
import com.example.suppileragrimart.viewmodel.SupplierViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SaleFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentSaleBinding
    private lateinit var navController: NavController

    private val supplierViewModel: SupplierViewModel by lazy {
        ViewModelProvider(requireActivity()).get(SupplierViewModel::class.java)
    }
    private val orderViewModel: OrderViewModel by lazy {
        ViewModelProvider(requireActivity()).get(OrderViewModel::class.java)
    }

    private var supplier: Supplier? = null
    private lateinit var orderAdapter: OrderAdapter
    private var sortByValues: ArrayList<String> = arrayListOf()
    private var monthValues: ArrayList<String> = arrayListOf()
    private lateinit var selectedType: String
    private lateinit var currentMonth: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSaleBinding.inflate(inflater)
        binding.toolbarLayout.titleToolbar.text = getString(R.string.sale)
        binding.toolbarLayout.imgBack.visibility = View.GONE

        supplier = supplierViewModel.supplier
        currentMonth = getCurrentMonth()
        if (supplier != null) {
            setupSortBySpinner()
            setupMonthSpinner()
            setupRecyclerView()
            val orderInfoApiRequest = OrderInfoApiRequest(supplier!!.id)
            getOrderInfo(orderInfoApiRequest)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        binding.tvSearch.setOnClickListener(this)

        setupSpinnerListener()
        setupMonthSpinnerListener()

        orderAdapter.onClick = {
            val b = Bundle().apply {
                putParcelable(Constants.ORDER_KEY, it)
            }
            navController.navigate(R.id.action_saleFragment_to_orderDetailsFragment, b)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvSearch -> goToSearchFragment()
        }
    }

    private fun goToSearchFragment() {
//        navController.navigate(R.id.action_saleFragment_to_searchFragment)
    }

    private fun showEmptyBox() {
        binding.rcvSale.visibility = View.GONE
        binding.emptyLayout.visibility = View.VISIBLE
    }

    private fun hideEmptyBox() {
        binding.rcvSale.visibility = View.VISIBLE
        binding.emptyLayout.visibility = View.GONE
    }

    private fun setupMonthSpinner() {
        monthValues.clear()
        for (i in 1..12) {
            monthValues.add("Tháng ${i}")
        }
        val spinnerAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item,
            monthValues
        )
        binding.spinnerMonth.adapter = spinnerAdapter
        binding.spinnerMonth.setSelection(getMonth())
    }

    private fun setupSortBySpinner() {
        sortByValues.clear()
        sortByValues.add(Constants.DATE_CREATED)
        sortByValues.add(Constants.STATE)
        val spinnerAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item,
            sortByValues
        )
        binding.spinnerSortBy.adapter = spinnerAdapter
        binding.spinnerSortBy.setSelection(0)
    }

    private fun getOrderInfo(orderInfoApiRequest: OrderInfoApiRequest) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                orderViewModel.getOrderBySupplierId(orderInfoApiRequest, currentMonth)
                    .collectLatest { pagingData ->
                        orderAdapter.addLoadStateListener { loadState ->
                            if (loadState.source.refresh is LoadState.NotLoading &&
                                loadState.append.endOfPaginationReached &&
                                orderAdapter.itemCount < 1
                            ) {
                                showEmptyBox()
                            } else {
                                hideEmptyBox()
                            }
                        }
                        orderAdapter.submitData(pagingData)
                    }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rcvSale.apply {
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            orderAdapter = OrderAdapter(requireContext())
            adapter = orderAdapter.withLoadStateHeaderAndFooter(
                header = ItemLoadingAdapter { orderAdapter.retry() },
                footer = ItemLoadingAdapter { orderAdapter.retry() }
            )
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
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
                if (selectedType.equals(Constants.DATE_CREATED)) {
                    val orderInfoApiRequest = OrderInfoApiRequest(supplier!!.id)
                    orderInfoApiRequest.sortBy = "dateCreated"
                    orderInfoApiRequest.sortDir = "desc"
                    getOrderInfo(orderInfoApiRequest)
                } else if (selectedType.equals(Constants.STATE)) {
                    val orderInfoApiRequest = OrderInfoApiRequest(supplier!!.id)
                    orderInfoApiRequest.sortBy = "orderStatus"
                    getOrderInfo(orderInfoApiRequest)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun setupMonthSpinnerListener() {
        binding.spinnerMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                currentMonth = updateMonthInString(position)
                val orderInfoApiRequest = OrderInfoApiRequest(supplier!!.id)
                getOrderInfo(orderInfoApiRequest)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }
}