package com.example.suppileragrimart.view.profile

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.suppileragrimart.R
import com.example.suppileragrimart.databinding.FragmentShopInfoBinding
import com.example.suppileragrimart.model.Image
import com.example.suppileragrimart.model.Supplier
import com.example.suppileragrimart.model.SupplierIntro
import com.example.suppileragrimart.utils.Constants
import com.example.suppileragrimart.utils.LoginUtils
import com.example.suppileragrimart.utils.ProgressDialog
import com.example.suppileragrimart.utils.ScreenState
import com.example.suppileragrimart.viewmodel.SupplierIntroViewModel
import com.example.suppileragrimart.viewmodel.SupplierViewModel
import com.google.android.material.snackbar.Snackbar


class ShopInfoFragment : Fragment() {
    private lateinit var binding: FragmentShopInfoBinding
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

    private lateinit var alertDialog: AlertDialog
    private var supplier: Supplier? = null
    private var currentShopInfo: SupplierIntro? = null
    private var currentGardenInfo: SupplierIntro? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShopInfoBinding.inflate(inflater)
        binding.toolbarLayout.titleToolbar.text = getString(R.string.shop_info)

        supplier = supplierViewModel.supplier
        if (supplier != null) {
            getSupplierIntroInfo()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        binding.btnUpdate.setOnClickListener() {
            val b = Bundle().apply {
                putParcelable(Constants.SHOP, currentShopInfo)
                putParcelable(Constants.GARDEN, currentGardenInfo)
            }
            navController.navigate(R.id.action_shopInfoFragment_to_updateShopInfoFragment, b)
        }

        binding.toolbarLayout.imgBack.setOnClickListener {
            navController.navigateUp()
        }
    }

    private fun getSupplierIntroInfo() {
        supplierIntroViewModel.getAllSupplierIntro(supplier!!.id).observe(
            requireActivity(), { state -> processSupplierIntro(state) }
        )
    }

    private fun setupShopImages(images: MutableList<Image>) {
        val imageList = ArrayList<SlideModel>()
        for (image in images) {
            val modifiedUrl = image.imageUrl.replace("http://", "https://")
            imageList.add(SlideModel(modifiedUrl))
        }
        imageList.add(SlideModel(Constants.BANNER))

        binding.imageShopIntro.setImageList(imageList, ScaleTypes.CENTER_INSIDE)
    }

    private fun setupGardenImages(images: MutableList<Image>) {
        val imageList = ArrayList<SlideModel>()
        for (image in images) {
            val modifiedUrl = image.imageUrl.replace("http://", "https://")
            imageList.add(SlideModel(modifiedUrl))
        }
        imageList.add(SlideModel(Constants.BANNER))

        binding.imageGardenIntro.setImageList(imageList, ScaleTypes.CENTER_INSIDE)
    }

    private fun hideImagePlaceholder() {
        binding.contentLayout.visibility = View.VISIBLE
        binding.imagePlaceholder.visibility = View.GONE
        binding.textAnnounce.visibility = View.GONE
    }

    private fun showImagePlaceholder() {
        binding.contentLayout.visibility = View.GONE
        binding.imagePlaceholder.visibility = View.VISIBLE
        binding.textAnnounce.visibility = View.VISIBLE
    }

    private fun processSupplierIntro(state: ScreenState<ArrayList<SupplierIntro>?>) {
        when (state) {
            is ScreenState.Loading -> {
                alertDialog = progressDialog.createAlertDialog(requireActivity())
            }

            is ScreenState.Success -> {
                if (state.data != null) {
                    alertDialog.dismiss()
                    val dataList = state.data
                    if (dataList.isEmpty()) {
                        showImagePlaceholder()
                    } else {
                        hideImagePlaceholder()
                        for (s in dataList) {
                            if (s.type.equals(Constants.SHOP)) {
                                currentShopInfo = s
                                binding.tvShopInfo.text = s.description
                                setupShopImages(s.images)
                            } else if (s.type.equals(Constants.GARDEN)) {
                                currentGardenInfo = s
                                binding.tvGardenInfo.text = s.description
                                setupGardenImages(s.images)
                            }
                        }
                    }
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