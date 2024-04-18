package com.example.suppileragrimart.network

import android.util.Log
import androidx.paging.PagingSource
import com.example.suppileragrimart.model.Warehouse
import com.example.suppileragrimart.model.WarehouseApiRequest
import com.example.suppileragrimart.utils.AES
import retrofit2.HttpException
import java.io.IOException

class WarehousePagingSource(
    private val apiService: Api,
    private val warehouseApiRequest: WarehouseApiRequest,
    private val secretKey: String,
    private val iv: String
) : PagingSource<Int, Warehouse>() {
    override val keyReuseSupported: Boolean = true
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Warehouse> {
        return try {
            val nextPageNumber = params.key ?: 0

            val supplierId = warehouseApiRequest.id
            val sortBy = warehouseApiRequest.sortBy
            val sortDir = warehouseApiRequest.sortDir

            val response = apiService.getWarehouseBySupplierId(
                supplierId,
                nextPageNumber.toString(),
                sortBy,
                sortDir
            )

            val encryptedData = response.body()?.content
            val data = decryptData(encryptedData)

            LoadResult.Page(
                data,
                prevKey = if (nextPageNumber > 0) nextPageNumber - 1 else null,
                nextKey = if (nextPageNumber < response.body()!!.totalPage) nextPageNumber + 1 else null
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }

    private fun decryptData(warehouses: ArrayList<Warehouse>?): ArrayList<Warehouse>{
        val aes = AES.getInstance()
        aes.initFromString(secretKey, iv)
        val decryptedData: ArrayList<Warehouse> = arrayListOf()

        if (warehouses != null) {
            for (w in warehouses){
                val warehouse = Warehouse()
                warehouse.id = w.id
                warehouse.warehouseName = aes.decrypt(w.warehouseName)
                warehouse.email = aes.decrypt(w.email)
                warehouse.phone = aes.decrypt(w.phone)
                warehouse.contact = aes.decrypt(w.contact)
                warehouse.province = aes.decrypt(w.province)
                warehouse.district = aes.decrypt(w.district)
                warehouse.commune = aes.decrypt(w.commune)
                warehouse.detail = aes.decrypt(w.detail)
                warehouse.supplierContactName = aes.decrypt(w.supplierContactName)
                warehouse.isActive = w.isActive

                decryptedData.add(warehouse)
            }
        }
        return decryptedData
    }
}