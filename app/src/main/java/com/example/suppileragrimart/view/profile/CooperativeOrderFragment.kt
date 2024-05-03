package com.example.suppileragrimart.view.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.suppileragrimart.R
import com.example.suppileragrimart.adapter.CooperativeOrderAdapter
import com.example.suppileragrimart.adapter.ItemLoadingAdapter
import com.example.suppileragrimart.databinding.FragmentCooperativeOrderBinding
import com.example.suppileragrimart.model.Supplier
import com.example.suppileragrimart.utils.Constants
import com.example.suppileragrimart.viewmodel.CooperativeOrderViewModel
import com.example.suppileragrimart.viewmodel.SupplierViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class CooperativeOrderFragment : Fragment() {
    private lateinit var binding: FragmentCooperativeOrderBinding
    private lateinit var navController: NavController

    private val supplierViewModel: SupplierViewModel by lazy {
        ViewModelProvider(requireActivity()).get(SupplierViewModel::class.java)
    }
    private val cooperativeOrderAdapter: CooperativeOrderAdapter by lazy {
        CooperativeOrderAdapter()
    }
    private val cooperativeOrderViewModel: CooperativeOrderViewModel by lazy {
        ViewModelProvider(requireActivity()).get(CooperativeOrderViewModel::class.java)
    }
    private var supplier: Supplier? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCooperativeOrderBinding.inflate(inflater)
        binding.toolbarLayout.titleToolbar.text = getString(R.string.all_orders)

        supplier = supplierViewModel.supplier
        setupRecyclerView()
        if (supplier != null) getCooperativeOrder()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        binding.toolbarLayout.imgBack.setOnClickListener {
            navController.navigateUp()
        }

        cooperativeOrderAdapter.onClick = {
            val b = Bundle().apply {
                putLong(Constants.COOPERATIIVE_KEY, it.id)
            }
            navController.navigate(R.id.action_cooperativeOrderFragment_to_cooperativeDetailFragment, b)
        }
    }

    private fun setupRecyclerView() {
        binding.rvAllOrders.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = cooperativeOrderAdapter.withLoadStateHeaderAndFooter(
                header = ItemLoadingAdapter { cooperativeOrderAdapter.retry() },
                footer = ItemLoadingAdapter { cooperativeOrderAdapter.retry() }
            )
        }
    }

    private fun showOrderList(){
        binding.emptyBox.visibility = View.GONE
        binding.tvEmptyOrders.visibility = View.GONE
        binding.noBookmarks.visibility = View.GONE
        binding.rvAllOrders.visibility = View.VISIBLE
    }

    private fun hideOrderList(){
        binding.emptyBox.visibility = View.VISIBLE
        binding.tvEmptyOrders.visibility = View.VISIBLE
        binding.noBookmarks.visibility = View.VISIBLE
        binding.rvAllOrders.visibility = View.GONE
    }

    private fun getCooperativeOrder() {
        lifecycleScope.launch {
            cooperativeOrderViewModel.getCooperativeOrderBySupplierId(supplier!!.id).collectLatest { pagingData ->
                cooperativeOrderAdapter.addLoadStateListener { loadState ->
                    if (loadState.source.refresh is LoadState.NotLoading &&
                        loadState.append.endOfPaginationReached &&
                        cooperativeOrderAdapter.itemCount < 1
                    ) {
                        hideOrderList()
                    } else {
                        showOrderList()
                    }
                }
                cooperativeOrderAdapter.submitData(pagingData)
            }
        }
    }
}