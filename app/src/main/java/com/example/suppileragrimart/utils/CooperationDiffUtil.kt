package com.example.suppileragrimart.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.suppileragrimart.model.Cooperation

class CooperationDiffUtil : DiffUtil.ItemCallback<Cooperation>() {
    override fun areItemsTheSame(oldItem: Cooperation, newItem: Cooperation): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Cooperation, newItem: Cooperation): Boolean {
        return oldItem.equals(newItem)
    }
}