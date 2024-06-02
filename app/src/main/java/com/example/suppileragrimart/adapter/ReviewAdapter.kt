package com.example.suppileragrimart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.suppileragrimart.databinding.RecentReviewListItemBinding
import com.example.suppileragrimart.model.ReviewInfo

class ReviewAdapter(
    private val dataList: ArrayList<ReviewInfo>
) : RecyclerView.Adapter<ReviewAdapter.ViewHolderClass>() {

    var onClick: ((ReviewInfo) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        return ViewHolderClass(
            RecentReviewListItemBinding.inflate(LayoutInflater.from(parent.context))
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

    class ViewHolderClass(binding: RecentReviewListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val tvProductName = binding.tvProductName
        private val tvCustomerName = binding.tvCustomerName
        private val ratingProduct = binding.ratingProduct
        fun bind(reviewInfo: ReviewInfo) {
            tvProductName.text = reviewInfo.productName
            tvCustomerName.text = reviewInfo.userFullName
            ratingProduct.rating = reviewInfo.rating.toFloat()
        }
    }
}