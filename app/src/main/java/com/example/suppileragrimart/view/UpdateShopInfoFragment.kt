package com.example.suppileragrimart.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.suppileragrimart.R
import com.example.suppileragrimart.databinding.FragmentUpdateShopInfoBinding


class UpdateShopInfoFragment : Fragment() {
   private lateinit var binding: FragmentUpdateShopInfoBinding
    private lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpdateShopInfoBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        binding.btnSave.setOnClickListener{
            navController.navigate(R.id.action_updateShopInfoFragment_to_shopInfoFragment)
        }
        binding.btnBack.setOnClickListener {
            navController.navigate(R.id.action_updateShopInfoFragment_to_shopInfoFragment)
        }
    }

}