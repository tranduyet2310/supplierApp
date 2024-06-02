package com.example.suppileragrimart.view.product

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.suppileragrimart.R
import com.example.suppileragrimart.databinding.FragmentEditProductBinding
import com.example.suppileragrimart.model.CategoryApiResponse
import com.example.suppileragrimart.model.MessageResponse
import com.example.suppileragrimart.model.Product
import com.example.suppileragrimart.model.Supplier
import com.example.suppileragrimart.network.Api
import com.example.suppileragrimart.network.RetrofitClient
import com.example.suppileragrimart.utils.Constants
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


class EditProductFragment : Fragment() {
    private lateinit var binding: FragmentEditProductBinding
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
    private val args: EditProductFragmentArgs by navArgs()
    private lateinit var currentProduct: Product
    private var currentSubcategory: String ?= null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProductBinding.inflate(inflater)
        binding.toolbarLayout.titleToolbar.text = getString(R.string.update_info)

        supplier = supplierViewModel.supplier
        currentProduct = args.product

        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                alertDialog = progressDialog.createAlertDialog(requireActivity())
            }
            getWarehouseData()
            getCategoryData()
            withContext(Dispatchers.Main) {
                alertDialog.dismiss()
                showInfo()
            }
        }

        setupImageListener()

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

        binding.tvProductAdditionalImage.setOnClickListener {
            setupProductAdditionalImage()
        }

        binding.tvProductImage.setOnClickListener {
            setupProductAvatarListener()
        }

        binding.btnSave.setOnClickListener {
            if (uriList.isEmpty()){
                updateInfo()
            } else {
                updateAll()
            }
        }

        binding.btnCancel.setOnClickListener {
            deleteProduct()
        }
    }

    private fun deleteProduct() {
        val dialog = AlertDialog.Builder(requireContext()).apply {
            setTitle(getString(R.string.check_out))
            setMessage(getString(R.string.product_question))
            setNegativeButton(getString(R.string.cancel)) { dialogs, _ ->
                dialogs.dismiss()
            }
            setPositiveButton(getString(R.string.ok)) { dialogs, _ ->
                val token = loginUtils.getSupplierToken()
                productViewModel.deleteProduct(token, supplier!!.id, currentProduct.productId).observe(
                    requireActivity(), { state -> handleDeleteProduct(state) }
                )
                dialogs.dismiss()
            }
        }
        dialog.create()
        dialog.show()
    }

    private fun updateInfo() {
        val productName = binding.inputProductName.text.toString().trim()
        val description = binding.inputProductDescription.text.toString().trim()
        val isActive = binding.checkboxActiveProduct.isChecked
        val standardPrice = binding.inputProductPrice.text.toString().trim()
        var discountPrice = binding.inputProductDiscountPrice.text.toString().trim()
        val isAvailable = binding.rbHaveProduct.isChecked
        val isNew = binding.rbCommingSoon.isChecked
        var quantity = binding.inputDetail.text.toString().trim()

        if (productName.isEmpty() || description.isEmpty() || standardPrice.isEmpty()) {
            showSnackbar(Constants.FIELD_REQUIRED)
            return
        } else if (isAvailable && quantity.isEmpty()) {
            showSnackbar(getString(R.string.quantity_required))
            return
        } else if (categorySelected.equals(getString(R.string.category_placeholder))){
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

        val token = loginUtils.getSupplierToken()
        productViewModel.updateProductInfo(token, supplier!!.id, currentProduct.productId, product).observe(
            requireActivity(), { state -> processProductResponse(state) })
    }

    private fun updateAll() {
        val productName = binding.inputProductName.text.toString().trim()
        val description = binding.inputProductDescription.text.toString().trim()
        val isActive = binding.checkboxActiveProduct.isChecked
        val standardPrice = binding.inputProductPrice.text.toString().trim()
        var discountPrice = binding.inputProductDiscountPrice.text.toString().trim()
        val isAvailable = binding.rbHaveProduct.isChecked
        val isNew = binding.rbCommingSoon.isChecked
        var quantity = binding.inputDetail.text.toString().trim()

        if (productName.isEmpty() || description.isEmpty() || standardPrice.isEmpty()) {
            showSnackbar(Constants.FIELD_REQUIRED)
            return
        } else if (categorySelected.equals(getString(R.string.category_placeholder))){
            showSnackbar(getString(R.string.category_required))
            return
        } else if (subcategorySelected.equals(getString(R.string.subcategory_placeholder))){
            showSnackbar(getString(R.string.subcategory_required))
            return
        } else if (warehouseSelected.equals(getString(R.string.warehouse_placeholder))){
            showSnackbar(getString(R.string.warehouse_required))
            return
        } else if (isAvailable && quantity.isEmpty()) {
            showSnackbar(getString(R.string.quantity_required))
            return
        } else if (imageUri == null || uriList.size == 0) {
            showSnackbar(getString(R.string.images_required))
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

        prepareImageFilePart()

        val token = loginUtils.getSupplierToken()
        productViewModel.updateProductAll(token, supplier!!.id, currentProduct.productId, product, images).observe(
            requireActivity(), { state -> processProductResponse(state) })
    }
    private fun showInfo() {
        binding.inputProductName.text = Editable.Factory.getInstance().newEditable(currentProduct.productName)
        binding.inputProductDescription.text = Editable.Factory.getInstance().newEditable(currentProduct.description)

        val categoryPos = categoryValues.indexOf(currentProduct.productCategory)
        binding.spProductCategory.setSelection(categoryPos)
        currentSubcategory = currentProduct.productSubcategory
        binding.checkboxActiveProduct.isChecked = currentProduct.isActive
        binding.inputProductPrice.text = Editable.Factory.getInstance().newEditable(currentProduct.standardPrice.toString())
        binding.inputProductDiscountPrice.text = Editable.Factory.getInstance().newEditable(currentProduct.discountPrice.toString())

        val warehousePos = warehouseValues.indexOf(currentProduct.warehouseName)
        binding.spWarehouseName.setSelection(warehousePos)
        binding.rbHaveProduct.isChecked = currentProduct.isAvailable
        binding.rbCommingSoon.isChecked = currentProduct.isNew
        binding.inputDetail.text = Editable.Factory.getInstance().newEditable(currentProduct.productQuantity.toString())

        val imageList = currentProduct.productImage
        for (i in 1..imageList.size) {
            when (i) {
                1 -> showImage(requireContext(), imageList.get(0).imageUrl, binding.imgProductAvatar)
                2 -> showImage(requireContext(), imageList.get(1).imageUrl, binding.imagProduct1)
                3 -> showImage(requireContext(), imageList.get(2).imageUrl, binding.imagProduct2)
                4 -> showImage(requireContext(), imageList.get(3).imageUrl, binding.imagProduct3)
                5 -> showImage(requireContext(), imageList.get(4).imageUrl, binding.imagProduct4)
                else -> {}
            }
        }
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
                binding.imgProductAvatar.setBackgroundResource(R.drawable.unselected_button_background)
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
                        1 -> {
                            GlideApp.with(requireContext()).load(uriList[0]).into(binding.imagProduct1)
                            binding.imagProduct1.setBackgroundResource(R.drawable.unselected_button_background)
                        }
                        2 -> {
                            GlideApp.with(requireContext()).load(uriList[1]).into(binding.imagProduct2)
                            binding.imagProduct2.setBackgroundResource(R.drawable.unselected_button_background)
                        }
                        3 -> {
                            GlideApp.with(requireContext()).load(uriList[2]).into(binding.imagProduct3)
                            binding.imagProduct3.setBackgroundResource(R.drawable.unselected_button_background)
                        }
                        4 -> {
                            GlideApp.with(requireContext()).load(uriList[3]).into(binding.imagProduct4)
                            binding.imagProduct4.setBackgroundResource(R.drawable.unselected_button_background)
                        }
                        else -> {}
                    }
                }

            }
    }

    private fun setupProductAvatarListener() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        imageActivityResultLauncher.launch(intent)
    }
    private fun setupProductAdditionalImage() {
        uriList.clear()
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        imagesActivityResultLauncher.launch(intent)
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
                    currentSubcategory = subcategorySelected
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

                    if (currentSubcategory != null){
                        val pos = subCategoryValues.indexOf(currentSubcategory)
                        binding.spSubcategoryProduct.setSelection(pos)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
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
                    showSnackbar(getString(R.string.update_success))
                    navController.navigate(R.id.action_editProductFragment_to_productFragment)
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

    private fun handleDeleteProduct(state: ScreenState<MessageResponse?>) {
        when (state) {
            is ScreenState.Loading -> {
                alertDialog = progressDialog.createAlertDialog(requireActivity())
            }

            is ScreenState.Success -> {
                if (state.data != null) {
                    alertDialog.dismiss()
                    showSnackbar(getString(R.string.delete_product))
                    navController.navigate(R.id.action_editProductFragment_to_productFragment)
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

    private fun showImage(context: Context, imageUrl: String, imgProduct: ImageView) {
        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transform(FitCenter(), RoundedCorners(16))
        val modifiedUrl = imageUrl.replace("http://", "https://")
        GlideApp.with(context)
            .load(modifiedUrl)
            .apply(requestOptions)
            .skipMemoryCache(true)
            .into(imgProduct)
    }
}