package com.example.suppileragrimart.view.product

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.suppileragrimart.R
import com.example.suppileragrimart.databinding.FragmentNewProductBinding
import com.example.suppileragrimart.model.CategoryApiResponse
import com.example.suppileragrimart.model.Product
import com.example.suppileragrimart.model.Supplier
import com.example.suppileragrimart.network.Api
import com.example.suppileragrimart.network.RetrofitClient
import com.example.suppileragrimart.utils.AES
import com.example.suppileragrimart.utils.Constants.FIELD_REQUIRED
import com.example.suppileragrimart.utils.GlideApp
import com.example.suppileragrimart.utils.LoginUtils
import com.example.suppileragrimart.utils.ProgressDialog
import com.example.suppileragrimart.utils.ScreenState
import com.example.suppileragrimart.viewmodel.ProductViewModel
import com.example.suppileragrimart.viewmodel.SupplierViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class NewProductFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentNewProductBinding
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
    private val apiService: Api by lazy {
        RetrofitClient.getInstance().getApi()
    }

    private var supplier: Supplier? = null
    private lateinit var alertDialog: AlertDialog

    private var warehouseValues: ArrayList<String> = arrayListOf()
    private var subCategoryValues: ArrayList<String> = arrayListOf()
    private var categoryValues: ArrayList<String> = arrayListOf()
    private lateinit var categoryList: ArrayList<CategoryApiResponse>

    private var uriList: ArrayList<Uri> = arrayListOf()
    private var imageUri: Uri? = null
    private lateinit var imageActivityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var imagesActivityResultLauncher: ActivityResultLauncher<Intent>
    private var images: ArrayList<MultipartBody.Part> = arrayListOf()

    private lateinit var categorySelected: String
    private lateinit var subcategorySelected: String
    private lateinit var warehouseSelected: String
    private lateinit var secretKey: String
    private lateinit var iv: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewProductBinding.inflate(inflater)
        binding.toolbarLayout.titleToolbar.text = getString(R.string.add_product)

        supplier = supplierViewModel.supplier
        secretKey = loginUtils.getAESKey()
        iv = loginUtils.getIv()

        setupImageListener()
        binding.rbHaveProduct.isChecked = true

        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                alertDialog = progressDialog.createAlertDialog(requireActivity())
            }
            getWarehouseData()
            getCategoryData()
            withContext(Dispatchers.Main) {
                alertDialog.dismiss()
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

        setupRadioGroupListener()
        setupCategoryListener()
        setupSubCategoryListener()
        setupWarehouseListener()

        binding.tvProductImage.setOnClickListener(this)
        binding.tvProductAdditionalImage.setOnClickListener(this)
        binding.btnCancel.setOnClickListener(this)
        binding.btnSave.setOnClickListener(this)
    }

    suspend fun getCategoryData() {
        withContext(Dispatchers.IO) {
            val response = apiService.getAllCategories()
            if (response.isSuccessful) {
                categoryList = response.body()!!
                categoryValues.add(getString(R.string.category_placeholder))
                subCategoryValues.add(getString(R.string.subcategory_placeholder))
                for (category in categoryList) {
                    categoryValues.add(category.categoryName)
                }
            }
        }
        withContext(Dispatchers.Main) {
            val spProductCategoryAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                categoryValues
            )
            val spSubcategoryProductAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                subCategoryValues
            )
            binding.spProductCategory.adapter = spProductCategoryAdapter
            binding.spSubcategoryProduct.adapter = spSubcategoryProductAdapter
        }
    }

    suspend fun getWarehouseData() {
        withContext(Dispatchers.IO) {
            val response = apiService.getAllWarehouseBySupplierId(supplier!!.id)
            if (response.isSuccessful) {
                val warehouseList = response.body()
                warehouseValues.add(getString(R.string.warehouse_placeholder))
                if (warehouseList != null) {
                    for (warehouse in warehouseList) {
                        if (warehouse.isActive) {
                            warehouseValues.add(warehouse.warehouseName)
                        }
                    }
                }
            }
        }
        withContext(Dispatchers.Main) {
            val spWarehouseNameAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                warehouseValues
            )
            binding.spWarehouseName.adapter = spWarehouseNameAdapter
        }
    }

    private fun setupImageListener() {
        imageActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                imageUri = it.data?.data
                GlideApp.with(requireContext()).load(imageUri).into(binding.imgProductAvatar)
            }

        imagesActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.data!!.clipData != null) {
                    val mClipData = it.data!!.clipData
                    for (i in 0 until mClipData!!.itemCount) {
                        val item = mClipData.getItemAt(i)
                        uriList.add(item.uri)
                    }
                } else if (it.data != null) {
                    val imageUri = it.data?.data
                    if (imageUri != null) {
                        uriList.add(imageUri)
                    }
                }

                for (i in 1.. uriList.size){
                    when(i){
                        1 -> GlideApp.with(requireContext()).load(uriList[0]).into(binding.imagProduct1)
                        2 -> GlideApp.with(requireContext()).load(uriList[1]).into(binding.imagProduct2)
                        3 -> GlideApp.with(requireContext()).load(uriList[2]).into(binding.imagProduct3)
                        4 -> GlideApp.with(requireContext()).load(uriList[3]).into(binding.imagProduct4)
                        else -> { }
                    }
                }

            }
    }

    private fun setupRadioGroupListener() {
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                binding.rbHaveProduct.id -> {
                    binding.inputDetail.isClickable = true
                    binding.inputDetail.isEnabled = true
                }

                binding.rbCommingSoon.id -> {
                    binding.inputDetail.isEnabled = false
                    binding.inputDetail.isClickable = false
                }
            }
        }
    }

    private fun setupWarehouseListener() {
        binding.spWarehouseName.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    warehouseSelected = warehouseValues[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
    }

    private fun setupSubCategoryListener() {
        binding.spSubcategoryProduct.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    subcategorySelected = subCategoryValues[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
    }

    private fun setupCategoryListener() {
        binding.spProductCategory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    categorySelected = categoryValues[position]
                    subCategoryValues.clear()
                    subCategoryValues.add(getString(R.string.subcategory_placeholder))
                    for (category in categoryList) {
                        if (category.categoryName.equals(categorySelected)) {
                            val subcategoryList = category.subCategoryList
                            for (sub in subcategoryList) {
                                subCategoryValues.add(sub.subcategoryName)
                            }
                        }
                    }
                    val spSubcategoryProductAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        subCategoryValues
                    )
                    binding.spSubcategoryProduct.adapter = spSubcategoryProductAdapter
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvProductImage -> chooseSingleImage()
            R.id.tvProductAdditionalImage -> chooseMultipleImage()
            R.id.btnSave -> createProduct()
            R.id.btnCancel -> goToProductFragment()
        }
    }

    private fun createProduct() {
        val productName = binding.inputProductName.text.toString().trim()
        val description = binding.inputProductDescription.text.toString().trim()
        val isActive = binding.checkboxActiveProduct.isChecked
        val standardPrice = binding.inputProductPrice.text.toString().trim()
        var discountPrice = binding.inputProductDiscountPrice.text.toString().trim()
        val isAvailable = binding.rbHaveProduct.isChecked
        val isNew = binding.rbCommingSoon.isChecked
        var quantity = binding.inputDetail.text.toString().trim()

        if (productName.isEmpty() || description.isEmpty() || standardPrice.isEmpty()) {
            showSnackbar(FIELD_REQUIRED)
            return
        } else if (isAvailable && quantity.isEmpty()) {
            showSnackbar(getString(R.string.quantity_required))
            return
        } else if (imageUri == null || uriList.size == 0) {
            showSnackbar(getString(R.string.images_required))
            return
        }

        if (categorySelected.equals(getString(R.string.category_placeholder))){
            showSnackbar(getString(R.string.category_required))
            return
        } else if (subcategorySelected.equals(getString(R.string.subcategory_placeholder))){
            showSnackbar(getString(R.string.subcategory_required))
            return
        } else if (warehouseSelected.equals(getString(R.string.warehouse_placeholder))){
            showSnackbar(getString(R.string.warehouse_required))
            return
        }

        if (discountPrice.isEmpty()) discountPrice = "0"
        if (isNew) quantity = "0"

        val product = Product()
        product.productName = productName
        product.description = description
        product.productCategory = categorySelected
        product.productSubcategory = subcategorySelected
        product.isActive = isActive
        product.standardPrice = standardPrice.toLong()
        product.discountPrice = discountPrice.toLong()
        product.warehouseName = warehouseSelected
        product.isNew = isNew
        product.isAvailable = isAvailable
        product.productQuantity = quantity.toInt()

//        Log.d("TEST", "name "+product.productName)
//        val encryptedProduct = encryptData(product)
//        Log.d("TEST", "name "+encryptedProduct.productName)

        prepareImageFilePart()
        val token = loginUtils.getSupplierToken()
        productViewModel.createProduct(token, supplier!!.id, product, images).observe(
            requireActivity(), { state -> processProductResponse(state) }
        )
    }

    private fun goToProductFragment() {
        navController.navigate(R.id.action_newProductFragment_to_productFragment)
    }

    private fun encryptData(product: Product): Product {
        val encryptedProduct = Product()
        val aes = AES.getInstance()
        aes.initFromString(secretKey, iv)

        encryptedProduct.productName = aes.encrypt(product.productName)
        encryptedProduct.description = aes.encrypt(product.description)
        encryptedProduct.productCategory = aes.encrypt(product.productCategory)
        encryptedProduct.productSubcategory = aes.encrypt(product.productSubcategory)
        encryptedProduct.isActive = product.isActive
        encryptedProduct.standardPrice = product.standardPrice
        encryptedProduct.discountPrice = product.discountPrice
        encryptedProduct.warehouseName = aes.encrypt(product.warehouseName)
        encryptedProduct.isNew = product.isNew
        encryptedProduct.isAvailable = product.isAvailable
        encryptedProduct.productQuantity = product.productQuantity

        return encryptedProduct
    }

    private fun chooseMultipleImage() {
        uriList.clear()
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        imagesActivityResultLauncher.launch(intent)
    }

    private fun chooseSingleImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        imageActivityResultLauncher.launch(intent)
    }

    private fun uriToFile(context: Context, uri: Uri, i: Int): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "temp_image${i}.jpg")
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    private fun fileToMultipartBodyPart(file: File): MultipartBody.Part {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("file", file.name, requestFile)
    }

    private fun prepareImageFilePart() {
        val imageFile = uriToFile(requireContext(), imageUri!!, 0)
        val image = fileToMultipartBodyPart(imageFile)
        images.add(image)
        var i = 1
        for (uri in uriList) {
            val file = uriToFile(requireContext(), uri, i)
            val img = fileToMultipartBodyPart(file)
            images.add(img)
            i++
        }
    }

    private fun processProductResponse(state: ScreenState<Product?>) {
        when (state) {
            is ScreenState.Loading -> {
                alertDialog = progressDialog.createAlertDialog(requireActivity())
            }

            is ScreenState.Success -> {
                if (state.data != null) {
                    alertDialog.dismiss()
                    showSnackbar(getString(R.string.created_product))
                    navController.navigate(R.id.action_newProductFragment_to_productFragment)
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