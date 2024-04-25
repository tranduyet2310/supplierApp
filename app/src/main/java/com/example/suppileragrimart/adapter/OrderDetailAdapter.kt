package com.example.suppileragrimart.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.suppileragrimart.databinding.OrderProductListItemBinding
import com.example.suppileragrimart.model.OrderProductDto
import com.example.suppileragrimart.utils.GlideApp
import com.example.suppileragrimart.utils.Utils.Companion.formatPrice

class OrderDetailAdapter (
    private val dataList: ArrayList<OrderProductDto>
) : RecyclerView.Adapter<OrderDetailAdapter.ViewHolderClass>() {

//    var onClick: ((OrderProductDto) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        return ViewHolderClass(
            OrderProductListItemBinding.inflate(LayoutInflater.from(parent.context)),
            parent.context
        )
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]
        holder.bind(currentItem)
//        holder.itemView.setOnClickListener {
//            onClick?.invoke(currentItem)
//        }
    }

    class ViewHolderClass(binding: OrderProductListItemBinding, private val context: Context) :
        RecyclerView.ViewHolder(binding.root) {
        val imgProduct = binding.imgProduct
        val tvProductName = binding.tvProductName
        val tvProductPrice = binding.tvProductPrice
        val tvProductQuantity = binding.tvProductQuantity
        val tvWarehouse = binding.tvWarehouse
        fun bind(orderProductDto: OrderProductDto) {
            tvProductName.text = orderProductDto.productName
            if (orderProductDto.discountPrice > 0){
                tvProductPrice.text = orderProductDto.discountPrice.formatPrice()
            } else {
                tvProductPrice.text = orderProductDto.standardPrice.formatPrice()
            }
            tvProductQuantity.text = orderProductDto.quantity.toString()
            tvWarehouse.text = orderProductDto.warehouseName

            var requestOptions = RequestOptions()
            requestOptions = requestOptions.transform(FitCenter(), RoundedCorners(16))
            val imageUrl = orderProductDto.productImage
            GlideApp.with(context)
                .load(imageUrl)
                .apply(requestOptions)
                .skipMemoryCache(true)
                .into(imgProduct)
        }
    }
}