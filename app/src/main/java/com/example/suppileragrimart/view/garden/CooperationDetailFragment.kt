package com.example.suppileragrimart.view.garden

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
import com.example.suppileragrimart.databinding.FragmentCooperationDetailBinding
import com.example.suppileragrimart.model.Cooperation
import com.example.suppileragrimart.model.FieldApiResponse
import com.example.suppileragrimart.model.UserFirebase
import com.example.suppileragrimart.network.Api
import com.example.suppileragrimart.network.RetrofitClient
import com.example.suppileragrimart.utils.Constants
import com.example.suppileragrimart.utils.LoginUtils
import com.example.suppileragrimart.utils.OrderStatus
import com.example.suppileragrimart.utils.ProgressDialog
import com.example.suppileragrimart.utils.ScreenState
import com.example.suppileragrimart.utils.Utils.Companion.formatPrice
import com.example.suppileragrimart.utils.Utils.Companion.formatYield
import com.example.suppileragrimart.viewmodel.CooperationViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CooperationDetailFragment : Fragment() {
    private lateinit var binding: FragmentCooperationDetailBinding
    private lateinit var navController: NavController

    private val cooperationViewModel: CooperationViewModel by lazy {
        ViewModelProvider(requireActivity()).get(CooperationViewModel::class.java)
    }
    private val progressDialog: ProgressDialog by lazy {
        ProgressDialog()
    }
    private val loginUtils: LoginUtils by lazy {
        LoginUtils(requireContext())
    }
    private val apiService: Api by lazy {
        RetrofitClient.getInstance().getApi()
    }

    val args: CooperationDetailFragmentArgs by navArgs()
    private lateinit var alertDialog: AlertDialog
    private lateinit var cooperationResponse: Cooperation
    private lateinit var currentField: FieldApiResponse
    private var status: OrderStatus = OrderStatus.PROCESSING
    private lateinit var currentAddress: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCooperationDetailBinding.inflate(inflater)
        binding.toolbarLayout.titleToolbar.text = getString(R.string.request)

        cooperationResponse = args.cooperation

        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                alertDialog = progressDialog.createAlertDialog(requireActivity())
            }
            getCurrentField()
            withContext(Dispatchers.Main) {
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

        binding.btnCancel.setOnClickListener {
            if (status == OrderStatus.CANCELLED) {
                findUserUid(cooperationResponse.userId) { userSnapshot ->
                    if (userSnapshot != null) {
                        val userFirebase = userSnapshot.getValue(UserFirebase::class.java)
                        if (userFirebase != null) {
                            val b = Bundle().apply {
                                putString(Constants.USER_UID_KEY, userFirebase.uid)
                            }
                            navController.navigate(R.id.action_cooperationDetailFragment_to_chatDetailFragment, b)
                        } else {
                            Log.d("TEST", "cooperation - uid not fount")
                        }
                    }
                }
            } else {
                updateCooperationStatus(OrderStatus.CANCELLED)
            }
        }

        binding.btnAgree.setOnClickListener {
            if (status == OrderStatus.PROCESSING) {
                updateCooperationStatus(OrderStatus.CONFIRMED)
            } else if (status == OrderStatus.CONFIRMED) {
                updateCooperationStatus(OrderStatus.DELIVERING)
            }
        }
    }

    suspend fun getCurrentField() {
        withContext(Dispatchers.IO) {
            val response = apiService.getFieldById(cooperationResponse.fieldId)
            if (response.isSuccessful) {
                if (response.body() != null) {
                    currentField = response.body()!!
                }
            }
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
            tvYield.text = formatYield(cooperationResponse.requireYield)
            tvDescription.text = cooperationResponse.description
        }
        status = cooperationResponse.cooperationStatus

        if (cooperationResponse.addressId != null) {
            lifecycleScope.launch {
                getAddressById(cooperationResponse.addressId)
                withContext(Dispatchers.Main) {
                    binding.tvAddress.text = currentAddress
                }
            }
        } else {
            binding.tvAddress.text = getString(R.string.no_address)
        }

        if (cooperationResponse.paymentStatus != null) {
            binding.tvPayment.text = cooperationResponse.paymentStatus
        }
    }

    suspend fun getAddressById(addressId: Long) {
        withContext(Dispatchers.IO) {
            val response = apiService.getAddressByIdV2(addressId)
            if (response.isSuccessful) {
                val userAddress = response.body()
                if (userAddress != null) {
                    currentAddress =
                        "${userAddress.details} - ${userAddress.commune} - ${userAddress.district} - ${userAddress.province}"
                }
            }
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

        when(status){
            OrderStatus.PROCESSING -> {
                binding.btnCancel.visibility = View.VISIBLE
            }
            OrderStatus.CONFIRMED -> {
                binding.btnCancel.visibility = View.VISIBLE
                binding.btnAgree.text = getString(R.string.delivery_order)
            }
            OrderStatus.DELIVERING -> {
                binding.btnCancel.visibility = View.INVISIBLE
                binding.btnCancel.isEnabled = false

                binding.btnAgree.isEnabled = false
                binding.btnAgree.setBackgroundColor(Color.parseColor("#E9EAEC"))
            }
            OrderStatus.COMPLETED -> {
                binding.btnCancel.visibility = View.INVISIBLE
                binding.btnCancel.isEnabled = false

                binding.stepView.done(true)

                binding.btnAgree.text = getString(R.string.received)
                binding.btnAgree.isEnabled = false
                binding.btnAgree.setBackgroundColor(Color.parseColor("#E9EAEC"))
            }
            OrderStatus.CANCELLED -> {
                binding.btnAgree.text = getString(R.string.terminated)
                binding.btnAgree.isEnabled = false

                binding.btnCancel.visibility = View.VISIBLE
                binding.btnCancel.text = getString(R.string.contact)
            }
        }

    }

    private fun updateCooperationStatus(status: OrderStatus) {
        val token = loginUtils.getSupplierToken()
        cooperationResponse.cooperationStatus = status
        cooperationViewModel.updateCooperationStatus(
            token,
            cooperationResponse.id,
            cooperationResponse
        )
            .observe(requireActivity(), { state -> processCooperationResponse(state) })
    }

    private fun processCooperationResponse(state: ScreenState<Cooperation?>) {
        when (state) {
            is ScreenState.Loading -> {
                alertDialog = progressDialog.createAlertDialog(requireActivity())
            }

            is ScreenState.Success -> {
                if (state.data != null) {
                    alertDialog.dismiss()
                    showSnackbar(getString(R.string.updated_status))
                    navController.navigate(R.id.action_cooperationDetailFragment_to_gardenInfoFragment2)
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

    private fun showSnackbar(text: String) {
        Snackbar.make(requireView(), text, Snackbar.LENGTH_SHORT).show()
    }

    private fun findUserUid(userId: Long, onResult: (DataSnapshot?) -> Unit) {
        val ref = FirebaseDatabase.getInstance().reference.child(Constants.USER)
        val query = ref.orderByChild("idInServer").equalTo(userId.toDouble())

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        onResult(userSnapshot)
                        return
                    }
                } else {
                    onResult(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onResult(null)
            }
        })
    }
}