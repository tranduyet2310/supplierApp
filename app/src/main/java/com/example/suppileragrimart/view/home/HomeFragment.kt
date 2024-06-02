package com.example.suppileragrimart.view.home

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.request.RequestOptions
import com.example.suppileragrimart.R
import com.example.suppileragrimart.adapter.OrderTodayAdapter
import com.example.suppileragrimart.adapter.RecentOrderAdapter
import com.example.suppileragrimart.adapter.ReviewAdapter
import com.example.suppileragrimart.databinding.FragmentHomeBinding
import com.example.suppileragrimart.model.OrderStatistic
import com.example.suppileragrimart.model.ReviewInfo
import com.example.suppileragrimart.model.Supplier
import com.example.suppileragrimart.network.Api
import com.example.suppileragrimart.network.RetrofitClient
import com.example.suppileragrimart.utils.Constants
import com.example.suppileragrimart.utils.GlideApp
import com.example.suppileragrimart.utils.LoginUtils
import com.example.suppileragrimart.utils.ProgressDialog
import com.example.suppileragrimart.utils.Utils.Companion.formatCurrentMonth
import com.example.suppileragrimart.utils.Utils.Companion.formatPrice
import com.example.suppileragrimart.utils.Utils.Companion.getCurrentDate
import com.example.suppileragrimart.utils.Utils.Companion.getCurrentMonth
import com.example.suppileragrimart.viewmodel.SupplierViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
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
    private val apiService: Api by lazy {
        RetrofitClient.getInstance().getApi()
    }

    private lateinit var supplier: Supplier
    private lateinit var alertDialog: AlertDialog
    private lateinit var orderTodayAdapter: OrderTodayAdapter
    private lateinit var recentOrderAdapter: RecentOrderAdapter
    private lateinit var reviewAdapter: ReviewAdapter
    private val orderTodayList: ArrayList<OrderStatistic> = arrayListOf()
    private val recentOrderList: ArrayList<OrderStatistic> = arrayListOf()
    private val reviewList: ArrayList<ReviewInfo> = arrayListOf()
    private lateinit var secretKey: String
    private lateinit var iv: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        supplier = loginUtils.getSupplierInfo()
        if (supplier.avatar != null){
            showUserAvatar(supplier.avatar)
        }
        if (supplier.contactName != null){
            binding.tvFullNameUser.text =  supplier.contactName
        }
        setupRecyclerView()
        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main){
                alertDialog = progressDialog.createAlertDialog(requireActivity())
            }
            getMonthStatistic()
            getOrderToday()
            getRecentOrder()
            getReviewData()

            iv = loginUtils.getIv()
            secretKey = loginUtils.getAESKey()

            if (supplier.contactName == null){
                getSupplierById()
            }
            if (supplier.avatar == null){
                getSupplierAvatar()
            }

            withContext(Dispatchers.Main){
                alertDialog.dismiss()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        reviewAdapter.onClick = {
            val b = Bundle().apply {
                putParcelable(Constants.REVIEW_KEY, it)
            }
            navController.navigate(R.id.action_homeFragment_to_reviewDetailFragment, b)
        }
    }
    private fun setupRecyclerView() {
        binding.homeContent.rcvOrderToday.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            orderTodayAdapter = OrderTodayAdapter(orderTodayList)
            adapter = orderTodayAdapter
        }

        binding.homeContent.rcvRecentOrder.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            recentOrderAdapter = RecentOrderAdapter(recentOrderList)
            adapter = recentOrderAdapter
        }

        binding.homeContent.rcvRecentReview.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            reviewAdapter = ReviewAdapter(reviewList)
            adapter = reviewAdapter
        }
    }

    suspend fun getReviewData(){
        withContext(Dispatchers.IO){
            val response = apiService.getReviewInfo(supplier.id)
            if (response.isSuccessful){
                if (response.body() != null){
                    val dataList = response.body()
                    if (dataList!!.isEmpty()){
                        withContext(Dispatchers.Main){
                            hideReviewRcv()
                        }
                    } else {
                        reviewList.clear()
                        reviewList.addAll(dataList)
                        withContext(Dispatchers.Main){
                            showReviewRcv()
                            binding.homeContent.rcvRecentReview.adapter!!.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }
    suspend fun getMonthStatistic(){
        withContext(Dispatchers.IO){
            val response = apiService.getStatistic(supplier.id, getCurrentMonth())
            if (response.isSuccessful){
                if (response.body() != null){
                    val saleValue = "${response.body()!!.total.formatPrice()} đ"
                    withContext(Dispatchers.Main){
                        binding.homeContent.tvMonth.text = formatCurrentMonth()
                        binding.homeContent.tvSale.text = saleValue
                        binding.homeContent.tvTotalProduct.text= response.body()!!.quantity.toString()
                    }
                }
            }
        }
    }
    suspend fun getOrderToday(){
        withContext(Dispatchers.IO){
            val response = apiService.getOrderStatistic(supplier.id, getCurrentDate())
            if (response.isSuccessful){
                if (response.body() != null){
                    val dataList = response.body()
                    if (dataList!!.isEmpty()){
                        withContext(Dispatchers.Main){
                            hideOrderTodayRcv()
                        }
                    } else {
                        orderTodayList.clear()
                        orderTodayList.addAll(dataList)
                        withContext(Dispatchers.Main){
                            showOrderTodayRcv()
                            binding.homeContent.rcvOrderToday.adapter!!.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }

    suspend fun getRecentOrder(){
        withContext(Dispatchers.IO){
            val response = apiService.getRecentOrderStatistic(supplier.id, getCurrentMonth())
            if (response.isSuccessful){
                if (response.body() != null){
                    val dataList = response.body()
                    if (dataList!!.isEmpty()){
                        withContext(Dispatchers.Main){
                            hideRecentOrderRcv()
                        }
                    } else {
                        recentOrderList.clear()
                        recentOrderList.addAll(dataList)
                        withContext(Dispatchers.Main){
                            showRecentOrderRcv()
                            binding.homeContent.rcvRecentOrder.adapter!!.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }
    suspend fun getSupplierById(){
        val token = loginUtils.getSupplierToken()
        withContext(Dispatchers.IO) {
            val response = apiService.getSupplierInfoById(token, supplier.id)
            if (response.isSuccessful) {
                if (response.body() != null) {
                    supplier.contactName = response.body()!!.contactName
                    supplier.phone = response.body()!!.phone
                    supplierViewModel.supplier = supplier
                    loginUtils.saveSupplierInfo(supplier)
                    withContext(Dispatchers.Main){
                        binding.tvFullNameUser.text = supplier.contactName
                    }
                }
            }
        }
    }
    suspend fun getSupplierAvatar(){
        withContext(Dispatchers.IO){
            val response = apiService.getSupplierAvatarV2(supplier.id)
            if (response.isSuccessful){
                if (response.body() != null){
                    supplier.avatar = response.body()!!.imageUrl
                    loginUtils.saveSupplierInfo(supplier)
                    supplierViewModel.supplier = supplier
                    if (supplier.avatar != null){
                        withContext(Dispatchers.Main){
                            showUserAvatar(supplier.avatar)
                        }
                    }
                }
            }
        }
    }

    private fun showUserAvatar(imageUrl: String) {
        val modifiedUrl = imageUrl.replace("http://", "https://")
        val requestOptions = RequestOptions().placeholder(R.drawable.user).error(R.drawable.user)
        GlideApp.with(requireContext())
            .load(modifiedUrl)
            .apply(requestOptions)
            .into(binding.imgUserAccount)
    }

    private fun showSnackbar(message: String){
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showOrderTodayRcv(){
        binding.homeContent.tvNoOrder.visibility = View.GONE
        binding.homeContent.imgCartEmpty.visibility = View.GONE
        binding.homeContent.rcvOrderToday.visibility = View.VISIBLE
    }

    private fun hideOrderTodayRcv(){
        binding.homeContent.tvNoOrder.visibility = View.VISIBLE
        binding.homeContent.imgCartEmpty.visibility = View.VISIBLE
        binding.homeContent.rcvOrderToday.visibility = View.GONE
    }

    private fun showRecentOrderRcv(){
        binding.homeContent.tvNoOrderV2.visibility = View.GONE
        binding.homeContent.imgCartEmptyV2.visibility = View.GONE
        binding.homeContent.rcvRecentOrder.visibility = View.VISIBLE
    }

    private fun hideRecentOrderRcv(){
        binding.homeContent.tvNoOrderV2.visibility = View.VISIBLE
        binding.homeContent.imgCartEmptyV2.visibility = View.VISIBLE
        binding.homeContent.rcvRecentOrder.visibility = View.GONE
    }

    private fun showReviewRcv(){
        binding.homeContent.tvNoOrderV3.visibility = View.GONE
        binding.homeContent.imgCartEmptyV3.visibility = View.GONE
        binding.homeContent.rcvRecentReview.visibility = View.VISIBLE
    }

    private fun hideReviewRcv(){
        binding.homeContent.tvNoOrderV3.visibility = View.VISIBLE
        binding.homeContent.imgCartEmptyV3.visibility = View.VISIBLE
        binding.homeContent.rcvRecentReview.visibility = View.GONE
    }
}