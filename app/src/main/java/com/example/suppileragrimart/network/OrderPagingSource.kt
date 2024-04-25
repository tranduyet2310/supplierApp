package com.example.suppileragrimart.network

import androidx.paging.PagingSource
import com.example.suppileragrimart.model.OrderInfo
import com.example.suppileragrimart.model.OrderInfoApiRequest
import retrofit2.HttpException
import java.io.IOException

class OrderPagingSource (
    private val apiService: Api,
    private val datePattern: String,
    private val orderInfoApiRequest: OrderInfoApiRequest
) : PagingSource<Int, OrderInfo>() {
    override val keyReuseSupported: Boolean = true
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, OrderInfo> {
        return try {
            val nextPageNumber = params.key ?: 0

            val supplierId = orderInfoApiRequest.id
            val sortBy = orderInfoApiRequest.sortBy
            val sortDir = orderInfoApiRequest.sortDir

            val response = apiService.getOrderBySupplierId(
                supplierId,
                datePattern,
                nextPageNumber.toString(),
                sortBy,
                sortDir
            )
            val data = response.body()?.content

            LoadResult.Page(
                data ?: arrayListOf(),
                prevKey = if (nextPageNumber > 0) nextPageNumber - 1 else null,
                nextKey = if (nextPageNumber < response.body()!!.totalPage) nextPageNumber + 1 else null
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }
}