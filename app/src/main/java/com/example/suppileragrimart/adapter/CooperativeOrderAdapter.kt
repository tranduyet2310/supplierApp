package com.example.suppileragrimart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.suppileragrimart.databinding.CooperativeOrderItemBinding
import com.example.suppileragrimart.model.CooperativePayment
import com.example.suppileragrimart.utils.DiffUtilCooperativeOrder
import com.example.suppileragrimart.utils.Utils

class CooperativeOrderAdapter () :
    PagingDataAdapter<CooperativePayment, CooperativeOrderAdapter.ViewHolderClass>(DiffUtilCooperativeOrder()) {

    var onClick: ((CooperativePayment) -> Unit)? = null

    class ViewHolderClass(
        private val binding: CooperativeOrderItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        val tvShopName = binding.tvShopName
        val tvCropsName = binding.tvCropsName
        val tvRequiredYield = binding.tvRequiredYield
        val tvDateCreated = binding.tvDateCreated

        fun bind(cooperativePayment: CooperativePayment){
            val dateCreated = "Ngày tạo: ${cooperativePayment.dateCreated}"
            tvDateCreated.text = dateCreated
            tvShopName.text = cooperativePayment.userFullName
            tvCropsName.text = cooperativePayment.cropsName
            tvRequiredYield.text = Utils.formatYield(cooperativePayment.requireYield)
        }
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        getItem(position)?.let { currentItem ->
            holder.bind(currentItem)
            holder.itemView.setOnClickListener {
                onClick?.invoke(currentItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        return ViewHolderClass(CooperativeOrderItemBinding.inflate(LayoutInflater.from(parent.context)))
    }
}