package com.example.suppileragrimart.view.profile

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.suppileragrimart.R
import com.example.suppileragrimart.databinding.FragmentUpdateShopInfoBinding
import com.example.suppileragrimart.model.Supplier
import com.example.suppileragrimart.model.SupplierIntro
import com.example.suppileragrimart.network.Api
import com.example.suppileragrimart.network.RetrofitClient
import com.example.suppileragrimart.utils.Constants
import com.example.suppileragrimart.utils.GlideApp
import com.example.suppileragrimart.utils.LoginUtils
import com.example.suppileragrimart.utils.ProgressDialog
import com.example.suppileragrimart.viewmodel.SupplierIntroViewModel
import com.example.suppileragrimart.viewmodel.SupplierViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class UpdateShopInfoFragment : Fragment() {
    private lateinit var binding: FragmentUpdateShopInfoBinding
    private lateinit var navController: NavController

    private val supplierViewModel: SupplierViewModel by lazy {
        ViewModelProvider(requireActivity()).get(SupplierViewModel::class.java)
    }
    private val loginUtils: LoginUtils by lazy {
        LoginUtils(requireContext())
    }
    private val progressDialog: ProgressDialog by lazy {
        ProgressDialog()
    }
    private val supplierIntroViewModel: SupplierIntroViewModel by lazy {
        ViewModelProvider(requireActivity()).get(SupplierIntroViewModel::class.java)
    }
    private val apiService: Api by lazy {
        RetrofitClient.getInstance().getApi()
    }

    private lateinit var alertDialog: AlertDialog
    private var supplier: Supplier? = null
    private var currentShopInfo: SupplierIntro? = null
    private var currentGardenInfo: SupplierIntro? = null

    private val args: UpdateShopInfoFragmentArgs by navArgs()
    private var uriShopList: ArrayList<Uri> = arrayListOf()
    private var uriGardenList: ArrayList<Uri> = arrayListOf()
    private lateinit var shopImagesActivityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var gardenImagesActivityResultLauncher: ActivityResultLauncher<Intent>
    private var shopImages: ArrayList<MultipartBody.Part> = arrayListOf()
    private var gardenImages: ArrayList<MultipartBody.Part> = arrayListOf()
    private var isUpdate: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpdateShopInfoBinding.inflate(inflater)
        binding.toolbarLayout.titleToolbar.text = getString(R.string.update_info)

        supplier = supplierViewModel.supplier
        currentShopInfo = args.shop
        currentGardenInfo = args.garden

        if (currentShopInfo != null) {
            showShopInfo()
        }
        if (currentGardenInfo != null) {
            showGardenInfo()
        }

        if (currentGardenInfo != null && currentShopInfo != null) {
            isUpdate = true
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        setupShopImageListener()
        setupGardenImageListener()

        binding.btnBack.setOnClickListener {
            navController.navigate(R.id.action_updateShopInfoFragment_to_shopInfoFragment)
        }

        binding.toolbarLayout.imgBack.setOnClickListener {
            navController.navigateUp()
        }

        binding.btnSave.setOnClickListener {
            val shopDesciption = binding.edtShopIntro.text.toString().trim()
            val gardenDesciption = binding.edtGardenIntro.text.toString().trim()

            if (shopDesciption.isEmpty() || gardenDesciption.isEmpty()) {
                showSnackbar("Yêu cầu nhập đủ thông tin giới thiệu")
                return@setOnClickListener
            }

            if (isUpdate && uriShopList.isEmpty() && uriGardenList.isEmpty()) {
                updateDescriptionIntro(shopDesciption, gardenDesciption)
            } else {
                if (uriGardenList.size == 0 || uriShopList.size == 0) {
                    showSnackbar("Yêu cầu thêm ảnh cho phần giới thiệu")
                    return@setOnClickListener
                }

                prepareImageFilePart(shopImages, uriShopList, 1)
                prepareImageFilePart(gardenImages, uriGardenList, 10)

                if (isUpdate) {
                    updateSupplierIntro(shopDesciption, gardenDesciption)
                } else {
                    createNewSupplierIntro(shopDesciption, gardenDesciption)
                }
            }
        }

        binding.tvShopImage.setOnClickListener {
            uriShopList.clear()
            val intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            shopImagesActivityResultLauncher.launch(intent)
        }

        binding.tvGardenImage.setOnClickListener {
            uriGardenList.clear()
            val intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            gardenImagesActivityResultLauncher.launch(intent)
        }
    }

    private fun updateDescriptionIntro(shopDesciption: String, gardenDesciption: String) {
        val token = loginUtils.getSupplierToken()
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                alertDialog = progressDialog.createAlertDialog(requireActivity())
            }
            updateShopDescriptionIntro(token, shopDesciption)
            updateGardenDescriptionIntro(token, gardenDesciption)
            withContext(Dispatchers.Main) {
                alertDialog.dismiss()
                showSnackbar(getString(R.string.update_success))
                navController.navigate(R.id.action_updateShopInfoFragment_to_shopInfoFragment)
            }
        }
    }

    suspend fun updateGardenDescriptionIntro(token: String, gardenDesciption: String) {
        val supplierIntro = SupplierIntro().apply {
            supplierId = supplier!!.id
            description = gardenDesciption
            type = Constants.GARDEN
        }
        withContext(Dispatchers.IO) {
            val response =
                apiService.updateDescriptionIntro(token, currentGardenInfo!!.id, supplierIntro)
            if (response.isSuccessful) {
                if (response.body() != null) {
                    Log.d("TEST", "update garden description intro successfully")
                } else {
                    Log.d("TEST", response.message())
                }
            } else {
                Log.d("TEST", response.message())
            }
        }
    }

    suspend fun updateShopDescriptionIntro(token: String, shopDesciption: String) {
        val supplierIntro = SupplierIntro().apply {
            supplierId = supplier!!.id
            description = shopDesciption
            type = Constants.SHOP
        }
        withContext(Dispatchers.IO) {
            val response =
                apiService.updateDescriptionIntro(token, currentShopInfo!!.id, supplierIntro)
            if (response.isSuccessful) {
                if (response.body() != null) {
                    Log.d("TEST", "update shop description intro successfully")
                } else {
                    Log.d("TEST", response.message())
                }
            } else {
                Log.d("TEST", response.message())
            }
        }
    }

    private fun updateSupplierIntro(shopDesciption: String, gardenDesciption: String) {
        val token = loginUtils.getSupplierToken()
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                alertDialog = progressDialog.createAlertDialog(requireActivity())
            }
            updateShopIntro(token, shopDesciption)
            updateGardenIntro(token, gardenDesciption)
            withContext(Dispatchers.Main) {
                alertDialog.dismiss()
                showSnackbar(getString(R.string.update_success))
                navController.navigate(R.id.action_updateShopInfoFragment_to_shopInfoFragment)
            }
        }
    }

    suspend fun updateGardenIntro(token: String, gardenDesciption: String) {
        withContext(Dispatchers.IO) {
            val response = apiService.updateSupplierIntro(
                token, supplier!!.id,
                currentGardenInfo!!.id, gardenDesciption, Constants.GARDEN, gardenImages
            )
            if (response.isSuccessful) {
                if (response.body() != null) {
                    Log.d("TEST", "update garden intro successfully")
                } else {
                    Log.d("TEST", response.message())
                }
            } else {
                Log.d("TEST", response.message())
            }
        }
    }

    suspend fun updateShopIntro(token: String, shopDesciption: String) {
        withContext(Dispatchers.IO) {
            val response = apiService.updateSupplierIntro(
                token, supplier!!.id,
                currentShopInfo!!.id, shopDesciption, Constants.SHOP, shopImages
            )
            if (response.isSuccessful) {
                if (response.body() != null) {
                    Log.d("TEST", "update shop intro successfully")
                } else {
                    Log.d("TEST", response.message())
                }
            } else {
                Log.d("TEST", response.message())
            }
        }
    }

    private fun createNewSupplierIntro(shopDesciption: String, gardenDesciption: String) {
        val token = loginUtils.getSupplierToken()
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                alertDialog = progressDialog.createAlertDialog(requireActivity())
            }
            createShopIntro(token, shopDesciption)
            createGardenIntro(token, gardenDesciption)
            withContext(Dispatchers.Main) {
                alertDialog.dismiss()
                showSnackbar(getString(R.string.update_success))
                navController.navigate(R.id.action_updateShopInfoFragment_to_shopInfoFragment)
            }
        }
    }

    suspend fun createShopIntro(token: String, description: String) {
        withContext(Dispatchers.IO) {
            val response = apiService.createSupplierIntro(
                token, supplier!!.id, description,
                Constants.SHOP, shopImages
            )
            if (response.isSuccessful) {
                if (response.body() != null) {
                    Log.d("TEST", "create shop intro successfully")
                } else {
                    Log.d("TEST", response.message())
                }
            } else {
                Log.d("TEST", response.message())
            }
        }
    }

    suspend fun createGardenIntro(token: String, description: String) {
        withContext(Dispatchers.IO) {
            val response = apiService.createSupplierIntro(
                token, supplier!!.id, description,
                Constants.GARDEN, gardenImages
            )
            if (response.isSuccessful) {
                if (response.body() != null) {
                    Log.d("TEST", "create garden intro successfully")
                } else {
                    Log.d("TEST", response.message())
                }
            } else {
                Log.d("TEST", response.message())
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

    private fun prepareImageFilePart(
        images: ArrayList<MultipartBody.Part>,
        uriList: ArrayList<Uri>,
        start: Int
    ) {
        var i = start
        for (uri in uriList) {
            val file = uriToFile(requireContext(), uri, i)
            val image = fileToMultipartBodyPart(file)
            images.add(image)
            i++
        }
    }

    private fun setupGardenImageListener() {
        gardenImagesActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.data!!.clipData != null) {
                    val mClipData = it.data!!.clipData
                    for (i in 0 until mClipData!!.itemCount) {
                        val item = mClipData.getItemAt(i)
                        uriGardenList.add(item.uri)
                    }
                } else if (it.data != null) {
                    val imageUri = it.data?.data
                    if (imageUri != null) {
                        uriGardenList.add(imageUri)
                    }
                }

                for (i in 1.. uriGardenList.size){
                    when(i){
                        1 -> GlideApp.with(requireContext()).load(uriGardenList[0]).into(binding.imageGarden1)
                        2 -> GlideApp.with(requireContext()).load(uriGardenList[1]).into(binding.imageGarden2)
                        3 -> GlideApp.with(requireContext()).load(uriGardenList[2]).into(binding.imageGarden3)
                        4 -> GlideApp.with(requireContext()).load(uriGardenList[3]).into(binding.imageGarden4)
                        else -> {}
                    }
                }
            }
    }

    private fun setupShopImageListener() {
        shopImagesActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.data!!.clipData != null) {
                    val mClipData = it.data!!.clipData
                    for (i in 0 until mClipData!!.itemCount) {
                        val item = mClipData.getItemAt(i)
                        uriShopList.add(item.uri)
                    }
                } else if (it.data != null) {
                    val imageUri = it.data?.data
                    if (imageUri != null) {
                        uriShopList.add(imageUri)
                    }
                }

                for (i in 1.. uriShopList.size) {
                    when(i){
                        1 -> GlideApp.with(requireContext()).load(uriShopList[0]).into(binding.imageShop1)
                        2 -> GlideApp.with(requireContext()).load(uriShopList[1]).into(binding.imageShop2)
                        3 -> GlideApp.with(requireContext()).load(uriShopList[2]).into(binding.imageShop3)
                        4 -> GlideApp.with(requireContext()).load(uriShopList[3]).into(binding.imageShop4)
                        else -> {}
                    }
                }
            }
    }

    private fun glideApp(imageUrl: String, view: ImageView) {
        val modifiedUrl = imageUrl.replace("http://", "https://")
        GlideApp.with(requireContext())
            .load(modifiedUrl)
            .into(view)
    }

    private fun showGardenInfo() {
        binding.edtGardenIntro.text =
            Editable.Factory.getInstance().newEditable(currentGardenInfo!!.description)
        val dataList = currentGardenInfo!!.images
        if (!dataList.isEmpty()) {
            var size = 1
            for (image in dataList) {
                if (size <= dataList.size) {
                    showGardenImage(size, image.imageUrl)
                    size += 1
                }
            }
        }
    }

    private fun showGardenImage(value: Int, url: String) {
        when (value) {
            1 -> glideApp(url, binding.imageGarden1)
            2 -> glideApp(url, binding.imageGarden2)
            3 -> glideApp(url, binding.imageGarden3)
            4 -> glideApp(url, binding.imageGarden4)
        }
    }

    private fun showShopImage(value: Int, url: String) {
        when (value) {
            1 -> glideApp(url, binding.imageShop1)
            2 -> glideApp(url, binding.imageShop2)
            3 -> glideApp(url, binding.imageShop3)
            4 -> glideApp(url, binding.imageShop4)
        }
    }

    private fun showShopInfo() {
        binding.edtShopIntro.text =
            Editable.Factory.getInstance().newEditable(currentShopInfo!!.description)
        val dataList = currentShopInfo!!.images
        if (!dataList.isEmpty()) {
            var size = 1
            for (image in dataList) {
                if (size <= dataList.size) {
                    showShopImage(size, image.imageUrl)
                    size += 1
                }
            }
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }
}