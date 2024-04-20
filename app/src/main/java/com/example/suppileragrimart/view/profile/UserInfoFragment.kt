package com.example.suppileragrimart.view.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.suppileragrimart.R
import com.example.suppileragrimart.adapter.ViewpagerAdapter
import com.example.suppileragrimart.databinding.FragmentUserInfoBinding
import com.google.android.material.tabs.TabLayoutMediator


class UserInfoFragment : Fragment() {

    private lateinit var binding: FragmentUserInfoBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       binding = FragmentUserInfoBinding.inflate(inflater)
        binding.toolbarLayout.titleToolbar.text = getString(R.string.personal_info)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        val userInfoFragment = arrayListOf(
            GeneralInfoFragment(),
            BankInfoFragment()
        )

        binding.viewpagerUserInfo.isUserInputEnabled = false

        val viewPager2Adapter =
            ViewpagerAdapter(userInfoFragment, childFragmentManager, lifecycle)
        binding.viewpagerUserInfo.adapter = viewPager2Adapter
        TabLayoutMediator(binding.tabLayout, binding.viewpagerUserInfo) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.general_info)
                1 -> tab.text = getString(R.string.bank_info)

            }
        }.attach()

        binding.toolbarLayout.imgBack.setOnClickListener{
            navController.navigateUp()
        }
    }

}