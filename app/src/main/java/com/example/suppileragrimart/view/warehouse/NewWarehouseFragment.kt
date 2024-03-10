package com.example.suppileragrimart.view.warehouse

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.suppileragrimart.R
import com.example.suppileragrimart.databinding.FragmentNewWarehouseBinding
import com.example.suppileragrimart.databinding.FragmentWarehouseBinding


class NewWarehouseFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentNewWarehouseBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewWarehouseBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        binding.btnCancel.setOnClickListener(this)
        binding.btnSave.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnCancel, R.id.btnSave -> goToWarehouseFragment()

        }
    }

    private fun goToWarehouseFragment() {
        navController.navigate(R.id.action_newWarehouseFragment_to_warehouseFragment)
    }

}