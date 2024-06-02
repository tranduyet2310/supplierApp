package com.example.suppileragrimart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.suppileragrimart.databinding.CropsListItemBinding
import com.example.suppileragrimart.model.FieldApiResponse

class CropsAdapter(
    private val dataList: ArrayList<FieldApiResponse>
) : RecyclerView.Adapter<CropsAdapter.ViewHolderClass>() {
    var onClick: ((FieldApiResponse) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        return ViewHolderClass(CropsListItemBinding.inflate(LayoutInflater.from(parent.context)))
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

    class ViewHolderClass(binding: CropsListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val tvSeason = binding.tvSeason
        private val tvCropsType = binding.tvCropsType
        private val tvCropsName = binding.tvCropsName
        private val tvArea = binding.tvArea
        fun bind(field: FieldApiResponse) {
            val area = "Diện tích: ${field.area}"
            tvSeason.text = field.season
            tvCropsType.text = field.cropsType
            tvCropsName.text = field.cropsName
            tvArea.text = area
        }
    }
}