package com.example.suppileragrimart.view.garden

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.suppileragrimart.R
import com.example.suppileragrimart.adapter.FieldDetailAdapter
import com.example.suppileragrimart.databinding.FragmentGardenDetailBinding
import com.example.suppileragrimart.model.FieldApiResponse
import com.example.suppileragrimart.model.FieldDetail
import com.example.suppileragrimart.model.Supplier
import com.example.suppileragrimart.utils.Constants
import com.example.suppileragrimart.viewmodel.FieldViewModel
import com.example.suppileragrimart.viewmodel.SupplierViewModel
import com.google.android.material.snackbar.Snackbar


class GardenDetailFragment : Fragment() {
    private lateinit var binding: FragmentGardenDetailBinding

    private val supplierViewModel: SupplierViewModel by lazy {
        ViewModelProvider(requireActivity()).get(SupplierViewModel::class.java)
    }
    private val fieldViewModel: FieldViewModel by lazy {
        ViewModelProvider(requireActivity()).get(FieldViewModel::class.java)
    }

    private var supplier: Supplier? = null
    private var currentField: FieldApiResponse? = null
    private lateinit var fieldDetailAdapter: FieldDetailAdapter
    private lateinit var fieldList: ArrayList<FieldDetail>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGardenDetailBinding.inflate(inflater)

        supplier = supplierViewModel.supplier
        currentField = fieldViewModel.fieldData
        setupRecyclerView()
        if (currentField != null)
            showFieldDetailData()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fieldDetailAdapter.onClick = {
            val b = Bundle().apply {
                putString(Constants.SCREEN_KEY, "detail")
                putParcelable(Constants.FIELD_KEY, it)
            }
            this@GardenDetailFragment.findNavController()
                .navigate(R.id.action_gardenInfoFragment_to_updateGardenInfoFragment, b)
        }
    }

    private fun showFieldDetailData() {
        fieldList.addAll(currentField!!.fieldDetails)
        if (fieldList.isEmpty())
            hideReyclerView()
        else showRecyclerView()
    }

    private fun setupRecyclerView() {
        fieldList = arrayListOf()
        binding.rcvGardenInfo.apply {
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            fieldDetailAdapter = FieldDetailAdapter(fieldList)
            adapter = fieldDetailAdapter
        }
    }

    private fun showRecyclerView() {
        binding.rcvGardenInfo.visibility = View.VISIBLE
        binding.textAnnounce.visibility = View.GONE
        binding.imagePlaceholder.visibility = View.GONE
    }

    private fun hideReyclerView() {
        binding.rcvGardenInfo.visibility = View.GONE
        binding.textAnnounce.visibility = View.VISIBLE
        binding.imagePlaceholder.visibility = View.VISIBLE
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }
}