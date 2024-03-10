package com.example.suppileragrimart.view.sale

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.suppileragrimart.R
import com.example.suppileragrimart.databinding.FragmentSaleBinding


class SaleFragment : Fragment(), View.OnClickListener {

    private lateinit var binding:FragmentSaleBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSaleBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        binding.tvSearch.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when(v?.id){
            R.id.tvSearch -> goToSearchFragment()
        }
    }

    private fun goToSearchFragment() {
        navController.navigate(R.id.action_saleFragment_to_searchFragment)
    }

}