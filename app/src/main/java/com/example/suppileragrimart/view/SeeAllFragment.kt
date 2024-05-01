package com.example.suppileragrimart.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.suppileragrimart.R
import com.example.suppileragrimart.adapter.ItemLoadingAdapter
import com.example.suppileragrimart.adapter.OrderAdapter
import com.example.suppileragrimart.adapter.ProductAdapter
import com.example.suppileragrimart.adapter.WarehouseAdapter
import com.example.suppileragrimart.databinding.FragmentSeeAllBinding
import com.example.suppileragrimart.model.MessageResponse
import com.example.suppileragrimart.model.OrderInfoApiRequest
import com.example.suppileragrimart.model.Product
import com.example.suppileragrimart.model.SearchApiRequest
import com.example.suppileragrimart.model.Supplier
import com.example.suppileragrimart.model.Warehouse
import com.example.suppileragrimart.utils.Constants
import com.example.suppileragrimart.utils.LoginUtils
import com.example.suppileragrimart.utils.ProgressDialog
import com.example.suppileragrimart.utils.ScreenState
import com.example.suppileragrimart.viewmodel.OrderViewModel
import com.example.suppileragrimart.viewmodel.ProductViewModel
import com.example.suppileragrimart.viewmodel.SearchViewModel
import com.example.suppileragrimart.viewmodel.SupplierViewModel
import com.example.suppileragrimart.viewmodel.WarehouseViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class SeeAllFragment : Fragment() {
    private lateinit var binding: FragmentSeeAllBinding
    private lateinit var navController: NavController

    private val args: SeeAllFragmentArgs by navArgs()
    private val loginUtils: LoginUtils by lazy {
        LoginUtils(requireContext())
    }
    private val progressDialog: ProgressDialog by lazy {
        ProgressDialog()
    }
    private val supplierViewModel: SupplierViewModel by lazy {
        ViewModelProvider(requireActivity()).get(SupplierViewModel::class.java)
    }
    private val searchViewModel: SearchViewModel by lazy {
        ViewModelProvider(requireActivity()).get(SearchViewModel::class.java)
    }
    private val productViewModel: ProductViewModel by lazy {
        ViewModelProvider(requireActivity()).get(ProductViewModel::class.java)
    }
    private val productAdapter: ProductAdapter by lazy {
        ProductAdapter(requireContext())
    }
    private val warehouseAdapter: WarehouseAdapter by lazy {
        WarehouseAdapter()
    }
    private val warehouseViewModel: WarehouseViewModel by lazy {
        ViewModelProvider(requireActivity()).get(WarehouseViewModel::class.java)
    }
    private val orderViewModel: OrderViewModel by lazy {
        ViewModelProvider(requireActivity()).get(OrderViewModel::class.java)
    }
    private val orderAdapter: OrderAdapter by lazy {
        OrderAdapter(requireContext())
    }

    private var supplier: Supplier? = null
    private lateinit var alertDialog: AlertDialog
    private var currentState: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSeeAllBinding.inflate(inflater)
        binding.toolbarLayout.titleToolbar.text = getString(R.string.search_result)

        supplier = supplierViewModel.supplier
        val productKeyword = args.product
        val warehouseKeyword = args.warehouse
        val orderKeyword = args.orderInfo

        if (supplier != null){
            if (productKeyword != null){
                setupRecyclerViewForProduct()
                searchProduct(productKeyword)
            } else if (warehouseKeyword != null){
                setupRecyclerViewForWarehouse()
                searchWarehouse(warehouseKeyword)
            } else if(orderKeyword != null){
                setupRecyclerViewForOrder()
                searchOrder(orderKeyword)
            }
        }

        return binding.root
    }

    private fun setupRecyclerViewForOrder() {
        binding.seeAllProductRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = orderAdapter.withLoadStateHeaderAndFooter(
                header = ItemLoadingAdapter { orderAdapter.retry() },
                footer = ItemLoadingAdapter { orderAdapter.retry() }
            )
        }
    }

    private fun searchOrder(orderKeyword: String) {
        val orderInfoApiRequest = OrderInfoApiRequest(supplier!!.id)
        lifecycleScope.launch {
            orderViewModel.getOrderBySupplierId(orderInfoApiRequest, orderKeyword).collectLatest { pagingData ->
                orderAdapter.addLoadStateListener { loadState ->
                    if (loadState.source.refresh is LoadState.NotLoading &&
                        loadState.append.endOfPaginationReached &&
                        orderAdapter.itemCount < 1
                    ) {
                        binding.notFoundLayout.visibility = View.VISIBLE
                        binding.seeAllProductRecyclerView.visibility = View.INVISIBLE
                    } else {
                        binding.notFoundLayout.visibility = View.GONE
                        binding.seeAllProductRecyclerView.visibility = View.VISIBLE
                    }
                }
                orderAdapter.submitData(pagingData)
            }
        }
    }

    private fun setupRecyclerViewForWarehouse() {
        binding.seeAllProductRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = warehouseAdapter.withLoadStateHeaderAndFooter(
                header = ItemLoadingAdapter { warehouseAdapter.retry() },
                footer = ItemLoadingAdapter { warehouseAdapter.retry() }
            )
        }
    }

    private fun searchWarehouse(warehouseKeyword: String) {
        val searchApiRequest = SearchApiRequest(warehouseKeyword)
        lifecycleScope.launch {
            searchViewModel.searchWarehouse(searchApiRequest, supplier!!.id).collectLatest { pagingData ->
                warehouseAdapter.addLoadStateListener { loadState ->
                    if (loadState.source.refresh is LoadState.NotLoading &&
                        loadState.append.endOfPaginationReached &&
                        warehouseAdapter.itemCount < 1
                    ) {
                        binding.notFoundLayout.visibility = View.VISIBLE
                        binding.seeAllProductRecyclerView.visibility = View.INVISIBLE
                    } else {
                        binding.notFoundLayout.visibility = View.GONE
                        binding.seeAllProductRecyclerView.visibility = View.VISIBLE
                    }
                }
                warehouseAdapter.submitData(pagingData)
            }
        }
    }

    private fun setupRecyclerViewForProduct() {
        binding.seeAllProductRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = productAdapter.withLoadStateHeaderAndFooter(
                header = ItemLoadingAdapter { productAdapter.retry() },
                footer = ItemLoadingAdapter { productAdapter.retry() }
            )
        }
    }

    private fun searchProduct(productKeyword: String) {
        val searchApiRequest = SearchApiRequest(productKeyword)
        lifecycleScope.launch {
            searchViewModel.searchProduct(searchApiRequest, supplier!!.id).collectLatest { pagingData ->
                productAdapter.addLoadStateListener { loadState ->
                    if (loadState.source.refresh is LoadState.NotLoading &&
                        loadState.append.endOfPaginationReached &&
                        productAdapter.itemCount < 1
                    ) {
                        binding.notFoundLayout.visibility = View.VISIBLE
                        binding.seeAllProductRecyclerView.visibility = View.INVISIBLE
                    } else {
                        binding.notFoundLayout.visibility = View.GONE
                        binding.seeAllProductRecyclerView.visibility = View.VISIBLE
                    }
                }
                productAdapter.submitData(pagingData)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        binding.toolbarLayout.imgBack.setOnClickListener{
            navController.navigateUp()
        }

        productAdapter.onSwitchState = {
            updateProductState(it)
        }

        productAdapter.onEdit = {
            val b = Bundle().apply {
                putParcelable(Constants.PRODUCT_KEY, it)
            }
            navController.navigate(R.id.action_seeAllFragment_to_editProductFragment, b)
        }

        warehouseAdapter.onSwitchState = {
            updateWarehouseState(it)
        }

        warehouseAdapter.onEdit = {
            val b = Bundle().apply {
                putParcelable(Constants.WAREHOUSE_KEY, it)
            }
            navController.navigate(R.id.action_seeAllFragment_to_editWarehouseFragment2, b)
        }

        orderAdapter.onClick = {
            val b = Bundle().apply {
                putParcelable(Constants.ORDER_KEY, it)
            }
            navController.navigate(R.id.action_seeAllFragment_to_orderDetailsFragment, b)
        }
    }
    // Warehouse
    private fun updateWarehouseState(it: Warehouse) {
        val token = loginUtils.getSupplierToken()
        val messageResponse = MessageResponse()
        messageResponse.isSuccessful = !it.isActive
        currentState = !it.isActive
        warehouseViewModel.updateState(token, supplier!!.id, it.id, messageResponse).observe(
            requireActivity(), { state -> processWarehouseResponse(state, it) }
        )
    }
    private fun processWarehouseResponse(state: ScreenState<Warehouse?>, warehouse: Warehouse) {
        when (state) {
            is ScreenState.Loading -> {
                alertDialog = progressDialog.createAlertDialog(requireActivity())
            }

            is ScreenState.Success -> {
                if (state.data != null) {
                    alertDialog.dismiss()
                    warehouse.isActive = currentState
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
    // product
    private fun updateProductState(product: Product){
        val token = loginUtils.getSupplierToken()
        productViewModel.updateProductState(token, product.productId).observe(
            requireActivity(), {state -> processProductResponse(state, product)}
        )
    }
    private fun processProductResponse(state: ScreenState<Product?>, product: Product) {
        when (state) {
            is ScreenState.Loading -> {
                alertDialog = progressDialog.createAlertDialog(requireActivity())
            }

            is ScreenState.Success -> {
                if (state.data != null) {
                    alertDialog.dismiss()
                    product.isActive = state.data.isActive
                    showSnackbar(getString(R.string.update_success))
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