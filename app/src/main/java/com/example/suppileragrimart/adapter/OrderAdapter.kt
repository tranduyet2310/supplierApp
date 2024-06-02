package com.example.suppileragrimart.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.suppileragrimart.R
import com.example.suppileragrimart.databinding.SaleListItemBinding
import com.example.suppileragrimart.model.OrderInfo
import com.example.suppileragrimart.utils.GlideApp
import com.example.suppileragrimart.utils.OrderDiffUtil
import com.example.suppileragrimart.utils.OrderStatus

class OrderAdapter(private val context: Context) :
    PagingDataAdapter<OrderInfo, OrderAdapter.ViewHolderClass>(OrderDiffUtil()) {
    var onClick: ((OrderInfo) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        return ViewHolderClass(
            SaleListItemBinding.inflate(LayoutInflater.from(parent.context)),
            context
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

    class ViewHolderClass(binding: SaleListItemBinding, val context: Context) :
        RecyclerView.ViewHolder(binding.root) {
        private val tvOrderId = binding.tvOrderId
        private val imgProductAvatar = binding.imgProductAvatar
        private val tvProductName: TextView = binding.tvProductName
        private val tvOrderCreatedDate = binding.tvOrderCreatedDate
        private val tvOrderState = binding.tvOrderState

        fun bind(orderInfo: OrderInfo) {
            tvOrderId.text = orderInfo.id.toString()
            tvProductName.text = orderInfo.productList[0].productName
            tvOrderCreatedDate.text = orderInfo.dateCreated

            var requestOptions = RequestOptions()
            requestOptions = requestOptions.transform(FitCenter(), RoundedCorners(16))
            val imageUrl = orderInfo.productList[0].productImage
            val modifiedUrl = imageUrl.replace("http://", "https://")
            GlideApp.with(context)
                .load(modifiedUrl)
                .apply(requestOptions)
                .skipMemoryCache(true)
                .into(imgProductAvatar)

            when (orderInfo.orderStatus) {
                OrderStatus.PROCESSING -> {
                    tvOrderState.text = context.getString(R.string.PROCESSING)
                    tvOrderState.setTextColor(context.getColor(R.color.orange))
                }
                OrderStatus.CANCELLED -> {
                    tvOrderState.text = context.getString(R.string.CANCELLED)
                    tvOrderState.setTextColor(context.getColor(R.color.redAgri))
                }
                else -> {
                    when (orderInfo.orderStatus) {
                        OrderStatus.CONFIRMED -> {
                            tvOrderState.text = context.getString(R.string.CONFIRMED)
                        }
                        OrderStatus.DELIVERING -> {
                            tvOrderState.text = context.getString(R.string.DELIVERING)
                        }
                        else -> {
                            tvOrderState.text = context.getString(R.string.COMPLETED)
                        }
                    }
                    tvOrderState.setTextColor(context.getColor(R.color.greenAgri))
                }
            }
        }
    }
}