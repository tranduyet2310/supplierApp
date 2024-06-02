package com.example.suppileragrimart.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.suppileragrimart.R
import com.example.suppileragrimart.databinding.CropsDetailListItemBinding
import com.example.suppileragrimart.model.FieldDetail
import com.example.suppileragrimart.utils.CropsStatus

class FieldDetailAdapter(
    private val dataList: ArrayList<FieldDetail>
) : RecyclerView.Adapter<FieldDetailAdapter.ViewHolderClass>() {

    var onClick: ((FieldDetail) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        return ViewHolderClass(
            CropsDetailListItemBinding.inflate(LayoutInflater.from(parent.context)),
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

    class ViewHolderClass(binding: CropsDetailListItemBinding, private val context: Context) :
        RecyclerView.ViewHolder(binding.root) {
        private val tvStatus = binding.tvStatus
        private val tvDateCreated = binding.tvDateCreated
        private val tvDetail = binding.tvDetail
        private val imgCropsStatus = binding.imgCropsStatus
        fun bind(fieldDetail: FieldDetail) {
            tvDateCreated.text = fieldDetail.dateCreated
            tvDetail.text = fieldDetail.details
            when (fieldDetail.cropsStatus) {
                CropsStatus.MAKE_LAND -> {
                    tvStatus.text = context.getString(R.string.make_land_time)
                    imgCropsStatus.setImageResource(R.drawable.land)
                }

                CropsStatus.SOW_SEED -> {
                    tvStatus.text = context.getString(R.string.sow_seed_time)
                    imgCropsStatus.setImageResource(R.drawable.sow)
                }

                CropsStatus.GERMINATION -> {
                    tvStatus.text = context.getString(R.string.germination_time)
                    imgCropsStatus.setImageResource(R.drawable.germination)
                }

                CropsStatus.TAKE_CARE -> {
                    tvStatus.text = context.getString(R.string.take_care_time)
                    imgCropsStatus.setImageResource(R.drawable.planting)
                }

                CropsStatus.FLOWERING -> {
                    tvStatus.text = context.getString(R.string.flowering_time)
                    imgCropsStatus.setImageResource(R.drawable.flowering_v2)
                }

                CropsStatus.PEST_PREVENTION -> {
                    tvStatus.text = context.getString(R.string.chemical_time)
                    imgCropsStatus.setImageResource(R.drawable.chemical)
                }

                CropsStatus.FRUITING -> {
                    tvStatus.text = context.getString(R.string.fruiting_time)
                    imgCropsStatus.setImageResource(R.drawable.fruiting_v2)
                }

                CropsStatus.HARVEST -> {
                    tvStatus.text = context.getString(R.string.harvest_time)
                    imgCropsStatus.setImageResource(R.drawable.harvester)
                }

                else -> {

                }
            }
        }
    }
}