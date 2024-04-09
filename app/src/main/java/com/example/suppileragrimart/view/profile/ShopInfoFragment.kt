package com.example.suppileragrimart.view.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.suppileragrimart.R
import com.example.suppileragrimart.databinding.FragmentShopInfoBinding
import com.google.android.material.snackbar.Snackbar


class ShopInfoFragment : Fragment() {
    private lateinit var binding: FragmentShopInfoBinding
    private lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShopInfoBinding.inflate(inflater)
        binding.toolbarLayout.titleToolbar.text = getString(R.string.shop_info)

        setupShopImages();
        setupGardenImages()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        binding.btnUpdate.setOnClickListener() {
            Snackbar.make(requireView(), "Clicked", Snackbar.LENGTH_SHORT).show()
            navController.navigate(R.id.action_shopInfoFragment_to_updateShopInfoFragment)
        }

        binding.contentLayout.visibility = View.VISIBLE
        binding.imagePlaceholder.visibility = View.GONE
        binding.textAnnounce.visibility = View.GONE

        binding.toolbarLayout.imgBack.setOnClickListener{
            navController.navigateUp()
        }
    }

    private fun setupShopImages() {
        val imageList = ArrayList<SlideModel>();
        imageList.add(SlideModel("https://res.cloudinary.com/dtdctll9c/image/upload/v1710340597/buupvmd6jmb1aofikr9n.png"))
        imageList.add(SlideModel("https://bit.ly/2YoJ77H"))
        imageList.add(SlideModel("https://bit.ly/2BteuF2"))

        binding.imageShopIntro.setImageList(imageList, ScaleTypes.CENTER_INSIDE)
    }

    private fun setupGardenImages() {
        val imageList = ArrayList<SlideModel>();
        imageList.add(SlideModel("https://res.cloudinary.com/dtdctll9c/image/upload/v1710340597/buupvmd6jmb1aofikr9n.png"))
        imageList.add(SlideModel("https://bit.ly/2YoJ77H"))
        imageList.add(SlideModel("https://bit.ly/2BteuF2"))

        binding.imageGardenIntro.setImageList(imageList, ScaleTypes.CENTER_INSIDE)
    }
}