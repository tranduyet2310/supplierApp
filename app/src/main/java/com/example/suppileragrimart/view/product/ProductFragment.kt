package com.example.suppileragrimart.view.product

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.suppileragrimart.R
import com.example.suppileragrimart.adapter.ItemLoadingAdapter
import com.example.suppileragrimart.adapter.ProductAdapter
import com.example.suppileragrimart.adapter.WarehouseAdapter
import com.example.suppileragrimart.databinding.FragmentProductBinding
import com.example.suppileragrimart.model.ProductApiRequest
import com.example.suppileragrimart.model.Supplier
import com.example.suppileragrimart.model.WarehouseApiRequest
import com.example.suppileragrimart.utils.LoginUtils
import com.example.suppileragrimart.utils.ProgressDialog
import com.example.suppileragrimart.viewmodel.ProductViewModel
import com.example.suppileragrimart.viewmodel.SupplierViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProductFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentProductBinding
    private lateinit var navController: NavController

    private val loginUtils: LoginUtils by lazy {
        LoginUtils(requireContext())
    }
    private val progressDialog: ProgressDialog by lazy {
        ProgressDialog()
    }
    private val supplierViewModel: SupplierViewModel by lazy {
        ViewModelProvider(requireActivity()).get(SupplierViewModel::class.java)
    }
    private val productViewModel: ProductViewModel by lazy {
        ViewModelProvider(requireActivity()).get(ProductViewModel::class.java)
    }

    private var supplier: Supplier? = null
    private lateinit var alertDialog: AlertDialog
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductBinding.inflate(inflater)
        binding.toolbarLayout.titleToolbar.text = getString(R.string.product_list)
        binding.toolbarLayout.imgBack.visibility = View.GONE

        supplier = supplierViewModel.supplier
        val isValidPubKey = supplierViewModel.isValidPublicKey

        if (!isValidPubKey) {
            displayErrorSnackbar("Không tìm thấy public key để giải mã")
        } else if (supplier == null) {
            displayErrorSnackbar("supplier is null")
        } else {
            setupRecyclerView()
            getProductData()
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.rcvProduct.apply {
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            productAdapter = ProductAdapter(requireContext())
            adapter = productAdapter.withLoadStateHeaderAndFooter(
                header = ItemLoadingAdapter { productAdapter.retry() },
                footer = ItemLoadingAdapter { productAdapter.retry() }
            )
        }
    }
// QrYFP23/sV7zpievbzJ6yw==
//    bhppZzubx8gPSwW/
    private fun getProductData() {
        val productApiRequest = ProductApiRequest(supplier!!.id)
        val aesKey = loginUtils.getAESKey()
        val iv = loginUtils.getIv()
        Log.d("TEST", "pro aes "+aesKey)
        Log.d("TEST", "pro iv "+iv)
        lifecycleScope.launch {
            productViewModel.getProducts(productApiRequest, aesKey, iv)
                .collectLatest { pagingData ->
                    productAdapter.addLoadStateListener { loadState ->
                        if (loadState.source.refresh is LoadState.NotLoading &&
                            loadState.append.endOfPaginationReached &&
                            productAdapter.itemCount < 1
                        ) {
                            showEmptyBox()
                        } else {
                            hideEmptyBox()
                        }
                    }
                    productAdapter.submitData(pagingData)
                }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        binding.btnAddNewProduct.setOnClickListener(this)
        binding.tvSearch.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnAddNewProduct -> goToNewProductFragment()
            R.id.tvSearch -> goToSearchFragment()
        }
    }

    private fun goToSearchFragment() {
        navController.navigate(R.id.action_productFragment_to_searchFragment)
    }

    private fun goToNewProductFragment() {
        navController.navigate(R.id.action_productFragment_to_newProductFragment)
    }

    private fun hideEmptyBox() {
        binding.emptyLayout.visibility = View.GONE
        binding.rcvProduct.visibility = View.VISIBLE
    }

    private fun showEmptyBox() {
        binding.emptyLayout.visibility = View.VISIBLE
        binding.rcvProduct.visibility = View.GONE
    }

    private fun displayErrorSnackbar(errorMessage: String) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
    }
}