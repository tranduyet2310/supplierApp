package com.example.suppileragrimart.view.garden

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.util.Log
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
import androidx.navigation.fragment.navArgs
import com.example.suppileragrimart.R
import com.example.suppileragrimart.databinding.FragmentUpdateGardenInfoBinding
import com.example.suppileragrimart.model.FieldApiResponse
import com.example.suppileragrimart.model.FieldDetail
import com.example.suppileragrimart.model.MessageResponse
import com.example.suppileragrimart.model.Supplier
import com.example.suppileragrimart.network.Api
import com.example.suppileragrimart.network.RetrofitClient
import com.example.suppileragrimart.utils.Constants
import com.example.suppileragrimart.utils.Constants.KG_UNIT
import com.example.suppileragrimart.utils.Constants.LONG_TERM_PLANT
import com.example.suppileragrimart.utils.Constants.TAN_UNIT
import com.example.suppileragrimart.utils.Constants.TA_UNIT
import com.example.suppileragrimart.utils.Constants.YEN_UNIT
import com.example.suppileragrimart.utils.CropsStatus
import com.example.suppileragrimart.utils.LoginUtils
import com.example.suppileragrimart.utils.ProgressDialog
import com.example.suppileragrimart.utils.ScreenState
import com.example.suppileragrimart.viewmodel.FieldViewModel
import com.example.suppileragrimart.viewmodel.SupplierViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext


class UpdateGardenInfoFragment : Fragment() {
    private lateinit var binding: FragmentUpdateGardenInfoBinding
    private lateinit var navController: NavController

    private val supplierViewModel: SupplierViewModel by lazy {
        ViewModelProvider(requireActivity()).get(SupplierViewModel::class.java)
    }
    private val fieldViewModel: FieldViewModel by lazy {
        ViewModelProvider(requireActivity()).get(FieldViewModel::class.java)
    }
    private val loginUtils: LoginUtils by lazy {
        LoginUtils(requireContext())
    }
    private val progressDialog: ProgressDialog by lazy {
        ProgressDialog()
    }
    private val apiService: Api by lazy {
        RetrofitClient.getInstance().getApi()
    }

    private var supplier: Supplier? = null
    private var currentField: FieldApiResponse? = null
    private var fieldDetail: FieldDetail? = null
    private lateinit var alertDialog: AlertDialog
    private lateinit var currentState: CropsStatus
    private val massUnit: ArrayList<String> = arrayListOf()
    private var coefficient: Int = 1
    val args: UpdateGardenInfoFragmentArgs by navArgs()
    private lateinit var screen: String
    private var index: Int = 0
    private var totalYieldBefore: Double = 0.0
    private var isShowDialog = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpdateGardenInfoBinding.inflate(inflater)
        binding.toolbarLayout.titleToolbar.text = getString(R.string.update_info)

        supplier = supplierViewModel.supplier
        currentField = fieldViewModel.fieldData
        screen = args.screen

        setupRadioGroup()
        setupSpinner()
        if (screen.equals("detail")) {
            fieldDetail = args.field
            fieldDetail?.let { showInfo(it) }
            index = currentField!!.fieldDetails.indexOf(fieldDetail)
            binding.btnBack.text = getString(R.string.delete)
            Log.d("TEST", "select "+coefficient)

            totalYieldBefore = currentField!!.estimateYield
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        binding.toolbarLayout.imgBack.setOnClickListener {
            navController.navigateUp()
        }

        setupRadioListener()
        setupSpinnerListener()

        binding.btnBack.setOnClickListener {
            if (screen.equals("detail")) {
                deleteFieldDetail()
            } else
                navController.navigateUp()
        }

        binding.btnSave.setOnClickListener {
            val detail = binding.edtDetail.text.toString().trim()
            val yield = binding.edtCropYield.text.toString().trim()
            val price = binding.edtPrice.text.toString().trim()
            val fieldDetail = FieldDetail()
            fieldDetail.cropsStatus = currentState
            fieldDetail.details = detail

            if (!screen.equals("detail")) {
                if (!checkValidStatus(currentState)) {
                    showSnackbar("Giai đoạn này đã có trước đó")
                    return@setOnClickListener
                }
            }

            if (yield.isEmpty() && price.isEmpty()) {
                if (screen.equals("detail")) {
                    updateFieldDetail(fieldDetail)
                } else
                    createFieldDetail(fieldDetail)
            } else {
                lifecycleScope.launch {
                    withContext(Dispatchers.Main) {
                        alertDialog = progressDialog.createAlertDialog(requireActivity())
                    }

                    withContext(Dispatchers.IO) {
                        if (yield.toDouble()*coefficient < totalYieldBefore){
                            withContext(Dispatchers.Main){
                                alertDialog.dismiss()
                            }
                            isShowDialog = true

                            val userConfirmed = showConfirmationDialog()
                            if (userConfirmed){
                                updateYieldInfo(yield, price)
                                if (screen.equals("detail")) {
                                    updateFieldDetailV2(fieldDetail)
                                } else
                                    createFieldDetailV2(fieldDetail)
                            }
                        } else {
                            updateYieldInfo(yield, price)
                            if (screen.equals("detail")) {
                                updateFieldDetailV2(fieldDetail)
                            } else
                                createFieldDetailV2(fieldDetail)
                        }

                    }

                    withContext(Dispatchers.Main) {
                        if (!isShowDialog){
                            alertDialog.dismiss()
                            navController.navigate(R.id.action_updateGardenInfoFragment_to_gardenInfoFragment)
                        } else {
                            navController.navigate(R.id.action_updateGardenInfoFragment_to_gardenInfoFragment)
                        }
                    }
                }
            }

        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun showConfirmationDialog(): Boolean {
        return withContext(Dispatchers.Main) {
            suspendCancellableCoroutine { continuation ->
                val dialog = AlertDialog.Builder(requireActivity())
                    .setTitle("Xác nhận")
                    .setMessage("Số lượng nhập vào thấp hơn so với ban đầu? Hệ thống sẽ hủy đơn (nếu có) để đảm bảo khả năng cung cấp")
                    .setPositiveButton("Đồng ý") { _, _ ->
                        continuation.resume(true) {}
                    }
                    .setNegativeButton("Hủy") { _, _ ->
                        continuation.resume(false) {}
                    }
                    .setOnCancelListener {
                        continuation.resume(false) {}
                    }
                    .create()
                dialog.show()
            }
        }
    }

    private fun deleteFieldDetail() {
        val token = loginUtils.getSupplierToken()
        fieldViewModel.deleteFieldDetail(token, fieldDetail!!.id).observe(
            requireActivity(), { state -> processDeleteResponse(state) }
        )
    }

    private fun updateFieldDetail(fieldDetail: FieldDetail) {
        fieldDetail.id = this.fieldDetail!!.id
        val token = loginUtils.getSupplierToken()
        fieldViewModel.updateFieldDetail(token, currentField!!.id, fieldDetail).observe(
            requireActivity(), { state -> processUpdateResponse(state) }
        )
    }

    suspend fun updateFieldDetailV2(fieldDetail: FieldDetail) {
        fieldDetail.id = this.fieldDetail!!.id
        val token = loginUtils.getSupplierToken()
        val response = apiService.updateFieldDetailV2(token, currentField!!.id, fieldDetail)
        if (response.isSuccessful) {
            if (response.body() != null) {
                currentField!!.fieldDetails.removeAt(index)
                currentField!!.fieldDetails.add(response.body())
                fieldViewModel.fieldData = currentField
            }
        } else {
            Log.d("TEST", response.message())
        }
    }

    private fun showInfo(fieldDetail: FieldDetail) {
        if (currentField!!.cropsType.equals(LONG_TERM_PLANT)) {
            binding.rgLong.visibility = View.VISIBLE
            binding.rgShort.visibility = View.GONE
            currentState = fieldDetail.cropsStatus
            when (currentState) {
                CropsStatus.TAKE_CARE -> {
                    binding.rbTakeCareTimeV2.isChecked = true
                    hideInputField()
                }

                CropsStatus.FLOWERING -> {
                    binding.rbFloweringV2.isChecked = true
                    hideInputField()
                }

                CropsStatus.PEST_PREVENTION -> {
                    binding.rbChemicalTimeV2.isChecked = true
                    hideInputField()
                }

                CropsStatus.FRUITING -> {
                    binding.rbFruitingV2.isChecked = true
                    showInputField()
                }

                CropsStatus.HARVEST -> {
                    binding.rbHarvestTimeV2.isChecked = true
                    showInputField()
                }

                else -> {}
            }
            binding.edtDetail.text = Editable.Factory.getInstance().newEditable(fieldDetail.details)
            binding.edtPrice.text =
                Editable.Factory.getInstance().newEditable(currentField!!.estimatePrice.toString())
            convertYield(currentField!!.estimateYield)
        } else {
            binding.rgLong.visibility = View.GONE
            binding.rgShort.visibility = View.VISIBLE
            currentState = fieldDetail.cropsStatus
            when (currentState) {
                CropsStatus.MAKE_LAND -> {
                    binding.rbMakeLandTime.isChecked = true
                    hideInputField()
                }

                CropsStatus.SOW_SEED -> {
                    binding.rbSowSeedTime.isChecked = true
                    hideInputField()
                }

                CropsStatus.GERMINATION -> {
                    binding.rbGerminationTime.isChecked = true
                    hideInputField()
                }

                CropsStatus.TAKE_CARE -> {
                    binding.rbTakeCareTime.isChecked = true
                    hideInputField()
                }

                CropsStatus.PEST_PREVENTION -> {
                    binding.rbChemicalTime.isChecked = true
                    showInputField()
                }

                CropsStatus.HARVEST -> {
                    binding.rbHarvestTime.isChecked = true
                    showInputField()
                }

                else -> {}
            }
            binding.edtDetail.text = Editable.Factory.getInstance().newEditable(fieldDetail.details)
            binding.edtPrice.text =
                Editable.Factory.getInstance().newEditable(currentField!!.estimatePrice.toString())
            convertYield(currentField!!.estimateYield)
        }
    }

    private fun setupSpinner() {
        massUnit.add(KG_UNIT)
        massUnit.add(YEN_UNIT)
        massUnit.add(TA_UNIT)
        massUnit.add(TAN_UNIT)
        val spMassUnitAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            massUnit
        )
        binding.spMassUnit.adapter = spMassUnitAdapter
    }

    private fun setupRadioGroup() {
        if (currentField != null) {
            if (currentField!!.cropsType.equals(Constants.LONG_TERM_PLANT)) {
                binding.rgLong.visibility = View.VISIBLE
                binding.rgShort.visibility = View.GONE
            } else {
                binding.rgLong.visibility = View.GONE
                binding.rgShort.visibility = View.VISIBLE
            }
        }
    }

    private fun setupSpinnerListener() {
        binding.spMassUnit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val value = massUnit[position]
                when (value) {
                    KG_UNIT -> coefficient = 1
                    YEN_UNIT -> coefficient = 10
                    TA_UNIT -> coefficient = 100
                    TAN_UNIT -> coefficient = 1000
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    suspend fun updateYieldInfo(yield: String, price: String) {
        val token = loginUtils.getSupplierToken()
        val fieldApiResponse = FieldApiResponse()
        fieldApiResponse.estimateYield = yield.toDouble() * coefficient
        fieldApiResponse.estimatePrice = price.toLong()

        val response = apiService.updateYield(token, currentField!!.id, fieldApiResponse)
        if (response.isSuccessful) {
            if (response.body() != null) {
                currentField!!.estimateYield = response.body()!!.estimateYield
                currentField!!.estimatePrice = response.body()!!.estimatePrice
                fieldViewModel.fieldData = currentField
            }
        } else {
            Log.d("TEST", response.message())
        }
    }

    suspend fun createFieldDetailV2(fieldDetail: FieldDetail) {
        val token = loginUtils.getSupplierToken()
        val response = apiService.createFieldDetailV2(token, currentField!!.id, fieldDetail)
        if (response.isSuccessful) {
            if (response.body() != null) {
                currentField!!.fieldDetails.add(response.body())
                fieldViewModel.fieldData = currentField
            }
        } else {
            Log.d("TEST", response.message())
        }
    }

    private fun checkValidStatus(currentState: CropsStatus): Boolean {
        val listFieldDetail = currentField!!.fieldDetails
        if (listFieldDetail.isEmpty()) {
            return true
        }

        for (field in listFieldDetail) {
            if (field.cropsStatus == currentState) {
                return false
            }
        }
        return true
    }

    private fun setupRadioListener() {
        if (currentField!!.cropsType.equals(Constants.LONG_TERM_PLANT)) {
            binding.rgLong.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    binding.rbTakeCareTimeV2.id -> {
                        hideInputField()
                        currentState = CropsStatus.TAKE_CARE
                    }

                    binding.rbFloweringV2.id -> {
                        hideInputField()
                        currentState = CropsStatus.FLOWERING
                    }

                    binding.rbChemicalTimeV2.id -> {
                        hideInputField()
                        currentState = CropsStatus.PEST_PREVENTION
                    }

                    binding.rbFruitingV2.id -> {
                        showInputField()
                        currentState = CropsStatus.FRUITING
                    }

                    binding.rbHarvestTimeV2.id -> {
                        showInputField()
                        currentState = CropsStatus.HARVEST
                    }
                }
            }

        } else {
            binding.rgShort.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    binding.rbMakeLandTime.id -> {
                        hideInputField()
                        currentState = CropsStatus.MAKE_LAND
                    }

                    binding.rbSowSeedTime.id -> {
                        hideInputField()
                        currentState = CropsStatus.SOW_SEED
                    }

                    binding.rbGerminationTime.id -> {
                        hideInputField()
                        currentState = CropsStatus.GERMINATION
                    }

                    binding.rbTakeCareTime.id -> {
                        hideInputField()
                        currentState = CropsStatus.TAKE_CARE
                    }

                    binding.rbChemicalTime.id -> {
                        showInputField()
                        currentState = CropsStatus.PEST_PREVENTION
                    }

                    binding.rbHarvestTime.id -> {
                        showInputField()
                        currentState = CropsStatus.HARVEST
                    }

                }
            }
        }
    }

    private fun createFieldDetail(fieldDetail: FieldDetail) {
        val token = loginUtils.getSupplierToken()
        fieldViewModel.createFieldDetail(token, currentField!!.id, fieldDetail).observe(
            requireActivity(), { state -> processFieldResponse(state) }
        )
    }

    private fun processFieldResponse(state: ScreenState<FieldDetail?>) {
        when (state) {
            is ScreenState.Loading -> {
                alertDialog = progressDialog.createAlertDialog(requireActivity())
            }

            is ScreenState.Success -> {
                if (state.data != null) {
                    alertDialog.dismiss()
                    showSnackbar("Thêm mới thành công!")
                    currentField!!.fieldDetails.add(state.data)
                    fieldViewModel.fieldData = currentField
                    navController.navigate(R.id.action_updateGardenInfoFragment_to_gardenInfoFragment)
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

    private fun processUpdateResponse(state: ScreenState<FieldDetail?>) {
        when (state) {
            is ScreenState.Loading -> {
                alertDialog = progressDialog.createAlertDialog(requireActivity())
            }

            is ScreenState.Success -> {
                if (state.data != null) {
                    alertDialog.dismiss()
                    currentField!!.fieldDetails.removeAt(index)
                    currentField!!.fieldDetails.add(state.data)
                    fieldViewModel.fieldData = currentField
                    navController.navigate(R.id.action_updateGardenInfoFragment_to_gardenInfoFragment)
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

    private fun processDeleteResponse(state: ScreenState<MessageResponse?>) {
        when (state) {
            is ScreenState.Loading -> {
                alertDialog = progressDialog.createAlertDialog(requireActivity())
            }

            is ScreenState.Success -> {
                if (state.data != null) {
                    alertDialog.dismiss()
                    currentField!!.fieldDetails.removeAt(index)
                    fieldViewModel.fieldData = currentField
                    navController.navigate(R.id.action_updateGardenInfoFragment_to_gardenInfoFragment)
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

    private fun hideInputField() {
        binding.edtPrice.visibility = View.GONE
        binding.edtCropYield.visibility = View.GONE
        binding.spMassUnit.visibility = View.GONE
    }

    private fun showInputField() {
        binding.edtPrice.visibility = View.VISIBLE
        binding.edtCropYield.visibility = View.VISIBLE
        binding.spMassUnit.visibility = View.VISIBLE
    }

    private fun convertYield(value: Double) {
        val result: Double
        if (value >= 1000) {
            result = value / 1000
            binding.edtCropYield.text = Editable.Factory.getInstance()
                .newEditable(result.toString())
            binding.spMassUnit.setSelection(3)
        } else if (value >= 100) {
            result = value / 100
            binding.spMassUnit.setSelection(2)
            binding.edtCropYield.text = Editable.Factory.getInstance()
                .newEditable(result.toString())
        } else if (value >= 10) {
            result = value / 10
            binding.spMassUnit.setSelection(1)
            binding.edtCropYield.text = Editable.Factory.getInstance()
                .newEditable(result.toString())
        } else {
            binding.spMassUnit.setSelection(0)
            binding.edtCropYield.text = Editable.Factory.getInstance()
                .newEditable(value.toString())
        }
    }
}