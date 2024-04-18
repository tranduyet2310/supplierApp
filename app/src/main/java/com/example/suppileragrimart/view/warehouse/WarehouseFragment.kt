package com.example.suppileragrimart.view.warehouse

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.suppileragrimart.R
import com.example.suppileragrimart.adapter.ItemLoadingAdapter
import com.example.suppileragrimart.adapter.WarehouseAdapter
import com.example.suppileragrimart.databinding.FragmentWarehouseBinding
import com.example.suppileragrimart.model.MessageResponse
import com.example.suppileragrimart.model.Supplier
import com.example.suppileragrimart.model.Warehouse
import com.example.suppileragrimart.model.WarehouseApiRequest
import com.example.suppileragrimart.utils.Constants.RETRY
import com.example.suppileragrimart.utils.Constants.WAREHOUSE_KEY
import com.example.suppileragrimart.utils.LoginUtils
import com.example.suppileragrimart.utils.ProgressDialog
import com.example.suppileragrimart.utils.ScreenState
import com.example.suppileragrimart.viewmodel.SupplierViewModel
import com.example.suppileragrimart.viewmodel.WarehouseViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class WarehouseFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentWarehouseBinding
    private lateinit var navController: NavController

    private val warehouseViewModel: WarehouseViewModel by lazy {
        ViewModelProvider(requireActivity()).get(WarehouseViewModel::class.java)
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
    private lateinit var warehouseAdapter: WarehouseAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWarehouseBinding.inflate(inflater)
        binding.toolbarLayout.titleToolbar.text = getString(R.string.warehouse)
        binding.toolbarLayout.imgBack.visibility = View.GONE

        supplier = supplierViewModel.supplier
        val isValidPubKey = supplierViewModel.isValidPublicKey

        if (!isValidPubKey) {
            displayErrorSnackbar("Không tìm thấy public key để giải mã")
        } else if (supplier == null) {
            displayErrorSnackbar("supplier is null")
        } else {
            setupRecyclerView()
            getWarehouseData()
        }

        return binding.root
    }

    private fun getWarehouseData() {
        val warehouseApiRequest = WarehouseApiRequest(supplier!!.id)
        val aesKey = loginUtils.getAESKey()
        val iv = loginUtils.getIv()
        Log.d("TEST", "ware aes "+aesKey)
        Log.d("TEST", "ware iv "+iv)
        lifecycleScope.launch {
            warehouseViewModel.getWarehouseBySupplierId(warehouseApiRequest, aesKey, iv)
                .collectLatest { pagingData ->
                    warehouseAdapter.addLoadStateListener { loadState ->
                        if (loadState.source.refresh is LoadState.NotLoading &&
                            loadState.append.endOfPaginationReached &&
                            warehouseAdapter.itemCount < 1
                        ) {
                            showEmptyBox()
                        } else {
                            hideEmptyBox()
                        }
                    }
                    warehouseAdapter.submitData(pagingData)
                }
        }
    }

    private fun setupRecyclerView() {
        binding.rcvWarehouse.apply {
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            warehouseAdapter = WarehouseAdapter()
            adapter = warehouseAdapter.withLoadStateHeaderAndFooter(
                header = ItemLoadingAdapter { warehouseAdapter.retry() },
                footer = ItemLoadingAdapter { warehouseAdapter.retry() }
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        binding.tvSearch.setOnClickListener(this)
        binding.btnAddWareHouse.setOnClickListener(this)

        warehouseAdapter.onSwitchState = {
            val token = loginUtils.getSupplierToken()
            val messageResponse = MessageResponse()
            messageResponse.isSuccessful = !it.isActive
            warehouseViewModel.updateState(token, supplier!!.id, it.id, messageResponse).observe(
                requireActivity(), { state -> processWarehouseResponse(state) }
            )
        }
        warehouseAdapter.onEdit = {
            val b = Bundle().apply {
                putParcelable(WAREHOUSE_KEY, it)
            }
            navController.navigate(R.id.action_warehouseFragment_to_editWarehouseFragment2, b)
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvSearch -> goToSearchFragment()
            R.id.btnAddWareHouse -> goToAddWareHouseFragment()
        }
    }

    private fun goToAddWareHouseFragment() {
        navController.navigate(R.id.action_warehouseFragment_to_newWarehouseFragment)
    }

    private fun goToSearchFragment() {
        navController.navigate(R.id.action_warehouseFragment_to_searchFragment)
    }

    private fun hideEmptyBox() {
        binding.emptyLayout.visibility = View.GONE
        binding.rcvWarehouse.visibility = View.VISIBLE
    }

    private fun showEmptyBox() {
        binding.emptyLayout.visibility = View.VISIBLE
        binding.rcvWarehouse.visibility = View.GONE
    }

    private fun processWarehouseResponse(state: ScreenState<Warehouse?>) {
        when (state) {
            is ScreenState.Loading -> {
                alertDialog = progressDialog.createAlertDialog(requireActivity())
            }

            is ScreenState.Success -> {
                if (state.data != null) {
                    alertDialog.dismiss()
                    showSnackbar(getString(R.string.updated_warehouse))
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

    private fun displayErrorSnackbar(errorMessage: String) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
    }
}