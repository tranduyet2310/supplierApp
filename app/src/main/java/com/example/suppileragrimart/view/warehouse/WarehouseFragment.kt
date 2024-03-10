package com.example.suppileragrimart.view.warehouse

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.suppileragrimart.R
import com.example.suppileragrimart.databinding.FragmentWarehouseBinding


class WarehouseFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentWarehouseBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWarehouseBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        binding.tvSearch.setOnClickListener(this)
        binding.btnAddWareHouse.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.tvSearch -> goToSearchFragment()
            R.id.btnAddWareHouse -> goToAddWareHouseFragment()
        }
    }

    private fun goToAddWareHouseFragment() {
        navController.navigate(R.id.action_warehouseFragment_to_newWarehouseFragment)
    }

    private fun goToSearchFragment() {
        navController.navigate(R.id.action_warehouseFragment_to_searchFragment)
    }

}