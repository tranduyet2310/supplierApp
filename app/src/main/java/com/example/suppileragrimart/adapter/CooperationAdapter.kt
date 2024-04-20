package com.example.suppileragrimart.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.suppileragrimart.R
import com.example.suppileragrimart.databinding.InfoDialogItemBinding
import com.example.suppileragrimart.model.Cooperation
import com.example.suppileragrimart.utils.CooperationDiffUtil
import com.example.suppileragrimart.utils.OrderStatus

class CooperationAdapter() :
    PagingDataAdapter<Cooperation, CooperationAdapter.ViewHolderClass>(CooperationDiffUtil()) {
    var onClick: ((Cooperation) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        return ViewHolderClass(
            InfoDialogItemBinding.inflate(LayoutInflater.from(parent.context)),
            parent.context
        )
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        getItem(position)?.let { currentItem ->
            holder.bind(currentItem)
            holder.itemView.setOnClickListener {
                onClick?.invoke(currentItem)
            }
        }
    }

    class ViewHolderClass(binding: InfoDialogItemBinding, val context: Context) :
        RecyclerView.ViewHolder(binding.root) {
        val fullName = binding.tvFullName
        val crops = binding.tvCropsType
        val state = binding.tvState

        fun bind(cooperation: Cooperation) {
            fullName.text = cooperation.fullName
            crops.text = cooperation.cropsName

            if (cooperation.cooperationStatus == OrderStatus.PROCESSING) {
                state.text = cooperation.cooperationStatus.name
                state.setTextColor(context.getColor(R.color.orange))
            } else if (cooperation.cooperationStatus == OrderStatus.CANCELLED) {
                state.text = cooperation.cooperationStatus.name
                state.setTextColor(context.getColor(R.color.redAgri))
            } else {
                state.text = cooperation.cooperationStatus.name
                state.setTextColor(context.getColor(R.color.greenAgri))
            }
        }
    }
}