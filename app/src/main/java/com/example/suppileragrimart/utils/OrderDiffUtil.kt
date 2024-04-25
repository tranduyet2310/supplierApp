package com.example.suppileragrimart.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.suppileragrimart.model.OrderInfo

class OrderDiffUtil : DiffUtil.ItemCallback<OrderInfo>() {
    override fun areItemsTheSame(oldItem: OrderInfo, newItem: OrderInfo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: OrderInfo, newItem: OrderInfo): Boolean {
        return oldItem.equals(newItem)
    }
}