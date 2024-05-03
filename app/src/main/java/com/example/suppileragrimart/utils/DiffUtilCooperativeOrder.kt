package com.example.suppileragrimart.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.suppileragrimart.model.CooperativePayment

class DiffUtilCooperativeOrder : DiffUtil.ItemCallback<CooperativePayment>() {
    override fun areItemsTheSame(oldItem: CooperativePayment, newItem: CooperativePayment): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CooperativePayment, newItem: CooperativePayment): Boolean {
        return oldItem.equals(newItem)
    }
}