package com.example.suppileragrimart.network

import androidx.paging.PagingSource
import com.example.suppileragrimart.model.SearchApiRequest
import com.example.suppileragrimart.model.Warehouse
import retrofit2.HttpException
import java.io.IOException

class WarehouseSearchPagingSource(
    private val apiService: Api,
    private val searchApiRequest: SearchApiRequest,
    private val supplierId: Long
): PagingSource<Int, Warehouse>() {
    override val keyReuseSupported: Boolean = true
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Warehouse> {
        return try {
            val nextPageNumber = params.key ?: 0

            val query = searchApiRequest.query
            val sortBy = searchApiRequest.sortBy
            val sortDir = searchApiRequest.sortDir

            val response = apiService.searchProduct(supplierId, query, nextPageNumber.toString(), sortBy, sortDir)
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