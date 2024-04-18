package com.example.suppileragrimart.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.suppileragrimart.R
import com.example.suppileragrimart.databinding.InfoDialogItemBinding
import com.example.suppileragrimart.model.CooperationResponse
import com.example.suppileragrimart.utils.OrderStatus

class CooperationAdapter(private val dataList: ArrayList<CooperationResponse>) :
    RecyclerView.Adapter<CooperationAdapter.ViewHolderClass>() {
    var onClick: ((CooperationResponse) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        return ViewHolderClass(
            InfoDialogItemBinding.inflate(LayoutInflater.from(parent.context)),
            parent.context
        )
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]
        holder.bind(currentItem)
        holder.itemView.setOnClickListener {
            onClick?.invoke(currentItem)
        }
    }

    class ViewHolderClass(binding: InfoDialogItemBinding, val context: Context) :
        RecyclerView.ViewHolder(binding.root) {
        val fullName = binding.tvFullName
        val crops = binding.tvCropsType
        val state = binding.tvState

        fun bind(cooperationResponse: CooperationResponse) {
            fullName.text = cooperationResponse.fullName
            crops.text = cooperationResponse.cropsName

            if (cooperationResponse.cooperationStatus == OrderStatus.PROCESSING) {
                state.text = cooperationResponse.cooperationStatus.name
                state.setTextColor(context.getColor(R.color.orange))
            } else if (cooperationResponse.cooperationStatus == OrderStatus.CANCELLED) {
                state.text = cooperationResponse.cooperationStatus.name
                state.setTextColor(context.getColor(R.color.redAgri))
            } else {
                state.text = cooperationResponse.cooperationStatus.name
                state.setTextColor(context.getColor(R.color.greenAgri))
            }
        }
    }
}