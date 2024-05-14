package com.example.suppileragrimart.view.garden

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.filter
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.suppileragrimart.adapter.ItemLoadingAdapter
import com.example.suppileragrimart.adapter.YieldAdapter
import com.example.suppileragrimart.databinding.FragmentGardenYieldBinding
import com.example.suppileragrimart.model.Cooperation
import com.example.suppileragrimart.model.CooperationApiRequest
import com.example.suppileragrimart.model.FieldApiResponse
import com.example.suppileragrimart.model.Supplier
import com.example.suppileragrimart.network.Api
import com.example.suppileragrimart.network.RetrofitClient
import com.example.suppileragrimart.utils.LoginUtils
import com.example.suppileragrimart.utils.ProgressDialog
import com.example.suppileragrimart.utils.Utils
import com.example.suppileragrimart.utils.Utils.Companion.formatPrice
import com.example.suppileragrimart.viewmodel.CooperationViewModel
import com.example.suppileragrimart.viewmodel.FieldViewModel
import com.example.suppileragrimart.viewmodel.SupplierViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class GardenYieldFragment : Fragment() {
    private lateinit var binding: FragmentGardenYieldBinding

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
    private val apiService: Api by lazy {
        RetrofitClient.getInstance().getApi()
    }
    private val cooperationViewModel: CooperationViewModel by lazy {
        ViewModelProvider(requireActivity()).get(CooperationViewModel::class.java)
    }

    private var supplier: Supplier? = null
    private var currentField: FieldApiResponse? = null
    private lateinit var alertDialog: AlertDialog
    private lateinit var yieldAdapter: YieldAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGardenYieldBinding.inflate(inflater)

        supplier = supplierViewModel.supplier
        currentField = fieldViewModel.fieldData
        if (currentField != null && supplier != null){
            showBasicInfo()
            setupRecyclerView()
            lifecycleScope.launch {
                withContext(Dispatchers.Main){
                    alertDialog = progressDialog.createAlertDialog(requireActivity())
                }
                getCropsReceived()
                getCooperations()
                withContext(Dispatchers.Main){
                    alertDialog.dismiss()
                }
            }
        }

        return binding.root
    }

    suspend fun getCooperations(){
        val cooperationApiRequest = CooperationApiRequest()
        cooperationApiRequest.id = currentField!!.id
        lifecycleScope.launch {
            cooperationViewModel.getCooperation(supplier!!.id, cooperationApiRequest)
                .collectLatest { pagingData ->
                    yieldAdapter.addLoadStateListener { loadState ->
                        if (loadState.source.refresh is LoadState.NotLoading &&
                            loadState.append.endOfPaginationReached &&
                            yieldAdapter.itemCount < 1
                        ) {
                            showEmptyLayout()
                        } else {
                            hideEmptyLayout()
                        }
                    }
                    val newPagingData: PagingData<Cooperation> = pagingData.filter { cooperation -> cooperation.requireYield > 0.0 }
                        .map { cooperation -> Cooperation().apply {
                            fullName = cooperation.fullName
                            requireYield = cooperation.requireYield
                            investment = currentField!!.estimateYield.toString()
                        } }
                    yieldAdapter.submitData(newPagingData)
                }
        }
    }

    private fun setupRecyclerView() {
        binding.rcvRequiredYield.apply {
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            yieldAdapter = YieldAdapter()
            adapter = yieldAdapter.withLoadStateHeaderAndFooter(
                header = ItemLoadingAdapter { yieldAdapter.retry() },
                footer = ItemLoadingAdapter { yieldAdapter.retry() }
            )
        }
    }
    suspend fun getCropsReceived(){
        withContext(Dispatchers.IO){
            val response = apiService.calculateCurrentTotal(currentField!!.id, supplier!!.id)
            if (response.isSuccessful){
                if (response.body() != null){
                    withContext(Dispatchers.Main){
                        val currentProgress = response.body()!!.message.toDouble()
                        binding.tvCurrent.text = Utils.formatYield(currentProgress)

                        if (currentProgress != 0.0 && currentField!!.estimateYield != 0.0){
                            val progressPercent: Int = (currentProgress * 100 / currentField!!.estimateYield).toInt()
                            binding.progressBar.progress = progressPercent
                            binding.progressBar.max = 100
                        }
                    }
                }
            }
        }
    }

    private fun showBasicInfo() {
        binding.tvTotal.text = Utils.formatYield(currentField!!.estimateYield)
        val acceptPrice = "${currentField!!.estimatePrice.formatPrice()} đ"
        binding.tvAcceptPrice.text = acceptPrice
    }

    private fun showEmptyLayout(){
        binding.emptyBackground.visibility = View.VISIBLE
        binding.rcvRequiredYield.visibility = View.GONE
    }

    private fun hideEmptyLayout(){
        binding.emptyBackground.visibility = View.GONE
        binding.rcvRequiredYield.visibility = View.VISIBLE
    }

}