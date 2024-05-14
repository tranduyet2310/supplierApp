package com.example.suppileragrimart.view.garden

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.suppileragrimart.R
import com.example.suppileragrimart.adapter.ViewpagerAdapter
import com.example.suppileragrimart.databinding.FragmentGardenInfoBinding
import com.example.suppileragrimart.utils.Constants
import com.example.suppileragrimart.viewmodel.FieldViewModel
import com.google.android.material.tabs.TabLayoutMediator


class GardenGeneralFragment : Fragment() {
    private lateinit var binding: FragmentGardenInfoBinding
    private lateinit var navController: NavController

    private val fieldViewModel: FieldViewModel by lazy {
        ViewModelProvider(requireActivity()).get(FieldViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGardenInfoBinding.inflate(inflater)
        binding.toolbarLayout.titleToolbar.text = getString(R.string.garden_detail)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        setupTabLayout()

        binding.btnUpdate.setOnClickListener {
            val b = Bundle().apply {
                putString(Constants.SCREEN_KEY, "field")
                putParcelable(Constants.FIELD_KEY, null)
            }
            navController.navigate(R.id.action_gardenInfoFragment_to_updateGardenInfoFragment, b)
        }

        binding.toolbarLayout.imgBack.setOnClickListener {
            navController.navigateUp()
        }
    }

    private fun setupTabLayout() {
        val gardenInfoFragment = arrayListOf(
            GardenDetailFragment(),
            GardenCooperationFragment(),
            GardenYieldFragment(),
            GardenUpdateFragment()
        )
        binding.viewpagerUserInfo.isUserInputEnabled = false

        val viewPager2Adapter =
            ViewpagerAdapter(gardenInfoFragment, childFragmentManager, lifecycle)
        binding.viewpagerUserInfo.adapter = viewPager2Adapter
        TabLayoutMediator(binding.tabLayout, binding.viewpagerUserInfo) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.crops_detail)
                1 -> tab.text = getString(R.string.list_request)
                2 -> tab.text = "Sản lượng dự kiến"
                3 -> tab.text = getString(R.string.garden)
            }
        }.attach()
    }

}