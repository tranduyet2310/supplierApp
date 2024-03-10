package com.example.suppileragrimart.view.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.suppileragrimart.R
import com.example.suppileragrimart.databinding.FragmentChangePasswordBinding

class ChangePasswordFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        binding.cancel.setOnClickListener(this)
        binding.saveChanges.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.cancel, R.id.saveChanges -> goToProfileFragment()

        }
    }

    private fun goToProfileFragment() {
        Toast.makeText(requireContext(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show()
        navController.navigate(R.id.action_changePasswordFragment_to_userAccountFragment)
    }

}