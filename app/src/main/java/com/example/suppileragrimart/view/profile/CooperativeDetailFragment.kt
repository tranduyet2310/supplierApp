package com.example.suppileragrimart.view.profile

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
import com.example.suppileragrimart.R
import com.example.suppileragrimart.databinding.FragmentCooperativeDetailBinding
import com.example.suppileragrimart.model.Cooperation
import com.example.suppileragrimart.model.CooperativePayment
import com.example.suppileragrimart.model.FieldApiResponse
import com.example.suppileragrimart.model.Supplier
import com.example.suppileragrimart.network.Api
import com.example.suppileragrimart.network.RetrofitClient
import com.example.suppileragrimart.utils.OrderStatus
import com.example.suppileragrimart.utils.ProgressDialog
import com.example.suppileragrimart.utils.Utils
import com.example.suppileragrimart.utils.Utils.Companion.formatPrice
import com.example.suppileragrimart.viewmodel.SupplierViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CooperativeDetailFragment : Fragment() {
    private lateinit var binding: FragmentCooperativeDetailBinding
    private lateinit var navController: NavController

    private val supplierViewModel: SupplierViewModel by lazy {
        ViewModelProvider(requireActivity()).get(SupplierViewModel::class.java)
    }
    private val progressDialog: ProgressDialog by lazy {
        ProgressDialog()
    }
    private val apiService: Api by lazy {
        RetrofitClient.getInstance().getApi()
    }

    private var supplier: Supplier? = null
    private lateinit var alertDialog: AlertDialog
    val args: CooperativeDetailFragmentArgs by navArgs()
    private lateinit var cooperationResponse: Cooperation
    private lateinit var currentField: FieldApiResponse
    private lateinit var currenCooperativeOrder: CooperativePayment
    private var status: OrderStatus = OrderStatus.PROCESSING
    private var cooperativePaymentId: Long = 0L
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCooperativeDetailBinding.inflate(inflater)
        binding.toolbarLayout.titleToolbar.text = getString(R.string.order_detail)

        supplier = supplierViewModel.supplier
        cooperativePaymentId = args.cooperative

        lifecycleScope.launch {
            withContext(Dispatchers.Main){
                alertDialog = progressDialog.createAlertDialog(requireActivity())
            }

            getCooperativeOrder()
            getCooperationInfo()
            getFieldInfo()

            withContext(Dispatchers.Main){
                alertDialog.dismiss()
            }
            showData()
            setupStepView()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        binding.toolbarLayout.imgBack.setOnClickListener {
            navController.navigateUp()
        }
    }

    private fun setupStepView() {
        val statusList: ArrayList<String> = arrayListOf()
        statusList.add(getString(R.string.PROCESSING))
        statusList.add(getString(R.string.CONFIRMED))
        statusList.add(getString(R.string.DELIVERING))
        statusList.add(getString(R.string.COMPLETED))

        binding.stepView.setSteps(statusList)
        binding.stepView.go(status.value, false)
        if (status == OrderStatus.COMPLETED) {
            binding.stepView.done(true)
        }
    }
    private fun showData() {
        val totalPrice = "${cooperationResponse.investment.toLong().formatPrice()} đ"
        val pricePerKg = "${currentField.estimatePrice.formatPrice()} đ"
        binding.apply {
            tvShopName.text = cooperationResponse.shopName
            tvSupplierName.text = cooperationResponse.supplierName
            tvSupplierContact.text = cooperationResponse.supplierPhone
            tvCropsName.text = cooperationResponse.cropsName
            tvCropsType.text = currentField.cropsType
            tvSeason.text = currentField.season

            tvFullName.text = cooperationResponse.fullName
            tvUserContact.text = cooperationResponse.contact
            tvInvestment.text = totalPrice
            tvPrice.text = pricePerKg
            tvYield.text = Utils.formatYield(cooperationResponse.requireYield)

            tvPayment.text = currenCooperativeOrder.paymentStatus
            if (currenCooperativeOrder.paymentStatus.equals(getString(R.string.payment_100))){
                tvRemainAmount.text = getString(R.string.zero_dong)
            } else {
                val remainingAmount = cooperationResponse.investment.toLong() - currenCooperativeOrder.total
                val remainingAmountShow = "${remainingAmount.formatPrice()} đ"
                tvRemainAmount.text = remainingAmountShow
            }
            tvPaymentMethod.text = currenCooperativeOrder.paymentMethod
            val paymentAmount = "${currenCooperativeOrder.total.formatPrice()} đ"
            tvPaymentAmount.text = paymentAmount
            tvDateCreated.text = currenCooperativeOrder.dateCreated
        }
        status = currenCooperativeOrder.orderStatus
    }
    suspend fun getCooperativeOrder() {
        withContext(Dispatchers.IO){
            val response = apiService.getCooperativeOrderById(cooperativePaymentId)
            if (response.isSuccessful){
                if (response.body() != null){
                    currenCooperativeOrder = response.body()!!
                }
            }
        }
    }

    suspend fun getCooperationInfo() {
        withContext(Dispatchers.IO){
            val response = apiService.getCooperationByIdV2(currenCooperativeOrder.cooperationId)
            if (response.isSuccessful){
                if (response.body() != null){
                    cooperationResponse = response.body()!!
                }
            }
        }
    }

    suspend fun getFieldInfo(){
        withContext(Dispatchers.IO){
            val response = apiService.getFieldById(cooperationResponse.fieldId)
            if (response.isSuccessful){
                if (response.body() != null){
                    currentField = response.body()!!
                }
            }
        }
    }
}