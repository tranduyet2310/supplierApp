package com.example.suppileragrimart.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.suppileragrimart.R
import com.example.suppileragrimart.databinding.RecentOrderListItemBinding
import com.example.suppileragrimart.model.OrderStatistic
import com.example.suppileragrimart.utils.OrderStatus
import com.example.suppileragrimart.utils.Utils.Companion.formatPrice

class RecentOrderAdapter(
    private val dataList: ArrayList<OrderStatistic>
) : RecyclerView.Adapter<RecentOrderAdapter.ViewHolderClass>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        return ViewHolderClass(
            RecentOrderListItemBinding.inflate(LayoutInflater.from(parent.context)),
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

    class ViewHolderClass(binding: RecentOrderListItemBinding, private val context: Context) :
        RecyclerView.ViewHolder(binding.root) {
        private val tvOrderId = binding.tvOrderId
        private val tvFullName = binding.tvFullName
        private val tvOrderState = binding.tvOrderState
        private val tvTotal = binding.tvTotal
        fun bind(orderStatistic: OrderStatistic) {
            tvOrderId.text = orderStatistic.id.toString()
            tvFullName.text = orderStatistic.userFullName
            val total = "${orderStatistic.total.formatPrice()} đ"
            tvTotal.text = total

            when (orderStatistic.orderStatus) {
                OrderStatus.PROCESSING -> {
                    tvOrderState.text = context.getString(R.string.PROCESSING)
                    tvOrderState.setTextColor(context.getColor(R.color.orange))
                }
                OrderStatus.CANCELLED -> {
                    tvOrderState.text = context.getString(R.string.CANCELLED)
                    tvOrderState.setTextColor(context.getColor(R.color.redAgri))
                }
                else -> {
                    when (orderStatistic.orderStatus) {
                        OrderStatus.CONFIRMED -> {
                            tvOrderState.text = context.getString(R.string.CONFIRMED)
                        }
                        OrderStatus.DELIVERING -> {
                            tvOrderState.text = context.getString(R.string.DELIVERING)
                        }
                        else -> {
                            tvOrderState.text = context.getString(R.string.completed)
                        }
                    }
                    tvOrderState.setTextColor(context.getColor(R.color.greenAgri))
                }
            }
        }
    }
}