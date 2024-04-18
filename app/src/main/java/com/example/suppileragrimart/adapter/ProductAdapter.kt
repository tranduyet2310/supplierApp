package com.example.suppileragrimart.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.suppileragrimart.databinding.ProductListItemBinding
import com.example.suppileragrimart.model.Product
import com.example.suppileragrimart.utils.GlideApp
import com.example.suppileragrimart.utils.ProductDiffUtil
import com.example.suppileragrimart.utils.Utils.Companion.formatPrice

class ProductAdapter(private val context: Context) :
    PagingDataAdapter<Product, ProductAdapter.ViewHolderClass>(ProductDiffUtil()) {

    var onSwitchState: ((Product) -> Unit)? = null
    var onEdit: ((Product) -> Unit)? = null

    class ViewHolderClass(
        binding: ProductListItemBinding,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {
        val imgProduct = binding.imgProduct
        val tvProductName = binding.tvProductName
        val tvProductPrice = binding.tvProductPrice
        val tvProductWarehouse = binding.tvProductWarehouse
        val switchState = binding.switchState
        val imgEdit = binding.imgEdit

        fun bind(product: Product) {
            var requestOptions = RequestOptions()
            requestOptions = requestOptions.transform(FitCenter(), RoundedCorners(16))

            val imageUrl = product.productImage[0].imageUrl

            GlideApp.with(context)
                .load(imageUrl)
                .apply(requestOptions)
                .skipMemoryCache(true)
                .into(imgProduct)

            tvProductName.text = product.productName
            tvProductPrice.text = product.standardPrice.formatPrice()
            tvProductWarehouse.text = product.warehouseName
            switchState.isChecked = product.isActive
        }

    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        getItem(position)?.let { currentItem ->
            holder.bind(currentItem)
            holder.imgEdit.setOnClickListener {
                onEdit?.invoke(currentItem)
            }
            holder.switchState.setOnClickListener {
                onSwitchState?.invoke(currentItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        return ViewHolderClass(
            ProductListItemBinding.inflate(LayoutInflater.from(parent.context)),
            context
        )
    }
}