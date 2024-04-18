package com.example.suppileragrimart.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.suppileragrimart.databinding.WarehouseListItemBinding
import com.example.suppileragrimart.model.Warehouse
import com.example.suppileragrimart.utils.WarehouseDiffUtil

class WarehouseAdapter :
    PagingDataAdapter<Warehouse, WarehouseAdapter.ViewHolderClass>(WarehouseDiffUtil()) {

    var onSwitchState: ((Warehouse) -> Unit)? = null
    var onEdit: ((Warehouse) -> Unit)? = null

    class ViewHolderClass(
        binding: WarehouseListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        val tvWarehouseId = binding.tvWarehouseId
        val tvWarehouseName = binding.tvWarehouseName
        val tvWarehouseAddress = binding.tvWarehouseAddress
        val tvWarehouseContactName = binding.tvWarehouseContactName
        val tvWarehousePhone = binding.tvWarehousePhone
        val switchState = binding.switchState
        val imgEdit = binding.imgEdit

        fun bind(warehouse: Warehouse) {
            tvWarehouseId.text = warehouse.id.toString()
            tvWarehouseName.text = warehouse.warehouseName
            val address = "${warehouse.detail} - ${warehouse.commune} - ${warehouse.district} - ${warehouse.province}"
            tvWarehouseAddress.text = address
            tvWarehouseContactName.text = warehouse.supplierContactName
            tvWarehousePhone.text = warehouse.phone
            switchState.isChecked = warehouse.isActive
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
        return ViewHolderClass(WarehouseListItemBinding.inflate(LayoutInflater.from(parent.context)))
    }
}