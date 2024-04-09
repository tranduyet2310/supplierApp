package com.example.suppileragrimart.view.product

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.suppileragrimart.R
import com.example.suppileragrimart.databinding.FragmentNewProductBinding


class NewProductFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentNewProductBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewProductBinding.inflate(inflater)
        binding.toolbarLayout.titleToolbar.text = getString(R.string.add_product)

        val categoryValues = arrayOf("Danh mục sản phẩm *", "Rau củ", "Hoa quả", "Gia vị")
        val subCategoryValues = arrayOf("Danh mục con *", "Rau củ", "Hoa quả", "Gia vị")
        val warehouseValues = arrayOf("Điểm lấy hàng *", "Kho 1", "Kho 2", "Kho 3")
        val spProductCategoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categoryValues)
        val spSubcategoryProductAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, subCategoryValues)
        val spWarehouseNameAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, warehouseValues)

        binding.spProductCategory.adapter = spProductCategoryAdapter
        binding.spSubcategoryProduct.adapter = spSubcategoryProductAdapter
        binding.spWarehouseName.adapter = spWarehouseNameAdapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        binding.toolbarLayout.imgBack.setOnClickListener{
            navController.navigateUp()
        }
    }

    override fun onClick(v: View?) {

    }


}