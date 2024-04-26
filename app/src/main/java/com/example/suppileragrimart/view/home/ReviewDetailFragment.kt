package com.example.suppileragrimart.view.home

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.suppileragrimart.R
import com.example.suppileragrimart.databinding.FragmentReviewDetailBinding
import com.example.suppileragrimart.model.Product
import com.example.suppileragrimart.model.ReviewInfo
import com.example.suppileragrimart.network.Api
import com.example.suppileragrimart.network.RetrofitClient
import com.example.suppileragrimart.utils.ProgressDialog
import com.example.suppileragrimart.utils.Utils.Companion.formatPrice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReviewDetailFragment : Fragment() {
    private lateinit var binding: FragmentReviewDetailBinding
    private lateinit var navController: NavController

    private val apiService: Api by lazy {
        RetrofitClient.getInstance().getApi()
    }
    private val progressDialog: ProgressDialog by lazy {
        ProgressDialog()
    }

    private lateinit var alertDialog: AlertDialog
    private lateinit var reviewInfo: ReviewInfo
    private val navArgs: ReviewDetailFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReviewDetailBinding.inflate(inflater)
        binding.toolbarLayout.titleToolbar.text = getString(R.string.detail)

        reviewInfo = navArgs.review
        binding.nameOfProduct.text = reviewInfo.productName
        showReviewContent()
        lifecycleScope.launch {
            withContext(Dispatchers.Main){
                alertDialog = progressDialog.createAlertDialog(requireActivity())
            }
            getAverageRating()
            getProductById()
            withContext(Dispatchers.Main){
                alertDialog.dismiss()
            }
        }

        return binding.root
    }

    private fun showReviewContent() {
        binding.dateOfReview.text = reviewInfo.reviewDate
        binding.userName.text = reviewInfo.userFullName
        binding.userFeedback.text = reviewInfo.feedBack
        binding.rateProduct.rating = reviewInfo.rating.toFloat()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        binding.toolbarLayout.imgBack.setOnClickListener {
            navController.navigateUp()
        }
    }

    suspend fun getProductById(){
        withContext(Dispatchers.IO){
            val response = apiService.getProductById(reviewInfo.productId)
            if (response.isSuccessful){
                if (response.body() != null){
                    withContext(Dispatchers.Main){
                       setupProductImage(response.body()!!)
                    }
                }
            }
        }
    }

    private fun setupProductImage(data: Product) {
        val standardPrice = "${data.standardPrice.formatPrice()} đ"
        val discountPrice = "${data.discountPrice.formatPrice()} đ"
        binding.tvProductSold.text = data.sold.toString()
        if (data.discountPrice > 0){
            binding.divider.visibility = View.VISIBLE
            binding.priceOfProduct.text = discountPrice
            binding.priceOfProductDiscount.text = standardPrice
        } else {
            binding.divider.visibility = View.GONE
            binding.priceOfProductDiscount.visibility = View.GONE
            binding.priceOfProduct.text = standardPrice
        }
        val imageList = ArrayList<SlideModel>()
        for (image in data.productImage) {
            imageList.add(SlideModel(image.imageUrl))
        }
        binding.imageOfProduct.setImageList(imageList, ScaleTypes.FIT)
    }

    suspend fun getAverageRating() {
        withContext(Dispatchers.IO){
            val response = apiService.averageRating(reviewInfo.productId)
            if (response.isSuccessful){
                if (response.body() != null){
                    val rating = response.body()!!.averageRating.toDouble()
                    withContext(Dispatchers.Main){
                        if (rating == 0.0) {
                            binding.tvRatingMiniView.text = getString(R.string._5_0)
                        } else {
                            binding.tvRatingMiniView.text = rating.toString()
                        }
                    }
                }
            }
        }
    }
}