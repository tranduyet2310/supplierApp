package com.example.suppileragrimart.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.suppileragrimart.databinding.YieldListItemBinding
import com.example.suppileragrimart.model.Cooperation
import com.example.suppileragrimart.utils.CooperationDiffUtil
import com.example.suppileragrimart.utils.Utils

class YieldAdapter:
    PagingDataAdapter<Cooperation, YieldAdapter.ViewHolderClass>(CooperationDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        return ViewHolderClass(
            YieldListItemBinding.inflate(LayoutInflater.from(parent.context)),
            parent.context
        )
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        getItem(position)?.let { currentItem ->
            holder.bind(currentItem)
        }
    }

    class ViewHolderClass(binding: YieldListItemBinding, val context: Context) :
        RecyclerView.ViewHolder(binding.root) {
        private val tvUserName = binding.tvUserName
        private val progressBar = binding.progressBar
        private val tvYield = binding.tvYield

        fun bind(cooperation: Cooperation) {
            tvUserName.text = cooperation.fullName
            tvYield.text = Utils.formatYield(cooperation.requireYield)
            val total = cooperation.investment.toDouble()

            if (cooperation.requireYield != 0.0 && total != 0.0){
                val currentProgress: Int = (cooperation.requireYield * 100 / total).toInt()
                progressBar.progress = currentProgress
            }
            progressBar.max = 100
        }
    }
}