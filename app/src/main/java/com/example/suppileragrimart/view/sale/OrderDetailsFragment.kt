package com.example.suppileragrimart.view.sale

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.suppileragrimart.R
import com.example.suppileragrimart.adapter.OrderDetailAdapter
import com.example.suppileragrimart.databinding.FragmentOrderDetailsBinding
import com.example.suppileragrimart.model.OrderBasicInfo
import com.example.suppileragrimart.model.OrderInfo
import com.example.suppileragrimart.model.OrderProductDto
import com.example.suppileragrimart.model.Supplier
import com.example.suppileragrimart.model.UserAddress
import com.example.suppileragrimart.network.Api
import com.example.suppileragrimart.network.RetrofitClient
import com.example.suppileragrimart.utils.Constants
import com.example.suppileragrimart.utils.LoginUtils
import com.example.suppileragrimart.utils.OrderStatus
import com.example.suppileragrimart.utils.ProgressDialog
import com.example.suppileragrimart.utils.ScreenState
import com.example.suppileragrimart.utils.Utils.Companion.formatPrice
import com.example.suppileragrimart.viewmodel.OrderViewModel
import com.example.suppileragrimart.viewmodel.SupplierViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class OrderDetailsFragment : Fragment() {
    private lateinit var binding: FragmentOrderDetailsBinding
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
    private val orderViewModel: OrderViewModel by lazy {
        ViewModelProvider(requireActivity()).get(OrderViewModel::class.java)
    }
    private val apiSerivce: Api by lazy {
        RetrofitClient.getInstance().getApi()
    }

    private var supplier: Supplier? = null
    private lateinit var alertDialog: AlertDialog
    private var orderInfo: OrderInfo? = null
    private val args: OrderDetailsFragmentArgs by navArgs()
    private val orderProductList: ArrayList<OrderProductDto> = arrayListOf()
    private lateinit var orderDetailAdapter: OrderDetailAdapter
    private lateinit var orderBasicInfo: OrderBasicInfo
    private lateinit var userAddress: UserAddress
    private lateinit var currentState: OrderStatus
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderDetailsBinding.inflate(inflater)
        binding.toolbarLayout.titleToolbar.text = getString(R.string.order_detail)

        supplier = supplierViewModel.supplier
        orderInfo = args.orderInfo
        if (supplier != null) {
            showSupplierInfo()
            setupRecyclerView()
            lifecycleScope.launch {
                withContext(Dispatchers.Main) {
                    alertDialog = progressDialog.createAlertDialog(requireActivity())
                }
                getOrderById()
                getAddressById()
                withContext(Dispatchers.Main) {
                    alertDialog.dismiss()
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        binding.toolbarLayout.imgBack.setOnClickListener {
            navController.navigateUp()
        }

        binding.btnSave.setOnClickListener {
            when (currentState) {
                OrderStatus.PROCESSING -> {
                    currentState = OrderStatus.CONFIRMED
                }

                OrderStatus.CONFIRMED -> {
                    currentState = OrderStatus.DELIVERING
                }

                else -> {}
            }
            updateOrderStatus()
        }
    }

    suspend fun getAddressById() {
        withContext(Dispatchers.IO) {
            val response = apiSerivce.getAddressById(orderInfo!!.addressId)
            if (response.isSuccessful) {
                if (response.body() != null) {
                    userAddress = response.body()!!
                }
                withContext(Dispatchers.Main) {
                    showCustomerInfo(userAddress)
                }
            }
        }
    }

    suspend fun getOrderById() {
        withContext(Dispatchers.IO) {
            val response = apiSerivce.getOrderById(orderInfo!!.id)
            if (response.isSuccessful) {
                if (response.body() != null) {
                    orderBasicInfo = response.body()!!
                }
                withContext(Dispatchers.Main) {
                    showOrderState(orderBasicInfo)
                }
            }
        }
    }

    private fun setupRecyclerView() {
        orderProductList.addAll(orderInfo!!.productList)
        binding.rcvOrderProducts.apply {
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            orderDetailAdapter = OrderDetailAdapter(orderProductList)
            adapter = orderDetailAdapter
        }
    }

    private fun showOrderState(orderBasicInfo: OrderBasicInfo) {
        binding.tvOrderCreatedDate.text = orderBasicInfo.dateCreated
        binding.tvPaymentState.text = orderBasicInfo.paymentStatus

        var totalValue = 0L
        for (o in orderProductList){
            if (o.discountPrice > 0){
                totalValue += o.discountPrice * o.quantity
            } else {
                totalValue += o.standardPrice * o.quantity
            }
        }
        val total = totalValue.formatPrice() + " đ"
        binding.tvTotal.text = total

        if (orderBasicInfo.paymentMethod.equals(Constants.PAYMENT_COD)) {
            binding.tvPaymentMethod.text = getString(R.string.cod)
        } else if (orderBasicInfo.paymentMethod.equals(Constants.PAYMENT_PAYPAL)) {
            binding.tvPaymentMethod.text = getString(R.string.paypal)
        }

        currentState = orderBasicInfo.orderStatus
        when (currentState) {
            OrderStatus.PROCESSING -> {
                binding.btnSave.text = getString(R.string.accept_order)
                binding.tvOrderState.text = getString(R.string.PROCESSING)
            }

            OrderStatus.CONFIRMED -> {
                binding.btnSave.text = getString(R.string.delivery_order)
                binding.tvOrderState.text = getString(R.string.CONFIRMED)
            }

            OrderStatus.DELIVERING -> {
                binding.btnSave.text = getString(R.string.DELIVERING)
                binding.btnSave.isEnabled = false
                binding.btnSave.setBackgroundColor(resources.getColor(R.color.grayAgri))
                binding.tvOrderState.text = getString(R.string.DELIVERING)
            }

            OrderStatus.COMPLETED -> {
                binding.btnSave.text = getString(R.string.received)
                binding.btnSave.isEnabled = false
                binding.btnSave.setBackgroundColor(resources.getColor(R.color.grayAgri))
                binding.tvOrderState.text = getString(R.string.COMPLETED)
            }

            OrderStatus.CANCELLED -> {
                binding.btnSave.text = getString(R.string.terminate_order)
                binding.btnSave.isEnabled = false
                binding.btnSave.setBackgroundColor(resources.getColor(R.color.grayAgri))
                binding.tvOrderState.text = getString(R.string.CANCELLED)
            }
        }
    }

    private fun showCustomerInfo(userAddress: UserAddress) {
        binding.tvReceiverFullName.text = userAddress.contactName
        binding.tvReceiverPhoneNumber.text = userAddress.phone
        val address =
            "${userAddress.details} - ${userAddress.commune} - ${userAddress.district} - ${userAddress.province}"
        binding.tvReceiverAddress.text = address
    }

    private fun showSupplierInfo() {
        binding.tvOrderId.text = orderInfo!!.id.toString()
        binding.tvShipmentId.text = orderInfo!!.orderNumber
        binding.tvShopEmail.text = supplier!!.email
        binding.tvShopFullName.text = supplier!!.contactName
        binding.tvShopPhoneNumber.text = supplier!!.phone
    }

    private fun updateOrderStatus() {
        val token = loginUtils.getSupplierToken()
        orderViewModel.updateOrderStatus(token, orderInfo!!.id, currentState.name).observe(
            requireActivity(), { state -> processOrderStatus(state) }
        )
    }

    private fun processOrderStatus(state: ScreenState<OrderBasicInfo?>) {
        when (state) {
            is ScreenState.Loading -> {
                alertDialog = progressDialog.createAlertDialog(requireActivity())
            }

            is ScreenState.Success -> {
                if (state.data != null) {
                    alertDialog.dismiss()
                    showSnackbar(getString(R.string.updated_status))
                    navController.navigate(R.id.action_orderDetailsFragment_to_saleFragment)
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