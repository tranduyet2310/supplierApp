package com.example.suppileragrimart.view.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.suppileragrimart.R
import com.example.suppileragrimart.databinding.FragmentGardenInfoBinding
import com.google.android.material.snackbar.Snackbar


class GardenInfoFragment : Fragment() {
   private lateinit var binding: FragmentGardenInfoBinding
    private lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentGardenInfoBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        binding.btnUpdate.setOnClickListener {
            Snackbar.make(requireView(), "Clicked", Snackbar.LENGTH_SHORT).show()
            navController.navigate(R.id.action_gardenInfoFragment_to_updateGardenInfoFragment)
        }
    }

}