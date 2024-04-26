package com.example.suppileragrimart.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.suppileragrimart.databinding.OrderTodayListItemBinding
import com.example.suppileragrimart.model.OrderStatistic
import com.example.suppileragrimart.utils.GlideApp

class OrderTodayAdapter (
    private val dataList: ArrayList<OrderStatistic>
) : RecyclerView.Adapter<OrderTodayAdapter.ViewHolderClass>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        return ViewHolderClass(
            OrderTodayListItemBinding.inflate(LayoutInflater.from(parent.context)),
            parent.context
        )
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]
        holder.bind(currentItem)
    }

    class ViewHolderClass(binding: OrderTodayListItemBinding, private val context: Context) :
        RecyclerView.ViewHolder(binding.root) {
        val tvProductName = binding.tvProductName
        val tvProductQuantity = binding.tvProductQuantity
        val tvProductPrice = binding.tvProductPrice
        val tvFullname = binding.tvFullname
        val tvOrderId = binding.tvOrderId
        val imgProduct = binding.imgProduct
        fun bind(orderStatistic: OrderStatistic) {
            tvProductName.text = orderStatistic.productName
            tvProductQuantity.text = orderStatistic.quantity.toString()
            if (orderStatistic.discountPrice > 0){
                val price = "${orderStatistic.discountPrice} đ"
                tvProductPrice.text = price
            } else {
                val price = "${orderStatistic.standardPrice} đ"
                tvProductPrice.text = price
            }
            tvFullname.text = orderStatistic.userFullName
            tvOrderId.text = orderStatistic.id.toString()

            var requestOptions = RequestOptions()
            requestOptions = requestOptions.transform(FitCenter(), RoundedCorners(16))
            val imageUrl = orderStatistic.productImage
            GlideApp.with(context)
                .load(imageUrl)
                .apply(requestOptions)
                .skipMemoryCache(true)
                .into(imgProduct)
        }
    }
}