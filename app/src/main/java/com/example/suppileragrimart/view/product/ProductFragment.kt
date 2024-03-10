package com.example.suppileragrimart.view.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.suppileragrimart.R
import com.example.suppileragrimart.databinding.FragmentProductBinding

class ProductFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentProductBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        binding.btnAddNewProduct.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnAddNewProduct -> goToNewProductFragment()
        }
    }

    private fun goToNewProductFragment() {
        navController.navigate(R.id.action_productFragment_to_newProductFragment)
    }

}