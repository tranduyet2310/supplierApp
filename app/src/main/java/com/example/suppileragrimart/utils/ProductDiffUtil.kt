package com.example.suppileragrimart.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.suppileragrimart.model.Product

class ProductDiffUtil: DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem.productId == newItem.productId
    }

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem.equals(newItem)
    }
}