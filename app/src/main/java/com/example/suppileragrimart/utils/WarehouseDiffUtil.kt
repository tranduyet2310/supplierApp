package com.example.suppileragrimart.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.suppileragrimart.model.Warehouse

class WarehouseDiffUtil : DiffUtil.ItemCallback<Warehouse>() {
    override fun areItemsTheSame(oldItem: Warehouse, newItem: Warehouse): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Warehouse, newItem: Warehouse): Boolean {
        return oldItem.equals(newItem)
    }
}